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
package org.fusesource.ide.camel.editor;

import org.fusesource.ide.camel.editor.properties.FusePropertySectionTest;
import org.fusesource.ide.camel.editor.properties.ParameterPriorityComparatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
					XmlMarshalTest.class,
					FusePropertySectionTest.class,
					ParameterPriorityComparatorTest.class
			 })
public class CamelEditorTestSuite {
}
