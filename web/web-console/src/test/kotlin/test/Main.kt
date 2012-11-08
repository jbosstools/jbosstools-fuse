package org.fusesource.webide

import java.io.File
import java.util.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.*
import org.mortbay.jetty.plugin.JettyWebAppContext

/**
* Returns true if the file exists
*/
fun fileExists(path: String): Boolean {
    val file = File(path)
    return file.exists() && file.isFile()
}

/**
 * Returns true if the directory exists
 */
fun directoryExists(path: String): Boolean {
    val file = File(path)
    return file.exists() && file.isDirectory()
}

/**
 * Runs the web app
 */
fun main(args: Array<String>): Unit {
    val port = 8080
    val contextPath = "/"
    var path = "src/main/webapp"
    if (!directoryExists(path)) {
        path = "website/$path"
        require(directoryExists(path), "No webapp path could be found for $path")
    }
    val webXml = path + "/WEB-INF/web.xml"
    require(fileExists(webXml), "No web.xml could be found for $webXml")

    println("Connect via http://localhost:$port$contextPath using web app path: $path")

    /** Returns true if we should scan this lib for annotations */
    fun isScannedWebInfLib(path: String): Boolean {
        return path.endsWith("kool/website/target/classes")
        //return path.contains("kool")
        //return true
    }

    val pathSeparator = File.pathSeparator ?: ":"

    val classpath = System.getProperty("java.class.path") ?: ""
    val classpaths = classpath.split(pathSeparator)
    val jarNames: Collection<String> = classpaths.filter{ isScannedWebInfLib(it) }

    // TODO remove the File? stuff and null checks when issue fixed:
    val files = jarNames.map<String, File?>{ File(it) }.filter{ it != null && it.exists() }

    // TODO remove .toList() when filter() returns List
    val jars = files.filter{ it != null && it.isFile() }.toSet()
    val extraClassDirs = files.filter{ it != null && it.isDirectory() }.toList()

    println("Using WEB-INF/lib jars: $jars and classes dirs: $extraClassDirs")

    val context = JettyWebAppContext()
    context.setWebInfLib(jars.toList())
    context.setConfigurations(array(
         WebXmlConfiguration(),
            WebInfConfiguration()
    ))

    context.setDescriptor(webXml)
    context.setResourceBase(path)
    context.setContextPath(contextPath)
    context.setParentLoaderPriority(true)

    val server = Server(port)
    server.setHandler(context)

    server.start()
    server.join()
}
