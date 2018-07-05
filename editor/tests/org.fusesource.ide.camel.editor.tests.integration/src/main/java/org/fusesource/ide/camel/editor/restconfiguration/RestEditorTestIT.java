/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;
import org.fusesource.ide.camel.test.util.editor.AbstractCamelEditorIT;
import org.junit.Test;

/**
 * @author brianf
 *
 * Load a Camel config with some Rest DSL
 * See if the Rest tab is there
 * Switch to the Rest tab <-- stopped here, but the rest is next
 * See if the first rest element is there (getSelection should by default return it)
 * Select a second rest element
 * Switch to Source tab
 * Switch back to Rest tab
 * See if the second rest element is still selected
 * repeat with REST operations
 * 
 */
public class RestEditorTestIT extends AbstractCamelEditorIT {

	public RestEditorTestIT() {
		this.routeContainerType = "camelContext";
	}
	
	private void flipTabsToRefreshModel(CamelEditor camelEditor) {
		// switch to the source tab and back again, which refreshes the REST model
		camelEditor.setActiveEditor(camelEditor.getSourceEditor());
		readAndDispatch(20);
		camelEditor.setActiveEditor(camelEditor.getRestEditor());
		readAndDispatch(20);
	}
	
	@Test
	public void openCamelFileMoveToRESTPage() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/rest");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);

		readAndDispatch(20);

		// ensure that the REST tab is available and select it
		assertThat(((CamelEditor)openEditorOnFileStore).getRestEditor()).isNotNull();
		CamelEditor camelEditor = (CamelEditor)openEditorOnFileStore;
		camelEditor.setActiveEditor(camelEditor.getRestEditor());
		assertThat(camelEditor.getActivePage()).isEqualTo(CamelEditor.REST_CONF_INDEX);
		RestConfigEditor restEditor = camelEditor.getRestEditor();
		camelEditor.setActiveEditor(restEditor);

		readAndDispatch(20);

		// grab the selection from the REST tab, which should be a REST Element
		StructuredSelection ssel = (StructuredSelection) restEditor.getSelection();
		assertThat(ssel.isEmpty()).isFalse(); 
		assertThat(ssel.getFirstElement()).isInstanceOf(RestElement.class);
		
		// stash the selection
		RestElement initialSelection = (RestElement) ssel.getFirstElement();
		
		// switch to the source tab and back again, which refreshes the REST model
		flipTabsToRefreshModel(camelEditor);

		// grab the selection again
		StructuredSelection secondSsel = (StructuredSelection) restEditor.getSelection();

		// Now make sure we have re-selected the rest element properly
		RestElement secondSelection = (RestElement) secondSsel.getFirstElement();
		assertThat(secondSelection.getId()).isEqualTo(initialSelection.getId());
		
		// now try with operations
		restEditor.selectRestVerbElement((RestVerbElement) secondSelection.getRestOperations().values().toArray()[0]);
		StructuredSelection opSsel1 = (StructuredSelection) restEditor.getSelection();
		assertThat(opSsel1.isEmpty()).isFalse();
		assertThat(opSsel1.getFirstElement()).isInstanceOf(RestVerbElement.class);
		
		// stash the first operation
		RestVerbElement initialRVE = (RestVerbElement) opSsel1.getFirstElement();

		// switch to the source tab and back again, which refreshes the REST model
		flipTabsToRefreshModel(camelEditor);

		// now make sure we have re-selected the operation properly
		StructuredSelection opSsel2 = (StructuredSelection) restEditor.getSelection();
		assertThat(opSsel1.getFirstElement() instanceof RestVerbElement).isTrue();
		RestVerbElement secondRVE = (RestVerbElement) opSsel2.getFirstElement();
		assertThat(initialRVE.getId()).isEqualTo(secondRVE.getId());
		
		// next up, try to select the second rest element in the list 
		// (/say (rest1) comes after /repeat (rest2))
		RestElement secondRestElement = (RestElement) restEditor.getCtx().getRestElements().get("rest1");
		restEditor.selectRestElement(secondRestElement);

		// switch to the source tab and back again, which refreshes the REST model
		flipTabsToRefreshModel(camelEditor);

		// grab the selection again
		StructuredSelection thirdSsel = (StructuredSelection) restEditor.getSelection();

		// Now make sure we have re-selected the second rest element properly
		RestElement thirdSelection = (RestElement) thirdSsel.getFirstElement();
		assertThat(thirdSelection.getId()).isEqualTo(secondRestElement.getId());
	}

	@Test
	public void openCamelFileAddRestConfigurationTest() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);

		readAndDispatch(20);

		// ensure that the REST tab is available and select it
		assertThat(((CamelEditor)openEditorOnFileStore).getRestEditor()).isNotNull();
		CamelEditor camelEditor = (CamelEditor)openEditorOnFileStore;
		camelEditor.setActiveEditor(camelEditor.getRestEditor());
		assertThat(camelEditor.getActivePage()).isEqualTo(CamelEditor.REST_CONF_INDEX);
		RestConfigEditor restEditor = camelEditor.getRestEditor();
		camelEditor.setActiveEditor(restEditor);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		CamelContextElement context = (CamelContextElement)model.getRouteContainer();

		// add a REST Configuration element
		restEditor.addRestConfigurationElement();
		readAndDispatch(20);
		
		// test for new component
		assertThat(context.getRestConfigurations().isEmpty()).isNotEqualTo(true);
		RestConfigurationElement rce = 
				(RestConfigurationElement) context.getRestConfigurations().values().iterator().next();
		assertThat(rce.getHost()).isEqualTo("localhost");
		assertThat(rce.getBindingMode()).isEqualTo("off");
	}

	@Test
	public void openCamelFileAddRestElementTest() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/route");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);

		readAndDispatch(20);

		// ensure that the REST tab is available and select it
		assertThat(((CamelEditor)openEditorOnFileStore).getRestEditor()).isNotNull();
		CamelEditor camelEditor = (CamelEditor)openEditorOnFileStore;
		camelEditor.setActiveEditor(camelEditor.getRestEditor());
		assertThat(camelEditor.getActivePage()).isEqualTo(CamelEditor.REST_CONF_INDEX);
		RestConfigEditor restEditor = camelEditor.getRestEditor();
		camelEditor.setActiveEditor(restEditor);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		CamelContextElement context = (CamelContextElement)model.getRouteContainer();

		// add a REST Configuration element
		restEditor.addRestConfigurationElement();
		readAndDispatch(20);
		
		// add a REST Element
		restEditor.addRestElement();
		readAndDispatch(20);

		// test for new component
		assertThat(context.getRestElements().isEmpty()).isNotEqualTo(true);
		RestElement re = 
				(RestElement) context.getRestElements().values().iterator().next();
		assertThat(re.getId()).isNotEmpty();
	}

	@Test
	public void openCamelFileRemoveRestConfigurationTest() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/rest");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);

		readAndDispatch(20);

		// ensure that the REST tab is available and select it
		assertThat(((CamelEditor)openEditorOnFileStore).getRestEditor()).isNotNull();
		CamelEditor camelEditor = (CamelEditor)openEditorOnFileStore;
		camelEditor.setActiveEditor(camelEditor.getRestEditor());
		assertThat(camelEditor.getActivePage()).isEqualTo(CamelEditor.REST_CONF_INDEX);
		RestConfigEditor restEditor = camelEditor.getRestEditor();
		camelEditor.setActiveEditor(restEditor);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		CamelContextElement context = (CamelContextElement)model.getRouteContainer();

		//test to make sure it's removed
		restEditor.removeRestConfigurationElement();
		readAndDispatch(20);
		assertThat(context.getRestConfigurations().isEmpty()).isNotEqualTo(false);
		assertThat(context.getRestElements().isEmpty()).isNotEqualTo(false);
	}

	@Test
	public void openCamelFileRemoveRestElementTest() throws Exception {
		IEditorPart openEditorOnFileStore = openFileInEditor("/rest");
		assertThat(openEditorOnFileStore).isNotNull();
		assertThat(openEditorOnFileStore).isInstanceOf(CamelEditor.class);

		readAndDispatch(20);

		// ensure that the REST tab is available and select it
		assertThat(((CamelEditor)openEditorOnFileStore).getRestEditor()).isNotNull();
		CamelEditor camelEditor = (CamelEditor)openEditorOnFileStore;
		camelEditor.setActiveEditor(camelEditor.getRestEditor());
		assertThat(camelEditor.getActivePage()).isEqualTo(CamelEditor.REST_CONF_INDEX);
		RestConfigEditor restEditor = camelEditor.getRestEditor();
		camelEditor.setActiveEditor(restEditor);
		
		CamelDesignEditor ed = ((CamelEditor)openEditorOnFileStore).getDesignEditor();
		CamelFile model = ed.getModel();
		CamelContextElement context = (CamelContextElement)model.getRouteContainer();

		//test to make sure it's removed (there are two REST elements in the sample)
		restEditor.removeRestElement();
		readAndDispatch(20);

		restEditor.removeRestElement();
		readAndDispatch(20);
		
		assertThat(context.getRestElements().isEmpty()).isNotEqualTo(false);
	}
}
