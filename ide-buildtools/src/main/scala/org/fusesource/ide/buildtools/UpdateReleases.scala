package org.fusesource.ide.buildtools

import java.net.{PasswordAuthentication, Authenticator}
import java.util.Properties
import java.io.{FileInputStream, File}
import org.sonatype.aether.repository.Authentication
import org.fusesource.insight.maven.aether.Authentications

/**
 * Downloads the latest XSDs and archetypes into the IDE build
 */
object UpdateReleases {

  val fuseArchetypeVersion = System.getProperty("fabric-version", "7.1.0-fuse-015")
  val fuseVersion = "fuse-71-" + fuseArchetypeVersion.split("-").last

  val releaseRepo = if (true)
    "nexus/content/groups/m2-release-proxy"
  else
    "nexus/content/repositories/releases"

  val fuseAuthenticator = new FuseRepoAuthenticator()


  def main(args: Array[String]): Unit = {
    println("Using fuseVersion " + fuseVersion + " and fuseArchetypeVersion " + fuseArchetypeVersion)
    println("Adding new authenticator!!!")
    Authenticator.setDefault(fuseAuthenticator)

    // lets find the eclipse plugins directory
    var rs = new File("../plugins")
    if (args.length > 1) {
      rs = new File(args(0))
    } else if (!rs.exists()) {
      rs = new File("../../plugins")
    }

    println("Using IDE directory: " + rs.getAbsolutePath)
    if (!rs.exists()) {
      fail("IDE directory does not exist!")
    }
    if (!rs.isDirectory()) {
      fail("IDE directory is a file, not a directory!")
    }

    val archetypesDir = new File(rs, "org.fusesource.ide.branding/archetypes")
    val xsdsDir = new File(rs, "org.fusesource.ide.catalogs")

    val tasks = List(
      new DownloadLatestXsds(xsdsDir, true),
      new DownloadFuseArchetypes(archetypesDir, true)
    )

    for (task <- tasks) {
      println("Starting: " + task)
      task.run()
    }
    println("Done!")
  }

  protected def fail(message: String): Unit = {
    println(message)
    System.exit(1)
  }
}
class FuseRepoAuthenticator extends Authenticator {
  override def getPasswordAuthentication() : PasswordAuthentication = {
    val authentication = Authentications.getFuseRepoAuthentication()
    val username = authentication.getUsername
    val password = authentication.getPassword()
    return new PasswordAuthentication(username, password.toCharArray());
  }
}

