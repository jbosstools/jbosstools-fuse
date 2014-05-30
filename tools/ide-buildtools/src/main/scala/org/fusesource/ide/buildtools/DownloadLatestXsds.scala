package org.fusesource.ide.buildtools

import java.net.URL
import org.fusesource.scalate.util.IOUtil
import java.io.File

case class Schema(val name: String, val postfix: String = "", val group: String = "camel") {
  val version = if (name.startsWith("activemq")) UpdateReleases.activemqVersion else UpdateReleases.camelVersion
}

class DownloadLatestXsds(rootDir: File, delete: Boolean) extends Runnable {

  override def toString = "Downloading latest XSDs to: " + rootDir

  val xsdArchetypes = List(
    Schema("activemq-spring", "", "activemq"),
    Schema("activemq-ra", "", "activemq"),
    Schema("camel-blueprint"),
    Schema("camel-cxf", "-blueprint"), Schema("camel-cxf", "-spring"),
    Schema("camel-spring"),
    Schema("camel-spring-integration"),
    Schema("camel-spring-security"))

  def run(): Unit = {
    if (!rootDir.exists()) {
      throw new IllegalArgumentException("XSD root dir " + rootDir + " does not exist!")
    }
    val outputDir = new File(rootDir, "xsd/fuse")
    // lets delete all the XSDs to start with
    if (delete && outputDir.exists()) {
      IOUtil.recursiveDelete(outputDir)
    }
    outputDir.mkdirs()

    val r = """<a href=\"(.*)\">(.*)</a>""".r

    val pluginXmlBuffer = new StringBuilder("""<?xml version="1.0" encoding="UTF-8"?>
    <?eclipse version="3.4"?>
    <plugin>
       <extension
             point="org.eclipse.wst.xml.core.catalogContributions">
      		  		<catalogContribution id="org.fusesource.ide.catalogs">
    """)

    for (schema <- xsdArchetypes) {
      try {
        val n = schema.name
        val postfix = schema.postfix
        val group = schema.group
        val version = schema.version
      println("Finding " + n + " group: " + group + " postfix '" + postfix + "'")
        val fileName = n + "-" + version.stripSuffix("/") + postfix + ".xsd"
      val xsd = "http://repository.jboss.org/" + UpdateReleases.releaseRepo + "/org/apache/" + group + "/" + n + "/" + version + "/" + fileName
      val outFile = new File(outputDir, fileName)
      println("Downloading xsd: " + xsd + " to " + outFile)
      IOUtil.copy(new URL(xsd).openStream(), outFile)

      pluginXmlBuffer.append("               <uri\n                      id=\"org.fusesource.xml.catalog.uri." + n
        + "\"\n                      name=\"http://" + group + ".apache.org/schema/" + n.substring(n.indexOf('-') + 1) + "/" + n + ".xsd\"\n"
        + "                      uri=\"xsd/fuse/" + fileName + "\"/>\n")
      } catch {
        case e =>
          println("WARNING: not found: " + e)
      }
    }

    pluginXmlBuffer.append("""

            </catalogContribution>
       </extension>

    </plugin>
    """)

    val pluginXml = new File(rootDir, "plugin.xml")
    println("Regenerating " + pluginXml)
    IOUtil.writeText(pluginXml, pluginXmlBuffer.toString())


    println("Running git add...")
    val pb = new ProcessBuilder("git", "add", "*")
    pb.directory(outputDir)
    pb.start()
  }
}
