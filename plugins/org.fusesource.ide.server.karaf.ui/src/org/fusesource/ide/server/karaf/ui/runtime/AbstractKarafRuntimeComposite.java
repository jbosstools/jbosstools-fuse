package org.fusesource.ide.server.karaf.ui.runtime;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.karaf.core.internal.KarafUtils;
import org.fusesource.ide.server.karaf.ui.Messages;


public abstract class AbstractKarafRuntimeComposite extends Composite implements Listener{
	
	public static final String SEPARATOR = File.separator;
	
	protected final Composite parent;
	protected final IWizardHandle wizardHandle;
	protected Text txtKarafDir;
	protected final KarafWizardDataModel model;
	protected boolean valid = false;

	public AbstractKarafRuntimeComposite(Composite parent, IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, SWT.NONE);
		this.parent = parent;
		this.wizardHandle = wizardHandle;
		this.model = model;
		wizardHandle.setTitle(Messages.AbstractKarafRuntimeComposite_wizard_tite);
		wizardHandle.setDescription(Messages.AbstractKarafRuntimeComposite_wizard_desc);
		wizardHandle.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_RUNTIME));	
	}
	
	public void handleEvent(Event event) {
		boolean valid = false;
		if (event.type == SWT.FocusIn){
			handleFocusEvent(event);
		} else{
			if (event.widget == txtKarafDir) {
				valid = validate();
				if (valid){
					String installDir = txtKarafDir.getText();
					model.setKarafInstallDir(installDir);
					model.setKarafPropertiesFileLocation(getKarafPropFileLocation(installDir));
					model.setKarafVersion(KarafUtils.getVersion(new File(installDir)));
				}
			}
		}
		
		wizardHandle.update();
	}

	public void handleFocusEvent(Event event) {
		if (event.widget == txtKarafDir) {
			wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_txt_info_msg, IMessageProvider.NONE);
		}
		
	}

	protected abstract boolean doClassPathEntiresExist(final String karafInstallDir) ;

	protected abstract String getKarafPropFileLocation(String karafInstallDir) ;
	
	public boolean validate(){
		valid = false;
		String dirLocation = txtKarafDir.getText().trim();
		if (dirLocation != null && !"".equals(dirLocation)) { 
			File file = new File(dirLocation);
			if (!file.exists()) {
				wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_no_dir,
						IMessageProvider.ERROR);
			} else if (!file.isDirectory()) {
				wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_not_a_dir,
						IMessageProvider.ERROR);
			} else{
				File binKaraf = new File(dirLocation + SEPARATOR + Messages.AbstractKarafRuntimeComposite_bin_karaf); 
				File binKarafBat = new File(dirLocation + SEPARATOR + Messages.AbstractKarafRuntimeComposite_bin_karaf_bat); 
				File confFile = new File(getKarafPropFileLocation(dirLocation));
				if ((binKaraf.exists() || binKarafBat.exists() )&& confFile.exists()
						&& doClassPathEntiresExist(dirLocation)) {
					valid = true;
					wizardHandle.setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
				} else {
					wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_invalid_dir, IMessageProvider.ERROR); //$NON-NLS-1$
				}
			}
		} else {
			wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_wizard_help_msg, IMessageProvider.NONE); //$NON-NLS-1$
		}
		return valid;
	}
	
	void createContents(){
		setLayout( new GridLayout(3,false));
		Label lblKarafInstallDir = new Label(this,SWT.NONE);
		lblKarafInstallDir.setText(Messages.AbstractKarafRuntimeComposite_install_dir_label);
		txtKarafDir = new Text(this,SWT.BORDER);
		txtKarafDir.addListener(SWT.Modify, this);
		txtKarafDir.setText(model.getKarafInstallDir());
		GridData txtKarafDirGridData = new GridData();
		txtKarafDirGridData.grabExcessHorizontalSpace = true;
		txtKarafDirGridData.horizontalAlignment = SWT.FILL;
		txtKarafDir.setLayoutData(txtKarafDirGridData);
		
		Button btnBrowseButton = new Button(this,SWT.NONE);
		btnBrowseButton.setText(Messages.AbstractKarafRuntimeComposite_browse_text);
		btnBrowseButton.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				
			}

			public void mouseUp(MouseEvent e) {
				DirectoryDialog dd = new DirectoryDialog(Display.getDefault()
						.getActiveShell(), SWT.OPEN);
				dd.setFilterPath(txtKarafDir.getText());
				String dir = dd.open();
				if (dir != null){
					txtKarafDir.setText(dd.getFilterPath());
				}
			}
			
		});
		wizardHandle.update();
	}
	
	void performFinish() {
		
	}
	
	protected boolean isValid(){
		return valid;
	}
}
