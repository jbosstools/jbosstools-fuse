/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.ClasspathResourceSelectionDialog;

/**
 * @author brianf
 *
 */
public class XMLPage extends XformWizardPage implements TransformationTypePage {

    final DataBindingContext context = new DataBindingContext(
            SWTObservables.getRealm(Display.getCurrent()));
    final ObservablesManager observablesManager = new ObservablesManager();
    private Composite _page;
    private boolean isSource = true;
    private Text _xmlFileText;
    private Button _xmlSchemaOption;
    private Button _xmlInstanceOption;
    private Text _xmlPreviewText;

    /**
     * @param model
     */
    public XMLPage(String pageName, final Model model, boolean isSource) {
        super(pageName, model);
        setTitle("XML Page");
        setImageDescriptor(Activator.imageDescriptor("transform.png"));
        this.isSource = isSource;
        observablesManager.addObservablesFromContext(context, true, true);
    }

    @Override
    public void createControl(final Composite parent) {
        if (this.isSource) {
            setTitle("Source Type (XML)");
            setDescription("Specify details for the source XML for this transformation.");
        } else {
            setTitle("Target Type (XML)");
            setDescription("Specify details for the target XML for this transformation.");
        }
        observablesManager.runAndCollect(new Runnable() {

            @Override
            public void run() {
                createPage(parent);
            }
        });

        WizardPageSupport.create(this, context);
        setErrorMessage(null);
    }

    private void createPage(Composite parent) {
        _page = new Composite(parent, SWT.NONE);
        setControl(_page);
        _page.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).numColumns(3).create());
        
        
        Group group = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group.setText("XML Type Definition");
        group.setLayout(new GridLayout(1, false)); 
        group.setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, false, 3, 2));
        
        _xmlSchemaOption = new Button(group, SWT.RADIO);
        _xmlSchemaOption.setText("XML Schema");
        _xmlSchemaOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        _xmlSchemaOption.setSelection(true);
        
        _xmlInstanceOption = new Button(group, SWT.RADIO);
        _xmlInstanceOption.setText("XML Instance Document");
        _xmlInstanceOption.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        
        _xmlSchemaOption.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.XSD);
                    model.setSourceFilePath("");
                } else {
                    model.setTargetType(ModelType.XSD);
                    model.setTargetFilePath("");
                }
                _xmlPreviewText.setText("");
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // empty
            }
        });
        
        _xmlInstanceOption.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isSourcePage()) {
                    model.setSourceType(ModelType.XML);
                    model.setSourceFilePath("");
                } else {
                    model.setTargetType(ModelType.XML);
                    model.setTargetFilePath("");
                }
                _xmlPreviewText.setText("");
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // empty
            }
        });

        // Create file path widgets
        Label label;
        if (isSourcePage()) {
            label = createLabel(_page, "Source File:", "The source XML file for the transformation.");
        } else {
            label = createLabel(_page, "Target File:", "The target XML file for the transformation.");
        }

        _xmlFileText = new Text(_page, SWT.BORDER | SWT.READ_ONLY);
        _xmlFileText.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _xmlFileText.setToolTipText(label.getToolTipText());

        final Button xmlFileBrowseButton = new Button(_page, SWT.NONE);
        xmlFileBrowseButton.setLayoutData(new GridData());
        xmlFileBrowseButton.setText("...");
        xmlFileBrowseButton.setToolTipText("Browse to specify the XML file.");

        xmlFileBrowseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                String extension = "xml";
                boolean isXML = true;
                if (_xmlInstanceOption.getSelection()) {
                    extension = "xml";
                    isXML = true;
                } else if (_xmlSchemaOption.getSelection()) {
                    extension = "xsd";
                    isXML = false;
                }
                String path = selectResourceFromWorkspace(_page.getShell(), extension);
                if (path != null) {
                    if (isSourcePage()) {
                        if (isXML) {
                            model.setSourceType(ModelType.XML);
                        } else {
                            model.setSourceType(ModelType.XSD);
                        }
                        model.setSourceFilePath(path);
                    } else {
                        if (isXML) {
                            model.setTargetType(ModelType.XML);
                        } else {
                            model.setTargetType(ModelType.XSD);
                        }
                        model.setTargetFilePath(path);
                    }
                    _xmlFileText.setText(path);
                    
                    IPath tempPath = new Path(path);
                    IFile xmlFile = model.getProject().getFile(tempPath);
                    if (xmlFile != null) {
                        try {
                            InputStream istream = xmlFile.getContents();
                            BufferedReader in = new BufferedReader(new InputStreamReader(istream));
                            String inputLine;
                            StringBuffer buffer = new StringBuffer();
                            while ((inputLine = in.readLine()) != null) {
                                buffer.append(inputLine + "\n");
                            }
                            _xmlPreviewText.setText(buffer.toString());
                        } catch (CoreException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    _xmlFileText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });
        
        Group group2 = new Group(_page, SWT.SHADOW_ETCHED_IN);
        group2.setText("XML Structure Preview");
        group2.setLayout(new FillLayout()); 
        group2.setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));

        _xmlPreviewText = new Text(group2, SWT.V_SCROLL | SWT.READ_ONLY );
        _xmlPreviewText.setBackground(_page.getBackground());

        bindControls();
        validatePage();
    }

    @Override
    public boolean isSourcePage() {
        return isSource;
    }

    @Override
    public boolean isTargetPage() {
        return !isSource;
    }

    private String selectResourceFromWorkspace(Shell shell, final String extension) {
        IJavaProject javaProject = null;
        if (getModel() != null) {
            if (getModel().getProject() != null) { 
                javaProject = JavaCore.create(getModel().getProject());
            }
        }
        ClasspathResourceSelectionDialog dialog = null;
        if (javaProject == null) {
            dialog = new ClasspathResourceSelectionDialog(shell, ResourcesPlugin.getWorkspace().getRoot(), extension);
        } else {
            dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), extension);
        }
        dialog.setTitle("Select " + extension.toUpperCase() + " From Project");
        dialog.setInitialPattern("*." + extension); //$NON-NLS-1$
        dialog.open();
        Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IResource)) {
            return null;
        }
        return ((IResource) result[0]).getProjectRelativePath().toPortableString();
    }

    private void bindControls() {

        // Bind source file path widget to UI model
        IObservableValue widgetValue = WidgetProperties.text(SWT.Modify).observe(_xmlFileText);
        IObservableValue modelValue = null;
        if (isSourcePage()) {
            modelValue = BeanProperties.value(Model.class, "sourceFilePath").observe(model);
        } else {
            modelValue = BeanProperties.value(Model.class, "targetFilePath").observe(model);
        }
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IValidator() {

            @Override
            public IStatus validate(final Object value) {
                final String path = value == null ? null : value.toString().trim();
                if (path == null || path.isEmpty()) {
                    return ValidationStatus
                            .error("A source file path must be supplied for the transformation.");
                }
                if (model.getProject().findMember(path) == null) {
                    return ValidationStatus
                            .error("Unable to find a file with the supplied path");
                }
                return ValidationStatus.ok();
            }
        });
        ControlDecorationSupport.create(context.bindValue(widgetValue, modelValue, strategy, null),
                SWT.TOP | SWT.LEFT);
    }    
}
