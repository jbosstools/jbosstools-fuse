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
