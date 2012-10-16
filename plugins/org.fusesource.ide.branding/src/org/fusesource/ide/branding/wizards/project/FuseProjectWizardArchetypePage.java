package org.fusesource.ide.branding.wizards.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.fusesource.camel.tooling.util.Archetype;
import org.fusesource.camel.tooling.util.Archetypes;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.branding.RiderHelpContextIds;
import org.fusesource.ide.branding.wizards.WizardMessages;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.util.Strings;
import org.osgi.framework.Bundle;


// import com.ibm.icu.lang.UCharacter;

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

	/**
	 * a flag indicating if the archetype selection is actually used in the
	 * wizard
	 */
	private boolean isUsed = true;

	volatile Collection<ArchetypeDetails> archetypes;

	/*
	 * 
	 * Collection<Archetype> lastVersionArchetypes;
	 * 
	 * ArchetypeCatalogFactory catalogFactory = null;
	 */

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

		// TODO
		// createAdvancedSettings(composite, new GridData(SWT.FILL, SWT.TOP,
		// true, false, 3, 1));

		setControl(composite);
	}

	/** Creates the archetype table viewer. */
	private void createViewer(Composite parent) {
		Label filterLabel = new Label(parent, SWT.NONE);
		filterLabel.setLayoutData(new GridData());
		filterLabel
		.setText(WizardMessages.MavenProjectWizardArchetypePage_lblFilter);

		QuickViewerFilter quickViewerFilter = new QuickViewerFilter();
		/*
		 * LastVersionFilter versionFilter = new LastVersionFilter();
		 * IncludeSnapshotsFilter snapshotsFilter = new
		 * IncludeSnapshotsFilter();
		 */

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

		// TODO
		/*
		 * clearToolItem.setImage(MavenImages.IMG_CLEAR);
		 * clearToolItem.setDisabledImage(MavenImages.IMG_CLEAR_DISABLED);
		 */
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

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (descriptionText == null) {
					return;
				}
				ArchetypeDetails archetype = getArchetype();
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
						String value = archetype.getArtifactId().replace("archetype-", "");
						artifactIdCombo.setText(value);
						userChangedArtifactId = false;
					}
				} else {
					descriptionText.setText(""); //$NON-NLS-1$
				}
				validate();
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

		/*
		 * showLastVersionButton = new Button(buttonComposite, SWT.CHECK);
		 * showLastVersionButton.setLayoutData(new GridData(SWT.LEFT,
		 * SWT.CENTER, true, false));
		 * showLastVersionButton.setText(WizardMessages
		 * .MavenProjectWizardArchetypePage_btnLast);
		 * showLastVersionButton.setSelection(true);
		 * showLastVersionButton.addSelectionListener(versionFilter);
		 * 
		 * includeShapshotsButton = new Button(buttonComposite, SWT.CHECK);
		 * GridData buttonData = new GridData(SWT.LEFT, SWT.CENTER, true,
		 * false); buttonData.horizontalIndent = 25;
		 * includeShapshotsButton.setLayoutData(buttonData);
		 * includeShapshotsButton
		 * .setText(WizardMessages.MavenProjectWizardArchetypePage_btnSnapshots
		 * ); includeShapshotsButton.setSelection(false);
		 * includeShapshotsButton.addSelectionListener(snapshotsFilter);
		 * 
		 * addArchetypeButton = new Button(buttonComposite, SWT.NONE);
		 * addArchetypeButton
		 * .setText(WizardMessages.MavenProjectWizardArchetypePage_btnAdd);
		 * addArchetypeButton.setData("name", "addArchetypeButton");
		 * //$NON-NLS-1$ //$NON-NLS-2$ buttonData = new GridData(SWT.RIGHT,
		 * SWT.CENTER, true, false); buttonData.horizontalIndent = 35;
		 * addArchetypeButton.setLayoutData(buttonData);
		 * 
		 * addArchetypeButton.addSelectionListener(new SelectionAdapter() {
		 * public void widgetSelected(SelectionEvent e) { CustomArchetypeDialog
		 * dialog = new CustomArchetypeDialog(getShell(),
		 * WizardMessages.MavenProjectWizardArchetypePage_add_title);
		 * if(dialog.open() == Window.OK) { String archetypeGroupId =
		 * dialog.getArchetypeGroupId(); String archetypeArtifactId =
		 * dialog.getArchetypeArtifactId(); String archetypeVersion =
		 * dialog.getArchetypeVersion(); String repositoryUrl =
		 * dialog.getRepositoryUrl(); downloadArchetype(archetypeGroupId,
		 * archetypeArtifactId, archetypeVersion, repositoryUrl); } } });
		 */

		if (!list.isEmpty()) {
			viewer.setSelection(new StructuredSelection(list.get(0)));
			Viewers.async(new Runnable() {

				@Override
				public void run() {
					userChangedArtifactId = false;
				}});
		}

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
		// Composite artifactGroup = new Composite(parent, SWT.NONE);
		// GridData gd_artifactGroup = new GridData( SWT.FILL, SWT.FILL, true,
		// false );
		// artifactGroup.setLayoutData(gd_artifactGroup);
		// artifactGroup.setLayout(new GridLayout(2, false));

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
				Object object = null;
				try {
					Unmarshaller unmarshaller;
					unmarshaller = Archetypes.newUnmarshaller();
					object = unmarshaller.unmarshal(xml);
				} catch (Exception e) {
					Activator.getLogger().warning(e);
				}
				if (object instanceof Archetypes) {
					Archetypes archetypesDTO = (Archetypes) object;
					List<Archetype> list = archetypesDTO.archetypes();
					for (Archetype archetype : list) {
						ArchetypeDetails details = new ArchetypeDetails(archetype);
						URL resource = bundle.getResource("/archetypes/" + details.getFullName());
						details.setResource(resource);
						answer.add(details);
					}
				}
				/*
				Enumeration entryPaths = bundle.getEntryPaths("/archetypes");
				while (entryPaths.hasMoreElements()) {
					Object next = entryPaths.nextElement();
					String name = next.toString();
					if (name.endsWith(".jar")) {
						URL resource = bundle.getResource(name);
						String id = name.replaceFirst("archetypes/", "");
						answer.add(new ArchetypeDetails(id, resource));
					}
				}
				 */
			}
			return answer;

		} catch (Exception ce) {
			setErrorMessage(WizardMessages.MavenProjectWizardArchetypePage_error_read);
			return null;
		}
	}

	/** Loads the available archetypes. */
	/*
	 * void loadArchetypes(final String groupId, final String artifactId, final
	 * String version) { Job job = new
	 * Job(WizardMessages.wizardProjectPageArchetypeRetrievingArchetypes) {
	 * protected IStatus run(IProgressMonitor monitor) { try { List<Archetype>
	 * catalogArchetypes = getArchetypesForCatalog();
	 * 
	 * if(catalogArchetypes == null || catalogArchetypes.size() == 0) {
	 * Display.getDefault().asyncExec(new Runnable() { public void run() {
	 * if(catalogFactory != null &&
	 * "Nexus Indexer".equals(catalogFactory.getDescription())) { //$NON-NLS-1$
	 * setErrorMessage(WizardMessages.MavenProjectWizardArchetypePage_error_no);
	 * } } }); } else { Display.getDefault().asyncExec(new Runnable() { public
	 * void run() { setErrorMessage(null); } }); } if(catalogArchetypes == null)
	 * { return Status.CANCEL_STATUS; } TreeSet<Archetype> archs = new
	 * TreeSet<Archetype>(ARCHETYPE_COMPARATOR);
	 * archs.addAll(catalogArchetypes); archetypes = archs;
	 * 
	 * Display.getDefault().asyncExec(new Runnable() { public void run() {
	 * updateViewer(groupId, artifactId, version); } }); } catch(Exception e) {
	 * monitor.done(); return Status.CANCEL_STATUS; }
	 * 
	 * return Status.OK_STATUS; } }; job.schedule(); }
	 * 
	 * public Set<Archetype> filterVersions(Collection<Archetype> archetypes) {
	 * HashMap<String, Archetype> filteredArchetypes = new HashMap<String,
	 * Archetype>();
	 * 
	 * for(Archetype currentArchetype : archetypes) { String key =
	 * getArchetypeKey(currentArchetype); Archetype archetype =
	 * filteredArchetypes.get(key); if(archetype == null) {
	 * filteredArchetypes.put(key, currentArchetype); } else {
	 * DefaultArtifactVersion currentVersion = new
	 * DefaultArtifactVersion(currentArchetype.getVersion());
	 * DefaultArtifactVersion version = new
	 * DefaultArtifactVersion(archetype.getVersion());
	 * if(currentVersion.compareTo(version) > 0) { filteredArchetypes.put(key,
	 * currentArchetype); } } }
	 * 
	 * TreeSet<Archetype> result = new TreeSet<Archetype>(new
	 * Comparator<Archetype>() { public int compare(Archetype a1, Archetype a2)
	 * { String k1 = a1.getGroupId() + ":" + a1.getArtifactId() + ":" +
	 * a1.getVersion(); //$NON-NLS-1$ //$NON-NLS-2$ String k2 = a2.getGroupId()
	 * + ":" + a2.getArtifactId() + ":" + a2.getVersion(); //$NON-NLS-1$
	 * //$NON-NLS-2$ return k1.compareTo(k2); } });
	 * result.addAll(filteredArchetypes.values()); return result; }
	 * 
	 * private String getArchetypeKey(Archetype archetype) { return
	 * archetype.getGroupId() + ":" + archetype.getArtifactId(); //$NON-NLS-1$ }
	 * 
	 * ArchetypeCatalog getArchetypeCatalog() throws CoreException { return
	 * catalogFactory == null ? null : catalogFactory.getArchetypeCatalog(); }
	 */

	/** Sets the flag that the archetype selection is used in the wizard. */
	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
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
			/*
			 * ArchetypeManager archetypeManager =
			 * MavenPlugin.getDefault().getArchetypeManager(); String catalogId
			 * = dialogSettings.get(KEY_CATALOG); catalogFactory = null;
			 * if(catalogId != null) { catalogFactory =
			 * archetypeManager.getArchetypeCatalogFactory(catalogId); }
			 * catalogsComboViewer.setSelection(new
			 * StructuredSelection(catalogFactory == null ? ALL_CATALOGS :
			 * catalogFactory));
			 */
			if (defaultFocusOnFilter ) {
				filterText.setFocus();
			} else {
				viewer.getTable().setFocus();
			}
			ArchetypeDetails selected = getArchetype();
			if (selected != null) {
				viewer.reveal(selected);
			}

			System.out.println("================= Setting visible");
			userChangedArtifactId = false;
		}
	}

	/** Returns the selected archetype. */
	public ArchetypeDetails getArchetype() {
		return (ArchetypeDetails) ((IStructuredSelection) viewer.getSelection())
				.getFirstElement();
	}

	/*
	 * void updateViewer(String groupId, String artifactId, String version) {
	 * lastVersionArchetypes = filterVersions(archetypes);
	 * 
	 * viewer.setInput(archetypes);
	 * 
	 * selectArchetype(groupId, artifactId, version);
	 * 
	 * Table table = viewer.getTable(); int columnCount =
	 * table.getColumnCount(); int width = 0; for(int i = 0; i < columnCount;
	 * i++ ) { TableColumn column = table.getColumn(i); column.pack(); width +=
	 * column.getWidth(); } GridData tableData = (GridData)
	 * table.getLayoutData(); int oldHint = tableData.widthHint; if(width >
	 * oldHint) { tableData.widthHint = width; } getShell().pack(true);
	 * tableData.widthHint = oldHint; }
	 * 
	 * protected void selectArchetype(String groupId, String artifactId, String
	 * version) { Archetype archetype = findArchetype(groupId, artifactId,
	 * version);
	 * 
	 * Table table = viewer.getTable(); if(archetype != null) {
	 * viewer.setSelection(new StructuredSelection(archetype), true);
	 * 
	 * int n = table.getSelectionIndex(); table.setSelection(n); } }
	 */

	/** Locates an archetype with given ids. */
	/*
	 * protected Archetype findArchetype(String groupId, String artifactId,
	 * String version) { for(Archetype archetype : archetypes) {
	 * if(archetype.getGroupId().equals(groupId) &&
	 * archetype.getArtifactId().equals(artifactId)) { if(version == null ||
	 * version.equals(archetype.getVersion())) { return archetype; } } }
	 * 
	 * return version == null ? null : findArchetype(groupId, artifactId, null);
	 * }
	 * 
	 * protected void downloadArchetype(final String archetypeGroupId, final
	 * String archetypeArtifactId, final String archetypeVersion, final String
	 * repositoryUrl) { final String archetypeName = archetypeGroupId + ":" +
	 * archetypeArtifactId + ":" + archetypeVersion; //$NON-NLS-1$ //$NON-NLS-2$
	 * 
	 * try { getContainer().run(true, true, new IRunnableWithProgress() { public
	 * void run(IProgressMonitor monitor) throws InterruptedException {
	 * monitor.beginTask
	 * (WizardMessages.MavenProjectWizardArchetypePage_task_downloading +
	 * archetypeName, IProgressMonitor.UNKNOWN);
	 * 
	 * try { final IMaven maven = MavenPlugin.getDefault().getMaven();
	 * 
	 * final MavenPlugin plugin = MavenPlugin.getDefault();
	 * 
	 * final List<ArtifactRepository> remoteRepositories;
	 * if(repositoryUrl.length() == 0) { remoteRepositories =
	 * maven.getArtifactRepositories(); // XXX should use
	 * ArchetypeManager.getArhetypeRepositories() } else { ArtifactRepository
	 * repository = new DefaultArtifactRepository( // "archetype",
	 * repositoryUrl, new DefaultRepositoryLayout(), null, null); //$NON-NLS-1$
	 * remoteRepositories = Collections.singletonList(repository); }
	 * 
	 * monitor.subTask(WizardMessages.MavenProjectWizardArchetypePage_task_resolving
	 * ); Artifact pomArtifact = maven.resolve(archetypeGroupId,
	 * archetypeArtifactId, archetypeVersion, "pom", null, remoteRepositories,
	 * monitor); //$NON-NLS-1$ monitor.worked(1); if(monitor.isCanceled()) {
	 * throw new InterruptedException(); }
	 * 
	 * File pomFile = pomArtifact.getFile(); if(pomFile.exists()) {
	 * monitor.subTask
	 * (WizardMessages.MavenProjectWizardArchetypePage_task_resolving2);
	 * Artifact jarArtifact = maven.resolve(archetypeGroupId,
	 * archetypeArtifactId, archetypeVersion, "jar", null, remoteRepositories,
	 * monitor); //$NON-NLS-1$ monitor.worked(1); if(monitor.isCanceled()) {
	 * throw new InterruptedException(); }
	 * 
	 * File jarFile = jarArtifact.getFile();
	 * 
	 * monitor.subTask(WizardMessages.MavenProjectWizardArchetypePage_task_reading
	 * ); monitor.worked(1); if(monitor.isCanceled()) { throw new
	 * InterruptedException(); }
	 * 
	 * monitor.subTask(WizardMessages.MavenProjectWizardArchetypePage_task_indexing
	 * ); IndexManager indexManager = plugin.getIndexManager(); IMutableIndex
	 * localIndex = indexManager.getLocalIndex();
	 * localIndex.addArtifact(jarFile, new ArtifactKey(pomArtifact));
	 * 
	 * //save out the archetype //TODO move this logig out of UI code! Archetype
	 * archetype = new Archetype(); archetype.setGroupId(archetypeGroupId);
	 * archetype.setArtifactId(archetypeArtifactId);
	 * archetype.setVersion(archetypeVersion);
	 * archetype.setRepository(repositoryUrl);
	 * org.apache.maven.archetype.Archetype archetyper =
	 * MavenPlugin.getDefault().getArchetype();
	 * archetyper.updateLocalCatalog(archetype);
	 * 
	 * loadArchetypes(archetypeGroupId, archetypeArtifactId, archetypeVersion);
	 * } else { final Artifact pom = pomArtifact; //the user tried to add an
	 * archetype that couldn't be resolved on the server
	 * getShell().getDisplay().asyncExec(new Runnable() { public void run() {
	 * setErrorMessage(NLS.bind(
	 * WizardMessages.MavenProjectWizardArchetypePage_error_resolve,
	 * pom.toString())); } }); }
	 * 
	 * } catch(InterruptedException ex) { throw ex;
	 * 
	 * } catch(final Exception ex) { final String msg = NLS.bind(
	 * WizardMessages.MavenProjectWizardArchetypePage_error_resolve2,
	 * archetypeName); log.error(msg, ex); getShell().getDisplay().asyncExec(new
	 * Runnable() { public void run() { setErrorMessage(msg + "\n" +
	 * ex.toString()); //$NON-NLS-1$ } });
	 * 
	 * } finally { monitor.done();
	 * 
	 * } } });
	 * 
	 * } catch(InterruptedException ex) { // ignore
	 * 
	 * } catch(InvocationTargetException ex) { String msg =
	 * NLS.bind(WizardMessages.MavenProjectWizardArchetypePage_error_resolve2,
	 * archetypeName); log.error(msg, ex); setErrorMessage(msg + "\n" +
	 * ex.toString()); //$NON-NLS-1$
	 * 
	 * } }
	 */

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

	// reload the table when index updating finishes
	// try to preserve selection in case this is a rebuild
	protected void reloadViewer() {
		/*
		 * Display.getDefault().asyncExec(new Runnable() { public void run() {
		 * if(isCurrentPage()) { StructuredSelection sel = (StructuredSelection)
		 * viewer.getSelection(); Archetype selArchetype = null; if(sel != null
		 * && sel.getFirstElement() != null) { selArchetype = (Archetype)
		 * sel.getFirstElement(); } if(selArchetype != null) {
		 * loadArchetypes(selArchetype.getGroupId(),
		 * selArchetype.getArtifactId(), selArchetype.getVersion()); } else {
		 * loadArchetypes("org.apache.maven.archetypes",
		 * "maven-archetype-quickstart", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
		 * //$NON-NLS-3$ } } } });
		 */
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

		// TODO: check validity of version?
		String packageName = packageCombo.getText();
		if (packageName.trim().length() != 0) {
			if (!Pattern
					.matches(
							"[A-Za-z_$][A-Za-z_$\\d]*(?:\\.[A-Za-z_$][A-Za-z_$\\d]*)*", packageName)) { //$NON-NLS-1$
				return WizardMessages.MavenProjectWizardArchetypeParametersPage_error_package;
			}
		}

		// validate project name
		IStatus nameStatus = validateProjectName();
		if (!nameStatus.isOK()) {
			return NLS
					.bind(WizardMessages.wizardProjectPageMaven2ValidatorProjectNameInvalid,
							nameStatus.getMessage());
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

	public String getProjectName() {
		return getArtifactId();
		/*
		 * // XXX should use resolved MavenProject or Model
		 * if(projectNameTemplate.length() == 0) { return getArtifactId(); }
		 * 
		 * String artifactId = getArtifactId(); String groupId = getGroupId();
		 * if(groupId == null && getParent() != null) { groupId =
		 * getParent().getGroupId(); } String version = getVersion(); if(version
		 * == null && getParent() != null) { version = getParent().getVersion();
		 * }
		 * 
		 * return projectNameTemplate.replaceAll(GROUP_ID,
		 * groupId).replaceAll(ARTIFACT_ID, artifactId).replaceAll(VERSION,
		 * version == null ? "" : version); //$NON-NLS-1$
		 */
	}

	public IStatus validateProjectName() {
		String projectName = getProjectName();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// check if the project name is valid
		IStatus nameStatus = workspace.validateName(projectName,
				IResource.PROJECT);
		if (!nameStatus.isOK()) {
			return nameStatus;
		}

		// check if project already exists
		if (workspace.getRoot().getProject(projectName).exists()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, NLS.bind(
					WizardMessages.importProjectExists, projectName), null);
		}

		return Status.OK_STATUS;
	}
}
