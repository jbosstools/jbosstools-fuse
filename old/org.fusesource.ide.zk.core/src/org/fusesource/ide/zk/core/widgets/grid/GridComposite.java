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

package org.fusesource.ide.zk.core.widgets.grid;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link Composite} that uses a {@link GridLayout}. This class is intended for use in {@link Dialog dialogs} and
 * {@link IWizard wizards}.
 * 
 * @author Mark Masse
 */
public abstract class GridComposite extends Composite {

    public static final Image DEC_ERROR = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_ERROR).getImage();

    public static final Image DEC_REQUIRED = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_REQUIRED).getImage();

    private final Map<String, ControlDecoration> _ControlDecorations;
    private final Map<String, Control> _Controls;
    private GridCompositeStatus _CurrentStatus;
    private CopyOnWriteArrayList<IGridCompositeEventListener> _EventListeners;
    private int _Margin = 8;
    private int _NumColumns = 2;

    /**
     * Constructor.
     * 
     * @param parent The parent {@link Composite}.
     */
    public GridComposite(Composite parent) {
        this(parent, SWT.NULL);
    }

    /**
     * Constructor.
     * 
     * @param parent The parent {@link Composite}.
     * @param style The {@link Composite#getStyle() style}.
     */
    public GridComposite(Composite parent, int style) {
        super(parent, style);
        _Controls = new HashMap<String, Control>();
        _ControlDecorations = new HashMap<String, ControlDecoration>();
    }

    /**
     * Adds a {@link IGridCompositeEventListener}.
     * 
     * @param listener The listener to add.
     */
    public final void addGridCompositeEventListener(IGridCompositeEventListener listener) {

        if (_EventListeners == null) {
            _EventListeners = new CopyOnWriteArrayList<IGridCompositeEventListener>();
        }

        if (!_EventListeners.contains(listener)) {
            _EventListeners.add(listener);
            modified(null);
        }
    }

    /**
     * Returns the named {@link Control}.
     * 
     * @param name The name of the {@link Control}.
     * @return The named {@link Control}.
     */
    public final Control getControl(String name) {
        if (_Controls.containsKey(name)) {
            return _Controls.get(name);
        }
        return null;
    }

    /**
     * Returns the current {@link GridCompositeStatus status}.
     * 
     * @return The current {@link GridCompositeStatus status}.
     */
    public final GridCompositeStatus getCurrentStatus() {
        return _CurrentStatus;
    }

    /**
     * Returns the margin.
     * 
     * @return The margin
     * 
     * @see GridLayout#marginTop
     * @see GridLayout#marginLeft
     * @see GridLayout#marginBottom
     * @see GridLayout#marginRight
     */
    public final int getMargin() {
        return _Margin;
    }

    /**
     * Returns the number of columns.
     * 
     * @return The number of columns.
     * 
     * @see GridLayout#numColumns
     */
    public final int getNumColumns() {
        return _NumColumns;
    }

    /**
     * Clients must call this method to create and layout the contents.
     */
    public void init() {
        setLayout(createLayout());
        createContents();
        layout(true);
        registerChildGridComposites(getChildren());
    }

    /**
     * Removes a {@link IGridCompositeEventListener}.
     * 
     * @param listener The listener to remove.
     */
    public final void removeGridCompositeEventListener(IGridCompositeEventListener listener) {

        if (_EventListeners == null) {
            return;
        }

        if (!_EventListeners.contains(listener)) {
            return;
        }

        _EventListeners.remove(listener);
    }

    /**
     * Sets the margin.
     * 
     * @param margin The margin to set.
     * 
     * @see GridLayout#marginTop
     * @see GridLayout#marginLeft
     * @see GridLayout#marginBottom
     * @see GridLayout#marginRight
     */
    public final void setMargin(int margin) {
        _Margin = margin;
    }

    /**
     * Sets the number of columns.
     * 
     * @param numColumns the number of columns to set.
     * 
     * @see GridLayout#numColumns
     */
    public final void setNumColumns(int numColumns) {
        _NumColumns = numColumns;
    }

    /**
     * Maps the specified name to the specified {@link Control}. Note that this method simply maps the control
     * internally and does not "add" the Control in any parent/child control hierarchy way.
     * 
     * @param name The name (map key) for the {@link Control}.
     * @param control The {@link Control} to map.
     */
    protected final void addControl(String name, Control control) {
        if (_Controls.containsKey(name)) {
            throw new IllegalArgumentException("Control '" + name + "' already exists.");
        }
        _Controls.put(name, control);
        // control.pack();
        // layout(true);
    }

    /**
     * Adds a {@link ControlDecoration} to the {@link Control}.
     * 
     * @param name The name (map key) for the {@link Control}.
     * @param control The {@link Control} to decorate.
     */
    protected final void addControlDecoration(String name, Control control) {
        addControlDecoration(name, control, SWT.LEFT | SWT.TOP);
    }

    /**
     * Adds a {@link ControlDecoration} to the {@link Control}.
     * 
     * @param name The name (map key) for the {@link Control}.
     * @param control The {@link Control} to decorate.
     * @param style The {@link ControlDecoration} style.
     */
    protected final void addControlDecoration(String name, Control control, int style) {
        addControlDecoration(name, control, style, this);
    }

    /**
     * Adds a {@link ControlDecoration} to the {@link Control}.
     * 
     * @param name The name (map key) for the {@link Control}.
     * @param control The {@link Control} to decorate.
     * @param style The {@link ControlDecoration} style.
     * @param container The {@link Composite} that owns the {@link Control} to decorate.
     */
    protected final void addControlDecoration(String name, Control control, int style, Composite container) {

        if (_ControlDecorations.containsKey(name)) {
            throw new IllegalArgumentException("ControlDecoration '" + name + "' already exists.");
        }

        ControlDecoration controlDecoration = new ControlDecoration(control, style, container);
        controlDecoration.setShowHover(true);
        _ControlDecorations.put(name, controlDecoration);
    }

    /**
     * Adds (maps) the specified {@link GridTextInput}.
     * 
     * @param textInput The {@link GridTextInput} to add.
     * 
     * @see #addControl(String, Control)
     * @see #addControlDecoration(String, Control, int, Composite)
     */
    protected final void addGridTextInput(GridTextInput textInput) {
        addGridTextInput(textInput, SWT.LEFT | SWT.TOP);
    }

    /**
     * Adds (maps) the specified {@link GridTextInput}.
     * 
     * @param textInput The {@link GridTextInput} to add.
     * @param controlDecorationStyle The {@link ControlDecoration} style.
     * 
     * @see #addControl(String, Control)
     * @see #addControlDecoration(String, Control, int, Composite)
     */
    protected final void addGridTextInput(final GridTextInput textInput, int controlDecorationStyle) {

        String name = textInput.getName();
        Text text = textInput.getText();

        addControl(name, text);
        addControlDecoration(name, text, controlDecorationStyle, text.getParent());

        text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                modified(textInput);
            }
        });

        if (textInput.getType().isInteger()) {
            text.addVerifyListener(new VerifyListener() {

                @Override
                public void verifyText(VerifyEvent event) {
                    String text = event.text;
                    event.doit = text.length() == 0
                            || (Character.isDigit(text.charAt(0)) && GridTextInput.isValidIntegerText(text));
                }
            });
        }
    }

    /**
     * This method must be implemented but not be called directly by subclasses.
     */
    protected abstract void createContents();

    /**
     * This method should not be called directly by subclasses.
     * 
     * @return The {@link GridLayout}.
     */
    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout(getNumColumns(), false);
        int margin = getMargin();
        layout.horizontalSpacing = margin;
        layout.verticalSpacing = margin;
        layout.marginWidth = margin;
        layout.marginHeight = margin;
        return layout;
    }

    /**
     * This method should be called by subclasses whenever data/input on the form changes.
     * 
     * @param source The source of the modification.
     */
    protected final void modified(Object source) {

        // Hide the decorations.
        for (ControlDecoration controlDecoration : _ControlDecorations.values()) {
            controlDecoration.hide();
        }

        // Re-evaluate the status
        _CurrentStatus = updateStatus();

        //
        // Show the appropriate ControlDecoration with the updated status information.
        //

        String controlName = _CurrentStatus.getControlName();
        String message = _CurrentStatus.getMessage();
        GridCompositeStatus.Type type = _CurrentStatus.getType();
        if (controlName != null && message != null && type.isError() && _ControlDecorations.containsKey(controlName)) {

            ControlDecoration controlDecoration = _ControlDecorations.get(controlName);

            Image image = null;
            if (type == GridCompositeStatus.Type.ERROR_INVALID) {
                image = DEC_ERROR;
            }
            else if (type == GridCompositeStatus.Type.ERROR_REQUIRED) {
                image = DEC_REQUIRED;
            }
            if (image != null) {
                controlDecoration.setImage(image);
            }

            controlDecoration.setDescriptionText(message);
            controlDecoration.show();
        }

        // Inform listeners
        fireModified(_CurrentStatus);
    }

    /**
     * Removes the mapped {@link Control}. Note this method does not remove the Control in any UI-visible way.
     * 
     * @param name The name of the {@link Control} to unmap.
     */
    protected final void removeControl(String name) {
        _Controls.remove(name);
    }

    /**
     * Removes the mapped {@link ControlDecoration}. Note this method does not remove the ControlDecoration in any UI-visible way.
     * 
     * @param name The name of the {@link ControlDecoration} to unmap.
     */
    protected final void removeControlDecoration(String name) {
        _ControlDecorations.remove(name);
    }

    /**
     * This method should not be called directly. Subclasses may override this method to check the status of
     * non-GridTextInput sources.
     * 
     * @param source The source of the modification.
     * @return The new status.
     */
    protected GridCompositeStatus updateStatus(Object source) {
        if (source instanceof GridTextInput) {
            return updateStatusFromGridTextInput((GridTextInput) source);
        }
        if (source instanceof GridComposite) {
            return ((GridComposite) source).getCurrentStatus();
        }

        return GridCompositeStatus.OK_STATUS;
    }

    private void fireModified(GridCompositeStatus status) {
        if (_EventListeners != null) {
            GridCompositeEvent event = new GridCompositeEvent(this, status);
            for (IGridCompositeEventListener listener : _EventListeners) {
                listener.modified(event);
            }
        }
    }

    private void registerChildGridComposites(Control[] controls) {

        for (Control control : controls) {

            if (control instanceof GridComposite) {

                final GridComposite gridComposite = (GridComposite) control;
                final IGridCompositeEventListener gridCompositeEventListener = new IGridCompositeEventListener() {

                    @Override
                    public void modified(GridCompositeEvent event) {
                        GridComposite.this.modified(gridComposite);
                    }

                };

                gridComposite.addGridCompositeEventListener(gridCompositeEventListener);
                gridComposite.addDisposeListener(new DisposeListener() {

                    @Override
                    public void widgetDisposed(DisposeEvent e) {
                        gridComposite.removeGridCompositeEventListener(gridCompositeEventListener);
                        gridComposite.removeDisposeListener(this);
                    }
                });

            }
            else if (control instanceof Composite) {
                registerChildGridComposites(((Composite) control).getChildren());
            }
        }
    }

    private GridCompositeStatus updateStatus() {
        return updateStatus(getChildren());
    }

    private GridCompositeStatus updateStatus(Control[] controls) {

        for (Control control : controls) {

            if (isVisible() != control.isVisible()) {
                continue;
            }

            Object controlSource = control;

            if (control instanceof Text) {
                Object controlData = control.getData();
                if (controlData instanceof GridTextInput) {
                    controlSource = controlData;
                }
            }

            GridCompositeStatus status = updateStatus(controlSource);
            if (status.getType().isError()) {
                return status;
            }

            if (control instanceof Composite) {
                status = updateStatus(((Composite) control).getChildren());
                if (status.getType().isError()) {
                    return status;
                }
            }
        }

        return GridCompositeStatus.OK_STATUS;
    }

    private GridCompositeStatus updateStatusFromGridTextInput(GridTextInput source) {
        Text textControl = source.getText();
        String name = source.getName();
        GridTextInput.Type inputType = source.getType();

        String message;
        GridCompositeStatus.Type statusType;

        String text = textControl.getText();
        String defaultValue = source.getDefaultTextValue();

        if (inputType.isValueRequired() && text.trim().length() == 0) {

            if (defaultValue != null) {
                message = name + " value must be specified.  Default value is " + defaultValue;
            }
            else {
                message = name + " value must be specified.";
            }

            statusType = GridCompositeStatus.Type.ERROR_REQUIRED;
            return new GridCompositeStatus(name, message, statusType);
        }

        if (inputType.isInteger() && !GridTextInput.isValidIntegerText(text)) {

            if (defaultValue != null) {
                message = name + " value must be a positive integer.  Default value is " + defaultValue;
            }
            else {
                message = name + " value must be a positive integer.";
            }

            statusType = GridCompositeStatus.Type.ERROR_INVALID;
            return new GridCompositeStatus(name, message, statusType);
        }

        return GridCompositeStatus.OK_STATUS;
    }

}
