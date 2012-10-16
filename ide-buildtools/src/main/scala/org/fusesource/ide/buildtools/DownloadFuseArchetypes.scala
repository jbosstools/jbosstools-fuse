package org.fusesource.ide.buildtools

import xml.XML
import collection.JavaConversions._
import collection.mutable.{HashMap, HashSet, Set}
import org.fusesource.scalate.util.IOUtil
import java.io.{FileWriter, PrintWriter, File}
import org.fusesource.insight.maven.aether.Aether
import java.net.{HttpURLConnection, Authenticator, URL}

case class GroupAndArtifact(groupId: String, artifactId: String) extends Ordered[GroupAndArtifact] {

  def compare(that: GroupAndArtifact) = {
    var answer = groupId.compareTo(that.groupId)
    if (answer == 0) {
      answer = artifactId.compareTo(that.artifactId)
    }
    answer
  }
}

class DownloadFuseArchetypes(outputDir: File = new File("fuse-ide-archetypes"), delete: Boolean = false) extends Runnable {
  val online = true
  //val archetypeCatalog = "http://repo.fusesource.com/nexus/content/repositories/releases/archetype-catalog.xml"

  // use direct port to avoid proxy server timing out on us :)
  //val archetypeCatalog = "http://repo.fusesource.com:8082/" + UpdateReleases.releaseRepo + "/archetype-catalog.xml"
  val archetypeCatalog = "http://repo.fusesource.com:8081/" + UpdateReleases.releaseRepo + "/archetype-catalog.xml"
  //val archetypeCatalog = "http://repo.fusesource.com:8000/" + UpdateReleases.releaseRepo + "/archetype-catalog.xml"
  //val archetypeCatalog = "http://repo.fusesource.com/" + UpdateReleases.releaseRepo + "/archetype-catalog.xml"

  var aether = new Aether()


  override def toString = "Download latest archetypes to: " + outputDir

  def run(): Unit = {
    if (!outputDir.exists()) {
      throw new IllegalArgumentException("No outputDir exists! " + outputDir)
    }
    val xml = if (online) {
      println("Loading archetype catalog from: " + archetypeCatalog)
      val url = new URL(archetypeCatalog)
      val huc = url.openConnection().asInstanceOf[HttpURLConnection]
      huc.setConnectTimeout(30 * 1000)
      huc.setReadTimeout(2 * 60 * 60 * 1000)
      huc.setRequestMethod("GET")
      huc.connect()
      val input = huc.getInputStream()
      XML.load(input)
    } else {
      val url = getClass.getClassLoader.getResource("archetype-catalog.xml")
      assert(url != null, "Could not find archetype-catalog.xml")
      XML.load(url)
    }

    val versions = HashMap[GroupAndArtifact, Set[String]]()
    val descriptions = HashMap[GroupAndArtifact, String]()

    val badVersions = Set("2.1.3.2-fuse", "1.4.1.0-fuse", "fuse-1.0.0.1", "3.3.1.9-fuse", "2.2.2-fuse-04-06")
    val archetypes = xml \\ "archetype"

    if (delete) {
      IOUtil.recursiveDelete(outputDir)
      outputDir.mkdirs()
    }

    for (a <- archetypes) {
      val gid = (a \ "groupId").text
      val aid = (a \ "artifactId").text
      val version = (a \ "version").text
      val desc = (a \ "description").text

      def validArchetype = {
        // lets ignore all old archetypes...
        if (gid.contains("camel")) {
          aid != "camel-archetype-war"
        } else {
          // lets ignore servicemix, karaf, cxf archetypes for now
          !gid.startsWith("org.apache.servicemix") && !gid.startsWith("org.apache.cxf") && !gid.startsWith("org.apache.karaf")
        }
      }

      if (gid.size > 0 && aid.size > 0 &&
              (version.contains(UpdateReleases.fuseVersion) || version.contains(UpdateReleases.fuseArchetypeVersion))) {
        if (validArchetype) {
          val ga = GroupAndArtifact(gid, aid)
          val set = versions.getOrElseUpdate(ga, new HashSet[String])
          set += version
          descriptions(ga) = desc
        } else {
          println("IGNORE: archetype " + a)
        }
      }
    }

    // now lets clear the authenticator
    println("==== clearing the authenticator so we can download archetypes")
    Authenticator.setDefault(null)

    outputDir.mkdirs
    val out = new PrintWriter(new FileWriter(new File(outputDir, "archetypes.xml")))
    out.println("<archetypes>")
    val gas = versions.keys.toList.sorted
    for (ga <- gas) {
      val version = versions(ga).max
      val desc = descriptions(ga)
      if (version.contains("fuse") && !badVersions.contains(version)) {
        downloadArchetype(ga, version, desc)
        out.println("<archetype groupId='" + ga.groupId + "' artifactId='" + ga.artifactId + "' version='" + version + "'>" + desc + "</archetype>")
      }

    }
    out.println("</archetypes>")
    out.close

    println("Running git add...")
    val pb = new ProcessBuilder("git", "add", "*")
    pb.directory(outputDir)
    pb.start()
  }

  def downloadArchetype(ga: GroupAndArtifact, version: String, desc: String) = {
    println(ga.groupId + ":" + ga.artifactId + ":" + version)

    val result = aether.resolve(ga.groupId, ga.artifactId, version)

    val dir = outputDir
    for (file <- result.resolvedFiles) {
      val newFile = new File(dir, file.getName)
      IOUtil.copy(file, newFile)
    }
  }
}