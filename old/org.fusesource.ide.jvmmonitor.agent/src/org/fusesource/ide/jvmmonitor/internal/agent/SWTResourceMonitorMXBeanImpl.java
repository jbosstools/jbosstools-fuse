/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The MXBean to monitor SWT resources.
 */
@SuppressWarnings("nls")
public class SWTResourceMonitorMXBeanImpl implements SWTResourceMonitorMXBean {

    /** The Display class. */
    private static final String DISPLAY_CLASS = "org.eclipse.swt.widgets.Display";

    /** The Device class. */
    private static final String DEVICE_CLASS = "org.eclipse.swt.graphics.Device";

    /** The FontData class. */
    private static final String FONT_DATA_CLASS = "org.eclipse.swt.graphics.FontData";

    /** The Color class. */
    private static final String COLOR_CLASS = "org.eclipse.swt.graphics.Color";

    /** The Font class. */
    private static final String FONT_CLASS = "org.eclipse.swt.graphics.Font";

    /** The field trackingLock in Device class. */
    private static final String TRACKING_LOCK_FIELD = "trackingLock";

    /** The field tracking in Device class. */
    private static final String TRACKING_FIELD = "tracking";

    /** The field objects in Device class. */
    private static final String OBJECTS_FIELD = "objects";

    /** The field errors in Device class. */
    private static final String ERRORS_FIELD = "errors";

    /** The method getStyle() in FontData class. */
    private static final String GET_STYLE_METHOD = "getStyle";

    /** The method getHeight() in FontData class. */
    private static final String GET_HEIGHT_METHOD = "getHeight";

    /** The method getName() in FontData class. */
    private static final String GET_NAME_METHOD = "getName";

    /** The method getFontData() in Font class. */
    private static final String GET_FONT_DATA_METHOD = "getFontData";

    /** The method getBlue() in Color class. */
    private static final String GET_BLUE_METHOD = "getBlue";

    /** The method getGreen() in Color class. */
    private static final String GET_GREEN_METHOD = "getGreen";

    /** The method getRed() in Color class. */
    private static final String GET_RED_METHOD = "getRed";

    /** The method getDefault() in Display class. */
    private static final String GET_DEFAULT_METHOD = "getDefault";

    /** The display object. */
    private Object displayObject;

    /** The display class. */
    private Class<?> displayClass;

    /** The device class. */
    private Class<?> deviceClass;

    /** The font class. */
    private Class<?> fontClass;

    /** The font data class. */
    private Class<?> fontDataClass;

    /** The color class. */
    private Class<?> colorClass;

    /** The resources. */
    private SWTResourceCompositeData[] resources;

    /** The instrumentation. */
    private Instrumentation inst;

    /**
     * The constructor.
     * 
     * @param inst
     *            The instrumentation
     */
    public SWTResourceMonitorMXBeanImpl(Instrumentation inst) {
        this.inst = inst;
        initialize();
    }

    /*
     * @see SWTResourceMonitorMXBean#setTracking(boolean)
     */
    @Override
    public void setTracking(boolean tracking) {
        if (tracking && (displayClass == null || deviceClass == null)) {
            initialize();
        }

        if (!isSuppoted()) {
            return;
        }

        try {
            if (tracking && !isTracking()) {
                clear();
            }

            Field field = deviceClass.getDeclaredField(TRACKING_FIELD);
            field.setAccessible(true);
            field.set(getDisplayObject(), tracking);
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_SET_RESOURCE_TRACKING_STATE);
        }
    }

    /*
     * @see SWTResourceMonitorMXBean#isTracking()
     */
    @Override
    public boolean isTracking() {
        if (!isSuppoted()) {
            return false;
        }

        try {
            Field field = deviceClass.getDeclaredField(TRACKING_FIELD);
            field.setAccessible(true);
            return (Boolean) field.get(getDisplayObject());
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_GET_RESOURCE_TRACKING_STATE);
            return false;
        }
    }

    /*
     * @see SWTResourceMonitorMXBean#getResources()
     */
    @Override
    public SWTResourceCompositeData[] getResources() {
        if (!isSuppoted()) {
            return new SWTResourceCompositeData[0];
        }

        try {
            refresh();
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_GET_RESOURCES);
        }
        return resources;
    }

    /*
     * @see SWTResourceMonitorMXBean#clear()
     */
    @Override
    public void clear() {
        if (!isSuppoted()) {
            return;
        }

        try {
            Field field = deviceClass.getDeclaredField(ERRORS_FIELD);
            field.setAccessible(true);
            field.set(getDisplayObject(), new Error[127]);

            field = deviceClass.getDeclaredField(OBJECTS_FIELD);
            field.setAccessible(true);
            field.set(getDisplayObject(), new Object[127]);
        } catch (Throwable t) {
            Agent.logError(t, Messages.CANNOT_CLEAR_RESOURCE_TRACKING_DATA);
        }
    }

    /**
     * Gets the state indicating if monitoring SWT resources is supported.
     * 
     * @return <tt>true</tt> if monitoring SWT resources is supported
     */
    public boolean isSuppoted() {
        return displayClass != null && deviceClass != null;
    }

    /**
     * Initializes by searching the SWT resource classes in loaded classes and
     * setting tracking lock in device class.
     */
    private void initialize() {
        for (@SuppressWarnings("rawtypes")
        Class clazz : inst.getAllLoadedClasses()) {
            String className = clazz.getName();
            if (DISPLAY_CLASS.equals(className)) {
                displayClass = clazz;
            } else if (DEVICE_CLASS.equals(className)) {
                deviceClass = clazz;
            } else if (FONT_CLASS.equals(className)) {
                fontClass = clazz;
            } else if (FONT_DATA_CLASS.equals(className)) {
                fontDataClass = clazz;
            } else if (COLOR_CLASS.equals(className)) {
                colorClass = clazz;
            }
        }

        if (deviceClass == null) {
            return;
        }

        try {
            Field field = deviceClass.getDeclaredField(TRACKING_LOCK_FIELD);
            field.setAccessible(true);
            field.set(getDisplayObject(), new Object());
        } catch (Throwable t) {
            deviceClass = null;
            displayClass = null;
        }
    }

    /**
     * Refreshes the resources stored in this class.
     * 
     * @throws Throwable
     */
    private void refresh() throws SecurityException, Throwable {
        Field field = deviceClass.getDeclaredField(OBJECTS_FIELD);
        field.setAccessible(true);
        Object[] objects = (Object[]) field.get(getDisplayObject());

        field = deviceClass.getDeclaredField(ERRORS_FIELD);
        field.setAccessible(true);
        Error[] errors = (Error[]) field.get(getDisplayObject());

        if (objects == null || errors == null
                || objects.length != errors.length) {
            resources = new SWTResourceCompositeData[0];
            return;
        }

        List<SWTResourceCompositeData> resourcesList = new ArrayList<SWTResourceCompositeData>();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                continue;
            }

            SWTResourceCompositeData resource = new SWTResourceCompositeData(
                    getName(objects[i]), getStackTrace(errors[i]));
            resourcesList.add(resource);
        }
        resources = resourcesList
                .toArray(new SWTResourceCompositeData[resourcesList.size()]);
    }

    /**
     * Gets the stack trace as an array of composite data.
     * 
     * @param error
     *            The error
     * @return The stack trace
     */
    private List<StackTraceElementCompositeData> getStackTrace(Error error) {
        List<StackTraceElementCompositeData> list = new ArrayList<StackTraceElementCompositeData>();
        if (error != null) {
            for (StackTraceElement element : error.getStackTrace()) {
                list.add(new StackTraceElementCompositeData(element));
            }
        }
        return list;
    }

    /**
     * Gets the display object.
     * 
     * @return The display object
     * @throws Throwable
     */
    private Object getDisplayObject() throws Throwable {
        if (displayObject == null) {
            Method method = displayClass.getDeclaredMethod(GET_DEFAULT_METHOD);
            displayObject = method.invoke(null);
        }
        return displayObject;
    }

    /**
     * Gets the name corresponding to the given resource object.
     * 
     * @param object
     *            The resource object
     * @return The name
     */
    private String getName(Object object) throws Throwable {
        if (FONT_CLASS.equals(object.getClass().getName())) {
            if (fontClass == null || fontDataClass == null) {
                searchFontClass();
            }
            if (fontClass != null && fontDataClass != null) {
                return getFontName(object);
            }
        } else if (COLOR_CLASS.equals(object.getClass().getName())) {
            if (colorClass == null) {
                searchColorClass();
            }
            if (colorClass != null) {
                return getColorName(object);
            }
        }
        return object.getClass().getSimpleName() + " {hashCode="
                + System.identityHashCode(object) + "}";
    }

    /**
     * Searches Color class.
     */
    private void searchColorClass() {
        for (@SuppressWarnings("rawtypes")
        Class clazz : inst.getAllLoadedClasses()) {
            String className = clazz.getName();
            if (COLOR_CLASS.equals(className)) {
                colorClass = clazz;
            }
        }
    }

    /**
     * Searches Font class and FontData class.
     */
    private void searchFontClass() {
        for (@SuppressWarnings("rawtypes")
        Class clazz : inst.getAllLoadedClasses()) {
            String className = clazz.getName();
            if (FONT_CLASS.equals(className)) {
                fontClass = clazz;
            } else if (FONT_DATA_CLASS.equals(className)) {
                fontDataClass = clazz;
            }
        }
    }

    /**
     * Gets the color name.
     * 
     * @param object
     *            The color object
     * @return The color name
     */
    private String getColorName(Object object) throws Throwable {
        Method method = colorClass.getDeclaredMethod(GET_RED_METHOD);
        Integer red = (Integer) method.invoke(object);
        method = colorClass.getDeclaredMethod(GET_GREEN_METHOD);
        Integer green = (Integer) method.invoke(object);
        method = colorClass.getDeclaredMethod(GET_BLUE_METHOD);
        Integer blue = (Integer) method.invoke(object);
        return "Color {r=" + red + ", g=" + green + ", b=" + blue
                + ", hashCode=" + System.identityHashCode(object) + "}";
    }

    /**
     * Gets the font name.
     * 
     * @param object
     *            The font object
     * @return The font name
     */
    private String getFontName(Object object) throws Throwable {
        Method method = fontClass.getDeclaredMethod(GET_FONT_DATA_METHOD);
        Object[] fontData = (Object[]) method.invoke(object);
        method = fontDataClass.getDeclaredMethod(GET_NAME_METHOD);
        String name = (String) method.invoke(fontData[0]);
        method = fontDataClass.getDeclaredMethod(GET_HEIGHT_METHOD);
        Integer height = (Integer) method.invoke(fontData[0]);
        method = fontDataClass.getDeclaredMethod(GET_STYLE_METHOD);
        Integer style = (Integer) method.invoke(fontData[0]);
        String styleString;
        if (style == 0) {
            styleString = "SWT.NORMAL";
        } else if (style == 1 << 0) {
            styleString = "SWT.BOLD";
        } else if (style == 1 << 1) {
            styleString = "SWT.ITALIC";
        } else {
            throw new IllegalStateException();
        }
        return "Font {name=" + name + ", height=" + height + ", style="
                + styleString + ", hashCode=" + System.identityHashCode(object)
                + "}";
    }
}
