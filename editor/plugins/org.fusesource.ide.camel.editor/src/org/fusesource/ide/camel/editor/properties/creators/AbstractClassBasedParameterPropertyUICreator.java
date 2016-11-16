/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties.creators;

import java.net.URLClassLoader;
import java.util.Arrays;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class AbstractClassBasedParameterPropertyUICreator extends AbstractTextFieldParameterPropertyUICreator {

	public AbstractClassBasedParameterPropertyUICreator(DataBindingContext dbc, IObservableMap modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ModifyListener modifyListener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, modifyListener);
	}

	protected void init(Composite parent) {
		super.init(parent);
		final IProject project = camelModelElement.getCamelFile().getResource().getProject();
		URLClassLoader child = CamelComponentUtils.getProjectClassLoader(project);
		Class<?> classToLoad = computeClassToLoad(child);

		createCreateButton(parent, project, classToLoad);
		createBrowseButton(parent, project, classToLoad);
	}

	/**
	 * @param child
	 * @return
	 */
	private Class<?> computeClassToLoad(URLClassLoader child) {
		Class<?> classToLoad;
		try {
			if (parameter.getJavaType().indexOf("<") != -1) {
				classToLoad = child.loadClass(parameter.getJavaType().substring(0, parameter.getJavaType().indexOf("<")));
			} else {
				classToLoad = child.loadClass(parameter.getJavaType());
			}
		} catch (ClassNotFoundException ex) {
			CamelEditorUIActivator.pluginLog().logWarning("Cannot find class " + parameter.getJavaType() + " on classpath.", ex);
			classToLoad = null;
		}
		return classToLoad;
	}

	/**
	 * @param parent
	 * @param project
	 * @param fClass
	 */
	private void createBrowseButton(Composite parent, final IProject project, final Class<?> fClass) {
		Button btn_browse = getWidgetFactory().createButton(parent, "...", SWT.FLAT | SWT.PUSH);
		btn_browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
					IJavaElement[] elements = new IJavaElement[] { javaProject };
					IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

					FilteredTypesSelectionDialog dlg = new FilteredTypesSelectionDialog(Display.getDefault().getActiveShell(), false,
							PlatformUI.getWorkbench().getProgressService(), scope, IJavaSearchConstants.CLASS);

					if (Window.OK == dlg.open()) {
						Object o = dlg.getFirstResult();
						if (o instanceof SourceType) {
							getControl().setText(((SourceType) o).getFullyQualifiedName());
						} else if (o instanceof BinaryType) {
							getControl().setText(((BinaryType) o).getFullyQualifiedName());
						}
					}
				} catch (Exception ex) {
					CamelEditorUIActivator.pluginLog().logError(ex);
				}
			}
		});
		btn_browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btn_browse.setEnabled(fClass != null);
	}

	/**
	 * @param parent
	 * @param project
	 * @param fClass
	 */
	private void createCreateButton(Composite parent, final IProject project, final Class<?> fClass) {
		Button btn_create = getWidgetFactory().createButton(parent, " + ", SWT.FLAT | SWT.PUSH);
		btn_create.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewClassCreationWizard wiz = new NewClassCreationWizard();
				wiz.addPages();
				wiz.init(PlatformUI.getWorkbench(), null);
				NewClassWizardPage wp = (NewClassWizardPage) wiz.getStartingPage();
				WizardDialog wd = new WizardDialog(e.display.getActiveShell(), wiz);
				if (fClass.isInterface()) {
					wp.setSuperInterfaces(Arrays.asList(fClass.getName()), true);
				} else {
					wp.setSuperClass(fClass.getName(), true);
				}
				wp.setAddComments(true, true);
				setInitialPackageFrament(project, wp);
				if (Window.OK == wd.open()) {
					String value = wp.getCreatedType().getFullyQualifiedName();
					if (value != null)
						getControl().setText(value);
				}
			}

			private void setInitialPackageFrament(final IProject project, NewClassWizardPage wp) {
				try {
					IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
					if(javaProject != null){
						IPackageFragmentRoot fragroot = findPackageFragmentRoot(project, javaProject);
						wp.setPackageFragmentRoot(fragroot, true);
						wp.setPackageFragment(PropertiesUtils.getPackage(javaProject, fragroot), true);
					}
				} catch (Exception ex) {
					CamelEditorUIActivator.pluginLog().logError(ex);
				}
			}

			private IPackageFragmentRoot findPackageFragmentRoot(final IProject project, IJavaProject javaProject) throws JavaModelException {
				IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, new NullProgressMonitor());
				if(facade != null){
					IPath[] paths = facade.getCompileSourceLocations();
					if (paths != null && paths.length > 0) {
						for (IPath p : paths) {
							if (p == null)
								continue;
							IResource res = project.findMember(p);
							return javaProject.getPackageFragmentRoot(res);
						}
					}
				} else {
					IPackageFragmentRoot[] allPackageFragmentRoots = javaProject.getAllPackageFragmentRoots();
					if(allPackageFragmentRoots.length == 1){
						return allPackageFragmentRoots[0];
					}
				}
				return null;
			}
		});
		btn_create.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btn_create.setEnabled(fClass != null);
	}

	@Override
	protected GridData createPropertyFieldLayoutData() {
		return GridDataFactory.fillDefaults().indent(5, 0).grab(true, false).create();
	}

}
