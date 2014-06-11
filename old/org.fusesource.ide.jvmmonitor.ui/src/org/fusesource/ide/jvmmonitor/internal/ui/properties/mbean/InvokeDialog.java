/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.JMException;
import javax.management.JMRuntimeException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.PropertiesColumn;


/**
 * The invoke dialog.
 */
public class InvokeDialog extends Dialog {

    /** The dialog height. */
    private static final int DIALOG_HEIGHT = 500;

    /** The dialog width. */
    private static final int DIALOG_WIDTH = 450;

    /** The MBean operation info. */
    private MBeanOperationInfo info;

    /** The active JVM. */
    private IActiveJvm jvm;

    /** The object name. */
    private ObjectName objectName;

    /** The controls. */
    Map<MBeanParameterInfo, Control> controls;

    /** The message label. */
    Label messageLabel;

    /** The return value viewer. */
    TreeViewer returnValueViewer;

    /** The copy action. */
    CopyAction copyAction;

    /**
     * The constructor.
     * 
     * @param shell
     *            The shell
     * @param jvm
     *            The JVM
     * @param objectName
     *            The object name
     * @param info
     *            The MBean operation info
     */
    protected InvokeDialog(Shell shell, IActiveJvm jvm, ObjectName objectName,
            MBeanOperationInfo info) {
        super(shell);

        this.objectName = objectName;
        this.jvm = jvm;
        this.info = info;

        controls = new HashMap<MBeanParameterInfo, Control>();
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(Messages.invokeDialogTitle);
        getShell().setSize(Math.max(DIALOG_WIDTH, getShell().getSize().x),
                DIALOG_HEIGHT);
        validate();
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.INVOKE_DIALOG);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite panel = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        layout.marginLeft = 5;
        layout.marginRight = 5;
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));

        createOperationGroup(panel);
        createParameterGroup(panel);
        createReturnValueViewer(panel);
        createMessageArea(panel);

        applyDialogFont(panel);

        return panel;
    }

    /*
     * @see Dialog#createButtonsForButtonBar(Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button invokeButton = createButton(parent, IDialogConstants.CLIENT_ID,
                Messages.invokeButtonLabel, true);
        invokeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                invoke();
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CLOSE_LABEL, false);
    }

    /*
     * @see Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Creates the operation group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createOperationGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.operationGroupLabel);
        GridLayout layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label methodName = new Label(group, SWT.NONE);
        methodName.setText(getMethodSignature());
    }

    /**
     * Creates the parameter group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createParameterGroup(Composite parent) {
        if (info.getSignature().length == 0) {
            return;
        }

        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.parametersGroupLabel);
        GridLayout layout = new GridLayout(2, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        for (MBeanParameterInfo signature : info.getSignature()) {
            Label label = new Label(group, SWT.NONE);
            String type = signature.getType();
            if (type.startsWith("[")) { //$NON-NLS-1$
                type = Signature.toString(type);
                label.setToolTipText(Messages.enterCommaSeparatedValuesToolTip);
            }
            int index = type.lastIndexOf('.');
            if (index > 0) {
                type = type.substring(index + 1);
            }
            label.setText(type + ":"); //$NON-NLS-1$

            if (Boolean.class.getSimpleName().equalsIgnoreCase(type)) {
                Combo combo = new Combo(group, SWT.READ_ONLY);
                combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                combo.add(Boolean.TRUE.toString());
                combo.add(Boolean.FALSE.toString());
                combo.select(0);
                controls.put(signature, combo);
            } else {
                Text text = new Text(group, SWT.BORDER);
                text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                text.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        validate();
                    }
                });
                controls.put(signature, text);
            }
        }
    }

    /**
     * Creates the return value viewer.
     * 
     * @param parent
     *            The parent composite
     */
    private void createReturnValueViewer(Composite parent) {
        if (info.getReturnType().equals("void")) { //$NON-NLS-1$
            return;
        }

        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.returnValueLabel);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        returnValueViewer = new TreeViewer(composite, SWT.MULTI
                | SWT.FULL_SELECTION);
        configureTree(returnValueViewer.getTree());

        returnValueViewer.setContentProvider(new ReturnValueContentProvider());
        returnValueViewer.setLabelProvider(new ReturnValueLabelProvider());

        copyAction = new CopyAction();
        copyAction.setActionDefinitionId(null);
        returnValueViewer.addSelectionChangedListener(copyAction);
    }

    /**
     * Configures the tree.
     * 
     * @param tree
     *            The tree
     */
    private void configureTree(Tree tree) {
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        for (PropertiesColumn column : PropertiesColumn.values()) {
            TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
            treeColumn.setText(column.label);
            treeColumn.setWidth(column.defalutWidth);
            treeColumn.setAlignment(column.alignment);
            treeColumn.setToolTipText(column.toolTip);
        }

        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(copyAction);
            }
        });
        Menu menu = menuMgr.createContextMenu(tree);
        tree.setMenu(menu);
    }

    /**
     * Creates the return group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createMessageArea(Composite parent) {
        messageLabel = new Label(parent, SWT.NONE);
        messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Validates the specified text.
     */
    void validate() {
        boolean enableButton = true;
        for (Entry<MBeanParameterInfo, Control> entry : controls.entrySet()) {
            Control control = entry.getValue();
            if (!(control instanceof Text)) {
                continue;
            }

            String type = entry.getKey().getType();
            String text = ((Text) control).getText();
            if (!validateNumber(type, text)) {
                enableButton = false;
                break;
            }
        }
        Button invokeButton = getButton(IDialogConstants.CLIENT_ID);
        if (invokeButton != null) {
            invokeButton.setEnabled(enableButton);
        }
    }

    /**
     * Validates the given number.
     * 
     * @param type
     *            The type
     * @param string
     *            The string
     * @return True if the given number is valid
     */
    private boolean validateNumber(String type, String string) {
        try {
            if ("byte".equals(type)) { //$NON-NLS-1$
                Byte.valueOf(string);
            } else if ("double".equals(type)) { //$NON-NLS-1$
                Double.valueOf(string);
            } else if ("float".equals(type)) { //$NON-NLS-1$
                Float.valueOf(string);
            } else if ("int".equals(type)) { //$NON-NLS-1$
                Integer.valueOf(string);
            } else if ("long".equals(type)) { //$NON-NLS-1$
                Long.valueOf(string);
            } else if ("short".equals(type)) { //$NON-NLS-1$
                Short.valueOf(string);
            } else if (type.startsWith("[")) { //$NON-NLS-1$
                return validateNumberArray(type, string);
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Validates the given number array.
     * 
     * @param arrayType
     *            The type (e.g. [J)
     * @param string
     *            The string (e.g. 1,2,3)
     * @return True if the given number is valid
     */
    private boolean validateNumberArray(String arrayType, String string) {
        String readableString = Signature.toString(arrayType);
        String type = readableString.substring(0, readableString.indexOf("[")); //$NON-NLS-1$
        String[] elements = string.split(","); //$NON-NLS-1$
        for (String element : elements) {
            if (!validateNumber(type, element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the message.
     * 
     * @param message
     *            The message
     */
    void setMessage(final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * Gets the method signature.
     * 
     * @return The method signature
     */
    private String getMethodSignature() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(info.getName());
        buffer.append("("); //$NON-NLS-1$
        StringBuffer paramBuffer = new StringBuffer();
        for (MBeanParameterInfo parameterInfo : info.getSignature()) {
            if (paramBuffer.length() != 0) {
                paramBuffer.append(", "); //$NON-NLS-1$
            }
            String param = parameterInfo.getType();
            if (param.startsWith("[")) { //$NON-NLS-1$
                param = Signature.toString(param);
            }
            int index = param.lastIndexOf('.');
            if (index > 0) {
                param = param.substring(index + 1);
            }
            paramBuffer.append(param);
        }
        buffer.append(paramBuffer);
        buffer.append(")"); //$NON-NLS-1$
        return buffer.toString();
    }

    /**
     * Invokes the MBean operation with job.
     */
    void invoke() {
        if (objectName == null || jvm == null || info == null) {
            return;
        }
        new Job(Messages.invokeMBeanOperationJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    doInvoke();
                } catch (JMException e) {
                    setMessage(NLS.bind(Messages.mBeanOperationFailedLabel,
                            new Date()));
                    return Status.CANCEL_STATUS;
                } catch (IOException e) {
                    setMessage(NLS.bind(Messages.mBeanOperationFailedLabel,
                            new Date()));
                    return Status.CANCEL_STATUS;
                } catch (JMRuntimeException e) {
                    setMessage(NLS.bind(Messages.mBeanOperationFailedLabel,
                            new Date()));
                    return Status.CANCEL_STATUS;
                }
                setMessage(NLS.bind(Messages.mBeanOperationSucceededLabel,
                        new Date()));
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    /**
     * Invokes the MBean operation.
     * 
     * @throws JMException
     * @throws IOException
     */
    void doInvoke() throws JMException, IOException {
        String operationName = info.getName();
        String[] signature = getSignature();
        Object[] params = getParams();

        final Object result;
        try {
            result = jvm.getMBeanServer().invoke(objectName, operationName,
                    params, signature);
        } catch (JvmCoreException e) {
            Activator.log(IStatus.ERROR, Messages.mBeanOperationFailedMsg, e);
            return;
        }
        if (result == null) {
            return;
        }

        final AttributeNode attributeNode = new AttributeNode(result.getClass()
                .getCanonicalName(), null, result);
        new AttributeParser().refreshAttribute(attributeNode);

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                Object input;
                if (attributeNode.hasChildren()) {
                    input = attributeNode.getChildren().toArray(
                            new AttributeNode[0]);
                } else {
                    input = new AttributeNode[] { attributeNode };
                }
                returnValueViewer.setInput(input);
                returnValueViewer.refresh();
            }
        });
    }

    /**
     * Gets the parameters.
     * 
     * @return The parameters
     */
    private Object[] getParams() {
        List<Object> params = new ArrayList<Object>();
        for (final MBeanParameterInfo signature : info.getSignature()) {
            String text = getText(signature);
            Object object;

            String type = signature.getType();
            if ("int".equals(type)) { //$NON-NLS-1$
                object = Integer.valueOf(text);
            } else if ("long".equals(type)) { //$NON-NLS-1$
                object = Long.valueOf(text);
            } else if ("boolean".equals(type)) { //$NON-NLS-1$
                object = Boolean.valueOf(text);
            } else if ("java.lang.String".equals(type)) { //$NON-NLS-1$
                object = String.valueOf(text);
            } else if ("[J".equals(type)) { //$NON-NLS-1$
                String[] elements = text.split(","); //$NON-NLS-1$
                long[] objects = new long[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    objects[i] = Long.parseLong(elements[i]);
                }
                object = objects;
            } else {
                throw new IllegalStateException("unknown parameter type"); //$NON-NLS-1$
            }

            params.add(object);
        }
        return params.toArray(new Object[0]);
    }

    /**
     * Gets the text corresponding to the given signature.
     * 
     * @param signature
     *            The signature
     * @return The text
     */
    private String getText(final MBeanParameterInfo signature) {
        final String[] text = new String[1];
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                Control control = controls.get(signature);
                if (control instanceof Text) {
                    text[0] = ((Text) control).getText();
                } else if (control instanceof Combo) {
                    text[0] = ((Combo) control).getText();
                }
            }
        });
        return text[0];
    }

    /**
     * Gets the signature.
     * 
     * @return The signature
     */
    private String[] getSignature() {
        List<String> result = new ArrayList<String>();
        for (MBeanParameterInfo signature : info.getSignature()) {
            result.add(signature.getType());
        }
        return result.toArray(new String[0]);
    }

    /**
     * The content provider for return value of MBean operation.
     */
    private static class ReturnValueContentProvider implements
            ITreeContentProvider {

        /**
         * The constructor.
         */
        public ReturnValueContentProvider() {
            // do nothing
        }

        /*
         * @see IContentProvider#inputChanged(Viewer, Object, Object)
         */
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }

        /*
         * @see ITreeContentProvider#getElements(Object)
         */
        @Override
        public Object[] getElements(Object input) {
            if (input instanceof AttributeNode[]) {
                return (AttributeNode[]) input;
            }
            return null;
        }

        /*
         * @see ITreeContentProvider#getChildren(Object)
         */
        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof AttributeNode) {
                return ((AttributeNode) parentElement).getChildren().toArray(
                        new AttributeNode[0]);
            }
            return null;
        }

        /*
         * @see ITreeContentProvider#hasChildren(Object)
         */
        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof AttributeNode) {
                return ((AttributeNode) element).hasChildren();
            }
            return false;
        }

        /*
         * @see ITreeContentProvider#getParent(Object)
         */
        @Override
        public Object getParent(Object element) {
            return null;
        }

        /*
         * @see IContentProvider#dispose()
         */
        @Override
        public void dispose() {
            // do nothing
        }
    }

    /**
     * The label provider for return value of MBean operation.
     */
    private static class ReturnValueLabelProvider extends LabelProvider
            implements ITableLabelProvider {

        /**
         * The constructor.
         */
        public ReturnValueLabelProvider() {
            // do nothing
        }

        /*
         * @see ITableLabelProvider#getColumnText(Object, int)
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
            AttributeNode attribute = (AttributeNode) element;
            if (columnIndex == 0) {
                return attribute.getName();
            } else if (columnIndex == 1) {
                Object value = attribute.getValue();
                if (value != null && attribute.isValidLeaf()) {
                    return value.toString();
                }
            }
            return ""; //$NON-NLS-1$
        }

        /*
         * @see ITableLabelProvider#getColumnImage(Object, int)
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
}
