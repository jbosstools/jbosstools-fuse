package org.fusesource.ide.deployment.preferences;

import java.io.File;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.deployment.ConfigurationUtils;
import org.fusesource.ide.deployment.Messages;
import org.fusesource.ide.deployment.config.HotfolderDeploymentConfiguration;


/**
 * @author lhein
 */
public class DeploymentPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final String COLUMN_PROPERTY_DEFAULT = "default"; //$NON-NLS-1$
	private static final String COLUMN_PROPERTY_NAME = "name"; //$NON-NLS-1$
	private static final String COLUMN_PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String COLUMN_PROPERTY_DEPLOYPATH = "deploypath"; //$NON-NLS-1$
	private static final String[] COLUMN_PROPERTIES = new String[] {
		    COLUMN_PROPERTY_DEFAULT,
			COLUMN_PROPERTY_NAME,
			COLUMN_PROPERTY_DEPLOYPATH,
			COLUMN_PROPERTY_DESCRIPTION};
	private static final int[] COLUMN_WIDTHS = new int[] {
			50, 150, 250, 350
	};
	
	private HotfolderDeploymentConfiguration[] deploymentConfigurations;
	private Text txt_name;
	private Text txt_description;
	private Text txt_deploypath;
	private Button btn_browseDeployFolder;
	private TableViewer tableViewer;
	private Table table;
	private Button makeDefaultButton;
	private Button addButton;
	private Button removeButton;
	private Button clearButton;

	private final class TableContentProvider implements IStructuredContentProvider {
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return deploymentConfigurations;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	private final class TableLabelProvider implements ITableLabelProvider {
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			HotfolderDeploymentConfiguration dc = (HotfolderDeploymentConfiguration)element;
			
			if (columnIndex == 0)
				return Boolean.toString(dc.isDefaultConfig());
			else if (columnIndex == 1)
				return dc.getName();
			else if (columnIndex == 2)					
				return dc.getHotDeployPath();
			else if (columnIndex == 3)
				return dc.getDescription();
			else
				return "";
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return true;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl = new GridLayout(3, false);
		c.setLayout(gl);

        // Sets up the context sensitive help for this page
        PlatformUI.getWorkbench().getHelpSystem().setHelp(c, "org.fusesource.ide.camel.editor.deployFolderPref");

		applyDialogFont(c);
		
		createContentControls(c);
		hookListeners();
		
		refreshFromModel();		
		
		validateFormFields();

		return c;
	}
	
	protected void createContentControls(Composite parent) {
		createForm(parent);
		createTable(parent);
		createTableColumns(table);
		createTableViewer(table);
		initializeTableViewerProviders(tableViewer);		
		
		Composite buttonColumn = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, true);
		gl.marginWidth = 0;
		buttonColumn.setLayout(gl);

		createButtons(buttonColumn);
	}
	
	protected void createForm(Composite parent) {
		Label label_name = new Label(parent, SWT.NONE);
		label_name.setText(Messages.tableHeaderNameLabel);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		label_name.setLayoutData(gd);
		
		txt_name = new Text(parent, SWT.LEFT | SWT.BORDER);
		txt_name.setText("");
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;		
		txt_name.setLayoutData(gd);

		new Label(parent, SWT.NONE);
		
		Label label_deployPath = new Label(parent, SWT.NONE);
		label_deployPath.setText(Messages.tableHeaderDeployPathLabel);
		label_deployPath.setToolTipText(Messages.tableHeaderDeployPathToolTip);
		
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;		
		label_deployPath.setLayoutData(gd);
		
		txt_deploypath = new Text(parent, SWT.LEFT | SWT.BORDER);
		txt_deploypath.setText("");
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		txt_deploypath.setLayoutData(gd);
		
		btn_browseDeployFolder = new Button(parent, SWT.PUSH);
		btn_browseDeployFolder.setText("...");
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		btn_browseDeployFolder.setLayoutData(gd);
				
		Label label_description = new Label(parent, SWT.NONE);
		label_description.setText(Messages.tableHeaderDescriptionLabel);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;		
		label_description.setLayoutData(gd);
		
		txt_description = new Text(parent, SWT.LEFT | SWT.BORDER);
		txt_description.setText("");
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		txt_description.setLayoutData(gd);

		new Label(parent, SWT.NONE);
		
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		
		Composite btnCompo = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		btnCompo.setLayout(gl);
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = GridData.FILL;
		btnCompo.setLayoutData(gd);

		addButton = new Button(btnCompo, SWT.PUSH);
		addButton.setText(Messages.addDeployConfigButtonLabel);
		addButton.setEnabled(false);
		setButtonLayoutData(addButton);
		
        removeButton = new Button(btnCompo, SWT.PUSH);
		removeButton.setText(Messages.removeDeployConfigButtonLabel);
		removeButton.setEnabled(false);
		setButtonLayoutData(removeButton);
		
		clearButton = new Button(btnCompo, SWT.PUSH);
		clearButton.setText(Messages.clearDeployConfigButtonLabel);
		clearButton.setEnabled(true);
		setButtonLayoutData(clearButton);
	}
	
	protected void createButtons(Composite parent) {
		makeDefaultButton = new Button(parent, SWT.PUSH);
		makeDefaultButton.setText(Messages.makeDefaultConfigButtonLabel);
		makeDefaultButton.setEnabled(true);
		setButtonLayoutData(makeDefaultButton);
	}
	
	private boolean isEditing() {
		if (table.getSelectionCount()>0 && ((HotfolderDeploymentConfiguration)table.getSelection()[0].getData()).getName().equals(txt_name.getText())) {
			return true; // user edits an existing entry
		}
		return false;
	}
	
	/**
	 * 
	 */
	protected void addNewElement() {
		if (isEditing()) {
			HotfolderDeploymentConfiguration cfg = (HotfolderDeploymentConfiguration)table.getSelection()[0].getData();
			cfg.setDescription(txt_description.getText());
			cfg.setHotDeployPath(txt_deploypath.getText());
		} else {
			int n = deploymentConfigurations.length;
			HotfolderDeploymentConfiguration[] adc = new HotfolderDeploymentConfiguration[n+1];
			System.arraycopy(deploymentConfigurations, 0, adc, 0, n);
			adc[n] = new HotfolderDeploymentConfiguration();
			adc[n].setDefaultConfig(n == 0);
			adc[n].setName(txt_name.getText());
			adc[n].setDescription(txt_description.getText());
			adc[n].setHotDeployPath(txt_deploypath.getText());
			deploymentConfigurations = adc;
			tableViewer.reveal(adc[n]);
		}
		tableViewer.refresh();
		clearForm();
		clearSelection();
	}
	
	protected void clearSelection() {
		table.deselectAll();
	}
	
	protected void clearForm() {
		txt_name.setText("");
		txt_description.setText("");
		txt_deploypath.setText("");
		setErrorMessage(null);
	}
	
	protected void removeSelectedElements() {
		int j=0, n = deploymentConfigurations.length - table.getSelectionCount();
		HotfolderDeploymentConfiguration[] dcs = new HotfolderDeploymentConfiguration[n];
		for (int i=0; i < table.getItemCount(); i++) {
			if (!table.isSelected(i))
				dcs[j++] = deploymentConfigurations[i];
		}
		deploymentConfigurations = dcs;
		tableViewer.refresh();
		clearForm();
		clearSelection();
	}
	
	protected void markSelectedElementAsDefault() {
		if (table.getSelectionCount() != 1) {
			return; // only one config can be the default one
		}
		IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
		if (sel.getFirstElement() != null) {
			HotfolderDeploymentConfiguration dep = (HotfolderDeploymentConfiguration)sel.getFirstElement();
			for (TableItem ti : table.getItems()) {
				HotfolderDeploymentConfiguration cfg = (HotfolderDeploymentConfiguration)ti.getData();
				cfg.setDefaultConfig(false);
			}
			dep.setDefaultConfig(true);
			tableViewer.refresh();
		}
	}
	
	protected Table createTable(Composite parent) {
		GridData gd;
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 300;
		table = new Table(parent, SWT.SINGLE|SWT.BORDER|SWT.V_SCROLL|SWT.FULL_SELECTION);
		table.setLayoutData(gd);

		return table;
	}

	protected void createTableColumns(Table tbl) {
		TableColumn varDefault, varName, varDescription, varDeployPath;
		
		tbl.setHeaderVisible(true);		
		tbl.setLinesVisible(true);
		
		varDefault = new TableColumn(tbl, SWT.CENTER);
		varName = new TableColumn(tbl, SWT.LEFT);
		varDeployPath = new TableColumn(tbl, SWT.LEFT);
		varDescription = new TableColumn(tbl, SWT.LEFT);
		
		varDefault.setText(Messages.tableHeaderDefaultLabel);
		varDefault.setWidth(COLUMN_WIDTHS[0]);
				
		varName.setText(Messages.tableHeaderNameLabel);
		varName.setWidth(COLUMN_WIDTHS[1]);
		
		varDescription.setText(Messages.tableHeaderDescriptionLabel);
		varDescription.setWidth(COLUMN_WIDTHS[2]);
		
		varDeployPath.setText(Messages.tableHeaderDeployPathLabel);
		varDeployPath.setWidth(COLUMN_WIDTHS[3]);
	}

	protected TableContentProvider createTableContentProvider() {
		return new TableContentProvider();
	}

	protected TableLabelProvider createTableLabelProvider() {
		return new TableLabelProvider();
	}

	protected TableViewer createTableViewer(Table table) {
		tableViewer = new TableViewer(table);

		return tableViewer;
	}

	protected void hookListeners() {
		btn_browseDeployFolder.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog ddlg = new DirectoryDialog(getShell(), SWT.OPEN);
				ddlg.setMessage(Messages.containerFolderDialogDescription);
				ddlg.setText(Messages.containerFolderDialogTitle);
				ddlg.setFilterPath(txt_deploypath.getText().trim());
				String path = ddlg.open();
				if (path != null) {
					txt_deploypath.setText(path);
				}
			}
		});
		clearButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearForm();
				clearSelection();
				removeButton.setEnabled(false);
				validateFormFields();
			}
		});
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (validateFormFields()) {
					addNewElement();	
				}
			}
		});
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeSelectedElements();	
				removeButton.setEnabled(false);
			}
		});
		makeDefaultButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				markSelectedElementAsDefault();
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeButton.setEnabled(table.getSelectionCount() > 0);
				displaySelection();
			}
		});
		txt_name.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				addButton.setEnabled(validateFormFields());
				updateButtons();
			}
		});
		txt_description.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				validateFormFields();
			}
		});
		txt_deploypath.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				addButton.setEnabled(validateFormFields());
				updateButtons();
			}
		});
	}
	
	/**
	 * updates the label of the "Add" Button
	 */
	private void updateButtons() {
		if (table.getSelectionCount()>0 &&
			table.getSelection().length>0) {
			if (((HotfolderDeploymentConfiguration)table.getSelection()[0].getData()).getName().equals(txt_name.getText())) {
				// seems the user is editing an existing entry
				addButton.setText(Messages.updateDeployConfigButtonLabel);
			} else {
				addButton.setText(Messages.addDeployConfigButtonLabel);
			}
		} else {
			addButton.setText(Messages.addDeployConfigButtonLabel);
		}
	}
	
	private boolean validateFormFields() {
		if (containsInvalidChars(txt_name.getText()) ||
			containsInvalidChars(txt_description.getText()) ||
			containsInvalidChars(txt_deploypath.getText())) {
			setErrorMessage(Messages.invalidCharsError);
			return false;
		}
		if (txt_name.getText().trim().length()<1) {
			setErrorMessage(Messages.nameTooShortError);
			return false;
		} else if (!isUniqueName(txt_name.getText().trim())) {
			setErrorMessage(Messages.nameNotUniqueError);
			return false;
		} else if (txt_deploypath.getText().trim().length()<1) {
			setErrorMessage(Messages.deployFolderTooShortError);
			return false;
		} else if (!isValidFolder(txt_deploypath.getText().trim())) {
			setErrorMessage(Messages.deployFolderNotValidError);
			return false;
		}
		setErrorMessage(null);
		return true;
	}
	
	private boolean containsInvalidChars(String value) {
		return value.indexOf(',') != -1 || value.indexOf(';') != -1;
	}
	
	private boolean isUniqueName(String name) {
		if (isEditing()) {
			return true; // user edits an existing entry
		}
		for (HotfolderDeploymentConfiguration cfg : this.deploymentConfigurations) {
			if (cfg.getName().trim().equalsIgnoreCase(name)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isValidFolder(String folderPath) {
		File f = new File(folderPath);
		return f.exists() && f.isDirectory();
	}
	
	private void displaySelection() {
		clearForm();
		
		TableItem[] items = table.getSelection();
		if (items.length!=1) {
			return;
		}
		HotfolderDeploymentConfiguration config = (HotfolderDeploymentConfiguration)items[0].getData();
		txt_name.setText(config.getName());
		txt_description.setText(config.getDescription());
		txt_deploypath.setText(config.getHotDeployPath());
	}
	
	protected void initializeTableViewerProviders(TableViewer tv) {
		tv.setLabelProvider(createTableLabelProvider());
		tv.setContentProvider(createTableContentProvider());		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performCancel()
	 */
	@Override
	public boolean performCancel() {
		refreshFromModel();
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		HotfolderDeploymentConfiguration[] dc = new HotfolderDeploymentConfiguration[deploymentConfigurations.length];
		for (int i=0; i < dc.length; i++) {
			dc[i] = new HotfolderDeploymentConfiguration();
			dc[i].setDefaultConfig(deploymentConfigurations[i].isDefaultConfig());
			dc[i].setName(deploymentConfigurations[i].getName());
			dc[i].setDescription(deploymentConfigurations[i].getDescription());
			dc[i].setHotDeployPath(deploymentConfigurations[i].getHotDeployPath());
		}
		
		deploymentConfigurations = dc;
		ConfigurationUtils.savePreferences(deploymentConfigurations);
		
		return true;
	}


	protected void refreshFromModel() {
		clearForm();
		clearSelection();
		deploymentConfigurations = ConfigurationUtils.loadPreferences();		
		tableViewer.setInput(deploymentConfigurations);			
		tableViewer.refresh();			
	}
}
