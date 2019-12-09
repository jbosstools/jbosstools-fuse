/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids, sdavids@gmx.de - bug 38507
 *     Sebastian Davids, sdavids@gmx.de - 113998 [JUnit] New Test Case Wizard: Class Under Test Dialog -- allow Enums
 *     Kris De Volder <kris.de.volder@gmail.com> - Allow changing the default superclass in NewCamelTestWizardPageOne - https://bugs.eclipse.org/312204
 *******************************************************************************/
package org.fusesource.ide.branding.wizards;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.junit.BasicElementLabels;
import org.eclipse.jdt.internal.junit.JUnitCorePlugin;
import org.eclipse.jdt.internal.junit.Messages;
import org.eclipse.jdt.internal.junit.buildpath.BuildPathSupport;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.junit.util.CoreTestSearchEngine;
import org.eclipse.jdt.internal.junit.util.JUnitStatus;
import org.eclipse.jdt.internal.junit.util.JUnitStubUtility;
import org.eclipse.jdt.internal.junit.util.JUnitStubUtility.GenStubSettings;
import org.eclipse.jdt.internal.junit.util.LayoutUtil;
import org.eclipse.jdt.internal.junit.wizards.MethodStubsSelectionButtonGroup;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.ControlContentAssistHelper;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaTypeCompletionProcessor;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.branding.RiderHelpContextIds;
import org.fusesource.ide.branding.wizards.NewCamelTestWizardPageTwo.EndpointMaps;
import org.fusesource.ide.foundation.core.contenttype.CamelXmlMatchingStrategy;
import org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.ResourceModelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.util.URIs;
import org.fusesource.ide.foundation.ui.util.Widgets;

/**
 * Creates a new test case based on the currently selected Camel XML file
 */
@SuppressWarnings("restriction")
public class NewCamelTestWizardPageOne extends NewTypeWizardPage {
	private static final String BUILD_PATH_BLOCK = "block_until_buildpath_applied"; //$NON-NLS-1$

	private static final String BUILD_PATH_KEY_ADD_ENTRY = "add_classpath_entry"; //$NON-NLS-1$

	private static final String BUILD_PATH_PAGE_ID = "org.eclipse.jdt.ui.propertyPages.BuildPathsPropertyPage"; //$NON-NLS-1$

	public static final String PAGE_NAME = "NewCamelTestWizardPage"; //$NON-NLS-1$

	/** Field ID of the class under test field. */
	public static final  String CLASS_UNDER_TEST = PAGE_NAME + ".classundertest"; //$NON-NLS-1$
	private static final String COMPLIANCE_PAGE_ID = "org.eclipse.jdt.ui.propertyPages.CompliancePreferencePage"; //$NON-NLS-1$

	private static final int IDX_SETUP_CLASS = 0;
	private static final int IDX_TEARDOWN_CLASS = 1;
	private static final int IDX_SETUP = 2;
	private static final int IDX_TEARDOWN = 3;
	private static final int IDX_CONSTRUCTOR = 4;

	/**
	 * Field ID of the Junit4 toggle
	 * 
	 * @since 3.2
	 */
	public static final String JUNIT4TOGGLE = PAGE_NAME + ".junit4toggle"; //$NON-NLS-1$

	private static final String KEY_NO_LINK = "PropertyAndPreferencePage.nolink"; //$NON-NLS-1$
	private static final String PREFIX = "test"; //$NON-NLS-1$

	private static final String STORE_CONSTRUCTOR = PAGE_NAME + ".USE_CONSTRUCTOR"; //$NON-NLS-1$
	private static final String STORE_SETUP = PAGE_NAME + ".USE_SETUP"; //$NON-NLS-1$
	private static final String STORE_SETUP_CLASS = PAGE_NAME + ".USE_SETUPCLASS"; //$NON-NLS-1$
	private static final String STORE_TEARDOWN = PAGE_NAME + ".USE_TEARDOWN"; //$NON-NLS-1$
	private static final String STORE_TEARDOWN_CLASS = PAGE_NAME + ".USE_TEARDOWNCLASS"; //$NON-NLS-1$

	private XmlMatchingStrategySupport camelXmlMatcher = new CamelXmlMatchingStrategy();

	private Label fImage;

	private boolean fIsJunit4;
	private boolean fIsJunit4Enabled;

	private IStatus fJunit4Status; // status
	private Button fJUnit4Toggle;

	private Link fLink;
	private MethodStubsSelectionButtonGroup fMethodStubsButtons;

	private NewCamelTestWizardPageTwo fPage2;
	private JavaTypeCompletionProcessor fXmlFileToTestCompletionProcessor;
	private IFile fXmlFileUnderTest; // resolved model, can be null
	private Button fXmlFileUnderTestButton;
	private Text fXmlFileUnderTestControl; // control
	private IStatus fXmlFileUnderTestStatus; // status

	private String fXmlFileUnderTestText; // model

	private NewCamelTestWizard wizard;

	private IContainer resourceContainer;

	private FileFilter folderFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	/**
	 * Creates a new <code>NewTestCaseCreationWizardPage</code>.
	 * 
	 * @param page2
	 *            The second page
	 * 
	 * @since 3.1
	 */
	public NewCamelTestWizardPageOne(NewCamelTestWizard wizard, NewCamelTestWizardPageTwo page2) {
		super(true, PAGE_NAME);
		this.wizard = wizard;
		fPage2 = page2;

		setTitle(WizardMessages.NewCamelTestWizardPageOne_title);
		setDescription(WizardMessages.NewCamelTestWizardPageOne_description);

		String[] buttonNames = new String[] {
				/* IDX_SETUP_CLASS */WizardMessages.NewCamelTestWizardPageOne_methodStub_setUpBeforeClass,
				/* IDX_TEARDOWN_CLASS */WizardMessages.NewCamelTestWizardPageOne_methodStub_tearDownAfterClass,
				/* IDX_SETUP */WizardMessages.NewCamelTestWizardPageOne_methodStub_setUp,
				/* IDX_TEARDOWN */WizardMessages.NewCamelTestWizardPageOne_methodStub_tearDown,
				/* IDX_CONSTRUCTOR */WizardMessages.NewCamelTestWizardPageOne_methodStub_constructor };
		enableCommentControl(true);

		fMethodStubsButtons = new MethodStubsSelectionButtonGroup(SWT.CHECK, buttonNames, 2);
		fMethodStubsButtons.setLabelText(WizardMessages.NewCamelTestWizardPageOne_method_Stub_label);

		fXmlFileToTestCompletionProcessor = new JavaTypeCompletionProcessor(false, false, true);

		fXmlFileUnderTestStatus = new JUnitStatus();

		fXmlFileUnderTestText = ""; //$NON-NLS-1$

		fJunit4Status = new JUnitStatus();
		fIsJunit4 = false;
	}

	private void appendMockEndpointField(IType type, String key, String value, int idx) throws JavaModelException {
		String delimiter = getLineDelimiter();
		String prefix = (idx > 0) ? "" : delimiter + delimiter + delimiter + "// "
				+ WizardMessages.NewCamelTestWizardPageOne_mock_endpoint_fields + delimiter;

		String uri = getMockEndpointUri(key);
		if (URIs.isMockEndpointURI(value)) {
			// lets consume directly from mock endpoints if they are the outputs in the real route to test
			// typically users would never do this - but we use mocks in some archetypes
			uri = value;
		}
		type.createField(prefix + "@EndpointInject(uri = \"" + uri + "\")" + delimiter
				+ "protected MockEndpoint " + getMockEndpointVariableName(key) + ";", null, false, null);
	}

	private void appendProducerTemplateField(IType type, String key, String value, int idx, EndpointMaps endpointMaps)
			throws JavaModelException {
		String delimiter = getLineDelimiter();
		String prefix = (idx > 0) ? "" : delimiter + delimiter + "// "
				+ WizardMessages.NewCamelTestWizardPageOne_producer_template_fields + delimiter;

		type.createField(prefix + "@Produce(uri = \"" + value + "\")" + delimiter + "protected ProducerTemplate "
				+ endpointMaps.getInputEndpointVariableName(key) + ";", null, false, null);
	}

	private void appendTestMethodBody(StringBuilder buffer, ICompilationUnit compilationUnit, EndpointMaps endpointMaps)
			throws CoreException {
		endpointMaps.getInputEndpoints();
		Map<String, String> outputEndpoints = new HashMap<>(endpointMaps.getOutputEndpoints());
		// lets remove all output endpoints which are already mocks as we can assert directly on those
		Iterator<Entry<String, String>> iter = outputEndpoints.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String value = entry.getValue();
			if (URIs.isMockEndpointURI(value)) {
				iter.remove();
			}
		}

		String inputEndpoint = endpointMaps.getInputEndpoint();
		final String delimiter = getLineDelimiter();
		buffer.append('{').append(delimiter);

		String mockEndpoint = null;
		if (!outputEndpoints.isEmpty()) {
			// consume from the output endpoints to the mock endpoints
			buffer.append("// ").append(WizardMessages.NewCamelTestWizardPageOne_consume_output_endpoints_to_mocks).append(delimiter); //$NON-NLS-1$
			buffer.append("context.addRoutes(new RouteBuilder() {").append(delimiter);
			buffer.append("    @Override").append(delimiter);
			buffer.append("    public void configure() throws Exception {").append(delimiter);

			for (Map.Entry<String, String> entry : outputEndpoints.entrySet()) {
				String mockEndpointVariableName = getMockEndpointVariableName(entry.getKey().toString());
				if (mockEndpoint == null) {
					mockEndpoint = mockEndpointVariableName;
				}
				buffer.append("        from(\"" + entry.getValue() + "\").to(" + mockEndpointVariableName + ");")
				.append(delimiter);
			}
			buffer.append("    }").append(delimiter);
			buffer.append("});").append(delimiter);
			buffer.append(delimiter);
		}

		buffer.append(delimiter);
		buffer.append("// ").append(WizardMessages.NewCamelTestWizardPageOne_not_implemented_define_expectations).append(delimiter).append(delimiter); //$NON-NLS-1$
		if (mockEndpoint != null && inputEndpoint != null) {
			buffer.append("// TODO Ensure expectations make sense for the route(s) we're testing").append(delimiter);
			buffer.append(mockEndpoint + ".expectedBodiesReceivedInAnyOrder(expectedBodies);").append(delimiter); //$NON-NLS-1$
		} else {
            buffer.append("// For now, let's just wait for some messages");

			if (mockEndpoint == null) {
				Object[] array = endpointMaps.getOutputEndpoints().keySet().toArray();
				if (array != null && array.length > 0) {
					mockEndpoint = getMockEndpointVariableName(array[0].toString());
				}
			}
			if (mockEndpoint != null) {
				buffer.append(delimiter).append(mockEndpoint + ".expectedMessageCount(2);").append(delimiter); //$NON-NLS-1$
			}
			buffer.append("// TODO Add some expectations here");

		}
		buffer.append(delimiter);

		if (inputEndpoint != null) {
			buffer.append("// ").append(WizardMessages.NewCamelTestWizardPageOne_not_implemented_sending_messages).append(delimiter); //$NON-NLS-1$
			buffer.append("for (Object expectedBody : expectedBodies) {").append(delimiter); //$NON-NLS-1$
			buffer.append("    " + inputEndpoint + ".sendBody(expectedBody);").append(delimiter); //$NON-NLS-1$
			buffer.append('}').append(delimiter);
		}

		buffer.append(delimiter);
		buffer.append("// Validate our expectations").append(delimiter);
		buffer.append("assertMockEndpointsSatisfied();").append(delimiter);
		buffer.append('}').append(delimiter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getXmlFileUnderTest() != null;
	}

	private IFile chooseXmlFileToTestType() {
		FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(getShell(), false,
				resourceContainer, IResource.FILE) {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog#fillContentProvider(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider, org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter, org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected void fillContentProvider(final AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
					IProgressMonitor progressMonitor) throws CoreException {
				AbstractContentProvider filteringContentProvider = new AbstractContentProvider() {
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider#add(java.lang.Object, org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter)
					 */
					@Override
					public void add(Object item, ItemsFilter itemsFilter) {
						if (itemsFilter.matchItem(item)) {
							if (item instanceof IFile) {
								IFile ifile = (IFile) item;
								boolean matches = camelXmlMatcher.matches(ifile);
								Activator.getLogger().debug("File " + ifile + " matches CamelXML: " + matches);
								if (matches) {
									contentProvider.add(item, itemsFilter);
								}
							}
						}
					}
				};
				super.fillContentProvider(filteringContentProvider, itemsFilter, progressMonitor);
			}

		};

		dialog.setInitialPattern("*.xml", FilteredItemsSelectionDialog.FULL_SELECTION);
		dialog.setTitle(WizardMessages.NewCamelTestWizardPageOne_class_to_test_dialog_title);
		dialog.setMessage(WizardMessages.NewCamelTestWizardPageOne_class_to_test_dialog_message);

		if (dialog.open() == Window.OK) {
			Object[] resultArray = dialog.getResult();
			if (resultArray != null && resultArray.length > 0) {
				Object firstSelection = resultArray[0];
				if (firstSelection instanceof IFile) {
					return (IFile) firstSelection;
				}
			}
		}
		return null;
	}

	/**
	 * Creates the controls for the JUnit 4 toggle control. Expects a
	 * <code>GridLayout</code> with at least 3 columns.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 * 
	 * @since 3.2
	 */
	protected void createBuildPathConfigureControls(Composite composite, int nColumns) {
		Composite inner = new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, nColumns, 1));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		inner.setLayout(layout);

		fImage = new Label(inner, SWT.NONE);
		fImage.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
		fImage.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));

		fLink = new Link(inner, SWT.WRAP);
		fLink.setText("\n\n"); //$NON-NLS-1$
		fLink.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				performBuildpathConfiguration(e.text);
			}
		});
		GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
		gd.widthHint = convertWidthInCharsToPixels(60);
		fLink.setLayoutData(gd);
		updateBuildPathMessage();
	}

	private void createConstructor(IType type, ImportsManager imports) throws CoreException {
		ITypeHierarchy typeHierarchy;
		IType[] superTypes;
		String content;
		IMethod methodTemplate = null;
		if (type.exists()) {
			typeHierarchy = type.newSupertypeHierarchy(null);
			superTypes = typeHierarchy.getAllSuperclasses(type);
			for (IType superType : superTypes) {
				if (superType.exists()) {
					IMethod constrMethod = superType.getMethod(superType.getElementName(),
							new String[] { "Ljava.lang.String;" }); //$NON-NLS-1$
					if (constrMethod.exists() && constrMethod.isConstructor()) {
						methodTemplate = constrMethod;
						break;
					}
				}
			}
		}
		GenStubSettings settings = JUnitStubUtility.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments = isAddComments();

		if (methodTemplate != null) {
			settings.callSuper = true;
			settings.methodOverwrites = true;
			content = JUnitStubUtility.genStub(type.getCompilationUnit(), getTypeName(), methodTemplate, settings,
					null, imports);
		} else {
			final String delimiter = getLineDelimiter();
			StringBuilder buffer = new StringBuilder(32);
			buffer.append("public "); //$NON-NLS-1$
			buffer.append(getTypeName());
			buffer.append('(');
			if (!isJUnit4()) {
				buffer.append(imports.addImport("java.lang.String")).append(" name"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			buffer.append(") {"); //$NON-NLS-1$
			buffer.append(delimiter);
			if (!isJUnit4()) {
				buffer.append("super(name);").append(delimiter); //$NON-NLS-1$
			}
			buffer.append('}');
			buffer.append(delimiter);
			content = buffer.toString();
		}
		type.createMethod(content, null, true, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);
		// createJUnit4Controls(composite, nColumns);
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createSeparator(composite, nColumns);
		createXmlFileUnderTestControls(composite, nColumns);
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		// createSuperClassControls(composite, nColumns);
		createMethodStubSelectionControls(composite, nColumns);
		createCommentControls(composite, nColumns);
		createBuildPathConfigureControls(composite, nColumns);

		setControl(composite);

		// set default and focus
		setTypeNameFromXmlFile(fXmlFileUnderTest);
		/*
		 * String classUnderTest= getXmlFileUnderTestText(); if
		 * (classUnderTest.length() > 0) {
		 * setTypeName(Signature.getSimpleName(classUnderTest)+TEST_SUFFIX,
		 * true); }
		 */
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(composite, RiderHelpContextIds.NEW_CAMEL_TESTCASE_WIZARD_PAGE);

		setFocus();
	}

	/**
	 * Creates the controls for the JUnit 4 toggle control. Expects a
	 * <code>GridLayout</code> with at least 3 columns.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 * 
	 * @since 3.2
	 */
	protected void createJUnit4Controls(Composite composite, int nColumns) {
		Composite inner = new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, nColumns, 1));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		SelectionAdapter listener = new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button) e.widget).getSelection();
				internalSetJUnit4(isSelected);
			}
		};

		Button junti3Toggle = new Button(inner, SWT.RADIO);
		junti3Toggle.setText(WizardMessages.NewCamelTestWizardPageOne_junit3_radio_label);
		junti3Toggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		junti3Toggle.setSelection(!fIsJunit4);
		junti3Toggle.setEnabled(fIsJunit4Enabled);

		fJUnit4Toggle = new Button(inner, SWT.RADIO);
		fJUnit4Toggle.setText(WizardMessages.NewCamelTestWizardPageOne_junit4_radio_label);
		fJUnit4Toggle.setSelection(fIsJunit4);
		fJUnit4Toggle.setEnabled(fIsJunit4Enabled);
		fJUnit4Toggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		fJUnit4Toggle.addSelectionListener(listener);
	}

	/**
	 * Creates the controls for the method stub selection buttons. Expects a
	 * <code>GridLayout</code> with at least 3 columns.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 */
	protected void createMethodStubSelectionControls(Composite composite, int nColumns) {
		LayoutUtil.setHorizontalSpan(fMethodStubsButtons.getLabelControl(composite), nColumns);
		LayoutUtil.createEmptySpace(composite, 1);
		LayoutUtil.setHorizontalSpan(fMethodStubsButtons.getSelectionButtonsGroup(composite), nColumns - 1);
	}

	private void createSetUp(IType type, ImportsManager imports) throws CoreException {
		createSetupStubs(type, "setUp", false, "org.junit.Before", imports); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void createSetUpClass(IType type, ImportsManager imports) throws CoreException {
		createSetupStubs(type, "setUpBeforeClass", true, "org.junit.BeforeClass", imports); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void createSetupStubs(IType type, String methodName, boolean isStatic, String annotationType,
			ImportsManager imports) throws CoreException {
		String content;
		IMethod methodTemplate = findInHierarchy(type, methodName);
		String annotation = null;
		if (isJUnit4()) {
			annotation = '@' + imports.addImport(annotationType);
		}

		GenStubSettings settings = JUnitStubUtility.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments = isAddComments();

		if (methodTemplate != null) {
			settings.callSuper = !isStatic; // Don't use super for static methods...
			settings.methodOverwrites = true;
			content = JUnitStubUtility.genStub(type.getCompilationUnit(), getTypeName(), methodTemplate, settings,
					annotation, imports);
		} else {
			final String delimiter = getLineDelimiter();
			StringBuilder buffer = new StringBuilder();
			if (settings.createComments) {
				String[] excSignature = { Signature.createTypeSignature("java.lang.Exception", true) }; //$NON-NLS-1$
				String comment = CodeGeneration.getMethodComment(type.getCompilationUnit(), type.getElementName(),
						methodName, new String[0], excSignature, Signature.SIG_VOID, null, delimiter);
				if (comment != null) {
					buffer.append(comment);
				}
			}
			if (annotation != null) {
				buffer.append(annotation).append(delimiter);
			}

			if (isJUnit4()) {
				buffer.append("public "); //$NON-NLS-1$
			} else {
				buffer.append("protected "); //$NON-NLS-1$
			}
			if (isStatic) {
				buffer.append("static "); //$NON-NLS-1$
			}
			buffer.append("void "); //$NON-NLS-1$
			buffer.append(methodName);
			buffer.append("() throws "); //$NON-NLS-1$
			buffer.append(imports.addImport("java.lang.Exception")); //$NON-NLS-1$
			buffer.append(" {}"); //$NON-NLS-1$
			buffer.append(delimiter);
			content = buffer.toString();
		}
		type.createMethod(content, null, false, null);
	}

	private void createTearDown(IType type, ImportsManager imports) throws CoreException {
		createSetupStubs(type, "tearDown", false, "org.junit.After", imports); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void createTearDownClass(IType type, ImportsManager imports) throws CoreException {
		createSetupStubs(type, "tearDownAfterClass", true, "org.junit.AfterClass", imports); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#createTypeMembers(org.eclipse.jdt.core.IType, org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		if (fMethodStubsButtons.isSelected(IDX_CONSTRUCTOR)) {
			createConstructor(type, imports);
		}

		if (fMethodStubsButtons.isSelected(IDX_SETUP_CLASS)) {
			createSetUpClass(type, imports);
		}

		if (fMethodStubsButtons.isSelected(IDX_TEARDOWN_CLASS)) {
			createTearDownClass(type, imports);
		}

		if (fMethodStubsButtons.isSelected(IDX_SETUP)) {
			createSetUp(type, imports);
		}

		if (fMethodStubsButtons.isSelected(IDX_TEARDOWN)) {
			createTearDown(type, imports);
		}

		imports.addImport(getSuperClass());

		if (fXmlFileUnderTest != null) {
			createXmlFileTestMethodsAndFields(type, imports, fXmlFileUnderTest);

			if (wizard.isBlueprintFile(fXmlFileUnderTest.getRawLocation().toOSString())) {
				createXmlFileBlueprintDescriptor(type, imports, fXmlFileUnderTest);
			} else {
				createXmlFileApplicationContextMethod(type, imports, fXmlFileUnderTest);
			}
		}

		if (isJUnit4()) {
			imports.addStaticImport("org.junit.Assert", "*", false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void createXmlFileBlueprintDescriptor(IType type, ImportsManager imports, IFile camelXmlFile)
			throws CoreException {
		StringBuilder builder = new StringBuilder();

		String delimiter = getLineDelimiter();
		builder.append("@Override").append(delimiter);
		builder.append("protected String getBlueprintDescriptor() {");
		builder.append(delimiter);
		builder.append("return \"" + ResourceModelUtils.getRelativeFileUri(camelXmlFile) + "\";");
		builder.append(delimiter);
		builder.append("}");
		builder.append(delimiter);
		type.createMethod(builder.toString(), null, false, null);
	}

	private void createXmlFileApplicationContextMethod(IType type, ImportsManager imports, IFile camelXmlFile)
			throws CoreException {
		imports.addImport("org.springframework.context.support.ClassPathXmlApplicationContext");
		StringBuilder builder = new StringBuilder();

		String delimiter = getLineDelimiter();
		builder.append("@Override").append(delimiter);
		builder.append("protected ClassPathXmlApplicationContext createApplicationContext() {");
		builder.append(delimiter);
		builder.append("return new ClassPathXmlApplicationContext(\"" + ResourceModelUtils.getRelativeFileUri(camelXmlFile)
				+ "\");");
		builder.append(delimiter);
		builder.append("}");
		builder.append(delimiter);
		type.createMethod(builder.toString(), null, false, null);
	}

	private void createXmlFileTestMethodsAndFields(IType type, ImportsManager imports, IFile camelXmlFile)
			throws CoreException {
		// add a basic test method
		String testName = PREFIX + "CamelRoute";
		String delimiter = getLineDelimiter();

		StringBuilder builder = new StringBuilder();
		if (isJUnit4()) {
			builder.append('@').append(imports.addImport(JUnitCorePlugin.JUNIT4_ANNOTATION_NAME)).append(delimiter);
		}

		builder.append("public ");//$NON-NLS-1$
		if (fPage2.getCreateFinalMethodStubsButtonSelection()) {
			builder.append("final "); //$NON-NLS-1$
		}
		builder.append("void ");//$NON-NLS-1$
		builder.append(testName);
		builder.append("() throws Exception");//$NON-NLS-1$

		EndpointMaps endpointMaps = fPage2.getCheckedEndpointMaps();

		appendTestMethodBody(builder, type.getCompilationUnit(), endpointMaps);
		type.createMethod(builder.toString(), null, false, null);

		// now lets create the test fields

		// TODO configure how many bodies to use
		if (endpointMaps.getInputEndpoint() != null) {
			type.createField(delimiter +
					"// TODO Create test message bodies that work for the route(s) being tested" + delimiter +
					"// Expected message bodies" + delimiter
					+ "protected Object[] expectedBodies = {\"<something id='1'>expectedBody1</something>\","
					+ delimiter + "    \"<something id='2'>expectedBody2</something>\" };", null, false, null);
		}

		Map<String, String> inputEndpoints = endpointMaps.getInputEndpoints();
		Map<String, String> outputEndpoints = endpointMaps.getOutputEndpoints();

		int idx = 0;
		for (Map.Entry<String, String> entry : inputEndpoints.entrySet()) {
			appendProducerTemplateField(type, entry.getKey(), entry.getValue(), idx++, endpointMaps);
		}

		idx = 0;
		for (Map.Entry<String, String> entry : outputEndpoints.entrySet()) {
			appendMockEndpointField(type, entry.getKey(), entry.getValue(), idx++);
		}

		imports.addImport("org.apache.camel.EndpointInject");
		imports.addImport("org.apache.camel.Produce");
		imports.addImport("org.apache.camel.ProducerTemplate");
		imports.addImport("org.apache.camel.builder.RouteBuilder");
		imports.addImport("org.apache.camel.component.mock.MockEndpoint");
	}

	/**
	 * Creates the controls for the 'Xml file under test' field. Expects a
	 * <code>GridLayout</code> with at least 3 columns.
	 * 
	 * @param composite
	 *            the parent composite
	 * @param nColumns
	 *            number of columns to span
	 */
	protected void createXmlFileUnderTestControls(Composite composite, int nColumns) {
		Label xmlFileUnderTestLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		xmlFileUnderTestLabel.setFont(composite.getFont());
		xmlFileUnderTestLabel.setText(WizardMessages.NewCamelTestWizardPageOne_class_to_test_label);
		xmlFileUnderTestLabel.setLayoutData(new GridData());

		fXmlFileUnderTestControl = new Text(composite, SWT.SINGLE | SWT.BORDER);
		fXmlFileUnderTestControl.setEnabled(true);
		fXmlFileUnderTestControl.setFont(composite.getFont());
		fXmlFileUnderTestControl.setText(fXmlFileUnderTestText);
		fXmlFileUnderTestControl.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				String name = ((Text) e.widget).getText();
				internalSetXmlFileUnderText(name);
			}
		});
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = nColumns - 2;
		fXmlFileUnderTestControl.setLayoutData(gd);

		fXmlFileUnderTestButton = new Button(composite, SWT.PUSH);
		fXmlFileUnderTestButton.setText(WizardMessages.NewCamelTestWizardPageOne_class_to_test_browse);
		fXmlFileUnderTestButton.setEnabled(true);
		fXmlFileUnderTestButton.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				xmlFileToTestButtonPressed();
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				xmlFileToTestButtonPressed();
			}
		});
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = 1;
		gd.widthHint = LayoutUtil.getButtonWidthHint(fXmlFileUnderTestButton);
		fXmlFileUnderTestButton.setLayoutData(gd);

		ControlContentAssistHelper.createTextContentAssistant(fXmlFileUnderTestControl,
				fXmlFileToTestCompletionProcessor);
	}

	private IMethod findInHierarchy(IType type, String methodName) throws JavaModelException {
		if (type.exists()) {
			ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy(null);
			IType[] superTypes = typeHierarchy.getAllSuperclasses(type);
			for (IType superType : superTypes) {
				if (superType.exists()) {
					IMethod testMethod = superType.getMethod(methodName, new String[] {});
					if (testMethod.exists()) {
						return testMethod;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the default value for the super class field.
	 * 
	 * @return the default value for the super class field
	 * @since 3.7
	 */
	private String getDefaultSuperClassName() {
		return isJUnit4() ? "java.lang.Object" : getJUnit3TestSuperclassName(); //$NON-NLS-1$
	}

	protected String getIFileName(IFile file) {
		return file.getProjectRelativePath().toString();
	}

	/**
	 * Hook method that is called to determine the name of the superclass set
	 * for a JUnit 3 style test case. By default, the name of the JUnit 3
	 * TestCase class is returned. Implementors can override this behavior to
	 * return the name of a subclass instead.
	 * 
	 * @return the fully qualified name of a subclass of the JUnit 3 TestCase
	 *         class.
	 * 
	 * @since 3.7
	 */
	protected String getJUnit3TestSuperclassName() {
		return JUnitCorePlugin.TEST_SUPERCLASS_NAME;
	}

	private String getLineDelimiter() throws JavaModelException {
		/*
		 * IType classToTest= getXmlFileUnderTest();
		 * 
		 * if (classToTest != null && classToTest.exists() &&
		 * classToTest.getCompilationUnit() != null) return
		 * classToTest.getCompilationUnit().findRecommendedLineSeparator();
		 */
		return getPackageFragment().findRecommendedLineSeparator();
	}

	private String getMockEndpointUri(String mockEndpointName) {
		return "mock:" + mockEndpointName;
	}

	private String getMockEndpointVariableName(String mockEndpointName) {
		return mockEndpointName + "Endpoint";
	}

	/**
	 * Returns all status to be consider for the validation. Clients can
	 * override.
	 * 
	 * @return The list of status to consider for the validation.
	 */
	protected IStatus[] getStatusList() {
		return new IStatus[] { fContainerStatus, fPackageStatus, fTypeNameStatus, fXmlFileUnderTestStatus,
				fModifierStatus,
				// fSuperClassStatus,
				fJunit4Status };
	}

	/**
	 * Returns the XML file to be tested.
	 * 
	 * @return the class under test or <code>null</code> if the entered values
	 *         are not valid
	 */
	public IFile getXmlFileUnderTest() {
		return fXmlFileUnderTest;
	}

	/**
	 * Returns the content of the class to test text field.
	 * 
	 * @return the name of the class to test
	 */
	public String getXmlFileUnderTestText() {
		return fXmlFileUnderTestText;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#handleFieldChanged(java.lang.String)
	 */
	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName.equals(CONTAINER)) {
			fXmlFileUnderTestStatus = xmlFileUnderTestChanged();
			/*
			 * if (fXmlFileUnderTestButton != null &&
			 * !fXmlFileUnderTestButton.isDisposed()) {
			 * fXmlFileUnderTestButton.setEnabled(true); }
			 */
			fJunit4Status = junit4Changed();

			updateBuildPathMessage();
		} else if (fieldName.equals(JUNIT4TOGGLE)) {
			updateBuildPathMessage();
			fMethodStubsButtons.setEnabled(IDX_SETUP_CLASS, isJUnit4());
			fMethodStubsButtons.setEnabled(IDX_TEARDOWN_CLASS, isJUnit4());
			fMethodStubsButtons.setEnabled(IDX_CONSTRUCTOR, !isJUnit4());
		}
		updateStatus(getStatusList());
	}

	/**
	 * Initialized the page with the current selection
	 * 
	 * @param selection
	 *            The selection
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement elem = getInitialJavaElement(selection);
		IJavaProject jproject = elem.getJavaProject();
		IPackageFragmentRoot packageFragmentRoot = null;

        if (selection != null && !selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			IFile ifile = null;

			if (selectedElement instanceof IFile) {
				ifile = (IFile) selectedElement;
			} else if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;
				ifile = adaptable.getAdapter(IFile.class);
			}

			if (ifile != null) {
				setXmlFileUnderTest(ifile);
            }
			
            // now we determine the container for the test classes
			if (jproject != null && jproject.exists()) {
				try {
					packageFragmentRoot = searchPackageFragmentRoot(jproject, "test");
					if (packageFragmentRoot == null) {
						packageFragmentRoot = searchPackageFragmentRoot(jproject, "main");
					}
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				}
			}
		}



		if (elem != null) {
			initContainerPage(elem);

			// if we found a suitable test class container then we set it here
			if (packageFragmentRoot != null) {
				// set the container correctly
				setPackageFragmentRoot(packageFragmentRoot, true);
			}

			IJavaProject project = elem.getJavaProject();
			resourceContainer = project.getProject();

			// evaluate the enclosing type
			IPackageFragment pack = (IPackageFragment) elem.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			if (pack != null) {
				setPackageFragment(pack, true);
			} else {
				File testFolderFile = project.getProject().getParent().getRawLocation().append(getPackageFragmentRoot().getPath().makeRelative()).toFile();
				File f = getBasePackage(testFolderFile);
				if (f != null && packageFragmentRoot != null) {
					IPath p = new Path(f.getPath());
					p = p.makeRelativeTo(project.getProject().getParent().getRawLocation().append(getPackageFragmentRoot().getPath().makeRelative()));
					String name = "";
					StringTokenizer strTok = new StringTokenizer(p.toOSString(), File.separator);
					while (strTok.hasMoreTokens()) {
						String tok = strTok.nextToken();
						if (name.trim().length()>0) {
							name += ".";
						}
						name += tok;
					}
					try {
						IPackageFragment pf = packageFragmentRoot.createPackageFragment(name, true, new NullProgressMonitor());
						setPackageFragment(pf, true);
					} catch (Exception ex) {
						Activator.getLogger().error(ex);
					}
				}
			}

			if (fXmlFileUnderTest == null) {
				try {
					// if we have no file selected yet, lets see if there's a
					// single one available
					List<IFile> files = ResourceModelUtils.filter(resourceContainer, 
							new org.fusesource.ide.foundation.core.util.Filter<IFile>() {
						@Override
						public boolean matches(IFile file) {
							if (Objects.equal(file.getFileExtension(), "xml")) {
								return camelXmlMatcher.matches(file);
							}
							return false;
						}
					});
					if (files.size() == 1) {
						setXmlFileUnderTest(files.get(0));
					}
				} catch (Exception e) {
					Activator.getLogger().error("Failed to search for Camel XML files: " + e, e);
				}
			}
		}
		setJUnit4(true, true);
		updateStatus(getStatusList());
	}

	private IPackageFragmentRoot searchPackageFragmentRoot(IJavaProject jproject, String sourceFolderName) throws JavaModelException {
		IPackageFragmentRoot[] roots= jproject.getPackageFragmentRoots();
		IPackageFragmentRoot packageFragmentRoot = null;
		for (int i= 0; i < roots.length; i++) {
			if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
				if (roots[i].getPath().toFile().getPath().contains(String.format("src%s"+sourceFolderName+"%sjava", File.separator, File.separator))) {
					packageFragmentRoot = roots[i];
					break;
				} else if (roots[i].getPath().toFile().getPath().contains(String.format("src%s"+sourceFolderName+"%sscala", File.separator, File.separator))) {
					packageFragmentRoot = roots[i];
					// we will prefer the src/test/java folder, so we don't break here and search for it
				}
			}
		}
		return packageFragmentRoot;
	}

	private File getBasePackage(File f) {
		File ret;
		File[] folders = f.listFiles(folderFilter);
		if (folders != null && folders.length == 1) {
			ret = getBasePackage(folders[0]);
		} else {
			ret = f;
		}
		return ret;
	}

	private void internalSetJUnit4(boolean isJUnit4) {
		fIsJunit4 = isJUnit4;
		fJunit4Status = junit4Changed();
		if (isDefaultSuperClass() || getSuperClass().trim().equals("")) {
			setSuperClass(getDefaultSuperClassName(), true);
		}
		fSuperClassStatus = superClassChanged(); // validate superclass field
		// when toggled
		handleFieldChanged(JUNIT4TOGGLE);
	}

	private void internalSetXmlFileUnderText(IFile file) {
		resourceContainer = file.getProject();
		fXmlFileUnderTest = file;
		String name = getIFileName(file);
		fXmlFileUnderTestText = name;
		fXmlFileUnderTestStatus = xmlFileUnderTestChanged();
		handleFieldChanged(CLASS_UNDER_TEST);
		fPage2.setXmlFileUnderTest(file);
	}

	private void internalSetXmlFileUnderText(String name) {
		if (resourceContainer == null) {
			Activator.getLogger().error("No resourceContainer! Cannot resolve: " + name);
		} else {
			IResource member = resourceContainer.findMember(name);
			if (member instanceof IFile) {
				internalSetXmlFileUnderText((IFile) member);
			}
		}
	}

	/**
	 * Returns whether the super class name is one of the default super class
	 * names.
	 * 
	 * @return <code>true</code> if the super class name is one of the default
	 *         super class names, <code>false</code> otherwise
	 * @since 3.7
	 */
	private boolean isDefaultSuperClass() {
		String superClass = getSuperClass();
		return superClass.equals(getJUnit3TestSuperclassName()) || superClass.equals("java.lang.Object"); //$NON-NLS-1$
	}

	/**
	 * Returns <code>true</code> if the test should be created as Junit 4 test
	 * 
	 * @return returns <code>true</code> if the test should be created as Junit
	 *         4 test
	 * 
	 * @since 3.2
	 */
	public boolean isJUnit4() {
		return fIsJunit4;
	}

	private IStatus junit4Changed() {
		JUnitStatus status = new JUnitStatus();
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#packageChanged()
	 */
	@Override
	protected IStatus packageChanged() {
		IStatus status = super.packageChanged();
		fXmlFileToTestCompletionProcessor.setPackageFragment(getPackageFragment());
		return status;
	}

	private void performBuildpathConfiguration(Object data) {
		IPackageFragmentRoot root = getPackageFragmentRoot();
		if (root == null) {
			return; // should not happen. Link shouldn't be visible
		}
		IJavaProject javaProject = root.getJavaProject();

		if ("a3".equals(data)) { // add and configure JUnit 3 //$NON-NLS-1$
			String id = BUILD_PATH_PAGE_ID;
			Map<String, Object> input = new HashMap<String, Object>();
			IClasspathEntry newEntry = BuildPathSupport.getJUnit3ClasspathEntry();
			input.put(BUILD_PATH_KEY_ADD_ENTRY, newEntry);
			input.put(BUILD_PATH_BLOCK, Boolean.TRUE);
			PreferencesUtil.createPropertyDialogOn(getShell(), javaProject, id, new String[] { id }, input).open();
		} else if ("a4".equals(data)) { // add and configure JUnit 4 //$NON-NLS-1$
			String id = BUILD_PATH_PAGE_ID;
			Map<String, Object> input = new HashMap<String, Object>();
			IClasspathEntry newEntry = BuildPathSupport.getJUnit4ClasspathEntry();
			input.put(BUILD_PATH_KEY_ADD_ENTRY, newEntry);
			input.put(BUILD_PATH_BLOCK, Boolean.TRUE);
			PreferencesUtil.createPropertyDialogOn(getShell(), javaProject, id, new String[] { id }, input).open();
		} else if ("b".equals(data)) { // open build path //$NON-NLS-1$
			String id = BUILD_PATH_PAGE_ID;
			Map<String, Object> input = new HashMap<String, Object>();
			input.put(BUILD_PATH_BLOCK, Boolean.TRUE);
			PreferencesUtil.createPropertyDialogOn(getShell(), javaProject, id, new String[] { id }, input).open();
		} else if ("c".equals(data)) { // open compliance //$NON-NLS-1$
			String buildPath = BUILD_PATH_PAGE_ID;
			String complianceId = COMPLIANCE_PAGE_ID;
			Map<String, Boolean> input = new HashMap<String, Boolean>();
			input.put(BUILD_PATH_BLOCK, Boolean.TRUE);
			input.put(KEY_NO_LINK, Boolean.TRUE);
			PreferencesUtil.createPropertyDialogOn(getShell(), javaProject, complianceId,
					new String[] { buildPath, complianceId }, data).open();
		}

		updateBuildPathMessage();
	}

	private IType resolveClassNameToType(IJavaProject jproject, IPackageFragment pack, String classToTestName)
			throws JavaModelException {
		if (!jproject.exists()) {
			return null;
		}

		IType type = jproject.findType(classToTestName);

		// search in current package
		if (type == null && pack != null && !pack.isDefaultPackage()) {
			type = jproject.findType(pack.getElementName(), classToTestName);
		}

		// search in java.lang
		if (type == null) {
			type = jproject.findType("java.lang", classToTestName); //$NON-NLS-1$
		}
		return type;
	}

	/**
	 * Since Finish was pressed, write widget values to the dialog store so that
	 * they will persist into the next invocation of this wizard page
	 */
	private void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			settings.put(STORE_SETUP, fMethodStubsButtons.isSelected(IDX_SETUP));
			settings.put(STORE_TEARDOWN, fMethodStubsButtons.isSelected(IDX_TEARDOWN));
			settings.put(STORE_SETUP_CLASS, fMethodStubsButtons.isSelected(IDX_SETUP_CLASS));
			settings.put(STORE_TEARDOWN_CLASS, fMethodStubsButtons.isSelected(IDX_TEARDOWN_CLASS));
			settings.put(STORE_CONSTRUCTOR, fMethodStubsButtons.isSelected(IDX_CONSTRUCTOR));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#setFocus()
	 */
	@Override
	protected void setFocus() {
		if (fXmlFileUnderTest == null && fXmlFileUnderTestControl.isEnabled()) {
			fXmlFileUnderTestControl.setFocus();
		} else {
			setFocusOnContainer();
		}
	}

	/**
	 * Specifies if the test should be created as JUnit 4 test.
	 * 
	 * @param isJUnit4
	 *            If set, a Junit 4 test will be created
	 * @param isEnabled
	 *            if <code>true</code> the modifier fields are editable;
	 *            otherwise they are read-only
	 * 
	 * @since 3.2
	 */
	public void setJUnit4(boolean isJUnit4, boolean isEnabled) {
		fIsJunit4Enabled = isEnabled;
		if (fJUnit4Toggle != null && !fJUnit4Toggle.isDisposed()) {
			fJUnit4Toggle.setSelection(isJUnit4);
			fJUnit4Toggle.setEnabled(isEnabled);
		}
		internalSetJUnit4(isJUnit4);
	}

	protected void setTypeNameFromXmlFile(IFile ifile) {
		if (ifile != null) {
			String name = ifile.getName();
			Activator.getLogger().debug("Selected file name: " + name);

			if (name != null) {
				String ext = ifile.getFileExtension();
				if (ext != null) {
					int start = name.lastIndexOf('/') >= 0 ? name.lastIndexOf('/') +1 : 0;
					name = name.substring(start, name.length() - ext.length());
				}

				// lets replace "_" or "-" in the name
				String[] names = name.split("_|\\-|\\.");
				for (int i = 0; i < names.length; i++) {
					names[i] = Strings.capitalize(names[i]);
				}
				name = Strings.join(names, "");
				if (name.length() > 0) {
					name = name.substring(0, 1).toUpperCase() + name.substring(1) + "XmlTest";
					setTypeName(name, true);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewElementWizardPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			saveWidgetValues();
		}

		// if (visible) setFocus();
	}

	/**
	 * Sets the name of the XML file under test.
	 * 
	 * @param name
	 *            The name to set
	 */
	public void setXmlFileUnderTest(IFile file) {
		if (!Widgets.isDisposed(fXmlFileUnderTestControl)) {
			String name = getIFileName(file);
			fXmlFileUnderTestControl.setText(name);
		}
		setTypeNameFromXmlFile(file);
		internalSetXmlFileUnderText(file);

		if (this.wizard.isBlueprintFile(file.getRawLocation().toOSString())) {
			setSuperClass("org.apache.camel.test.blueprint.CamelBlueprintTestSupport", false);
		} else {
			setSuperClass("org.apache.camel.test.spring.CamelSpringTestSupport", false);
		}
		superClassChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#superClassChanged()
	 */
	@Override
	protected IStatus superClassChanged() {
		IStatus stat = super.superClassChanged();
		if (stat.getSeverity() != IStatus.OK) {
			return stat;
		}
		String superClassName = getSuperClass();
		JUnitStatus status = new JUnitStatus();
		boolean isJUnit4 = isJUnit4();
		if (superClassName == null || superClassName.trim().equals("")) { //$NON-NLS-1$
			if (!isJUnit4) {
				status.setError(WizardMessages.NewCamelTestWizardPageOne_error_superclass_empty);
			}
			return status;
		}
		if (isJUnit4 && superClassName.equals("java.lang.Object")) {
			return status;
		}
		if (getPackageFragmentRoot() != null) {
			try {
				IType type = resolveClassNameToType(getPackageFragmentRoot().getJavaProject(), getPackageFragment(),
						superClassName);
				if (type == null) {
					status.setWarning(WizardMessages.NewCamelTestWizardPageOne_error_superclass_not_exist);
					return status;
				}
				if (type.isInterface()) {
					status.setError(WizardMessages.NewCamelTestWizardPageOne_error_superclass_is_interface);
					return status;
				}
				if (!isJUnit4 && !CoreTestSearchEngine.isTestImplementor(type)) { // TODO:
					// expensive!
					status.setError(Messages.format(
							WizardMessages.NewCamelTestWizardPageOne_error_superclass_not_implementing_test_interface,
							BasicElementLabels.getJavaElementName(JUnitCorePlugin.TEST_INTERFACE_NAME)));
					return status;
				}
			} catch (JavaModelException e) {
				JUnitPlugin.log(e);
			}
		}
		return status;
	}

	private void updateBuildPathMessage() {
		if (fLink == null || fLink.isDisposed()) {
			return;
		}

		String message = null;
		IPackageFragmentRoot root = getPackageFragmentRoot();
		if (root != null) {
			IJavaProject project = root.getJavaProject();
			if (project.exists()) {
				if (isJUnit4()) {
					if (!JUnitStubUtility.is50OrHigher(project)) {
						message = WizardMessages.NewCamelTestWizardPageOne_linkedtext_java5required;
					}
				}
			}
		}
		fLink.setVisible(message != null);
		fImage.setVisible(message != null);

		if (message != null) {
			fLink.setText(message);
		}
	}

	/**
	 * The method is called when the container has changed to validate if the
	 * project is suited for the JUnit test class. Clients can override to
	 * modify or remove that validation.
	 * 
	 * @return the status of the validation
	 */
	protected IStatus validateIfJUnitProject() {
		JUnitStatus status = new JUnitStatus();
		IPackageFragmentRoot root = getPackageFragmentRoot();
		if (root != null) {
			try {
				IJavaProject project = root.getJavaProject();
				if (project.exists()) {
					if (isJUnit4()) {
						if (!JUnitStubUtility.is50OrHigher(project)) {
							status.setError(WizardMessages.NewCamelTestWizardPageOne_error_java5required);
							return status;
						}
						if (project.findType(JUnitCorePlugin.JUNIT4_ANNOTATION_NAME) == null) {
							status.setWarning(WizardMessages.NewCamelTestWizardPageOne__error_junit4NotOnbuildpath);
							return status;
						}
					} else {
						if (project.findType(JUnitCorePlugin.TEST_SUPERCLASS_NAME) == null) {
							status.setWarning(WizardMessages.NewCamelTestWizardPageOne_error_junitNotOnbuildpath);
							return status;
						}
					}
				}
			} catch (JavaModelException e) {
			}
		}
		return status;
	}

	private void xmlFileToTestButtonPressed() {
		IFile file = chooseXmlFileToTestType();
		if (file != null) {
			setXmlFileUnderTest(file);
		}
	}

	/**
	 * Hook method that gets called when the class under test has changed. The
	 * method class under test returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus xmlFileUnderTestChanged() {
		JUnitStatus status = new JUnitStatus();

		status.setOK();
		return status;
	}

}
