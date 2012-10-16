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

package org.fusesource.ide.zk.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fusesource.ide.zk.core.resource.FileAssociationImages;

import org.osgi.framework.BundleContext;

/**
 * The main plug-in for the Eclipse Core library. Handles the loading of the shared/common images and defines keys for
 * retrieving images from the {@link ImageRegistry}.
 */
public class EclipseCoreActivator extends AbstractUIPlugin {

    /**
     * The unique id for this plug-in.
     */
    public static final String PLUGIN_ID = "org.fusesource.ide.zk.core";

    // The shared instance
    private static EclipseCoreActivator __Plugin;

    //
    // Action Image Keys
    //

    /**
     * The "Delete" action image key.
     */
    public static final String IMAGE_KEY_ACTION_DELETE = "IMAGE_KEY_ACTION_DELETE";

    /**
     * The "Refresh" action image key.
     */
    public static final String IMAGE_KEY_ACTION_REFRESH = "IMAGE_KEY_ACTION_REFRESH";

    /**
     * The "Table Edit" action image key.
     */
    public static final String IMAGE_KEY_ACTION_TABLE_EDIT = "IMAGE_KEY_ACTION_TABLE_EDIT";

    //
    // Object Image Keys
    //

    public static final String IMAGE_KEY_OBJECT_EDIT = "IMAGE_KEY_OBJECT_EDIT";
    public static final String IMAGE_KEY_OBJECT_HOME = "IMAGE_KEY_OBJECT_HOME";
    public static final String IMAGE_KEY_OBJECT_INFORMATION = "IMAGE_KEY_OBJECT_INFORMATION";
    public static final String IMAGE_KEY_OBJECT_MAIN_TAB = "IMAGE_KEY_OBJECT_MAIN_TAB";
    public static final String IMAGE_KEY_OBJECT_PROPERTIES = "IMAGE_KEY_OBJECT_PROPERTIES";
    public static final String IMAGE_KEY_OBJECT_SERVER = "IMAGE_KEY_OBJECT_SERVER";
    public static final String IMAGE_KEY_OBJECT_SERVER_ADMIN = "IMAGE_KEY_OBJECT_SERVER_ADMIN";
    public static final String IMAGE_KEY_OBJECT_SERVER_LARGE = "IMAGE_KEY_OBJECT_SERVER_LARGE";
    public static final String IMAGE_KEY_OBJECT_SERVERS = "IMAGE_KEY_OBJECT_SERVERS";
    public static final String IMAGE_KEY_OBJECT_SERVERS_LARGE = "IMAGE_KEY_OBJECT_SERVERS_LARGE";

    //
    // Wizard Banner Image Keys
    //

    public static final String IMAGE_KEY_WIZARD_BANNER_ADD_SERVER = "IMAGE_KEY_WIZARD_BANNER_ADD_SERVER";

    //
    // Action Image Paths
    //

    private static final String IMAGE_PATH_ACTION = "resources/images/png/actions/";
    private static final String IMAGE_PATH_ACTION_DELETE = IMAGE_PATH_ACTION + "delete.png";
    private static final String IMAGE_PATH_ACTION_REFRESH = IMAGE_PATH_ACTION + "refresh.png";
    private static final String IMAGE_PATH_ACTION_TABLE_EDIT = IMAGE_PATH_ACTION + "table-edit.png";

    //
    // Object Image Paths
    //

    private static final String IMAGE_PATH_OBJECT = "resources/images/png/objects/";
    private static final String IMAGE_PATH_OBJECT_EDIT = IMAGE_PATH_OBJECT + "edit.png";
    private static final String IMAGE_PATH_OBJECT_HOME = IMAGE_PATH_OBJECT + "home.png";
    private static final String IMAGE_PATH_OBJECT_INFORMATION = IMAGE_PATH_OBJECT + "information.png";
    private static final String IMAGE_PATH_OBJECT_MAIN_TAB = IMAGE_PATH_OBJECT + "main-tab.png";
    private static final String IMAGE_PATH_OBJECT_PROPERTIES = IMAGE_PATH_OBJECT + "properties.png";
    private static final String IMAGE_PATH_OBJECT_SERVER = IMAGE_PATH_OBJECT + "server.png";
    private static final String IMAGE_PATH_OBJECT_SERVER_ADMIN = IMAGE_PATH_OBJECT + "server_admin.png";
    private static final String IMAGE_PATH_OBJECT_SERVER_LARGE = IMAGE_PATH_OBJECT + "server_large.png";
    private static final String IMAGE_PATH_OBJECT_SERVERS = IMAGE_PATH_OBJECT + "servers.png";
    private static final String IMAGE_PATH_OBJECT_SERVERS_LARGE = IMAGE_PATH_OBJECT + "servers_large.png";

    //
    // Wizard Banner Image Paths
    //

    private static final String IMAGE_PATH_WIZARD_BANNER = "resources/images/png/wizard-banners/";
    private static final String IMAGE_PATH_WIZARD_BANNER_ADD_SERVER = IMAGE_PATH_WIZARD_BANNER + "add-server.png";

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Returns the managed {@link Image} for the specified key.
     * 
     * @param key One of the IMAGE_KEY constants defined by this class.
     * @return The managed {@link Image} for the specified key.
     */
    public static Image getManagedImage(String key) {
        return __Plugin.getImageRegistry().get(key);
    }

    /**
     * Returns the managed {@link ImageDescriptor} for the specified key.
     * 
     * @param key One of the IMAGE_KEY constants defined by this class.
     * @return The managed {@link ImageDescriptor} for the specified key.
     */
    public static ImageDescriptor getManagedImageDescriptor(String key) {
        return __Plugin.getImageRegistry().getDescriptor(key);
    }

    /**
     * The constructor. </br></br> NOTE: This is called by the IDE framework to load the plug-in. It should not be
     * called directly.
     * 
     * @see #getDefault()
     */
    public EclipseCoreActivator() {
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance.
     */
    public static EclipseCoreActivator getDefault() {
        return __Plugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        __Plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        __Plugin = null;

        FileAssociationImages fileAssociationImages = FileAssociationImages.getDefault();
        fileAssociationImages.dispose();

        super.stop(context);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry imageRegistry) {
        super.initializeImageRegistry(imageRegistry);

        imageRegistry.put(IMAGE_KEY_ACTION_DELETE, getImageDescriptor(IMAGE_PATH_ACTION_DELETE));
        imageRegistry.put(IMAGE_KEY_ACTION_REFRESH, getImageDescriptor(IMAGE_PATH_ACTION_REFRESH));
        imageRegistry.put(IMAGE_KEY_ACTION_TABLE_EDIT, getImageDescriptor(IMAGE_PATH_ACTION_TABLE_EDIT));

        imageRegistry.put(IMAGE_KEY_OBJECT_EDIT, getImageDescriptor(IMAGE_PATH_OBJECT_EDIT));
        imageRegistry.put(IMAGE_KEY_OBJECT_HOME, getImageDescriptor(IMAGE_PATH_OBJECT_HOME));
        imageRegistry.put(IMAGE_KEY_OBJECT_INFORMATION, getImageDescriptor(IMAGE_PATH_OBJECT_INFORMATION));
        imageRegistry.put(IMAGE_KEY_OBJECT_MAIN_TAB, getImageDescriptor(IMAGE_PATH_OBJECT_MAIN_TAB));
        imageRegistry.put(IMAGE_KEY_OBJECT_PROPERTIES, getImageDescriptor(IMAGE_PATH_OBJECT_PROPERTIES));
        imageRegistry.put(IMAGE_KEY_OBJECT_SERVER, getImageDescriptor(IMAGE_PATH_OBJECT_SERVER));
        imageRegistry.put(IMAGE_KEY_OBJECT_SERVER_ADMIN, getImageDescriptor(IMAGE_PATH_OBJECT_SERVER_ADMIN));
        imageRegistry.put(IMAGE_KEY_OBJECT_SERVER_LARGE, getImageDescriptor(IMAGE_PATH_OBJECT_SERVER_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_SERVERS, getImageDescriptor(IMAGE_PATH_OBJECT_SERVERS));
        imageRegistry.put(IMAGE_KEY_OBJECT_SERVERS_LARGE, getImageDescriptor(IMAGE_PATH_OBJECT_SERVERS_LARGE));

        imageRegistry.put(IMAGE_KEY_WIZARD_BANNER_ADD_SERVER, getImageDescriptor(IMAGE_PATH_WIZARD_BANNER_ADD_SERVER));
    }

}
