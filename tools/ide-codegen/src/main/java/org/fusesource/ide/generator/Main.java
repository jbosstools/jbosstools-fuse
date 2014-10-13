/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.generator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main for running the code generator
 */
public class Main {

    public static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        String outputDir = args.length > 0 ? args[0] : Generator.defaultOutputDir;
        String sourceDir = args.length > 1 ? args[1] : Generator.defaultSourceDir;
        Generator app = new Generator(outputDir, sourceDir);
        String basedir = System.getProperty("basedir", ".");
        LOG.info("basedir is {}", basedir);

        boolean images = false;
        boolean eclipseEditor = false;
        boolean eclipseModel = false;
        boolean webModel = false;
        boolean hawtio = false;

        if (args.length > 0) {
            switch (args[0]) {
                case "archetype":
                    String[] newArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                    ArchetypeCatalogConverter.run(newArgs);
                    break;
                case "images":
                    images = true;
                    break;
                case "eclipse":
                    eclipseEditor = true;
                    eclipseModel = true;
                    break;
                case "hawtio":
                    hawtio = true;
                    break;
                default:
                    images = true;
                    eclipseEditor = true;
                    eclipseModel = true;
                    webModel = true;
                    break;
            }
        }

        LOG.info("Generate eclipse model: " + eclipseModel + ", eclipse editor: " + eclipseEditor);

        String editorProjectDir = basedir + "/../../editor/plugins/org.fusesource.ide.camel.editor";
        String editorDir = editorProjectDir + "/src/org/fusesource/ide/camel/editor";
        String modelDir = basedir + "/../../core/plugins/org.fusesource.ide.camel.model/src/org/fusesource/ide/camel/model/generated";
        String hawtioDir = basedir + "/../../../hawtio/hawtio-web/src/main/webapp/app/camel";
        String hawtioLibDir = basedir + "/../../../hawtio/hawtio-web/src/main/webapp/lib";
        File imageDir = new File(editorProjectDir + "/icons");

        File camelDescriptionElementsFile = new File(basedir + "/../../../fabric8/tooling/camel-tooling-util/src/main/resources/camelDescriptionElements.txt");

        File camelDescriotionElementsDir = camelDescriptionElementsFile.getParentFile();
        if (!camelDescriotionElementsDir.exists()) {
            LOG.warn("Cannot generate camelDescriptionElements.txt due to missing directory {}", camelDescriotionElementsDir);
        } else {
            app.generateCamelDescriptionElements(camelDescriptionElementsFile);
            LOG.info("Generated camelDescriptionElements.txt to {}", camelDescriptionElementsFile);
        }

        if (eclipseModel) {
            app.generateEclipseModel(modelDir);
            LOG.info("Generated Eclipse Model");
        }

        if (eclipseEditor) {
            app.generateEclipseEditor(editorDir);
            LOG.info("Generated Eclipse Editor");
        }

        if (webModel) {
            LOG.info("Generating the web model");
            app.run();
            LOG.info("Generated the web model");
        }

        if (images) {
            LOG.info("Converting the images");
            if (!imageDir.exists()) {
                throw new RuntimeException("No such image directory: " + imageDir);
            }
            //imageDir = new File(basedir + "target/newIcons");

            convertImages(imageDir, basedir);
            LOG.info("Converted the images");
        }

        if (hawtio) {
            String hawtioJsDir = hawtioLibDir;
            new CamelHeaderModelGenerator(hawtioDir + "/js").run();
            app = new Generator(outputDir, hawtioDir + "/img");
            for (NodeDefinition<?> a: app.nodeDefinitions) {
                LOG.info("node def: {}", a);
            }
            for (NodeDefinition<?> a: app.baseClassAndNestedClasses) {
                LOG.info("base: {}", a);
            }
            app.generateHawtIO(hawtioJsDir);
            LOG.info("Generated hawtio to: " + app);
        }
        LOG.info("Done generating stuff! Enjoy!");

        // this is here to kill the mvn exec:java hanging
        System.exit(0);
    }

    private static void convertImages(File destDir, String basedir) throws IOException {
        LOG.info("Generating images to: {}", destDir);
        destDir.mkdirs();
        File srcDir = new File(basedir + "src/main/webapp/stencilsets/camel/icons");

        Set<String> extensions = new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "gif"));
        int[] sizes = new int[] { 24, 16 };
        String prefix = "node.";
        String format = "png";

        for (File f: srcDir.listFiles()) {
            String fullName = f.getName();
            String name = fullName.lastIndexOf('.') != -1 ? fullName.substring(0, fullName.lastIndexOf('.')) : fullName;
            String ext = fullName.lastIndexOf('.') != -1 ? fullName.substring(fullName.lastIndexOf('.')) : "";

            if (f.isFile() && name.startsWith(prefix) && extensions.contains(ext)) {
                String newPrefix = name.substring(prefix.length());

                for (int s: sizes) {
                    makeIcon(s, f, newPrefix, format, destDir);
                }

                String newName = newPrefix + "." + format;
                LOG.info("copy " + name + " to " + newName);

                BufferedImage img = ImageIO.read(f);
                int h = Math.max(img.getHeight(), 54);
                int w = img.getWidth();
                if (h > img.getHeight()) {
                    // lets increase width relative
                    w = (int)Math.round(Math.ceil(w * h / img.getHeight()));
                }
                BufferedImage smallImg = scaleImage(img, w, h);

                ImageIO.write(smallImg, format, new File(destDir, newName));
            }
        }
    }

    private static void makeIcon(int s, File f, String newPrefix, String format, File destDir) throws IOException {
        String newName = newPrefix + s + "." + format;

        BufferedImage img = ImageIO.read(f);
        BufferedImage smallImg = scaleImage(img, s, s);

        ImageIO.write(smallImg, format, new File(destDir, newName));
    }

    private static BufferedImage scaleImage(BufferedImage img, int targetWidth, int targetHeight) {
        int kind = img.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        BufferedImage ret = img;
        // Use multi-step technique: start with original size, then
        // scale down in multiple passes with drawImage()
        // until the target size is reached
        int w = img.getWidth();
        int h = img.getHeight();

        do {
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            } else if (w < targetWidth) {
                w *= 2;
                if (w > targetWidth) {
                    w = targetWidth;
                }
            }

            if (h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            } else if (h < targetHeight) {
                h *= 2;
                if (h > targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, kind);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

}
