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

package org.fusesource.ide.branding.wizards.project;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.branding.RiderHelpContextIds;
import org.fusesource.ide.branding.wizards.WizardMessages;
import org.fusesource.ide.commons.camel.tools.Archetype;
import org.fusesource.ide.commons.camel.tools.Archetypes;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.osgi.framework.Bundle;

/**
 * Maven Archetype selection wizard page presents the user with a list of
 * available Maven Archetypes available for creating new project.
 */
public class FuseProjectWizardArchetypePage extends AbstractFuseWizardPage {

	ComboViewer catalogsComboViewer;

	Text filterText;

	/** the archetype table viewer */
	TableViewer viewer;

	/** the description value label */
	Text descriptionText;

	/** the list of available archetypes */
	volatile Collection<ArchetypeDetails> archetypes;

	public static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT"; //$NON-NLS-1$

	public static final String DEFAULT_PACKAGE = "foo"; //$NON-NLS-1$

	Table propertiesTable;

	TableViewer propertiesViewer;

	final public static String KEY_PROPERTY = "key"; //$NON-NLS-1$

	final public static int KEY_INDEX = 0;

	final public static String VALUE_PROPERTY = "value"; //$NON-NLS-1$

	final public static int VALUE_INDEX = 1;

	/** group id text field */
	protected Combo groupIdCombo;

	/** artifact id text field */
	protected Combo artifactIdCombo;

	/** version text field */
	protected Combo versionCombo;

	/** package text field */
	protected Combo packageCombo;

	protected Button removeButton;

	protected Set<String> requiredProperties;

	protected Set<String> optionalProperties;

	protected ArchetypeDetails archetype;

	protected boolean archetypeChanged = false;

	/** shows if the package has been customized by the user */
	protected boolean packageCustomized = false;

	private boolean defaultFocusOnFilter = true;
	private boolean userChangedArtifactId = false;

	/**
	 * Default constructor. Sets the title and description of this wizard page
	 * and marks it as not being complete as user input is required for
	 * continuing.
	 */
	public FuseProjectWizardArchetypePage() {
		super("MavenProjectWizardArchetypePage"); //$NON-NLS-1$
		setTitle(WizardMessages.wizardProjectPageArchetypeTitle);
		setDescription(WizardMessages.wizardProjectPageArchetypeDescription);
		setPageComplete(false);
	}

	/** Creates the page controls. */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));

		// Set up context sensitive help
		PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(composite, RiderHelpContextIds.NEW_PROJECT_ARCHETYPE_WIZARD_PAGE);

		createViewer(composite);

		setControl(composite);
	}

	/** Creates the archetype table viewer. */
	private void createViewer(Composite parent) {
		Label filterLabel = new Label(parent, SWT.NONE);
		filterLabel.setLayoutData(new GridData());
		filterLabel
		.setText(WizardMessages.MavenProjectWizardArchetypePage_lblFilter);

		QuickViewerFilter quickViewerFilter = new QuickViewerFilter();

		filterText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		filterText
		.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		filterText.addModifyListener(quickViewerFilter);
		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					viewer.getTable().setFocus();
					viewer.getTable().setSelection(0);

					viewer.setSelection(
							new StructuredSelection(viewer.getElementAt(0)),
							true);
				}
			}
		});

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		final ToolItem clearToolItem = new ToolItem(toolBar, SWT.PUSH);
		clearToolItem.setEnabled(false);

		clearToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filterText.setText(""); //$NON-NLS-1$
			}
		});

		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				clearToolItem.setEnabled(filterText.getText().length() > 0);
			}
		});

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, false, true, 3,
				1);
		// gd_sashForm.widthHint = 500;
		gd_sashForm.heightHint = 200;
		sashForm.setLayoutData(gd_sashForm);
		sashForm.setLayout(new GridLayout());

		Composite composite1 = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.horizontalSpacing = 0;
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		composite1.setLayout(gridLayout1);

		viewer = new TableViewer(composite1, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setData("name", "archetypesTable"); //$NON-NLS-1$ //$NON-NLS-2$
		table.setHeaderVisible(true);

		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setWidth(200);
		column1.setText(WizardMessages.wizardProjectPageArchetypeColumnGroupId);


		TableColumn column0 = new TableColumn(table, SWT.LEFT);
		column0.setWidth(200);
		column0.setText(WizardMessages.wizardProjectPageArchetypeColumnArtifactId);

		TableColumn column2 = new TableColumn(table, SWT.LEFT);
		column2.setWidth(150);
		column2.setText(WizardMessages.wizardProjectPageArchetypeColumnVersion);


		GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableData.widthHint = 600;
		tableData.heightHint = 200;
		table.setLayoutData(tableData);

		viewer.setLabelProvider(new ArchetypeLabelProvider());

		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((ArchetypeDetails) e1).compareTo((ArchetypeDetails) e2);
			}
		});

		viewer.addFilter(quickViewerFilter);
		/*
		 * viewer.addFilter(versionFilter); viewer.addFilter(snapshotsFilter);
		 */

		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Collection) {
					return ((Collection<?>) inputElement).toArray();
				}
				return new Object[0];
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});

		viewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent openevent) {
				if (canFlipToNextPage()) {
					getContainer().showPage(getNextPage());
				}
			}
		});

		List<ArchetypeDetails> list = getArchetypes();
		viewer.setInput(list);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (descriptionText == null) {
					return;
				}
				archetype = (ArchetypeDetails)Selections.getFirstSelection(event.getSelection());
				if (archetype != null) {
					String repositoryUrl = archetype.getRepository();
					String description = archetype.getDescription();

					String text = description == null ? "" : description; //$NON-NLS-1$
					text = text.replaceAll("\n", "").replaceAll("\\s{2,}", " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

					if (repositoryUrl != null) {
						text += text.length() > 0 ? "\n" + repositoryUrl : repositoryUrl; //$NON-NLS-1$
					}

					descriptionText.setText(text);

					if (artifactIdCombo != null && (Strings.isBlank(getArtifactId()) || !userChangedArtifactId)) {
						String value = archetype.getArtifactId().replace("archetype-", "").replace("-archetype", "");
						artifactIdCombo.setText(value);
						userChangedArtifactId = false;
					}
				} else {
					descriptionText.setText(""); //$NON-NLS-1$
				}
				validate();
			}
		});
		
		Composite composite2 = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.marginHeight = 0;
		gridLayout2.marginWidth = 0;
		gridLayout2.horizontalSpacing = 0;
		composite2.setLayout(gridLayout2);

		descriptionText = new Text(composite2, SWT.WRAP | SWT.V_SCROLL
				| SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.NO_FOCUS);

		descriptionText.setEnabled(false);
		descriptionText.setEditable(false);


		GridData descriptionTextData = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		descriptionTextData.heightHint = 40;
		descriptionText.setLayoutData(descriptionTextData);
		// whole dialog resizes badly without the width hint to the desc text
		descriptionTextData.widthHint = 250;
		sashForm.setWeights(new int[] { 80, 20 });

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(3, false));
		createArtifactGroup(composite);

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridData gd_buttonComposite = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1);
		buttonComposite.setLayoutData(gd_buttonComposite);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 3;
		buttonComposite.setLayout(gridLayout);

		if (Strings.isBlank(getGroupId())) {
			// lets default to the last one
			int itemCount = groupIdCombo.getItemCount();
			String groupId = "com.mycompany";
			if (itemCount > 0) {
				groupId = groupIdCombo.getItem(0);
			}
			groupIdCombo.setText(groupId);
		}
	}

	private void createArtifactGroup(Composite parent) {
		Label groupIdlabel = new Label(parent, SWT.NONE);
		groupIdlabel.setText(WizardMessages.artifactComponentGroupId);

		groupIdCombo = new Combo(parent, SWT.BORDER);
		groupIdCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		addFieldWithHistory("groupId", groupIdCombo); //$NON-NLS-1$
		groupIdCombo.setData("name", "groupId"); //$NON-NLS-1$ //$NON-NLS-2$


		groupIdCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateJavaPackage();
				validate();
			}
		});

		Label artifactIdLabel = new Label(parent, SWT.NONE);
		artifactIdLabel.setText(WizardMessages.artifactComponentArtifactId);

		artifactIdCombo = new Combo(parent, SWT.BORDER);
		artifactIdCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		addFieldWithHistory("artifactId", artifactIdCombo); //$NON-NLS-1$
		artifactIdCombo.setData("name", "artifactId"); //$NON-NLS-1$ //$NON-NLS-2$
		artifactIdCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateJavaPackage();
				validate();
				userChangedArtifactId = true;
			}
		});

		Label versionLabel = new Label(parent, SWT.NONE);
		versionLabel.setText(WizardMessages.artifactComponentVersion);

		versionCombo = new Combo(parent, SWT.BORDER);
		GridData gd_versionCombo = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1);
		gd_versionCombo.widthHint = 150;
		versionCombo.setLayoutData(gd_versionCombo);
		versionCombo.setText(DEFAULT_VERSION);
		addFieldWithHistory("version", versionCombo); //$NON-NLS-1$
		versionCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		Label packageLabel = new Label(parent, SWT.NONE);
		packageLabel.setText(WizardMessages.artifactComponentPackage);

		packageCombo = new Combo(parent, SWT.BORDER);
		packageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		packageCombo.setData("name", "package"); //$NON-NLS-1$ //$NON-NLS-2$
		addFieldWithHistory("package", packageCombo); //$NON-NLS-1$
		packageCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String current = packageCombo.getText();
				String defaultPackage = getDefaultJavaPackage();

				if (!packageCustomized && !current.equals("")
						&& !packageCombo.getText().equals(defaultPackage)) {
					packageCustomized = true;
				}
				validate();
			}
		});
	}

	@Override
	protected IWizardContainer getContainer() {
		return super.getContainer();
	}

	public void addArchetypeSelectionListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	public List<ArchetypeDetails> getArchetypes() {
		try {
			List<ArchetypeDetails> answer = new ArrayList<ArchetypeDetails>();
			Bundle bundle = Activator.getDefault().getBundle();
			if (bundle != null) {
				URL xml = bundle.getResource("/archetypes/archetypes.xml");
				String catalog = IOUtils.loadText(xml.openStream(), "UTF-8");
				Object object = null;
				try {
					// create JAXB context and instantiate marshaller
				    JAXBContext context = JAXBContext.newInstance(Archetypes.class, Archetype.class);
				    Unmarshaller unmarshaller = context.createUnmarshaller();
					object = unmarshaller.unmarshal(new StringReader(catalog));
				} catch (Exception e) {
					Activator.getLogger().warning(e);
				}
				if (object instanceof Archetypes) {
					Archetypes archetypesDTO = (Archetypes) object;
					List<Archetype> list = archetypesDTO.getArchetypes();
					for (Archetype archetype : list) {
						ArchetypeDetails details = new ArchetypeDetails(archetype);
						URL resource = bundle.getResource("/archetypes/" + details.getFullName());
						details.setResource(resource);
						answer.add(details);
					}
				}
			}
			return answer;

		} catch (Exception ce) {
			setErrorMessage(WizardMessages.MavenProjectWizardArchetypePage_error_read);
			return null;
		}
	}

	/** Overrides the default to return "true" if the page is not used. */
	@Override
	public boolean isPageComplete() {
		String error = validateInput();
		return super.isPageComplete() && error == null && getArchetype() != null;
	}

	/** Sets the focus to the table component. */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			if (defaultFocusOnFilter ) {
				filterText.setFocus();
			} else {
				viewer.getTable().setFocus();
			}
			ArchetypeDetails selected = getArchetype();
			if (selected != null) {
				viewer.reveal(selected);
			}

			Activator.getLogger().debug("================= Setting visible");
			userChangedArtifactId = false;
		}
	}

	/** Returns the selected archetype. */
	public ArchetypeDetails getArchetype() {
		return this.archetype;
	}

	/**
	 * ArchetypeLabelProvider
	 */
	protected static class ArchetypeLabelProvider extends LabelProvider
	implements ITableLabelProvider {
		/** Returns the element text */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ArchetypeDetails) {
				ArchetypeDetails archetype = (ArchetypeDetails) element;
				switch (columnIndex) {
				case 0:
					return archetype.getGroupId();
				case 1:
					return archetype.getArtifactId();
				case 2:
					return archetype.getVersion();
				}
			}
			return super.getText(element);
		}

		/** Returns the element text */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	/**
	 * QuickViewerFilter
	 */
	protected class QuickViewerFilter extends ViewerFilter implements
	ModifyListener {

		private String currentFilter;

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (currentFilter == null || currentFilter.length() == 0) {
				return true;
			}
			ArchetypeDetails archetype = (ArchetypeDetails) element;
			return archetype.contains(currentFilter);
		}

		@Override
		public void modifyText(ModifyEvent e) {
			this.currentFilter = filterText.getText().trim();
			viewer.refresh();
		}
	}

	/**
	 * Validates the contents of this wizard page.
	 * <p>
	 * Feedback about the validation is given to the user by displaying error
	 * messages or informative messages on the wizard page. Depending on the
	 * provided user input, the wizard page is marked as being complete or not.
	 * <p>
	 * If some error or missing input is detected in the user input, an error
	 * message or informative message, respectively, is displayed to the user.
	 * If the user input is complete and correct, the wizard page is marked as
	 * begin complete to allow the wizard to proceed. To that end, the following
	 * conditions must be met:
	 * <ul>
	 * <li>The user must have provided a valid group ID.</li>
	 * <li>The user must have provided a valid artifact ID.</li>
	 * <li>The user must have provided a version for the artifact.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
	 * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
	 * @see org.eclipse.jface.wizard.WizardPage#setPageComplete(boolean)
	 */
	void validate() {
		String error = validateInput();
		// lets not show the error message until an archetype is selected
		ArchetypeDetails a = getArchetype();
		if (a != null || error == null) {
			setErrorMessage(error);
		}
		setPageComplete(error == null);
	}

	private String validateInput() {
		String error = validateGroupIdInput(groupIdCombo.getText().trim());
		if (error != null) {
			return error;
		}

		error = validateArtifactIdInput(artifactIdCombo.getText().trim());
		if (error != null) {
			return error;
		}

		String versionValue = versionCombo.getText().trim();
		if (versionValue.length() == 0) {
			return WizardMessages.wizardProjectPageMaven2ValidatorVersion;
		}

		String packageName = packageCombo.getText();
		if (packageName.trim().length() != 0) {
			if (!Pattern
					.matches(
							"[A-Za-z_$][A-Za-z_$\\d]*(?:\\.[A-Za-z_$][A-Za-z_$\\d]*)*", packageName)) { //$NON-NLS-1$
				return WizardMessages.MavenProjectWizardArchetypeParametersPage_error_package;
			}
		}

		/*
		 * if (getArchetype() == null) { return
		 * WizardMessages.wizardProjectPageMaven2ValidatorRequiredArchetype; }
		 */

		if (requiredProperties != null && requiredProperties.size() > 0) {
			Properties properties = getProperties();
			for (String key : requiredProperties) {
				String value = properties.getProperty(key);
				if (value == null || value.length() == 0) {
					return NLS
							.bind(WizardMessages.wizardProjectPageMaven2ValidatorRequiredProperty,
									key);
				}
			}
		}

		return null;
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		if (propertiesViewer != null && propertiesViewer.isCellEditorActive()) {
			propertiesTable.setFocus();
		}
		if (propertiesTable != null) {
			for (int i = 0; i < propertiesTable.getItemCount(); i++) {
				TableItem item = propertiesTable.getItem(i);
				properties.put(item.getText(KEY_INDEX),
						item.getText(VALUE_INDEX));
			}
		}
		return properties;
	}

	/** Updates the package name if the related fields changed. */
	protected void updateJavaPackage() {
		if (packageCustomized) {
			return;
		}

		String defaultPackageName = getDefaultJavaPackage();
		packageCombo.setText(defaultPackageName);
	}

	/** Returns the default package name. */
	protected String getDefaultJavaPackage() {
		return getDefaultJavaPackage(groupIdCombo.getText().trim(),
				artifactIdCombo.getText().trim());
	}

	/** Returns the package name. */
	public String getJavaPackage() {
		if (packageCombo.getText().length() > 0) {
			return packageCombo.getText();
		}
		return getDefaultJavaPackage();
	}

	public static String getDefaultJavaPackage(String groupId, String artifactId) {
		StringBuffer sb = new StringBuffer(groupId);

		if (sb.length() > 0 && artifactId.length() > 0) {
			sb.append('.');
		}

		sb.append(artifactId);

		if (sb.length() == 0) {
			sb.append(DEFAULT_PACKAGE);
		}

		boolean isFirst = true;
		StringBuffer pkg = new StringBuffer();
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '-') {
				pkg.append('.');
				isFirst = false;
			} else {
				if (isFirst) {
					// if(UCharacter.isJavaIdentifierStart(c)) {
					if (Character.isJavaIdentifierStart(c)) {
						pkg.append(c);
						isFirst = false;
					}
				} else {
					if (c == '.') {
						pkg.append('.');
						isFirst = true;
						// } else if(UCharacter.isJavaIdentifierPart(c)) {
					} else if (Character.isJavaIdentifierPart(c)) {
						pkg.append(c);
					}
				}
			}
		}

		return pkg.toString();
	}

	public String getGroupId() {
		return groupIdCombo.getText();
	}

	public String getArtifactId() {
		return artifactIdCombo.getText();
	}

	public String getVersion() {
		return versionCombo.getText();
	}
}
