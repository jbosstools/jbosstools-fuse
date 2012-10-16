package org.fusesource.ide.generator

import xml.{Elem, Node, Text, XML}
import java.net.URL
import xml.transform.{RuleTransformer, RewriteRule}
import java.lang.String

object ArchetypeCatalogConverter {
  def run(args: Array[String]): Unit = {
    println("About to process: " + args.toList)

    if (args.size > 0) {
      val outFile = args(0)
      val url = if (args.size > 1) args(1) else "http://repo.fusesource.com/nexus/content/groups/public/archetype-catalog.xml"

      println("Saving XML to file: " + outFile)
      println("Parsing Archetype Catalog: " + url)

      val elem = XML.load(new URL(url))

      val repo = Text("  ") ::
              <repository>http://repo.fusesource.com/nexus/content/groups/public</repository> ::
              Text("\n    ") :: Nil

      object rewrite extends RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
          case e: Elem if (e.label == "archetype") =>
            val newChildren = e.child ++ repo
            e.copy(child = newChildren)

          case o => o
        }
      }
      object transform extends RuleTransformer(rewrite)

      val newXml = transform(elem)
      XML.save(outFile, newXml, "UTF-8", true)

      println("Wrote XML to file: " + outFile)
    } else {
      println("Usage: outputFile archetypeCatalogUrl")
    }
  }
}