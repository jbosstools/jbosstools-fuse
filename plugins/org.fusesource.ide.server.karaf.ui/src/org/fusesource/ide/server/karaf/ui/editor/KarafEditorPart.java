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

package org.fusesource.ide.server.karaf.ui.editor;

import javax.management.MBeanServerConnection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;
import org.eclipse.wst.server.ui.internal.ImageResource;

public class KarafEditorPart extends ServerEditorPart {

	public KarafEditorPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
		ManagedForm managedForm = new ManagedForm(parent);
		setManagedForm(managedForm);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		toolkit.decorateFormHeading(form.getForm());
		form.setText("Karaf Server");
		form.setImage(ImageResource.getImage(ImageResource.IMG_SERVER));
		form.getBody().setLayout(new GridLayout());

		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessVerticalSpace = true;

		Composite comp = toolkit.createComposite(form.getBody());
		comp.setLayout(new GridLayout());
		comp.setLayoutData(layoutData);

		Section section = toolkit.createSection(comp,
				ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("OSGi Bundles");
		section.setDescription("List of installed OSGi bundles");
		section.setLayoutData(layoutData);

		Composite c1 = toolkit.createComposite(section);
		c1.setLayout(new GridLayout(2, false));
		c1.setLayoutData(layoutData);

		List listOSGiModules = new List(c1, SWT.SINGLE | SWT.BORDER);
		GridData listData = new GridData();
		listData.verticalAlignment = SWT.FILL;
		listData.widthHint = 100;
		listData.grabExcessHorizontalSpace = false;
		listData.horizontalAlignment = SWT.LEFT;
		listData.grabExcessVerticalSpace = true;
		listOSGiModules.setLayoutData(listData);
		
		section.setClient(c1);
	}

	@Override
	public void setFocus() {

	}

	private MBeanServerConnection getKarafConnection() {
//		IServerWorkingCopy copy = getServer();
//		IFuseESBJMXProvider bdel = (IFuseESBJMXProvider) copy.loadAdapter( IFuseESBJMXProvider.class, null);
//		if (bdel != null)
//			return bdel.getMBeanServerConnection();
//
		return null;
	}

	/*private String[] getServiceAssemblies() {
		MBeanServerConnection FuseESBServerConnection = getFuseESBConnection();
		java.util.List<String> serviceAssemblies = new LinkedList<String>();
		try {
			if (FuseESBServerConnection != null
					&& isFuseESBDomainPresent(FuseESBServerConnection)) {
				ObjectName name = new ObjectName(
						IFuseESBJMXProvider.ORG_APACHE_SERVICEMIX + ":*");
				Set<?> beans = FuseESBServerConnection.queryMBeans(name,
						null);
				for (Object obj : beans) {
					if (obj instanceof ObjectInstance) {
						ObjectInstance oi = (ObjectInstance) obj;
						ObjectName objectName = oi.getObjectName();
						if ("ServiceAssembly".equals(objectName .getKeyProperty("Type"))) { 
							serviceAssemblies.add(objectName .getKeyProperty("Name"));
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return serviceAssemblies.toArray(new String[serviceAssemblies.size()]);
	}

	private boolean isFuseESBDomainPresent(MBeanServerConnection conn) {
		if (conn != null) {
			try {
				String[] domains = conn.getDomains();
				if (domains != null) {
					for (String domain : domains) {
						if (domain.equals(IFuseESBJMXProvider.ORG_APACHE_SERVICEMIX)) {
							return true;
						}
					}
				}
			} catch (IOException o) {
			}
		}
		return false;

	}*/

}
