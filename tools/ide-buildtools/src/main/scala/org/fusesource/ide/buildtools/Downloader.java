/**
 * Copyright (C) 2010, FuseSource Corp.  All rights reserved.
 * http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * AGPL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.ide.buildtools;

import io.hawt.maven.indexer.ArtifactDTO;
import io.hawt.maven.indexer.MavenIndexerFacade;
import org.fusesource.insight.maven.aether.Aether;
import org.fusesource.insight.maven.aether.AetherResult;
import org.fusesource.scalate.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;

public class Downloader {
    private MavenIndexerFacade indexer;
    private Aether aether;
    private File archetypeDir = new File("fuse-ide-archetypes");
    private File xsdDir = new File("fuse-ide-xsds");
    private boolean delete = true;

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
            File rs = new File("../../plugins");
            if (args.length > 1) {
                rs = new File(args[0]);
            } else if (!rs.exists()) {
                rs = new File("../../plugins");
            }
            System.out.println("Using IDE directory: " + rs.getAbsolutePath());

            if (!rs.exists()) {
                fail("IDE directory does not exist!");
            }
            if (!rs.isDirectory()) {
                fail("IDE directory is a file, not a directory!");
            }

            File archetypesDir = new File(rs, "org.fusesource.ide.branding/archetypes");
            File xsdsDir = new File(rs, "org.fusesource.ide.catalogs");

            Downloader app = new Downloader(archetypesDir, xsdsDir);
            app.start();
            System.out.println("Indexer has started, now trying to find stuff");
            app.run();
            app.stop();
        } catch (Exception e) {
            System.out.println("Caught " + e);
            e.printStackTrace();
        }
    }

    protected static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public Downloader() {
    }

    public Downloader(File archetypeDir, File xsdDir) {
        this.archetypeDir = archetypeDir;
        this.xsdDir = xsdDir;
    }

    public static File targetDir() {
        String basedir = System.getProperty("basedir", ".");
        return new File(basedir + "/target");
    }

    public void start() throws Exception {
        indexer = new MavenIndexerFacade();
        String[] repositories = {"http://repository.jboss.org/nexus/content/groups/ea/"};
        indexer.setRepositories(repositories);
        indexer.setCacheDirectory(new File(targetDir(), "mavenIndexer"));
        indexer.start();

        aether = new Aether(Aether.userRepository(), Aether.defaultRepositories());
    }

    public void stop() throws Exception {
        indexer.destroy();
    }

    public void run() throws Exception {
        downloadArchetypes();
        downloadXsds();
    }

    public void downloadArchetypes() throws IOException {
        if (delete) {
          IOUtil.recursiveDelete(archetypeDir);
          archetypeDir.mkdirs();
        }

        PrintWriter out = new PrintWriter(new FileWriter(new File(archetypeDir, "archetypes.xml")));
        out.println("<archetypes>");

	    try {
            downloadArchetypesForGroup(out, "org.apache.camel.archetypes", System.getProperty("camel-version"));
            downloadArchetypesForGroup(out, "org.apache.cxf.archetype", System.getProperty("cxf-version"));
            downloadArchetypesForGroup(out, "org.fusesource.fabric", System.getProperty("fabric-version"));
	    } catch (Exception ex) {
			ex.printStackTrace();
	    } finally {
		    out.println("</archetypes>");
		    out.close();
	    }

        System.out.println("Running git add...");
        ProcessBuilder pb = new ProcessBuilder("git", "add", "*");
        pb.directory(archetypeDir);
        pb.start();
    }

    protected void downloadArchetypesForGroup(PrintWriter out, String groupId, String version)
            throws IOException {
        String classifier = null;
        String packaging = "maven-archetype";

        List<ArtifactDTO> answer = indexer.search(groupId, "", "", packaging, classifier, null);
        for (ArtifactDTO artifact : answer) {
            if (ignoredArtifacts.contains(artifact.getArtifactId())) {
                System.out.println("Ignored: " + artifact.getArtifactId());
                continue;
            }
            out.println("<archetype groupId='" + artifact.getGroupId() + "' artifactId='" + artifact.getArtifactId() + "' version='" + version + "'>" + artifact.getDescription() + "</archetype>");
            downloadArtifact(artifact, version);
        }
        System.out.println("Found " + answer.size() + " results for groupId " + groupId + " version " + version);
    }

    public void downloadXsds() throws Exception {
        // TODO can't seem to find the XSDs in the nexus index! No idea why! We find 2 out of the 8 schemas we need
        // so lets keep the scala code for this part
        new DownloadLatestXsds(xsdDir, true).run();
/*

        String[] groupIds = {"org.apache.camel.archetypes", "org.apache.cxf.archetype", "org.fusesource.fabric"};
        String classifier = null;
        String packaging =  "xsd";
        String groupId = "org.apache.camel";
        String artifactId = "camel-spring";
        List<ArtifactDTO> answer = indexer.search(groupId, artifactId, null, packaging, classifier);
        for (ArtifactDTO artifact : answer) {
            System.out.println("Found: " + artifact);
        }
        System.out.println("Found " + answer.size() + " results for groupId " + groupId);
*/
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
        AetherResult result = aether.resolve(artifact.getGroupId(), artifact.getArtifactId(),version, "jar", null);
        if (result != null) {
            List<File> files = result.resolvedFiles();
            if (files != null && files.size() > 0) {
                File file = files.get(0);
                //for (File file : files) {
                File newFile = new File(archetypeDir, file.getName());
                IOUtil.copy(file, newFile);
                System.out.println("Copied " + newFile.getPath());
            }
        }
    }
}
