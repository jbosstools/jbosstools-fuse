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

package org.fusesource.ide.zk.core.resource;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Provides access to the images associated with file types.
 * 
 * @author Mark Masse
 */
public final class FileAssociationImages {

    private static FileAssociationImages __Default;

    /**
     * Returns the default, global singleton instance.
     * 
     * @return The default instance.
     */
    public static final FileAssociationImages getDefault() {
        if (__Default == null) {
            __Default = new FileAssociationImages();
        }
        return __Default;
    }

    private ImageRegistry _ExternalEditorImageRegistry;
    private ImageRegistry _IdeEditorImageRegistry;

    /**
     * Returns the image associated with the specified file name using the file extension.
     * 
     * @param fileName The file name.
     * @param preferIdeEditor Flag that determines if the IDE's internal editor image should be preferred.
     * @return
     */
    public Image get(String fileName, boolean preferIdeEditor) {

        Image image = null;

        if (preferIdeEditor) {
            if (_IdeEditorImageRegistry != null) {
                image = _IdeEditorImageRegistry.get(fileName);
                if (image != null) {
                    return image;
                }
            }

            image = getIdeEditorImage(fileName);

            if (image != null) {
                if (_IdeEditorImageRegistry == null) {
                    _IdeEditorImageRegistry = new ImageRegistry();
                }

                _IdeEditorImageRegistry.put(fileName, image);
                return image;
            }
        }

        int index = fileName.lastIndexOf('.');
        if (index > -1 && index < fileName.length() - 1) {
            String extension = fileName.substring(index);

            if (_ExternalEditorImageRegistry != null) {
                image = _ExternalEditorImageRegistry.get(extension);
                if (image != null) {
                    return image;
                }
            }

            image = getExternalEditorImage(extension);

            if (image != null) {
                if (_ExternalEditorImageRegistry == null) {
                    _ExternalEditorImageRegistry = new ImageRegistry();
                }

                _ExternalEditorImageRegistry.put(extension, image);
                return image;
            }
        }

        image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

        return image;
    }

    /**
     * Disposes this instance, freeing all resources.
     */
    public void dispose() {
        if (_IdeEditorImageRegistry != null) {
            _IdeEditorImageRegistry.dispose();
            _IdeEditorImageRegistry = null;
        }

        if (_ExternalEditorImageRegistry != null) {
            _ExternalEditorImageRegistry.dispose();
            _ExternalEditorImageRegistry = null;
        }
    }

    /**
     * Returns the icon of the external editor for files type with the specified extension.
     * 
     * @param extension The file extension.
     * @return The image associated with the file extension.
     */
    private Image getExternalEditorImage(String extension) {

        Program program = Program.findProgram(extension);
        ImageData imageData = (program == null ? null : program.getImageData());

        Image image = null;
        if (imageData != null) {
            image = new Image(Display.getCurrent(), imageData);
        }

        return image;
    }

    /**
     * Returns the icon of the IDE editor for the specified file name.
     * 
     * @param fileName The name of the file.
     * @return The IDE editor icon image for the file type.
     */
    private Image getIdeEditorImage(String fileName) {

        Image image = null;

        IWorkbench workBench = PlatformUI.getWorkbench();
        IEditorDescriptor editorDescriptor = workBench.getEditorRegistry().getDefaultEditor(fileName);
        if (editorDescriptor != null) {
            ImageDescriptor imageDescriptor = editorDescriptor.getImageDescriptor();
            if (imageDescriptor != null) {
                image = imageDescriptor.createImage();
            }
            else {
                Image sourceImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
                image = new Image(Display.getCurrent(), sourceImage, SWT.IMAGE_COPY);
            }
        }

        return image;
    }

}
