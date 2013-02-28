/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.zk.jmx;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.fusesource.ide.zk.jmx.data.JmxConnection;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModel;
import org.fusesource.ide.zk.jmx.model.JmxConnectionModelSource;
import org.fusesource.ide.zk.jmx.runtime.JmxConnectionDescriptorFiles;
import org.fusesource.ide.zk.core.model.DataModelManager;

import java.io.File;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JmxActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.fusesource.ide.zk.jmx";

    //
    // Action Image Keys
    //

    public static final String IMAGE_KEY_ACTION_NEW_JMX_CONNECTION = "IMAGE_KEY_ACTION_NEW_JMX_CONNECTION";

    //
    // Object Image Keys
    //

    public static final String IMAGE_KEY_OBJECT_JMX_CONNECTION = "IMAGE_KEY_OBJECT_JMX_CONNECTION";
    public static final String IMAGE_KEY_OBJECT_JMX_CONNECTION_LARGE = "IMAGE_KEY_OBJECT_JMX_CONNECTION_LARGE";
    public static final String IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED = "IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED";
    public static final String IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED_LARGE = "IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED_LARGE";
    public static final String IMAGE_KEY_OBJECT_JMX_DOC = "IMAGE_KEY_OBJECT_JMX_DOC";
    public static final String IMAGE_KEY_OBJECT_MBEAN = "IMAGE_KEY_OBJECT_MBEAN";
    public static final String IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE = "IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_LARGE = "IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_VALUE = "IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_VALUE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTES = "IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTES";
    public static final String IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTES_LARGE = "IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTES_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_DOMAIN = "IMAGE_KEY_OBJECT_MBEAN_DOMAIN";
    public static final String IMAGE_KEY_OBJECT_MBEAN_DOMAIN_LARGE = "IMAGE_KEY_OBJECT_MBEAN_DOMAIN_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_LARGE = "IMAGE_KEY_OBJECT_MBEAN_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_NOTIFICATION = "IMAGE_KEY_OBJECT_MBEAN_NOTIFICATION";
    public static final String IMAGE_KEY_OBJECT_MBEAN_NOTIFICATION_LARGE = "IMAGE_KEY_OBJECT_MBEAN_NOTIFICATION_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_NOTIFICATIONS = "IMAGE_KEY_OBJECT_MBEAN_NOTIFICATIONS";
    public static final String IMAGE_KEY_OBJECT_MBEAN_NOTIFICATIONS_LARGE = "IMAGE_KEY_OBJECT_MBEAN_NOTIFICATIONS_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_OPERATION = "IMAGE_KEY_OBJECT_MBEAN_OPERATION";
    public static final String IMAGE_KEY_OBJECT_MBEAN_OPERATION_LARGE = "IMAGE_KEY_OBJECT_MBEAN_OPERATION_LARGE";
    public static final String IMAGE_KEY_OBJECT_MBEAN_OPERATIONS = "IMAGE_KEY_OBJECT_MBEAN_OPERATIONS";
    public static final String IMAGE_KEY_OBJECT_MBEAN_OPERATIONS_LARGE = "IMAGE_KEY_OBJECT_MBEAN_OPERATIONS_LARGE";
    public static final String IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE = "IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE";
    public static final String IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE_LARGE = "IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE_LARGE";

    //
    // Wizard Banner Image Keys
    //

    public static final String IMAGE_KEY_WIZARD_BANNER_NEW_JMX_CONNECTION = "IMAGE_KEY_WIZARD_BANNER_NEW_JMX_CONNECTION";

    //
    // Wizard (Extension) Image Keys
    //

    public static final String IMAGE_KEY_WIZARD_NEW_JMX_CONNECTION = "IMAGE_KEY_WIZARD_NEW_JMX_CONNECTION";

    //
    // Action Image Paths
    //

    private static final String IMAGE_PATH_ACTION = "resources/images/png/actions/";
    private static final String IMAGE_PATH_ACTION_NEW_JMX_CONNECTION = IMAGE_PATH_ACTION + "new-jmx-connection.png";

    //
    // Object Image Paths
    //

    private static final String IMAGE_PATH_OBJECT = "resources/images/png/objects/";
    private static final String IMAGE_PATH_OBJECT_JMX_CONNECTION = IMAGE_PATH_OBJECT + "jmx-connection.png";
    private static final String IMAGE_PATH_OBJECT_JMX_CONNECTION_LARGE = IMAGE_PATH_OBJECT + "jmx-connection_large.png";
    private static final String IMAGE_PATH_OBJECT_JMX_CONNECTION_NOT_CONNECTED = IMAGE_PATH_OBJECT
            + "jmx-connection_not_connected.png";
    private static final String IMAGE_PATH_OBJECT_JMX_CONNECTION_NOT_CONNECTED_LARGE = IMAGE_PATH_OBJECT
            + "jmx-connection_not_connected_large.png";

    private static final String IMAGE_PATH_OBJECT_JMX_DOC = IMAGE_PATH_OBJECT + "jmx-doc.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN = IMAGE_PATH_OBJECT + "mbean.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTE = IMAGE_PATH_OBJECT + "mbean-attribute.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTE_LARGE = IMAGE_PATH_OBJECT
            + "mbean-attribute_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTE_VALUE = IMAGE_PATH_OBJECT
            + "mbean-attribute-value.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTES = IMAGE_PATH_OBJECT + "mbean-attributes.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTES_LARGE = IMAGE_PATH_OBJECT
            + "mbean-attributes_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_DOMAIN = IMAGE_PATH_OBJECT + "mbean-domain.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_DOMAIN_LARGE = IMAGE_PATH_OBJECT + "mbean-domain_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_LARGE = IMAGE_PATH_OBJECT + "mbean_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_NOTIFICATION = IMAGE_PATH_OBJECT + "mbean-notification.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_NOTIFICATION_LARGE = IMAGE_PATH_OBJECT
            + "mbean-notification_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_NOTIFICATIONS = IMAGE_PATH_OBJECT + "mbean-notifications.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_NOTIFICATIONS_LARGE = IMAGE_PATH_OBJECT
            + "mbean-notifications_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_OPERATION = IMAGE_PATH_OBJECT + "mbean-operation.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_OPERATION_LARGE = IMAGE_PATH_OBJECT
            + "mbean-operation_large.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_OPERATIONS = IMAGE_PATH_OBJECT + "mbean-operations.png";
    private static final String IMAGE_PATH_OBJECT_MBEAN_OPERATIONS_LARGE = IMAGE_PATH_OBJECT
            + "mbean-operations_large.png";
    private static final String IMAGE_PATH_OBJECT_OBJECT_NAME_KEY_VALUE = IMAGE_PATH_OBJECT
            + "object-name-key-value.png";
    private static final String IMAGE_PATH_OBJECT_OBJECT_NAME_KEY_VALUE_LARGE = IMAGE_PATH_OBJECT
            + "object-name-key-value_large.png";

    //
    // Wizard (Extension) Image Paths
    //

    private static final String IMAGE_PATH_WIZARD = "resources/images/png/wizards/";
    private static final String IMAGE_PATH_WIZARD_NEW_JMX_CONNECTION = IMAGE_PATH_WIZARD + "new-jmx-connection.png";

    //
    // Wizard Banner Image Paths
    //

    private static final String IMAGE_PATH_WIZARD_BANNER = "resources/images/png/wizard-banners/";
    private static final String IMAGE_PATH_WIZARD_BANNER_NEW_JMX_CONNECTION = IMAGE_PATH_WIZARD_BANNER
            + "new-jmx-connection.png";

    // The shared instance
    private static JmxActivator __Plugin;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static JmxActivator getDefault() {
        return __Plugin;
    }

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
     * TODO: Comment.
     * 
     * @param key
     * @return
     */
    public static Image getManagedImage(String key) {
        return __Plugin.getImageRegistry().get(key);
    }

    /**
     * TODO: Comment.
     * 
     * @param key
     * @return
     */
    public static ImageDescriptor getManagedImageDescriptor(String key) {
        return __Plugin.getImageRegistry().getDescriptor(key);
    }

    public static void reportError(Throwable t) {
        Throwable cause = t.getCause();
        if (cause != null && cause != t) {
            reportError(cause);
            return;
        }

        Status status = new Status(IStatus.ERROR, PLUGIN_ID, t.getLocalizedMessage(), t);
        StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
    }

    private JmxConnectionDescriptorFiles _JmxConnectionDescriptorFiles;

    private DataModelManager<JmxConnectionModel, JmxConnectionDescriptor, JmxConnection> _JmxConnectionModelManager;

    /**
     * The constructor
     */
    public JmxActivator() {
    }

    public JmxConnectionDescriptorFiles getJmxConnectionDescriptorFiles() {
        return _JmxConnectionDescriptorFiles;
    }

    public DataModelManager<JmxConnectionModel, JmxConnectionDescriptor, JmxConnection> getJmxConnectionModelManager() {
        return _JmxConnectionModelManager;
    }

    public File getJmxConnectionsDirectory() {
        return getStateLocation().append("JmxConnections").toFile();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        __Plugin = this;

        _JmxConnectionDescriptorFiles = new JmxConnectionDescriptorFiles(getJmxConnectionsDirectory());

        JmxConnectionModelSource jmxConnectionModelSource = new JmxConnectionModelSource(_JmxConnectionDescriptorFiles);
        _JmxConnectionModelManager = new DataModelManager<JmxConnectionModel, JmxConnectionDescriptor, JmxConnection>(
                jmxConnectionModelSource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        __Plugin = null;

        _JmxConnectionModelManager.destroy();
        _JmxConnectionModelManager = null;

        super.stop(context);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry imageRegistry) {
        super.initializeImageRegistry(imageRegistry);

        imageRegistry
                .put(IMAGE_KEY_ACTION_NEW_JMX_CONNECTION, getImageDescriptor(IMAGE_PATH_ACTION_NEW_JMX_CONNECTION));

        imageRegistry.put(IMAGE_KEY_OBJECT_JMX_CONNECTION, getImageDescriptor(IMAGE_PATH_OBJECT_JMX_CONNECTION));
        imageRegistry.put(IMAGE_KEY_OBJECT_JMX_CONNECTION_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_JMX_CONNECTION_LARGE));

        imageRegistry.put(IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED,
                getImageDescriptor(IMAGE_PATH_OBJECT_JMX_CONNECTION_NOT_CONNECTED));
        imageRegistry.put(IMAGE_KEY_OBJECT_JMX_CONNECTION_NOT_CONNECTED_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_JMX_CONNECTION_NOT_CONNECTED_LARGE));

        imageRegistry.put(IMAGE_KEY_OBJECT_JMX_DOC, getImageDescriptor(IMAGE_PATH_OBJECT_JMX_DOC));

        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_LARGE, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_DOMAIN, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_DOMAIN));
        imageRegistry
                .put(IMAGE_KEY_OBJECT_MBEAN_DOMAIN_LARGE, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_DOMAIN_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTE_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTES, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTES));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTES_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTES_LARGE));

        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_ATTRIBUTE_VALUE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_ATTRIBUTE_VALUE));

        imageRegistry
                .put(IMAGE_KEY_OBJECT_MBEAN_NOTIFICATION, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_NOTIFICATION));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_NOTIFICATION_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_NOTIFICATION_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_NOTIFICATIONS,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_NOTIFICATIONS));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_NOTIFICATIONS_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_NOTIFICATIONS_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_OPERATION, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_OPERATION));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_OPERATION_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_OPERATION_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_OPERATIONS, getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_OPERATIONS));
        imageRegistry.put(IMAGE_KEY_OBJECT_MBEAN_OPERATIONS_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_MBEAN_OPERATIONS_LARGE));
        imageRegistry.put(IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE,
                getImageDescriptor(IMAGE_PATH_OBJECT_OBJECT_NAME_KEY_VALUE));
        imageRegistry.put(IMAGE_KEY_OBJECT_OBJECT_NAME_KEY_VALUE_LARGE,
                getImageDescriptor(IMAGE_PATH_OBJECT_OBJECT_NAME_KEY_VALUE_LARGE));

        imageRegistry
                .put(IMAGE_KEY_WIZARD_NEW_JMX_CONNECTION, getImageDescriptor(IMAGE_PATH_WIZARD_NEW_JMX_CONNECTION));

        imageRegistry.put(IMAGE_KEY_WIZARD_BANNER_NEW_JMX_CONNECTION,
                getImageDescriptor(IMAGE_PATH_WIZARD_BANNER_NEW_JMX_CONNECTION));
    }
}
