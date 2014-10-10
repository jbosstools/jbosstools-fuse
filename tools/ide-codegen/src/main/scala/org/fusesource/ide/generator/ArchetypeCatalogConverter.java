/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.fusesource.ide.generator;

//import xml.{Elem, Node, Text, XML}
//import java.net.URL
//import xml.transform.{RuleTransformer, RewriteRule}
//import java.lang.String

public class ArchetypeCatalogConverter {

    public static void run(String[] args) {
//        println("About to process: " + args.toList)
//
//        if (args.size > 0) {
//            val outFile = args(0)
//            val url = if (args.size > 1) args(1) else "http://repository.jboss.org/nexus/content/groups/fs-public/archetype-catalog.xml"
//
//            println("Saving XML to file: " + outFile)
//            println("Parsing Archetype Catalog: " + url)
//
//            val elem = XML.load(new URL(url))
//
//            val repo = Text("  ") ::
//                    <repository>http://repository.jboss.org/nexus/content/groups/fs-public</repository> ::
//            Text("\n    ") :: Nil
//
//            object rewrite extends RewriteRule {
//                override def transform(n: Node): Seq[Node] = n match {
//                    case e: Elem if (e.label == "archetype") =>
//                        val newChildren = e.child ++ repo
//                        e.copy(child = newChildren)
//
//                    case o => o
//                }
//            }
//            object transform extends RuleTransformer(rewrite)
//
//            val newXml = transform(elem)
//            XML.save(outFile, newXml, "UTF-8", true)
//
//            println("Wrote XML to file: " + outFile)
//        } else {
//            println("Usage: outputFile archetypeCatalogUrl")
//        }
    }

}
