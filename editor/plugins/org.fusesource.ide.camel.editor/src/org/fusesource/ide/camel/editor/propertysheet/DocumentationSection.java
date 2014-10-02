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

package org.fusesource.ide.camel.editor.propertysheet;

import java.net.URL;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;

/**
 * Shows the documentation for the currently selected node
 */
public class DocumentationSection extends NodeSectionSupport {

	private FormToolkit toolkit;
	private Form form;

	private ModifyListener listener = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent arg0) {
			/*
			 * ButtonElementProperties properties = (ButtonElementProperties)
			 * buttonElement .getAdapter(IPropertySource.class);
			 * properties.setPropertyValue
			 * (ButtonElementProperties.PROPERTY_TEXT, labelText.getText());
			 */
		}
	};

	private Composite parent;

	private Browser browser;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.editor.propertysheet.NodeSectionSupport#
	 * onNodeChanged(org.fusesource.ide.camel.model.AbstractNode)
	 */
	@Override
	protected void onNodeChanged(AbstractNode node) {
		this.node = node;
		form.setText(node != null ? String.format("%s - %s", EditorMessages.propertiesDocumentationTitle, node.getPatternName()) : EditorMessages.propertiesDocumentationTitle);
		showDocumentationPage();
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
		if (node != null) {
			boolean loadedPage = false;
			// lets see if we can find the docs for an endpoints URI...
			if (node instanceof Endpoint) {
				Endpoint endpoint = (Endpoint) node;
				String uri = endpoint.getUri();
				if (uri != null) {
					int idx = uri.indexOf(':');
					if (idx > 0) {
						String scheme = uri.substring(0, idx);
						String contextId = "org.fusesource.ide.camel.editor."
								+ scheme;
						// Activator.getLogger().debug("Resolving context ID:" +
						// contextId);
						loadedPage = resolvePage(contextId, true);
						Activator.getLogger().debug(
								"Loaded page " + contextId + " " + loadedPage);
					}
				}
			}
			if (!loadedPage) {
				String text = node.getDocumentationFileName();
				String uri = "org.fusesource.ide.camel.editor.allEIPs";
				if (text != null) {
					uri = "org.fusesource.ide.camel.editor." + text;
				}
				// Activator.getLogger().debug("Resolving context ID:" + uri);
				resolvePage(uri, false);
			}
			// browser.layout();
		} else {
			// lets zap the old form
		}
		/*
		 * browser.layout(); browser.pack(); parent.pack();
		 */
	}

	protected boolean resolvePage(String contextId, boolean endpoint) {
		String contextName = contextId;
		IContext context = HelpSystem.getContext(contextName);
		if (context == null) {
			if (endpoint) {
				contextName = "org.fusesource.ide.camel.editor.endpoint";
			} else {
				contextName = "org.fusesource.ide.camel.editor.allEIPs";
				context = HelpSystem.getContext("");
			}
			context = HelpSystem.getContext(contextName);

			// Activator.getLogger().debug("Context ID " + contextId +
			// " is bad using default.");
		}
		if (context == null) {
			Activator.getLogger().warning(
					"Could not find context: " + contextName);
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
		Activator.getLogger().warning(
				"Could not find resource in context: " + contextName);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		/*
		 * labelText.removeModifyListener(listener); if (node != null) {
		 * labelText.setText(node.getDisplayToolTip()); }
		 * labelText.addModifyListener(listener);
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls
	 * (org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(final Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {

		this.parent = parent;
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);

		if (form != null && !form.isDisposed()) {
			try {
				form.dispose();
			} catch (Exception e) {
				// ignore any expose exceptions
			}
		}
		form = null;

		if (parent.isDisposed())
			return;

		parent.setLayout(new GridLayout());
		// parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setText(EditorMessages.propertiesDocumentationTitle);
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

		back.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.back();
			}
		});
		forward.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				browser.forward();
			}
		});

		final LocationListener locationListener = new LocationListener() {
			public void changed(LocationEvent event) {
				Browser browser = (Browser) event.widget;
				back.setEnabled(browser.isBackEnabled());
				forward.setEnabled(browser.isForwardEnabled());
			}

			public void changing(LocationEvent event) {
			}
		};

		browser = new Browser(sbody, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		browser.setLayoutData(data);
		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
				.getHelpSystem();
		URL url = helpSystem
				.resolve("org.fusesource.ide.help/index.html", true);
		browser.setUrl(url.toExternalForm());
		browser.addLocationListener(locationListener);

		// section.pack();
		// form.pack();
		form.layout(true, true);
		parent.layout(true, true);

		// in case of timing issues, lets do another layout just in case...
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (form != null && !form.isDisposed()) {
					form.layout(true, true);
				}
				if (parent != null && !parent.isDisposed()) {
					parent.layout(true, true);
				}
			}
		});
	}
}
