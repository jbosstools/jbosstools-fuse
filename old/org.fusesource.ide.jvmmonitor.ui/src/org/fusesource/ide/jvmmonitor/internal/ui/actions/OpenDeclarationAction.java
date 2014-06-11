/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fusesource.ide.jvmmonitor.core.IHeapElement;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to open method declaration with Java editor.
 */
public class OpenDeclarationAction extends Action implements
        ISelectionChangedListener {

    /** The selected class name. */
    String className;

    /** The selected method name. */
    private String methodName;

    /** The selected method parameters. */
    private String[] parameters;

    /** The inner class indices. */
    private List<Integer> innterClassIndices;

    /** The state indicating if search engine has been initialized. */
    static boolean searchEngineInitialized = false;

    /**
     * The constructor.
     */
    public OpenDeclarationAction() {
        setText(Messages.openDeclarationLabel);
        setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);

        innterClassIndices = new ArrayList<Integer>();
    }

    /**
     * Creates open declaration action. If the open declaration action is found
     * in the given action bars, the found action will be returned, otherwise
     * the open declaration action will be newly created and set to the given
     * action bars as a global action.
     * 
     * @param actionBars
     *            The action bars.
     * @return The open declaration action
     */
    public static OpenDeclarationAction createOpenDeclarationAction(
            IActionBars actionBars) {
        IAction action = actionBars
                .getGlobalActionHandler(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
        if (action instanceof OpenDeclarationAction) {
            return (OpenDeclarationAction) action;
        }

        OpenDeclarationAction openDeclarationAction = new OpenDeclarationAction();
        actionBars.setGlobalActionHandler(
                IJavaEditorActionDefinitionIds.OPEN_EDITOR,
                openDeclarationAction);
        return openDeclarationAction;
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (event.getSelection() instanceof IStructuredSelection) {
            parseSelection((IStructuredSelection) event.getSelection());
        }
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (className == null) {
            return;
        }

        // search source
        IType source = null;
        try {
            source = searchSource();
        } catch (InterruptedException e) {
            return;
        }
        if (source == null) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(),
                    Messages.errorLabel, Messages.openDeclarationFailedMsg);
            return;
        }

        // get editor input
        IDebugModelPresentation presentation = DebugUITools
                .newDebugModelPresentation(JDIDebugModel.getPluginIdentifier());
        if (presentation == null) {
            return;
        }
        IEditorInput editorInput = presentation.getEditorInput(source);
        if (editorInput == null) {
            return;
        }

        // open editor
        IEditorPart editorPart = null;
        try {
            editorPart = openEditor(editorInput, presentation, source);
        } catch (PartInitException e) {
            Activator.log(IStatus.ERROR, Messages.openEditorFailedMsg, e);
        }
        presentation.dispose();
        if (editorPart == null) {
            return;
        }

        // highlight method
        try {
            highlightMethod(editorInput, editorPart);
        } catch (CoreException e) {
            Activator.log(IStatus.ERROR, Messages.highlightMethodFailedMsg, e);
        }
    }

    /**
     * Parses the selection.
     * 
     * @param selection
     *            The selection
     */
    private void parseSelection(IStructuredSelection selection) {
        Object element = selection.getFirstElement();
        boolean enabled = true;
        innterClassIndices.clear();
        if (element instanceof IMethodNode) {
            String qualifiedMethodName = ((IMethodNode) element).getName();
            int index = qualifiedMethodName.indexOf('(');
            String methodNameWithoutParameter = qualifiedMethodName.substring(
                    0, index);
            String qualifiedParameters = qualifiedMethodName.substring(index,
                    qualifiedMethodName.length());
            parameters = getSimplifiedParameters(qualifiedParameters);

            index = methodNameWithoutParameter.lastIndexOf('.');
            className = methodNameWithoutParameter.substring(0, index);
            methodName = methodNameWithoutParameter.substring(index + 1);
            if ("<init>".equals(methodName)) { //$NON-NLS-1$
                methodName = getSimplifiedConstrucor(className);
            }
        } else if (element instanceof StackTraceElement) {
            className = ((StackTraceElement) element).getClassName();
            methodName = ((StackTraceElement) element).getMethodName();
            parameters = new String[0];
        } else if (element instanceof IHeapElement
                && !((IHeapElement) element).getClassName().endsWith("[]")) { //$NON-NLS-1$
            className = ((IHeapElement) element).getClassName();
            methodName = null;
            parameters = new String[0];
        } else {
            className = null;
            methodName = null;
            enabled = false;
        }

        if (className != null && className.contains("$")) { //$NON-NLS-1$
            String[] elements = className.split("\\$"); //$NON-NLS-1$
            className = elements[0];
            try {
                for (int i = 1; i < elements.length; i++) {
                    innterClassIndices.add(Integer.valueOf(elements[i]));
                }
            } catch (NumberFormatException e) {
                // do nothing
            }
        }

        setEnabled(enabled);
    }

    /**
     * Highlights the method on editor.
     * 
     * @param editorInput
     *            The editor input
     * @param editorPart
     *            The editor part
     * @throws CoreException
     */
    private void highlightMethod(IEditorInput editorInput,
            IEditorPart editorPart) throws CoreException {
        if (!(editorPart instanceof ITextEditor) || methodName == null
                || parameters == null) {
            return;
        }

        ITextEditor textEditor = (ITextEditor) editorPart;
        IDocumentProvider provider = textEditor.getDocumentProvider();
        provider.connect(editorInput);

        int offset = 0;
        int length = 0;
        ISourceRange range = getMethodNameRange(editorInput);
        if (range != null) {
            offset = range.getOffset();
            length = range.getLength();
        }
        textEditor.selectAndReveal(offset, length);

        provider.disconnect(editorInput);
    }

    /**
     * Searches the source for the given class name with progress monitor.
     * 
     * @return The source
     * @throws InterruptedException
     *             if operation is canceled
     */
    private IType searchSource() throws InterruptedException {
        final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        dialog.setOpenOnRun(false);

        // search source corresponding to the class name
        final IType[] source = new IType[1];
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                if (!searchEngineInitialized) {
                    monitor.subTask(Messages.searchingSoruceMsg);
                    searchEngineInitialized = true;
                }

                // open progress monitor dialog when it takes long time
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                dialog.open();
                            }
                        });
                    }
                }, 400);

                if (className == null) {
                    return;
                }

                try {
                    source[0] = doSearchSource(className);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                }
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
            }
        };

        try {
            dialog.run(true, true, op);
        } catch (InvocationTargetException e) {
            Activator.log(IStatus.ERROR,
                    NLS.bind(Messages.searchClassFailedMsg, className), e);
        }

        return source[0];
    }

    /**
     * Searches the source for the given class name.
     * 
     * @param name
     *            The class name
     * @return The source
     * @throws CoreException
     */
    IType doSearchSource(String name) throws CoreException {
        final List<IType> results = new ArrayList<IType>();

        // create requester
        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match)
                    throws CoreException {
                Object element = match.getElement();
                if (element instanceof IType) {
                    results.add((IType) element);
                }
            }
        };

        String baseName = name.replace('$', '.');

        // create search engine and pattern
        SearchEngine engine = new SearchEngine();
        SearchPattern pattern = SearchPattern.createPattern(baseName,
                IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS,
                SearchPattern.R_EXACT_MATCH);

        // search the source for the given name
        engine.search(pattern, new SearchParticipant[] { SearchEngine
                .getDefaultSearchParticipant() }, SearchEngine
                .createWorkspaceScope(), requestor, null);

        if (results.size() > 0) {
            // at most one source should be found
            return results.get(0);
        }

        return null;
    }

    /**
     * Opens the editor.
     * 
     * @param editorInput
     *            The editor input
     * @param presentation
     *            The presentation
     * @param source
     *            The source
     * @return The editor part
     * @throws PartInitException
     */
    private IEditorPart openEditor(IEditorInput editorInput,
            ISourcePresentation presentation, Object source)
            throws PartInitException {
        if (editorInput == null) {
            return null;
        }

        String editorId = presentation.getEditorId(editorInput, source);
        if (editorId == null) {
            return null;
        }

        return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(editorInput, editorId);
    }

    /**
     * Gets the source range.
     * 
     * @param editorInput
     *            The editor input
     * @return The source range
     * @throws JavaModelException
     */
    private ISourceRange getMethodNameRange(IEditorInput editorInput)
            throws JavaModelException {

        ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editorInput);
        LinkedList<ISourceRange> sourceRanges = new LinkedList<ISourceRange>();
        if (typeRoot instanceof IClassFile) {
            // class file
            IType type = ((IClassFile) typeRoot).getType();
            getMethodNameRange(type.getChildren(), 0, sourceRanges);
        } else if (typeRoot instanceof ICompilationUnit) {
            // java file
            ICompilationUnit unit = (ICompilationUnit) typeRoot;

            IType[] allTypes = unit.getAllTypes();
            for (IType type : allTypes) {
                getMethodNameRange(type.getChildren(), 0, sourceRanges);
            }
        } else {
            return null;
        }

        if (sourceRanges.isEmpty()) {
            return null;
        }

        return sourceRanges.getFirst();
    }

    /**
     * Gets the method name range.
     * 
     * @param javaElements
     *            The java elements
     * @param level
     *            The inner class level
     * @param sourceRanges
     *            The candidates of source range
     * @throws JavaModelException
     */
    private void getMethodNameRange(IJavaElement[] javaElements, int level,
            LinkedList<ISourceRange> sourceRanges) throws JavaModelException {
        for (IJavaElement javaElement : javaElements) {
            if (!(javaElement instanceof IMethod)) {
                continue;
            }

            if (innterClassIndices.size() > level) {
                IJavaElement[] types = ((IMethod) javaElement).getChildren();
                if (types != null && types.length == 1
                        && types[0] instanceof IType) {
                    getMethodNameRange(((IType) types[0]).getChildren(),
                            level + 1, sourceRanges);
                }
                continue;
            }

            // check the method name
            if (!(methodName.equals(javaElement.getElementName()))) {
                continue;
            }

            // check the method arguments
            String[] paramTypes = ((IMethod) javaElement).getParameterTypes();
            String[] readableParamTypes = getReadableParamTypes(paramTypes);
            boolean isMatch = true;
            for (int i = 0; i < readableParamTypes.length; i++) {
                if (parameters.length == i) {
                    break;
                }
                if (!readableParamTypes[i].equals(parameters[i])) {
                    isMatch = false;

                    // keep the similar method
                    sourceRanges
                            .addLast(((IMethod) javaElement).getNameRange());
                    break;
                }
            }

            if (isMatch) {
                sourceRanges.addFirst(((IMethod) javaElement).getNameRange());
                break;
            }
        }
    }

    /**
     * Gets the readable parameter types with given type signatures.
     * 
     * @param signatures
     *            The type signatures e.g. <tt>QString;I</tt>
     * @return The readable parameter types <tt>String, int</tt>
     */
    private String[] getReadableParamTypes(String[] signatures) {
        String[] results = new String[signatures.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = Signature.toString(signatures[i]);
        }
        return results;
    }

    /**
     * Gets the simplified parameters with given qualified parameters e.g.
     * <tt>(java.lang.String, int)</tt>.
     * 
     * @param parameter
     *            The simplified parameters e.g. <tt>String, int</tt>
     * @return The simplified parameters
     */
    private String[] getSimplifiedParameters(String parameter) {

        // remove '(' and ')'
        String param = parameter.replaceAll("[()]", ""); //$NON-NLS-1$ //$NON-NLS-2$

        String[] results = param.split(","); //$NON-NLS-1$
        for (int i = 0; i < results.length; i++) {
            int index = results[i].lastIndexOf('$');
            if (index != -1) {
                results[i] = results[i].substring(index + 1).trim();
                continue;
            }

            index = results[i].lastIndexOf('.');
            results[i] = results[i].substring(index + 1).trim();
        }
        return results;
    }

    /**
     * Gets the simplified constructor name.
     * 
     * @param clazz
     *            The class name
     * @return The simplified constructor name
     */
    private String getSimplifiedConstrucor(String clazz) {
        int index = clazz.lastIndexOf('$');
        if (index != -1) {
            return clazz.substring(index + 1);
        }

        index = clazz.lastIndexOf('.');
        return clazz.substring(index + 1);
    }
}
