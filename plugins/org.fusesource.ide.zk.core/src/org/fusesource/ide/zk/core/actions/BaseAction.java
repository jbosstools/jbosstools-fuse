/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.core.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The base class for all library actions.
 * 
 * @author Mark Masse
 */
public class BaseAction extends Action {

    private boolean _Disposed;
    private final InputType _InputType;
    private Set<Class<?>> _InputTypeClasses;
    private ISelectionProvider _SelectionProvider;
    private SelectionProviderDisposeListener _SelectionProviderDisposeListener;
    private SelectionProviderSelectionChangedListener _SelectionProviderSelectionChangedListener;

    // private SelectionServiceSelectionListener _SelectionServiceSelectionListener;

    public BaseAction() {
        this(InputType.NONE, null);
    }

    public BaseAction(InputType inputType) {
        this(inputType, null);
    }

    public BaseAction(InputType inputType, ISelectionProvider selectionProvider) {
        // setActionDefinitionId(getClass().getName());
        _InputType = inputType;

        setSelectionProvider(selectionProvider);

        // ISelectionService selectionService = getSelectionService();
        // selectionService.addPostSelectionListener(_SelectionServiceSelectionListener);
    }

    public BaseAction(ISelectionProvider selectionProvider) {
        this(InputType.NONE, selectionProvider);
    }

    public void addInputTypeClass(Class<?> c) {
        if (_InputTypeClasses == null) {
            _InputTypeClasses = new HashSet<Class<?>>();
        }

        _InputTypeClasses.add(c);
    }

    public boolean canRun() {

        InputType inputType = getInputType();

        if (inputType.isStructuredSelection()) {
            IStructuredSelection structuredSelection = getCurrentStructuredSelection();
            if (canRunWithStructuredSelection(structuredSelection)) {
                return true;
            }
        }
        else if (inputType.isEditorInput()) {

            IEditorPart editor = getActiveEditor();
            IEditorInput editorInput = (editor != null) ? editor.getEditorInput() : null;

            if (canRunWithEditorInput(editorInput)) {
                return true;
            }
        }
        else if (canRunWithNothing()) {
            return true;
        }

        return false;

    }

    // public void dispose() {
    // if (isDisposed()) {
    // return;
    // }
    //
    // _Disposed = true;
    // ISelectionService selectionService = getSelectionService();
    // selectionService.removePostSelectionListener(_SelectionServiceSelectionListener);
    // setSelectionProvider(null);
    // }

    public boolean canRunWithEditorInput(IEditorInput editorInput) {

        if (editorInput == null) {
            return false;
        }

        return canRunWithObjectType(editorInput.getClass()) && canRunWithObject(editorInput);
    }

    public boolean canRunWithNothing() {
        return getInputType() == InputType.NONE;
    }

    public boolean canRunWithObject(Object object) {
        return true;
    }

    public boolean canRunWithObjectType(Class<?> objectType) {

        if (_InputTypeClasses == null) {
            return false;
        }

        for (Class<?> inputTypeClass : _InputTypeClasses) {
            if (inputTypeClass.isAssignableFrom(objectType)) {
                return true;
            }
        }

        return false;
    }

    public boolean canRunWithStructuredSelection(IStructuredSelection structuredSelection) {

        if (!getInputType().isAcceptableSelectionSize(structuredSelection)) {
            return false;
        }

        if (structuredSelection.isEmpty()) {
            return true;
        }

        Iterator<?> selectionIterator = structuredSelection.iterator();
        while (selectionIterator.hasNext()) {

            Object selectedObject = selectionIterator.next();

            if (!canRunWithObjectType(selectedObject.getClass()) || !canRunWithObject(selectedObject)) {
                return false;
            }

        }

        return true;

    }

    /**
     * Returns the inputType.
     * 
     * @return The inputType
     */
    public final InputType getInputType() {
        return _InputType;
    }

    /**
     * Returns the selectionProvider.
     * 
     * @return The selectionProvider
     */
    public final ISelectionProvider getSelectionProvider() {
        return _SelectionProvider;
    }

    /**
     * Returns the disposed.
     * 
     * @return The disposed
     */
    public boolean isDisposed() {
        return _Disposed;
    }

    public void removeInputTypeClass(Class<?> c) {
        if (_InputTypeClasses == null) {
            return;
        }

        if (_InputTypeClasses.contains(c)) {
            _InputTypeClasses.remove(c);
        }
    }

    public void reportError(Exception e) {
        openErrorMessageDialog(e.getMessage());
    }

    public void run() {

        if (!isEnabled()) {
            return;
        }

        InputType inputType = getInputType();

        if (inputType.isStructuredSelection()) {
            IStructuredSelection structuredSelection = getCurrentStructuredSelection();
            if (canRunWithStructuredSelection(structuredSelection)) {
                runWithStructuredSelection(structuredSelection);
                return;
            }
        }
        else if (inputType.isEditorInput()) {

            IEditorPart editor = getActiveEditor();
            IEditorInput editorInput = (editor != null) ? editor.getEditorInput() : null;

            if (canRunWithEditorInput(editorInput)) {
                runWithEditorInput(editorInput);
                return;
            }
        }
        else if (canRunWithNothing()) {
            runWithNothing();
            return;
        }

        openErrorMessageDialog(getText() + " action was not handled.");
    }

    public void runWithEditorInput(IEditorInput editorInput) {
        try {
            runWithObject(editorInput);
        }
        catch (Exception e) {
            reportError(e);
        }
    }

    public void runWithNothing() {
        try {
            runWithObject(null);
        }
        catch (Exception e) {
            reportError(e);
        }
    }

    public void runWithObject(Object object) throws Exception {
        openInformationMessageDialog(String.valueOf(object));
    }

    public void runWithStructuredSelection(IStructuredSelection selection) {
        Iterator<?> selectionIterator = selection.iterator();
        while (selectionIterator.hasNext()) {
            Object selectedObject = selectionIterator.next();
            try {
                runWithObject(selectedObject);
            }
            catch (Exception e) {
                reportError(e);
                break;
            }
        }
    }

    /**
     * Sets the selectionProvider.
     * 
     * @param selectionProvider the selectionProvider to set
     */
    public final void setSelectionProvider(ISelectionProvider selectionProvider) {

        if (_SelectionProvider != null) {

            if (_SelectionProviderSelectionChangedListener != null) {
                _SelectionProvider.removeSelectionChangedListener(_SelectionProviderSelectionChangedListener);

            }
            if (_SelectionProvider instanceof Viewer) {
                unhookViewer((Viewer) _SelectionProvider);
            }
        }

        _SelectionProvider = selectionProvider;

        if (_SelectionProvider != null) {

            if (_SelectionProviderSelectionChangedListener == null) {
                _SelectionProviderSelectionChangedListener = new SelectionProviderSelectionChangedListener();
            }

            _SelectionProvider.addSelectionChangedListener(_SelectionProviderSelectionChangedListener);

            if (_SelectionProvider instanceof Viewer) {
                hookViewer((Viewer) _SelectionProvider);
            }
        }

        updateState();
    }

    public void updateState() {
        setEnabled(canRun());
    }

    protected final IEditorPart getActiveEditor() {
        IWorkbenchPage workBenchPage = getActiveWorkbenchPage();
        if (workBenchPage != null) {
            return workBenchPage.getActiveEditor();
        }
        return null;
    }

    protected final Shell getActiveShell() {
        return getCurrentDisplay().getActiveShell();
    }

    protected final IWorkbenchPage getActiveWorkbenchPage() {
        IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
        if (workBenchWindow != null) {
            return workBenchWindow.getActivePage();
        }
        return null;
    }

    protected final IWorkbenchWindow getActiveWorkbenchWindow() {
        IWorkbench workBench = getWorkbench();
        return workBench.getActiveWorkbenchWindow();
    }

    protected final Display getCurrentDisplay() {
        return Display.getCurrent();
    }

    protected final ISelection getCurrentSelection() {
        ISelectionProvider selectionProvider = getSelectionProvider();
        if (selectionProvider != null) {
            return selectionProvider.getSelection();
        }

        return null;

        // ISelectionService selectionService = getSelectionService();
        // return selectionService.getSelection();
    }

    protected final IStructuredSelection getCurrentStructuredSelection() {
        ISelection selection = getCurrentSelection();
        if (selection instanceof IStructuredSelection) {
            return (IStructuredSelection) selection;
        }
        return null;
    }

    protected String getDefaultErrorMessageDialogTitle() {
        return getText() + " Failed";
    }

    protected String getDefaultInformationMessageDialogTitle() {
        return getText();
    }

    protected String getDefaultWarningMessageDialogTitle() {
        return getText();
    }

    protected final ISelectionService getSelectionService() {
        IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
        return workBenchWindow.getSelectionService();
    }

    protected final IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }

    protected void hookViewer(Viewer viewer) {

        final Control control = viewer.getControl();

        if (_SelectionProviderDisposeListener == null) {
            _SelectionProviderDisposeListener = new SelectionProviderDisposeListener();
        }

        control.addDisposeListener(_SelectionProviderDisposeListener);

    }

    protected final IEditorPart openEditor(IEditorInput input, String editorId) throws PartInitException {
        return getActiveWorkbenchPage().openEditor(input, editorId);
    }

    protected final IEditorPart openEditor(IEditorInput input, String editorId, boolean activate)
            throws PartInitException {
        return getActiveWorkbenchPage().openEditor(input, editorId, activate);
    }

    protected final IEditorPart openEditor(IEditorInput input, String editorId, boolean activate, int matchFlags)
            throws PartInitException {
        return getActiveWorkbenchPage().openEditor(input, editorId, activate, matchFlags);
    }

    protected final IEditorPart openEditor(String filePath) throws PartInitException {

        IPath location = Path.fromOSString(filePath);

        IFileStore fileStore = EFS.getLocalFileSystem().getStore(location);
        FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput(fileStore);

        String editorId = IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID;
        IEditorDescriptor editorDescriptor = getWorkbench().getEditorRegistry().getDefaultEditor(filePath);
        if (editorDescriptor != null) {
            editorId = editorDescriptor.getId();
        }

        return openEditor(fileStoreEditorInput, editorId);
    }

    protected final void openErrorMessageDialog(String message) {
        openErrorMessageDialog(getDefaultErrorMessageDialogTitle(), message);
    }

    protected final void openErrorMessageDialog(String title, String message) {
        MessageDialog.openError(getActiveShell(), title, message);
    }

    protected final void openErrorMessageDialog(Throwable t) {
        openErrorMessageDialog("Unhandled exception: " + t);
    }

    protected final void openInformationMessageDialog(String message) {
        openInformationMessageDialog(getDefaultInformationMessageDialogTitle(), message);
    }

    protected final void openInformationMessageDialog(String title, String message) {
        MessageDialog.openInformation(getActiveShell(), title, message);
    }

    protected final void openWarningMessageDialog(String message) {
        openWarningMessageDialog(getDefaultWarningMessageDialogTitle(), message);
    }

    protected final void openWarningMessageDialog(String title, String message) {
        MessageDialog.openWarning(getActiveShell(), title, message);
    }

    protected void unhookViewer(Viewer viewer) {
        if (_SelectionProviderDisposeListener != null) {
            Control control = viewer.getControl();
            control.removeDisposeListener(_SelectionProviderDisposeListener);
        }
    }

    /**
     * Defines the input types that the action will accept.
     * 
     * @author Mark Masse
     */
    public static enum InputType {

        /**
         * Input type: {@link IEditorInput}
         */
        EDITOR_INPUT(false, true, Integer.MAX_VALUE, Integer.MIN_VALUE),

        /**
         * Input type: {@link IStructuredSelection} with more than one element
         */
        MULTI_STRUCTURED_SELECTION(true, false, 2, Integer.MAX_VALUE),

        /**
         * Input type: <code>null</code>
         */
        NONE(false, false, Integer.MAX_VALUE, Integer.MIN_VALUE),

        /**
         * Input type: {@link IStructuredSelection} or <code>null</code>
         */
        OPTIONAL_STRUCTURED_SELECTION(true, false, 0, Integer.MAX_VALUE),

        /**
         * Input type: {@link IStructuredSelection} with exactly one element
         */
        SINGLE_STRUCTURED_SELECTION(true, false, 1, 1),

        /**
         * Input type: {@link IStructuredSelection} with at least one element
         */
        STRUCTURED_SELECTION(true, false, 1, Integer.MAX_VALUE);

        private final boolean _EditorInput;
        private final int _MaximumSelectionSize;
        private final int _MinimumSelectionSize;
        private final boolean _StructuredSelection;

        private InputType(boolean structuredSelection, boolean editorInput, int minimumSelectionSize,
                int maximumSelectionSize) {
            _StructuredSelection = structuredSelection;
            _EditorInput = editorInput;
            _MinimumSelectionSize = minimumSelectionSize;
            _MaximumSelectionSize = maximumSelectionSize;
        }

        /**
         * Returns the maximum selection size.
         * 
         * @return The maximum selection size.
         */
        public int getMaximumSelectionSize() {
            return _MaximumSelectionSize;
        }

        /**
         * Returns the minimum selection size.
         * 
         * @return The minimum selection size.
         */
        public int getMinimumSelectionSize() {
            return _MinimumSelectionSize;
        }

        /**
         * Returns <code>true</code> if the specified selection meets this {@link InputType}'s requirements.
         * 
         * @param structuredSelection The {@link IStructuredSelection} to test.
         * @return <code>true</code> if the specified selection meets this {@link InputType}'s requirements.
         */
        public boolean isAcceptableSelectionSize(IStructuredSelection structuredSelection) {
            int size = (structuredSelection == null) ? 0 : structuredSelection.size();
            return (size >= getMinimumSelectionSize() && size <= getMaximumSelectionSize());
        }

        /**
         * Returns <code>true</code> if this {@link InputType} allows {@link IEditorInput}.
         * 
         * @return <code>true</code> if this {@link InputType} allows {@link IEditorInput}.
         */
        public boolean isEditorInput() {
            return _EditorInput;
        }

        /**
         * Returns <code>true</code> if this {@link InputType} allows {@link IStructuredSelection}.
         * 
         * @return <code>true</code> if this {@link InputType} allows {@link IStructuredSelection}.
         */
        public boolean isStructuredSelection() {
            return _StructuredSelection;
        }

    }

    private class SelectionProviderDisposeListener implements DisposeListener {

        @Override
        public void widgetDisposed(DisposeEvent e) {
            setSelectionProvider(null);
        }
    }

    private class SelectionProviderSelectionChangedListener implements ISelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            updateState();
        }

    }

    // private class SelectionServiceSelectionListener implements ISelectionListener {
    //
    // @Override
    // public void selectionChanged(IWorkbenchPart part, ISelection selection) {
    // if (getSelectionProvider() == null) {
    // updateState();
    // }
    // }
    //
    // }

}
