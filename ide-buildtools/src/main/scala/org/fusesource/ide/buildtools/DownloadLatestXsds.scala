package org.fusesource.ide.buildtools

import java.net.URL
import org.fusesource.scalate.util.IOUtil
import java.io.File

case class Schema(val name: String, val postfix: String = "", val group: String = "camel")

class DownloadLatestXsds(rootDir: File, delete: Boolean) extends Runnable {

  override def toString = "Downloading latest XSDs to: " + rootDir

  val xsdArchetypes = List(
    Schema("activemq-core", "", "activemq"),
    Schema("activemq-ra", "", "activemq"),
    Schema("camel-blueprint"),
    Schema("camel-cxf", "-blueprint"), Schema("camel-cxf", "-spring"),
    Schema("camel-spring"), Schema("camel-spring-integration"), Schema("camel-spring-security"))

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

    for (Schema(n, postfix, group) <- xsdArchetypes) {
      println("Finding " + n + " group: " + group + " postfix '" + postfix + "'")
      val u = "http://repo.fusesource.com/" + UpdateReleases.releaseRepo + "/org/apache/" + group + "/" + n + "/"
      val text = IOUtil.loadText(new URL(u).openStream())
      val seq = r.findAllIn(text)
      val ms = seq.matchData
      var href = ""
      var version = ""
      for (m <- ms) {
        // TODO lets assume that things are in order!
        val g1 = m.group(1)
        val g2 = m.group(2)
        if (g1.contains(n) && !g2.startsWith("fuse") && !g2.contains("iona") && (g2.contains(UpdateReleases.fuseVersion))) {
          href = g1
          version = g2
        }
      }
      val fileName = n + "-" + version.stripSuffix("/") + postfix + ".xsd"
      val xsd = href + fileName
      val outFile = new File(outputDir, fileName)
      println("Downloading xsd: " + xsd + " to " + outFile)
      IOUtil.copy(new URL(xsd).openStream(), outFile)

      pluginXmlBuffer.append("               <uri\n                      id=\"org.fusesource.xml.catalog.uri." + n
        + "\"\n                      name=\"http://" + group + ".apache.org/schema/" + n.substring(n.indexOf('-') + 1) + "/" + n + ".xsd\"\n"
        + "                      uri=\"xsd/fuse/" + fileName + "\"/>\n")
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