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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadLatestXsds implements Runnable {

    public static Logger LOG = LoggerFactory.getLogger(DownloadLatestXsds.class);

    private File rootDir;
    private boolean delete;

    private List<Schema> xsdArchetypes = Arrays.asList(
            new Schema("activemq-spring", "", "activemq"),
            new Schema("activemq-ra", "", "activemq"),
            new Schema("camel-blueprint"),
            new Schema("camel-cxf", "-blueprint"),
            new Schema("camel-cxf", "-spring"),
            new Schema("camel-spring"),
            new Schema("camel-spring-integration"),
            new Schema("camel-spring-security")
    );

    public DownloadLatestXsds(File rootDir, boolean delete) {
        this.rootDir = rootDir;
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "Downloading latest XSDs to: " + rootDir;
    }

    public void run() {
        if (!rootDir.exists()) {
            throw new IllegalArgumentException("XSD root dir " + rootDir + " does not exist!");
        }
        File outputDir = new File(rootDir, "xsd/fuse");

        // lets delete all the XSDs to start with
        if (delete && outputDir.exists()) {
            try {
                FileUtils.deleteDirectory(outputDir);
            } catch (IOException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        outputDir.mkdirs();

        StringBuilder pluginXmlBuffer = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        pluginXmlBuffer.append("<?eclipse version=\"3.4\"?>\n");
        pluginXmlBuffer.append("<plugin>\n");
        pluginXmlBuffer.append("  <extension point=\"org.eclipse.wst.xml.core.catalogContributions\">\n");
        pluginXmlBuffer.append("    <catalogContribution id=\"default\">\n");

        for (Schema schema: xsdArchetypes) {
            try {
                String n = schema.name;
                String postfix = schema.postfix;
                String group = schema.group;
                String version = schema.version;
                LOG.info("Finding " + n + ", group: " + group + ", postfix '" + postfix + "'");
                while (version.endsWith("/")) {
                    version = version.substring(0, version.length() - 1);
                }
                String fileName = n + "-" + version + postfix + ".xsd";
                String xsd = "http://repository.jboss.org/" + UpdateReleases.releaseRepo + "/org/apache/" + group + "/" + n + "/" + version + "/" + fileName;
                File outFile = new File(outputDir, fileName);
                try {
                    LOG.info("Downloading xsd: " + xsd + " to " + outFile);
                    InputStream input = new URL(xsd).openStream();
                    FileOutputStream output = new FileOutputStream(outFile);
                    IOUtils.copy(input, output);
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);
                } catch (FileNotFoundException e) {
                    try {
	                	xsd = "https://repo1.maven.org/maven2/org/apache/" + group + "/" + n + "/" + version + "/" + fileName;
	                    LOG.info("Downloading xsd: " + xsd + " to " + outFile);
	                    InputStream input = new URL(xsd).openStream();
	                    FileOutputStream output = new FileOutputStream(outFile);
	                    IOUtils.copy(input, output);
	                    IOUtils.closeQuietly(input);
	                    IOUtils.closeQuietly(output);
                    } catch (Exception ex) {
                    	xsd = "http://repository.jboss.org/" + UpdateReleases.eaRepo + "/org/apache/" + group + "/" + n + "/" + version + "/" + fileName;
                        LOG.info("Downloading xsd: " + xsd + " to " + outFile);
                        InputStream input = new URL(xsd).openStream();
                        FileOutputStream output = new FileOutputStream(outFile);
                        IOUtils.copy(input, output);
                        IOUtils.closeQuietly(input);
                        IOUtils.closeQuietly(output);
                    }
                }

                pluginXmlBuffer.append("      <uri\n");
                pluginXmlBuffer.append("          name=\"http://" + group + ".apache.org/schema/");
                if (postfix.trim().length()>0) {
                	pluginXmlBuffer.append(postfix.substring(postfix.indexOf('-') + 1) + "/");
                }
                pluginXmlBuffer.append(n.substring(n.indexOf('-') + 1) + "\"\n");
                pluginXmlBuffer.append("          uri=\"platform:/plugin/org.fusesource.ide.catalogs/xsd/fuse/" + fileName + "\" />\n");
            } catch (IOException e) {
                LOG.error("WARNING: not found: " + e.getMessage(), e);
            }
        }

        pluginXmlBuffer.append("    </catalogContribution>\n");
        pluginXmlBuffer.append("  </extension>\n");
        pluginXmlBuffer.append("</plugin>\n");

        File pluginXml = new File(rootDir, "plugin.xml");
        LOG.info("Regenerating " + pluginXml);
        try {
            FileOutputStream output = new FileOutputStream(pluginXml);
            IOUtils.write(pluginXmlBuffer.toString(), output);
            IOUtils.closeQuietly(output);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.info("Running git add...");
        ProcessBuilder pb = new ProcessBuilder("git", "add", "*");
        pb.directory(outputDir);
        try {
            pb.start();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static class Schema {
        public String name;
        public String postfix;
        public String group;
        public String version;

        public Schema(String name) {
            this(name, "");
        }

        public Schema(String name, String postfix) {
            this(name, postfix, "camel");
        }

        public Schema(String name, String postfix, String group) {
            this.name = name;
            this.postfix = postfix;
            this.group = group;
            this.version = name.startsWith("activemq") ? UpdateReleases.activemqVersion : UpdateReleases.camelVersion;
        }
    }

}
