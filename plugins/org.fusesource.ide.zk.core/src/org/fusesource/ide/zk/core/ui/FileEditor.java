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

package org.fusesource.ide.zk.core.ui;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.fusesource.ide.zk.core.resource.FileAssociationImages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Provides a set of utility methods for reading and opening files.
 * 
 * @author Mark Masse
 */
public final class FileEditor {

    public static FileEditor createTempFileEditor(String prefix, String suffix, File directory, byte[] data,
            boolean preferIdeEditor) throws IOException {

        File file = null;
        file = File.createTempFile(prefix, suffix, directory);
        file.deleteOnExit();

        if (data != null) {
            FileOutputStream out = new FileOutputStream(file);

            try {
                out.write(data);
            }
            finally {
                out.close();
            }
        }

        return new FileEditor(file, preferIdeEditor, true);
    }

    public static IEditorPart editFile(File file, boolean preferIdeEditor) throws IOException, PartInitException {

        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("Invalid file: '" + file + "'");
        }

        IWorkbench workBench = PlatformUI.getWorkbench();
        IWorkbenchPage page = workBench.getActiveWorkbenchWindow().getActivePage();
        IPath location = Path.fromOSString(file.getAbsolutePath());

        IFileStore fileStore = EFS.getLocalFileSystem().getStore(location);
        FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput(fileStore);

        String editorId = IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID;
        if (preferIdeEditor) {
            IEditorDescriptor editorDescriptor = workBench.getEditorRegistry().getDefaultEditor(file.getName());
            if (editorDescriptor != null) {
                editorId = editorDescriptor.getId();
            }
        }

        return page.openEditor(fileStoreEditorInput, editorId);
    }

    public static Image getFileAssociationImage(File file, boolean preferIdeEditor) {
        return getFileAssociationImage(file.getName(), preferIdeEditor);
    }

    public static Image getFileAssociationImage(String fileName, boolean preferIdeEditor) {
        return FileAssociationImages.getDefault().get(fileName, preferIdeEditor);
    }

    public static byte[] readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];

        try {
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Failed to completely read file '" + file.getName() + "'.");
            }
        }
        finally {
            is.close();
        }

        return bytes;
    }

    private final File _File;
    private Image _FileAssociationImage;
    private long _LastRead;
    private final boolean _PreferIdeEditor;
    private final boolean _TempFile;

    public FileEditor(File file) {
        this(file, true, false);
    }

    public FileEditor(File file, boolean preferIdeEditor) {
        this(file, preferIdeEditor, false);
    }

    public FileEditor(File file, boolean preferIdeEditor, boolean tempFile) {
        _File = file;
        _LastRead = _File.lastModified();
        _PreferIdeEditor = preferIdeEditor;
        _TempFile = tempFile;
    }

    /**
     * Edit the file in the associated IDE or external editor.
     * 
     * @throws PartInitException
     */
    public IEditorPart edit() throws IOException, PartInitException {
        return editFile(getFile(), isPreferIdeEditor());
    }

    /**
     * Returns the file.
     * 
     * @return The file
     */
    public File getFile() {
        return _File;
    }

    /**
     * Returns the image icon associated with the file's type.
     * 
     * @return The image icon associated with the file's type.
     */
    public Image getFileAssociationImage() {
        if (_FileAssociationImage == null) {
            _FileAssociationImage = getFileAssociationImage(getFile(), isPreferIdeEditor());
        }

        return _FileAssociationImage;
    }

    /**
     * Returns the time that the file was last modified.
     * 
     * @return The time that the file was last modified.
     */
    public long getLastModified() {
        return getFile().lastModified();
    }

    /**
     * Returns the time that the file was last read.
     * 
     * @return The time that the file was last read.
     */
    public long getLastRead() {
        return _LastRead;
    }

    /**
     * Returns true if the file has been modified since the last call to read.
     * 
     * @return true if the file has been modified since the last call to read.
     */
    public boolean isModifiedSinceLastRead() {
        return getLastModified() > getLastRead();
    }

    public boolean isPreferIdeEditor() {
        return _PreferIdeEditor;
    }

    /**
     * Read the file contents.
     * 
     * @return
     * @throws IOException
     */
    public byte[] read() throws IOException {
        File file = getFile();
        byte[] bytes = readFile(file);
        _LastRead = file.lastModified();
        return bytes;
    }

    /**
     * Returns the tempFile.
     * 
     * @return The tempFile
     */
    public boolean isTempFile() {
        return _TempFile;
    }

    /**
     * Disposes of this FileEditor, deleting the associated file if it is temporary.
     * 
     * @see #isTempFile()
     */
    public void dispose() {
        if (isTempFile()) {
            File file = getFile();
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
