/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.buildtools;

import io.fabric8.insight.maven.aether.Aether;
import io.fabric8.insight.maven.aether.AetherResult;
import io.fabric8.insight.maven.aether.Repository;
import io.hawt.maven.indexer.ArtifactDTO;
import io.hawt.maven.indexer.MavenIndexerFacade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Downloader {

    public static Logger LOG = LoggerFactory.getLogger(Downloader.class);

    private MavenIndexerFacade indexer;
    private Aether aether;
    private File archetypeDir = new File("fuse-ide-archetypes");
    private File camelComponentMetaData = new File("camel-metadata");
    private File xsdDir = new File("fuse-ide-xsds");
    private boolean delete = true;
    
    private static final String langFilePrefix = "/*******************************************************************************\n" + 
            " * Copyright (c) 2013 Red Hat, Inc.\n" + 
            " * Distributed under license by Red Hat, Inc. All rights reserved.\n" + 
            " * This program is made available under the terms of the\n" + 
            " * Eclipse Public License v1.0 which accompanies this distribution,\n" + 
            " * and is available at http://www.eclipse.org/legal/epl-v10.html\n" + 
            " *\n" + 
            " * Contributors:\n" + 
            " *     Red Hat, Inc. - initial API and implementation\n" + 
            " ******************************************************************************/\n" + 
            "\n" + 
            "package org.fusesource.ide.camel.editor;\n" + 
            "\n" + 
            "import org.eclipse.osgi.util.NLS;\n" + 
            "\n" + 
            "/**\n" + 
            " * NOTE - this file is auto-generated.\n" +
            " *\n" +
            " * DO NOT EDIT!\n" + 
            " *\n" + 
            " * @author lhein\n" + 
            " */\n" + 
            "public class ConnectorsMessages extends NLS {\n" + 
            "\n" + 
            "    private static final String BUNDLE_NAME = \"org.fusesource.ide.camel.editor.l10n.connectorsMessages\";\n\n";
    
    private static final String langFilePostfix = "\n\n" + 
            "    static {\n" + 
            "        // initialize resource bundle\n" + 
            "        NLS.initializeMessages(BUNDLE_NAME, ConnectorsMessages.class);\n" + 
            "    }\n" + 
            "}\n";

    // setup an ignore list for unwanted archetypes
    private static ArrayList<String> ignoredArtifacts = new ArrayList<String>();

    static {
        ignoredArtifacts.add("camel-archetype-component-scala");
        ignoredArtifacts.add("camel-archetype-scala");
        ignoredArtifacts.add("camel-web-osgi-archetype");
        ignoredArtifacts.add("camel-archetype-groovy");
    }

    public static void main(String[] args) {
        try {
            // lets find the eclipse plugins directory
            File rs_editor = new File(targetDir(), "../../../editor/plugins");
            File rs_core = new File(targetDir(), "../../../core/plugins");
            
            if (args.length > 1) {
                rs_editor = new File(args[0]);
                rs_core = new File(args[1]);
            }

            LOG.info("Using editor plugins directory: {}", rs_editor.getAbsolutePath());
            LOG.info("Using core plugins directory: {}", rs_core.getAbsolutePath());

            if (!rs_editor.exists()) {
                fail("IDE editor plugins directory does not exist! " + rs_editor.getAbsolutePath());
            }
            if (!rs_editor.isDirectory()) {
                fail("IDE editor plugins directory is a file, not a directory! " + rs_editor.getAbsolutePath());
            }
            if (!rs_core.exists()) {
                fail("IDE core plugins directory does not exist! " + rs_core.getAbsolutePath());
            }
            if (!rs_core.isDirectory()) {
                fail("IDE core plugins directory is a file, not a directory! "  + rs_core.getAbsolutePath());
            }

            File archetypesDir = new File(rs_editor, "org.fusesource.ide.branding/archetypes");
            File xsdsDir = new File(rs_editor, "org.fusesource.ide.catalogs");
            File compDir = new File(rs_core, "org.fusesource.ide.camel.model/components");

            Downloader app = new Downloader(archetypesDir, xsdsDir, compDir);
            app.start();
            LOG.info("Indexer has started, now trying to find stuff");
            app.run();
            app.stop();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    protected static void fail(String message) {
        LOG.info(message);
        System.exit(1);
    }

    public Downloader() {
    }

    public Downloader(File archetypeDir, File xsdDir, File camelComponentMetaData) {
        this.archetypeDir = archetypeDir;
        this.xsdDir = xsdDir;
        this.camelComponentMetaData = camelComponentMetaData;
    }

    public static File targetDir() {
        String basedir = System.getProperty("basedir", ".");
        return new File(basedir + "/target");
    }

    public void start() throws Exception {
        indexer = new MavenIndexerFacade();
        String[] repositories = { "http://repository.jboss.org/nexus/content/groups/ea/", "http://repo1.maven.org/maven2" };
        indexer.setRepositories(repositories);
        indexer.setCacheDirectory(new File(targetDir(), "mavenIndexer"));
        indexer.start();

        List<Repository> repos = Aether.defaultRepositories();
        repos.add(new Repository("ea.repository.jboss.org", "http://repository.jboss.org/nexus/content/groups/ea"));
        aether = new Aether(Aether.USER_REPOSITORY, repos);
    }

    public void stop() throws Exception {
        indexer.destroy();
    }

    public void run() throws Exception {
        downloadArchetypes();
        downloadXsds();
        downloadCamelComponentData();
    }

    public void downloadArchetypes() throws IOException {
        if (delete) {
            FileUtils.deleteDirectory(archetypeDir);
            archetypeDir.mkdirs();
        }

        PrintWriter out = new PrintWriter(new FileWriter(new File(archetypeDir, "archetypes.xml")));
        out.println("<archetypes>");

        try {
            downloadArchetypesForGroup(out, "org.apache.camel.archetypes", System.getProperty("camel.version"));
            downloadArchetypesForGroup(out, "org.apache.cxf.archetype", System.getProperty("cxf.version"));
            downloadArchetypesForGroup(out, "io.fabric8", System.getProperty("fabric.version"));
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            out.println("</archetypes>");
            out.close();
        }

        LOG.info("Running git add...");
        ProcessBuilder pb = new ProcessBuilder("git", "add", "*");
        pb.directory(archetypeDir);
        pb.start();
    }

    protected void downloadArchetypesForGroup(PrintWriter out, String groupId, String version)
            throws Exception {
        String classifier = null;
        String packaging = "maven-archetype";

        List<ArtifactDTO> answer = indexer.search(groupId, "", "", packaging, classifier, null);
        for (ArtifactDTO artifact : answer) {
            if (ignoredArtifacts.contains(artifact.getArtifactId())) {
                LOG.debug("Ignored: {}", artifact.getArtifactId());
                continue;
            }
            out.println("<archetype groupId='" + artifact.getGroupId() + "' artifactId='" + artifact.getArtifactId() + "' version='" + version + "'>" + artifact.getDescription() + "</archetype>");
            downloadArtifact(artifact, version);
        }
        LOG.debug("Found " + answer.size() + " results for groupId " + groupId + ", version " + version);
    }

    public void downloadXsds() throws Exception {
        new DownloadLatestXsds(xsdDir, true).run();
    }

    public void downloadCamelComponentData() throws IOException {
        String version = System.getProperty("camel.version");

        File outputFile = new File(camelComponentMetaData, "components-" + version + ".xml");
        if (outputFile.exists() && outputFile.isFile()) outputFile.delete(); 
        
        HashMap<String, Component> knownComponents = new HashMap<String, Component>();
        try {
            List<ArtifactDTO> answer = indexer.search("org.apache.camel", "", version, "jar", "", null);

            for (ArtifactDTO artifact : answer) {
                if (!artifact.getGroupId().equalsIgnoreCase("org.apache.camel") &&
                    !artifact.getArtifactId().startsWith("camel-")) {
                    LOG.debug("Ignored: {}", artifact.getArtifactId());
                    continue;
                }
                
                String[] components = downloadProperties(artifact, artifact.getVersion());
                for (String component : components) {
                    String clazz = getCamelComponentClass(artifact, component);
                    Component c = knownComponents.get(clazz);
                    if (c == null) {
                        c = new Component();
                        c.setClazz(clazz);
                    }

                    c.getPrefixes().add(component);                        
                    c.setArtifact(artifact);

// commented for the moment
//                        ArrayList<UriParam> params = getUriParams(c);
//                        c.getUriParams().addAll(params);
                    
                    knownComponents.put(clazz, c);
                }
            }
            
            final String LABEL_POSTFIX = "_connector_title";
            final String DESC_POSTFIX  = "_connector_description";
            
            File propFile = new File(targetDir(), "../../../editor/plugins/org.fusesource.ide.camel.editor/src/org/fusesource/ide/camel/editor/l10n/connectorsMessages.properties");
            Properties languageProperties = new Properties();
            languageProperties.load(new FileInputStream(propFile));
            
            File langFile = new File(targetDir(), "../../../editor/plugins/org.fusesource.ide.camel.editor/src/org/fusesource/ide/camel/editor/ConnectorsMessages.java");
            StringBuffer lines = new StringBuffer();
            
            PrintWriter out = new PrintWriter(new FileWriter(outputFile));
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<components>");
            
            Collection<Component> comps = knownComponents.values();
            for (Component comp : comps) {
                
                // write to language property file
                String labelKey = String.format("%s%s", comp.getId().replaceAll("-", "_"), LABEL_POSTFIX);
                String descKey = String.format("%s%s", comp.getId().replaceAll("-", "_"), DESC_POSTFIX);
                if (!languageProperties.containsKey(labelKey)) {
                    languageProperties.setProperty(labelKey, comp.getId().replaceAll("-", "_"));
                } 
                if (!languageProperties.containsKey(descKey)) {
                    languageProperties.setProperty(descKey, comp.getId().replaceAll("-", "_"));
                } 
                
                // write to ConnectorsMessages.java file
                lines.append(String.format("\tpublic static String %s;\n", labelKey));
                lines.append(String.format("\tpublic static String %s;\n\n", descKey));
                                
                out.println("   <component>");
                out.println("       <id>" + comp.getId() + "</id>");
                out.println("       <class>" + comp.getClazz() + "</class>");
                out.println("       <prefixes>");
                for (String prefix : comp.getPrefixes()) {
                    out.println("           <prefix>" + prefix + "</prefix>");
                }
                out.println("       </prefixes>");
                out.println("       <dependencies>");
                out.println("           <dependency>");
                out.println(String.format("             <groupId>%s</groupId>", comp.getArtifact().getGroupId()));
                out.println(String.format("             <artifactId>%s</artifactId>", comp.getArtifact().getArtifactId()));
                out.println(String.format("             <version>%s</version>", comp.getArtifact().getVersion()));
                out.println("           </dependency>");
                out.println("       </dependencies>");

//                out.println("       <componentProperties />");
//                out.println("       <uriParameters>");
//                for (UriParam p : comp.getUriParams()) {
//                    out.println("           <parameter name=\"" + p.getName() + "\" type=\"" + p.getType() + "\" defaultValue=\"" + p.getDefaultValue() + "\" kind=\"" + p.getKind() + "\" mandatory=\"" + p.getMandatory() + "\" label=\"" + p.getLabel() + "\" description=\"" + p.getDescription() + "\"/>");
//                }
//                out.println("       </uriParameters>");
                
                out.println("   </component>");
            }
            out.println("</components>");
            out.close();
            
            // save the properties
            Properties tmp = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            tmp.putAll(languageProperties);
            tmp.store(new FileOutputStream(propFile), "#\n" + 
                    "# NOTE - this file is auto-generated.\n" + 
                    "#\n" + 
                    "# DO NOT ADD NEW PROPERTIES! ONLY EDIT EXISTING ONES! \n" + 
                    "#");
            
            // save the java language file
            IOUtils.write(String.format("%s%s%s", langFilePrefix, lines, langFilePostfix), new FileOutputStream(langFile));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        
        LOG.info("Running git add...");
        ProcessBuilder pb = new ProcessBuilder("git", "add", "*");
        pb.directory(camelComponentMetaData);
        pb.start();
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public Aether getAether() {
        return aether;
    }

    public void setAether(Aether aether) {
        this.aether = aether;
    }

    public MavenIndexerFacade getIndexer() {
        return indexer;
    }

    public void setIndexer(MavenIndexerFacade indexer) {
        this.indexer = indexer;
    }

    public File getArchetypeDir() {
        return archetypeDir;
    }

    public void setArchetypeDir(File archetypeDir) {
        this.archetypeDir = archetypeDir;
    }

    public File getXsdDir() {
        return xsdDir;
    }

    public void setXsdDir(File xsdDir) {
        this.xsdDir = xsdDir;
    }

    protected void downloadArtifact(ArtifactDTO artifact, String version) {
        try {
            AetherResult result = aether.resolve(artifact.getGroupId(), artifact.getArtifactId(), version, "jar", null);
            if (result != null) {
                List<File> files = result.getResolvedFiles();
                if (files != null && files.size() > 0) {
                    File file = files.get(0);
                    //for (File file : files) {
                    File newFile = new File(archetypeDir, file.getName());
                    FileInputStream input = new FileInputStream(file);
                    FileOutputStream output = new FileOutputStream(newFile);
                    IOUtils.copy(input, output);
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);
                    LOG.info("Copied {}", newFile.getPath());
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
    }

    protected String[] downloadProperties(ArtifactDTO artifact, String version) {
        try {
            AetherResult result = aether.resolve(artifact.getGroupId(), artifact.getArtifactId(), version, "properties", "camelComponent");
            if (result != null) {
                List<File> files = result.getResolvedFiles();
                if (files != null && files.size() > 0) {
                    File file = files.get(0);
                    Properties p = new Properties();
                    p.load(new FileInputStream(file));
                    String comps = p.getProperty("components", "");
                    return comps.split(" ");
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        return new String[0];
    }
    
    protected String getCamelComponentClass(ArtifactDTO artifact, String component) {
        String clazz = null;
        
        // download the artifact jar and check /META-INF/services/org/apache/camel/component/<component> for the class of the component
        try {
            AetherResult result = aether.resolve(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), "jar", null);
            if (result != null) {
                List<File> files = result.getResolvedFiles();
                if (files != null && files.size() > 0) {
                    File file = files.get(0);
                    //for (File file : files) {
                    File newFile = new File(System.getProperty("java.io.tmpdir"), file.getName());
                    FileInputStream input = new FileInputStream(file);
                    FileOutputStream output = new FileOutputStream(newFile);
                    IOUtils.copy(input, output);
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);

                    ZipFile zf = new ZipFile(newFile);
                    ZipEntry ze = zf.getEntry("META-INF/services/org/apache/camel/component/" + component);
                    if (ze != null) {
                        InputStream is = zf.getInputStream(ze);
                        if (is != null) {
                            Properties p = new Properties();
                            p.load(is);
                            is.close();
                            if (p.containsKey("class")) {
                                clazz = p.getProperty("class");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        
        return clazz;
    }

    protected File getCamelComponentJar(ArtifactDTO artifact) {
        File jar = null;
        
        // download the artifact jar and check /META-INF/services/org/apache/camel/component/<component> for the class of the component
        try {
            AetherResult result = aether.resolve(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), "jar", null);
            if (result != null) {
                List<File> files = result.getResolvedFiles();
                if (files != null && files.size() > 0) {
                    File file = files.get(0);
                    //for (File file : files) {
                    File newFile = new File(System.getProperty("java.io.tmpdir"), file.getName());
                    FileInputStream input = new FileInputStream(file);
                    FileOutputStream output = new FileOutputStream(newFile);
                    IOUtils.copy(input, output);
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);
                    jar = newFile;
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        
        return jar;
    }
    
    private ArrayList<UriParam> getUriParams(Component c) {
        ArrayList<UriParam> params = new ArrayList<UriParam>();
        
        File jarFile = getCamelComponentJar(c.getArtifact());
        
        ArtifactDTO core = new ArtifactDTO("org.apache.camel", "camel-core", c.getArtifact().getVersion(), null, null, null, 0, null, null);        
        File coreFile = getCamelComponentJar(core);
        
        if (jarFile != null && jarFile.exists() && jarFile.isFile() &&
            coreFile != null && coreFile.exists() && coreFile.isFile()) {
            try {
                URLClassLoader child = new URLClassLoader(new URL[] {jarFile.toURI().toURL(), coreFile.toURI().toURL()}, this.getClass().getClassLoader());
                Class classToLoad = Class.forName (c.getClazz(), true, child);
                Method method = classToLoad.getDeclaredMethod("createComponentConfiguration");
                Object instance = classToLoad.newInstance();
                Object compConf = method.invoke(instance);
                method = compConf.getClass().getDeclaredMethod("createParameterJsonSchema");
                Object jsonBlob = method.invoke(compConf);
                System.err.println(jsonBlob);
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
                return new ArrayList<Downloader.UriParam>();
            }
        }

        return params;
    }
    
    class Component {
        private String id;
        private String clazz;
        private ArrayList<String> prefixes = new ArrayList<String>();
        private ArrayList<UriParam> uriParams = new ArrayList<UriParam>();
        private ArtifactDTO artifact;

        /**
         * always sort prefixes and use first prefix as id
         */
        public void calculateId() {
            Collections.sort(prefixes);
            this.id = prefixes.get(0);
        }
        
        /**
         * @return the artifact
         */
        public ArtifactDTO getArtifact() {
            return this.artifact;
        }
        
        /**
         * @param artifact the artifact to set
         */
        public void setArtifact(ArtifactDTO artifact) {
            this.artifact = artifact;
        }
        
        /**
         * @return the id
         */
        public String getId() {
            calculateId();
            return this.id;
        }
        
        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }
        
        /**
         * @return the clazz
         */
        public String getClazz() {
            return this.clazz;
        }
        
        /**
         * @param clazz the clazz to set
         */
        public void setClazz(String clazz) {
            this.clazz = clazz;
        }
        
        /**
         * @return the prefixes
         */
        public ArrayList<String> getPrefixes() {
            Collections.sort(this.prefixes);
            return this.prefixes;
        }
        
        /**
         * @param prefixes the prefixes to set
         */
        public void setPrefixes(ArrayList<String> prefixes) {
            this.prefixes = prefixes;
        }
        
        /**
         * @return the uriParams
         */
        public ArrayList<UriParam> getUriParams() {
            return this.uriParams;
        }
        
        /**
         * @param uriParams the uriParams to set
         */
        public void setUriParams(ArrayList<UriParam> uriParams) {
            this.uriParams = uriParams;
        }
    }
    
    class UriParam {
        private String name;
        private String type;
        private String defaultValue;
        private String kind;
        private String mandatory;
        private String label;
        private String description;
        
        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }
        
        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }
        
        /**
         * @return the type
         */
        public String getType() {
            return this.type;
        }
        
        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }
        
        /**
         * @return the defaultValue
         */
        public String getDefaultValue() {
            return this.defaultValue;
        }
        
        /**
         * @param defaultValue the defaultValue to set
         */
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        /**
         * @return the kind
         */
        public String getKind() {
            return this.kind;
        }
        
        /**
         * @param kind the kind to set
         */
        public void setKind(String kind) {
            this.kind = kind;
        }
        
        /**
         * @return the mandatory
         */
        public String getMandatory() {
            return this.mandatory;
        }
        
        /**
         * @param mandatory the mandatory to set
         */
        public void setMandatory(String mandatory) {
            this.mandatory = mandatory;
        }
        
        /**
         * @return the label
         */
        public String getLabel() {
            return this.label;
        }
        
        /**
         * @param label the label to set
         */
        public void setLabel(String label) {
            this.label = label;
        }
        
        /**
         * @return the description
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
