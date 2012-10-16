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

package org.fusesource.ide.zk.core.reflect;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A {@link DelegatingInvocationHandler} that ensures SWT thread-safety when invoking methods on the delegate.
 * 
 * @see Proxy
 * @see InvocationHandler
 * 
 * @author Mark Masse
 */
public final class SwtThreadSafeDelegatingInvocationHandler extends DelegatingInvocationHandler {

    /**
     * Create a new {@link Proxy} instance with the specified {@link IWidgetProvider} delegate and interface type.
     *
     * @param delegate The {@link IWidgetProvider} delegate.
     * @param interfaceType The interface to proxy.
     * @return A new {@link Proxy} instance that synchronously delegates calls to a {@link SwtThreadSafeDelegatingInvocationHandler} instance. 
     */
    public static Object createProxyInstance(IWidgetProvider delegate, Class<?> interfaceType) {
        return createProxyInstance(delegate, interfaceType, false);
    }

    /**
     * Create a new {@link Proxy} instance with the specified {@link Widget} delegate and interface type.
     *
     * @param delegate The {@link Widget} delegate.
     * @param interfaceType The interface to proxy.
     * @return A new {@link Proxy} instance that synchronously delegates calls to a {@link SwtThreadSafeDelegatingInvocationHandler} instance. 
     */
    public static Object createProxyInstance(Widget delegate, Class<?> interfaceType) {
        return createProxyInstance(delegate, interfaceType, false);
    }

    /**
     * Create a new {@link Proxy} instance with the specified {@link IWidgetProvider} delegate and interface type.
     *
     * @param delegate The {@link IWidgetProvider} delegate.
     * @param interfaceType The interface to proxy.
     * @param async The invocation type:  <code>true</code> to invoke the delegate methods asynchronously.
     * @return A new {@link Proxy} instance that delegates calls to a {@link SwtThreadSafeDelegatingInvocationHandler} instance. 
     */
    public static Object createProxyInstance(IWidgetProvider delegate, Class<?> interfaceType, boolean async) {
        SwtThreadSafeDelegatingInvocationHandler invocationHandler = new SwtThreadSafeDelegatingInvocationHandler(
                delegate, async);
        return newProxyInstance(interfaceType, invocationHandler);
    }

    /**
     * Create a new {@link Proxy} instance with the specified {@link Widget} delegate and interface type.
     *
     * @param delegate The {@link Widget} delegate.
     * @param interfaceType The interface to proxy.
     * @param async The invocation type:  <code>true</code> to invoke the delegate methods asynchronously.
     * @return A new {@link Proxy} instance that delegates calls to a {@link SwtThreadSafeDelegatingInvocationHandler} instance. 
     */
    public static Object createProxyInstance(Widget delegate, Class<?> interfaceType, boolean async) {
        SwtThreadSafeDelegatingInvocationHandler invocationHandler = new SwtThreadSafeDelegatingInvocationHandler(
                delegate, async);
        return newProxyInstance(interfaceType, invocationHandler);
    }

    private static Object newProxyInstance(Class<?> interfaceType,
            SwtThreadSafeDelegatingInvocationHandler invocationHandler) {
        return Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[] { interfaceType }, invocationHandler);
    }

    private final boolean _Async;

    /**
     * Default constructor that creates a {@link SwtThreadSafeDelegatingInvocationHandler} with a <code>null</code>
     * {@link Widget} and synchronous invocation type.
     */
    public SwtThreadSafeDelegatingInvocationHandler() {
        this(false);
    }

    /**
     * Constructor that creates a {@link SwtThreadSafeDelegatingInvocationHandler} with a <code>null</code>
     * {@link Widget} and the specified invocation type.
     * 
     * @param async The invocation type.
     */
    public SwtThreadSafeDelegatingInvocationHandler(boolean async) {
        this((Widget) null, async);
    }

    /**
     * Constructor that creates a {@link SwtThreadSafeDelegatingInvocationHandler} with the specified
     * {@link IWidgetProvider} delegate and synchronous invocation type.
     * 
     * @param delegate The {@link IWidgetProvider} delegate.
     */
    public SwtThreadSafeDelegatingInvocationHandler(IWidgetProvider delegate) {
        this(delegate, false);
    }

    /**
     * Constructor that creates a {@link SwtThreadSafeDelegatingInvocationHandler} with the specified
     * {@link IWidgetProvider} delegate and invocation type.
     * 
     * @param delegate The {@link IWidgetProvider} delegate.
     * @param async The invocation type.
     */
    public SwtThreadSafeDelegatingInvocationHandler(IWidgetProvider delegate, boolean async) {
        super(delegate);
        _Async = async;
    }

    /**
     * Constructor that creates a {@link SwtThreadSafeDelegatingInvocationHandler} with the specified
     * {@link Widget} delegate and synchronous invocation type.
     * 
     * @param delegate The {@link Widget} delegate.
     */
    public SwtThreadSafeDelegatingInvocationHandler(Widget delegate) {
        this(delegate, false);
    }

    /**
     * Constructor that creates a {@link SwtThreadSafeDelegatingInvocationHandler} with the specified
     * {@link Widget} delegate and invocation type.
     * 
     * @param delegate The {@link Widget} delegate.
     * @param async The invocation type.
     */
    public SwtThreadSafeDelegatingInvocationHandler(Widget delegate, boolean async) {
        super(delegate);
        _Async = async;
    }

    /**
     * Returns the widget.
     * 
     * @return The widget
     */
    public final Widget getWidget() {
        Object delegate = getDelegate();
        if (delegate instanceof IWidgetProvider) {
            return ((IWidgetProvider) delegate).getWidget();
        }
        return (Widget) delegate;
    }

    @Override
    protected Object subInvoke(Object proxy, Method method, Object[] args) throws Throwable {

        Display currentDisplay = Display.getCurrent();
        Display widgetDisplay = null;

        Widget widget = getWidget();
        if (widget != null) {
            if (widget.isDisposed()) {
                // System.err.println("ERROR:  Invoking: " + method.getName() + " on Widget: " + widget +
                // " which is disposed.");
                return null;
            }
            widgetDisplay = widget.getDisplay();
        }

        if (currentDisplay == null && widgetDisplay == null) {
            // The current thread is not a UI thread but no widget display was provided so...
            return baseInvoke(proxy, method, args);
        }
        else if (currentDisplay != null && (widgetDisplay == null || currentDisplay == widgetDisplay)) {
            // Current thread is the widget's UI thread (or at least *a* UI thread).
            return baseInvoke(proxy, method, args);
        }
        else {
            // Current thread is not the widget's UI thread.

            IBaseInvokeRunnable runnable = createBaseInvokeRunnable(proxy, method, args);

            if (isAsync()) {
                widgetDisplay.asyncExec(runnable);
                return null;
            }

            widgetDisplay.syncExec(runnable);
            Throwable error = runnable.getError();
            if (error != null) {
                throw error;
            }
            return runnable.getResult();
        }
    }

    /**
     * Returns the invocation type flag.
     * 
     * @return The invocation type flag
     */
    public boolean isAsync() {
        return _Async;
    }

    /**
     * Interface for objects that can return a {@link Widget}.
     * 
     * @author Mark Masse
     */
    public interface IWidgetProvider {

        /**
         * Returns the {@link Widget}
         *
         * @return The {@link Widget}
         */
        public Widget getWidget();

    }

}
