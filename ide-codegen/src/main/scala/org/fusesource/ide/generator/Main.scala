package org.fusesource.ide.generator

import java.io.File
import java.awt._
import java.awt.image._
import javax.imageio.ImageIO
import org.fusesource.scalate.util.Files._
import scala.math._

/**
 * Main for running the code generator
 */
object Main {
  def main(args: Array[String]) {

    val outputDir = if (args.length > 0) args(0) else Generator.defaultOutputDir
    val sourceDir = if (args.length > 1) args(1) else Generator.defaultSourceDir
    var app = new Generator(outputDir, sourceDir)
    val basedir = System.getProperty("basedir", ".")
    println("basedir is " + basedir)

    def convertImages(destDir: File) {
      println("Generating images to: " + destDir)
      destDir.mkdirs
      val srcDir = new File(basedir + "src/main/webapp/stencilsets/camel/icons")

      val extensions = Set("png", "jpg", "jpeg", "gif")
      val sizes = Seq(24, 16)
      val prefix = "node."
      val format = "png"

      for (f <- srcDir.listFiles) {
        val fullName = f.getName
        val name = dropExtension(fullName)
        val ext = extension(fullName)

        if (f.isFile && name.startsWith(prefix) && extensions.contains(ext)) {
          val newPrefix = name.substring(prefix.length)

          def makeIcon(s: Int): Unit = {
            val newName = newPrefix + s + "." + format
            //println("copy " + name + " -> " + newName)

            val img = ImageIO.read(f)
            val smallImg = scaleImage(img, s, s)

            ImageIO.write(smallImg, format, new File(destDir, newName))
          }

          for (s <- sizes) {
            makeIcon(s)
          }

          val newName = newPrefix + "." + format
          println("copy " + name + " -> " + newName)

          val img = ImageIO.read(f)
          var h = max(img.getHeight, 54)
          var w = img.getWidth
          if (h > img.getHeight) {
            // lets increase width relative
            w = round(ceil(w * h / img.getHeight)).toInt
          }
          val smallImg = scaleImage(img, w, h)

          ImageIO.write(smallImg, format, new File(destDir, newName))

        }
      }

    }

    var images = false
    var eclipseEditor = true
    var eclipseModel = true
    var webModel = false
    var hawtio = false

    if (args.size > 0) {
      args(0) match {
        case "archetype" =>
          ArchetypeCatalogConverter.run(args.tail)

        case "images" =>
          images = true
        case "eclipse" =>
          eclipseEditor = true
          eclipseModel = true
        case "hawtio" =>
          hawtio = true
          eclipseEditor = false
          eclipseModel = false
          images = false
        case _ =>
          images = true
          eclipseEditor = true
          eclipseModel = true
          webModel = true
      }
    }
    println("Generate eclipse model " + eclipseModel + " eclipse editor " + eclipseEditor)

    val editorProjectDir = basedir + "/../plugins/org.fusesource.ide.camel.editor"
    val editorDir = editorProjectDir + "/src/org/fusesource/ide/camel/editor"
    val modelDir = basedir + "/../plugins/org.fusesource.ide.camel.model/src/org/fusesource/ide/camel/model/generated"
    val hawtioDir = basedir + "/../../hawtio/hawtio-web/src/main/webapp/app/camel"
    val imageDir = new File(editorProjectDir + "/icons")

    val camelDescriptionElementsFile = new File(basedir + "/../../fuse/tooling/camel-tooling-util/src/main/resources/camelDescriptionElements.txt")

    val camelDescriotionElementsDir = camelDescriptionElementsFile.getParentFile
    if (!camelDescriotionElementsDir.exists()) {
      println("WARNING: cannot generate camelDescriptionElements.txt due to missing directory " + camelDescriotionElementsDir)
    } else {
      app.generateCamelDescriptionElements(camelDescriptionElementsFile)
      println("Generated camelDescriptionElements.txt to " + camelDescriptionElementsFile)
    }

    if (eclipseModel) {
      app.generateEclipseModel(modelDir)
      println("Generated Eclipse model")
    }

    if (eclipseEditor) {
      app.generateEclipseEditor(editorDir)
      println("Generated Eclipse Editor")
    }

    if (webModel) {
      println("Generating the web model")
      app.run
      println("Generated the web model")
    }

    if (images) {
      println("Converting the images")
      assert(imageDir.exists, "No such image directory: " + imageDir)
      //val imageDir = new File(basedir + "target/newIcons")

      convertImages(imageDir)
      println("Converted the images")
    }

    if (hawtio) {
      app = new Generator(outputDir, hawtioDir + "/img")
      app.generateHawtIO(hawtioDir + "/js")
    }
    println("Done generating stuff! Enjoy!")

    // this is here to kill the mvn exec:java hanging
    System.exit(0)
  }


  def scaleImage(img: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage = {
    val kind = if (img.getTransparency() == Transparency.OPAQUE) {
      BufferedImage.TYPE_INT_RGB
    } else {
      BufferedImage.TYPE_INT_ARGB
    }

    var ret = img
    // Use multi-step technique: start with original size, then
    // scale down in multiple passes with drawImage()
    // until the target size is reached
    var w = img.getWidth
    var h = img.getHeight

    do {
      if (w > targetWidth) {
        w /= 2
        if (w < targetWidth) {
          w = targetWidth
        }
      } else if (w < targetWidth) {
        w *= 2
        if (w > targetWidth) {
          w = targetWidth
        }
      }

      if (h > targetHeight) {
        h /= 2
        if (h < targetHeight) {
          h = targetHeight
        }
      } else if (h < targetHeight) {
        h *= 2
        if (h > targetHeight) {
          h = targetHeight
        }
      }

      val tmp = new BufferedImage(w, h, kind)
      val g2 = tmp.createGraphics()
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
      g2.drawImage(ret, 0, 0, w, h, null)
      g2.dispose()

      ret = tmp
    } while (w != targetWidth || h != targetHeight)
    ret
  }

}