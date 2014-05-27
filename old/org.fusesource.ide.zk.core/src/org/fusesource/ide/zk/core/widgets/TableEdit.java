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

package org.fusesource.ide.zk.core.widgets;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class TableEdit {

    public TableEdit(Table table) {
        this(table, -1);
    }

    public TableEdit(Table table, int columnIndex) {
        this(table, null, columnIndex);
    }

    public TableEdit(Table table, CommitEditRunnable commitEditRunnable) {
        this(table, commitEditRunnable, -1);
    }

    public TableEdit(final Table table, final CommitEditRunnable commitEditRunnable, final int columnIndex) {

        // create a TableCursor to navigate around the table
        final TableCursor cursor = new TableCursor(table, SWT.NONE);
        // create an editor to edit the cell when the user hits "ENTER"
        // while over a cell in the table
        final ControlEditor editor = new ControlEditor(cursor);
        editor.grabHorizontal = true;
        editor.grabVertical = true;

        cursor.addSelectionListener(new SelectionAdapter() {

            // when the user hits "ENTER" in the TableCursor, pop up a text editor so that
            // they can change the text of the cell
            public void widgetDefaultSelected(SelectionEvent e) {
                beginTableEdit(cursor, editor, commitEditRunnable);
            }

            // when the TableEditor is over a cell, select the corresponding row in
            // the table
            public void widgetSelected(SelectionEvent e) {
                table.setSelection(new TableItem[] { cursor.getRow() });
            }
        });
        // Hide the TableCursor when the user hits the "CTRL" or "SHIFT" key.
        // This allows the user to select multiple items in the table.
        cursor.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CTRL || e.keyCode == SWT.SHIFT || (e.stateMask & SWT.CONTROL) != 0
                        || (e.stateMask & SWT.SHIFT) != 0) {
                    cursor.setVisible(false);
                }
            }
        });
        // When the user double clicks in the TableCursor, pop up a text editor so that
        // they can change the text of the cell.
        cursor.addMouseListener(new MouseAdapter() {

            public void mouseDown(MouseEvent e) {
                beginTableEdit(cursor, editor, commitEditRunnable);
            }
        });

        // Show the TableCursor when the user releases the "SHIFT" or "CTRL" key.
        // This signals the end of the multiple selection task.
        table.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CONTROL && (e.stateMask & SWT.SHIFT) != 0)
                    return;
                if (e.keyCode == SWT.SHIFT && (e.stateMask & SWT.CONTROL) != 0)
                    return;
                if (e.keyCode != SWT.CONTROL && (e.stateMask & SWT.CONTROL) != 0)
                    return;
                if (e.keyCode != SWT.SHIFT && (e.stateMask & SWT.SHIFT) != 0)
                    return;

                int column = (columnIndex >= 0) ? columnIndex : 0;

                TableItem[] selection = table.getSelection();
                TableItem row = (selection.length == 0) ? table.getItem(table.getTopIndex()) : selection[0];
                table.showItem(row);
                cursor.setSelection(row, column);
                cursor.setVisible(true);
                cursor.setFocus();
            }
        });

        if (columnIndex >= 0) {
            // Limit the cursor to the data column
            cursor.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    if (cursor.getColumn() != columnIndex) {
                        cursor.setSelection(cursor.getRow(), columnIndex);
                    }

                }
            });
        }

    }

    private final void beginTableEdit(final TableCursor cursor, final ControlEditor editor,
            final CommitEditRunnable commitEditRunnable) {

        final Text text = new Text(cursor, SWT.NONE);
        TableItem row = cursor.getRow();
        final int column = cursor.getColumn();
        text.setText(row.getText(column));
        text.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                // close the text editor and copy the data over
                // when the user hits "ENTER"
                if (e.character == SWT.CR) {
                    TableItem row = cursor.getRow();
                    try {
                        endTableEdit(cursor, editor, commitEditRunnable, row, column, text.getText());
                    }
                    catch (Exception error) {
                        String title = "Edit Failed";
                        String message = error.getLocalizedMessage();
                        MessageDialog.openError(cursor.getShell(), title, message);
                        return;
                    }

                    text.dispose();
                }
                // close the text editor when the user hits "ESC"
                if (e.character == SWT.ESC) {
                    text.dispose();
                }
            }
        });
        // close the text editor when the changes focus
        text.addFocusListener(new FocusAdapter() {

            public void focusLost(FocusEvent e) {
                text.dispose();
            }
        });
        editor.setEditor(text);
        text.setFocus();
    }

    protected void endTableEdit(TableCursor tableCursor, ControlEditor editor,
            final CommitEditRunnable commitEditRunnable, TableItem row, int column, String newValue) throws Exception {

        if (commitEditRunnable == null) {
            return;
        }

        commitEditRunnable.setTableCursor(tableCursor);
        commitEditRunnable.setEditor(editor);
        commitEditRunnable.setRow(row);
        commitEditRunnable.setColumn(column);
        commitEditRunnable.setNewValue(newValue);

        BusyIndicator.showWhile(row.getDisplay(), commitEditRunnable);

        Exception error = commitEditRunnable.getError();
        if (error != null) {
            throw error;
        }
    }

    public static abstract class CommitEditRunnable implements Runnable {

        private TableCursor _TableCursor;
        private ControlEditor _Editor;
        private TableItem _Row;
        private int _Column;
        private String _NewValue;
        private Exception _Error;

        /**
         * Returns the tableCursor.
         * 
         * @return The tableCursor
         */
        public TableCursor getTableCursor() {
            return _TableCursor;
        }

        /**
         * Sets the tableCursor.
         * 
         * @param tableCursor the tableCursor to set
         */
        public void setTableCursor(TableCursor tableCursor) {
            _TableCursor = tableCursor;
        }

        /**
         * Returns the editor.
         * 
         * @return The editor
         */
        public ControlEditor getEditor() {
            return _Editor;
        }

        /**
         * Sets the editor.
         * 
         * @param editor the editor to set
         */
        public void setEditor(ControlEditor editor) {
            _Editor = editor;
        }

        /**
         * Returns the row.
         * 
         * @return The row
         */
        public TableItem getRow() {
            return _Row;
        }

        /**
         * Sets the row.
         * 
         * @param row the row to set
         */
        public void setRow(TableItem row) {
            _Row = row;
        }

        /**
         * Returns the column.
         * 
         * @return The column
         */
        public int getColumn() {
            return _Column;
        }

        /**
         * Sets the column.
         * 
         * @param column the column to set
         */
        public void setColumn(int column) {
            _Column = column;
        }

        /**
         * Returns the newValue.
         * 
         * @return The newValue
         */
        public String getNewValue() {
            return _NewValue;
        }

        /**
         * Sets the newValue.
         * 
         * @param newValue the newValue to set
         */
        public void setNewValue(String newValue) {
            _NewValue = newValue;
        }

        /**
         * Returns the error.
         * 
         * @return The error
         */
        public Exception getError() {
            return _Error;
        }

        /**
         * Sets the error.
         * 
         * @param error the error to set
         */
        public void setError(Exception error) {
            _Error = error;
        }

    }

}
