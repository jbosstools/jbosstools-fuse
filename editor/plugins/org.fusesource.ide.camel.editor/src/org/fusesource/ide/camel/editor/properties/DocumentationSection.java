/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import java.net.URL;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Widgets;
import org.w3c.dom.Element;

/**
 * Shows the documentation for the currently selected node
 */
public class DocumentationSection extends NodeSectionSupport {

	private static final String HELP_CONTEXT_ID_PREFIX = "org.fusesource.ide.camel.editor.";
	private static final String ALL_EIPS_INFO = "allEIPs";
	private static final String ENDPOINT_GENERAL_INFO = "endpoint";
	private static final String REST_INFO_PAGE = "rest-tab";
	private static final String GLOBAL_BEAN_PAGE = "beanEIP";
	private static final String GLOBAL_CONFIG_PAGE = "global-config";
	private static final String GLOBAL_DATAFORMAT_SUFFIX = "-dataformat";
	private static final String BEAN_TAG = "bean";
	
	private Form form;
	private Browser browser;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.fusesource.ide.camel.editor.propertysheet.NodeSectionSupport#
	 * onNodeChanged(org.fusesource.ide.camel.model.AbstractNode)
	 */
	@Override
	protected void onNodeChanged(AbstractCamelModelElement node) {
		this.node = node;
		if (!form.isDisposed()) {
			form.setText(node != null ? String.format("%s - %s", UIMessages.propertiesDocumentationTitle, node.getNodeTypeId()) : UIMessages.propertiesDocumentationTitle);
			showDocumentationPage();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#aboutToBeShown
	 * ()
	 */
	@Override
	public void aboutToBeShown() {
		// the page might have changed as we could have edited the URI
		showDocumentationPage();
		super.aboutToBeShown();
	}

	protected void showDocumentationPage() {
		if (node == null) {
			return;
		}
		
		boolean loadedPage = false;
		String contextId = null;
		
		if (isRestEditorTabSelected()) {
			contextId = determineDocumentationPageForRestTab();
		} else if (isGlobalConfigTabSelected()) {
			contextId = determineDocumentationPageForGlobalConfigurationTab();
		} else {
			// lets see if we can find the docs for an endpoints URI...
			if (node.isEndpointElement()) {
				contextId = getEndpointDocumentationPage();
			}
		}
		
		if (contextId != null) {
			loadedPage = resolvePage(contextId, true);
			if (CamelEditorUIActivator.getDefault().isDebugging()) {
				CamelEditorUIActivator.pluginLog().logInfo("Loaded page " + contextId + " " + loadedPage);
			}
		}

		if (!loadedPage) {
			String text = node.getDocumentationFileName();
			String uri = HELP_CONTEXT_ID_PREFIX + ALL_EIPS_INFO;
			if (text != null) {
				uri = HELP_CONTEXT_ID_PREFIX + text;
			}
			resolvePage(uri, false);
		}
	}
	
	private String getEndpointDocumentationPage() {
		String scheme = getUriScheme(node);
		return HELP_CONTEXT_ID_PREFIX + scheme;
	}
	
	private String determineDocumentationPageForGlobalConfigurationTab() {
		// default to general topic for Configuration page if we don't have a tag or endpoint
		String contextId = HELP_CONTEXT_ID_PREFIX + GLOBAL_CONFIG_PAGE;
		// lets see if we can find the docs for an endpoints URI...
		if (node.isEndpointElement()) {
			contextId = getEndpointDocumentationPage();
		} else if (node.getXmlNode() != null) {
			Element element = (Element) node.getXmlNode();
			String tagName = element.getTagName();
			// if it's a bean, go with that context ID
			if (BEAN_TAG.equals(tagName)) {
				contextId = HELP_CONTEXT_ID_PREFIX + GLOBAL_BEAN_PAGE;
			// if it's a data format, go with that. 
			} else if (!Strings.isEmpty(tagName)) {
				contextId = HELP_CONTEXT_ID_PREFIX + tagName + GLOBAL_DATAFORMAT_SUFFIX;
			}
		}
		return contextId;
	}

	private String determineDocumentationPageForRestTab() {
		return HELP_CONTEXT_ID_PREFIX + REST_INFO_PAGE;
	}
	
	private boolean isRestEditorTabSelected() {
		CamelEditor editor = CamelUtils.getDiagramEditor().getParent(); 
		return editor != null && editor.getActiveEditor().equals(editor.getRestEditor());
	}
	
	private boolean isGlobalConfigTabSelected() {
		CamelEditor editor = CamelUtils.getDiagramEditor().getParent(); 
		return editor != null && editor.getActiveEditor().equals(editor.getGlobalConfigEditor());
	}

	private String getUriScheme(AbstractCamelModelElement node) {
		String uri = (String)node.getParameter("uri");
		if (!Strings.isBlank(uri)) {
			int idx = uri.indexOf(':');
			if (idx > 0) {
				return uri.substring(0, idx);
			}
		}
		return ENDPOINT_GENERAL_INFO;
	}
	
	protected boolean resolvePage(String contextId, boolean endpoint) {
		String contextName = contextId;
		IContext context = HelpSystem.getContext(contextName);
		if (context == null) {
			if (endpoint) {
				contextName = HELP_CONTEXT_ID_PREFIX + ENDPOINT_GENERAL_INFO;
			} else {
				contextName = HELP_CONTEXT_ID_PREFIX + ALL_EIPS_INFO;
			}
			context = HelpSystem.getContext(contextName);
		}
		if (context == null) {
			CamelEditorUIActivator.pluginLog().logWarning("Could not find context: " + contextName);
			return false;
		}
		IHelpResource[] relatedTopics = context.getRelatedTopics();
		if (relatedTopics != null && relatedTopics.length > 0) {
			IHelpResource resource = relatedTopics[0];
			if (resource != null) {
				String helpUrl = resource.getHref();
				IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
						.getHelpSystem();
				URL url = helpSystem.resolve(helpUrl, true);
				return browser.setUrl(url.toExternalForm());
			}
		}
		CamelEditorUIActivator.pluginLog().logWarning("Could not find resource in context: " + contextName);
		return false;
	}

	@Override
	public void createControls(final Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);

		if (!Widgets.isDisposed(form)) {
			try {
				form.dispose();
			} catch (Exception e) {
				// ignore any expose exceptions
			}
		}
		form = null;

		if (parent.isDisposed()) {
			return;
		}

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setText(UIMessages.propertiesDocumentationTitle);
		toolkit.decorateFormHeading(form);

		form.getBody().setLayout(new GridLayout(1, false));

		Composite sbody = form.getBody();

		ToolBar navBar = new ToolBar(sbody, SWT.NONE);
		toolkit.adapt(navBar);
		navBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_BEGINNING));
		final ToolItem back = new ToolItem(navBar, SWT.PUSH);
		back.setText("<");
		back.setEnabled(false);
		final ToolItem forward = new ToolItem(navBar, SWT.PUSH);
		forward.setText(">");
		forward.setEnabled(false);

		back.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.back();
			}
		});
		forward.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				browser.forward();
			}
		});

		final LocationListener locationListener = new LocationListener() {
			@Override
			public void changed(LocationEvent event) {
				Browser b = (Browser) event.widget;
				back.setEnabled(b.isBackEnabled());
				forward.setEnabled(b.isForwardEnabled());
			}

			@Override
			public void changing(LocationEvent event) {
				// not needed
			}
		};

		browser = new Browser(sbody, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		browser.setLayoutData(data);
		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		URL url = helpSystem.resolve("org.fusesource.ide.help/index.html", true);
		if(url != null){
			browser.setUrl(url.toExternalForm());
			browser.addLocationListener(locationListener);
		}

		form.layout(true, true);
		parent.layout(true, true);

		// in case of timing issues, lets do another layout just in case...
		Display.getCurrent().asyncExec( () -> {
			if (form != null && !form.isDisposed()) {
				form.layout(true, true);
			}
			if (parent != null && !parent.isDisposed()) {
				parent.layout(true, true);
			}
		});
	}
}
