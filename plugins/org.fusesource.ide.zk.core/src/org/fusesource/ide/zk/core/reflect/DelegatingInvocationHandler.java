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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link InvocationHandler} that delegates calls to an {@link #getDelegate() Object}.
 * 
 * @see Proxy
 * @see InvocationHandler
 * 
 * @author Mark Masse
 */
public class DelegatingInvocationHandler implements InvocationHandler {

    /**
     * Creates a unique String based on the method signature.
     * 
     * @param method The {@link Method}.
     * @return The method signature {@link String}.
     */
    public static String baseGetMethodKey(Method method) {
        StringBuilder sb = new StringBuilder(50);

        sb.append(method.getName() + "(");

        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i].getName());
            if (i < (params.length - 1))
                sb.append(",");
        }

        sb.append(")");

        return sb.toString();
    }

    private final Object _Delegate;
    private final Map<String, Method> _DelegateMethods;

    /**
     * Constructor.
     * 
     * @param delegate The {@link Object} to delegate calls to.
     */
    public DelegatingInvocationHandler(Object delegate) {
        _Delegate = delegate;

        Method[] methods = _Delegate.getClass().getMethods();
        _DelegateMethods = new HashMap<String, Method>(methods.length);
        for (Method method : methods) {
            _DelegateMethods.put(getMethodKey(method), method);
        }
    }

    /**
     * Returns the delegate.
     * 
     * @return The delegate
     */
    public final Object getDelegate() {
        return _Delegate;
    }

    /**
     * Returns the delegate methods.
     * 
     * @return The delegate methods
     */
    public final Map<String, Method> getDelegateMethods() {
        return Collections.unmodifiableMap(_DelegateMethods);
    }

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getDeclaringClass().equals(Object.class)) {
            String methodName = method.getName();
            if (methodName.equals("equals")) {
                Object arg = args[0];
                if (arg instanceof Proxy) {
                    return equals(Proxy.getInvocationHandler(arg));
                }
                return false;
            }
            else if (methodName.equals("hashCode")) {
                return hashCode();
            }
            else if (methodName.equals("toString")) {
                return toString();
            }
        }

        return subInvoke(proxy, method, args);
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" + (_Delegate != null ? "Delegate=" + _Delegate : "") + "]";
    }

    /**
     * Hook method for subclasses. This method is called from invoke and the default implementation calls baseInvoke.
     * 
     * @param proxy The proxy instance that the method was invoked on
     * @param method The {@link Method} instance corresponding to the interface method invoked on the proxy instance.
     *            The declaring class of the Method object will be the interface that the method was declared in, which
     *            may be a superinterface of the proxy interface that the proxy class inherits the method through.
     * 
     * @param args An array of objects containing the values of the arguments passed in the method invocation on the
     *            proxy instance, or null if interface method takes no arguments. Arguments of primitive types are
     *            wrapped in instances of the appropriate primitive wrapper class, such as {@link Integer} or
     *            {@link Boolean}.
     * 
     * @return The value to return from the method invocation on the proxy instance. If the declared return type of the
     *         interface method is a primitive type, then the value returned by this method must be an instance of the
     *         corresponding primitive wrapper class; otherwise, it must be a type assignable to the declared return
     *         type. If the value returned by this method is null and the interface method's return type is primitive,
     *         then a {@link NullPointerException} will be thrown by the method invocation on the proxy instance. If the
     *         value returned by this method is otherwise not compatible with the interface method's declared return
     *         type as described above, a {@link ClassCastException} will be thrown by the method invocation on the
     *         proxy instance.
     * 
     * @throws Throwable The exception to throw from the method invocation on the proxy instance. The exception's type
     *             must be assignable either to any of the exception types declared in the throws clause of the
     *             interface method or to the unchecked exception types {@link RuntimeException} or {@link Error}. If a
     *             checked exception is thrown by this method that is not assignable to any of the exception types
     *             declared in the throws clause of the interface method, then an {@link UndeclaredThrowableException}
     *             containing the exception that was thrown by this method will be thrown by the method invocation on
     *             the proxy instance.
     * 
     * @see #invoke(Object, Method, Object[])
     * @see InvocationHandler#invoke(Object, Method, Object[])
     */
    protected Object subInvoke(Object proxy, Method method, Object[] args) throws Throwable {
        return baseInvoke(proxy, method, args);
    }

    /**
     * The DelegatingInvocationHandler base class implementation of {@link #invoke(Object, Method, Object[])}
     * 
     * @param proxy The proxy instance that the method was invoked on
     * @param method The {@link Method} instance corresponding to the interface method invoked on the proxy instance.
     *            The declaring class of the Method object will be the interface that the method was declared in, which
     *            may be a superinterface of the proxy interface that the proxy class inherits the method through.
     * 
     * @param args An array of objects containing the values of the arguments passed in the method invocation on the
     *            proxy instance, or null if interface method takes no arguments. Arguments of primitive types are
     *            wrapped in instances of the appropriate primitive wrapper class, such as {@link Integer} or
     *            {@link Boolean}.
     * 
     * @return The value to return from the method invocation on the proxy instance. If the declared return type of the
     *         interface method is a primitive type, then the value returned by this method must be an instance of the
     *         corresponding primitive wrapper class; otherwise, it must be a type assignable to the declared return
     *         type. If the value returned by this method is null and the interface method's return type is primitive,
     *         then a {@link NullPointerException} will be thrown by the method invocation on the proxy instance. If the
     *         value returned by this method is otherwise not compatible with the interface method's declared return
     *         type as described above, a {@link ClassCastException} will be thrown by the method invocation on the
     *         proxy instance.
     * 
     * @throws Throwable The exception to throw from the method invocation on the proxy instance. The exception's type
     *             must be assignable either to any of the exception types declared in the throws clause of the
     *             interface method or to the unchecked exception types {@link RuntimeException} or {@link Error}. If a
     *             checked exception is thrown by this method that is not assignable to any of the exception types
     *             declared in the throws clause of the interface method, then an {@link UndeclaredThrowableException}
     *             containing the exception that was thrown by this method will be thrown by the method invocation on
     *             the proxy instance.
     * 
     * @see #invoke(Object, Method, Object[])
     * @see InvocationHandler#invoke(Object, Method, Object[])
     */
    protected final Object baseInvoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodKey = getMethodKey(method);
        Object result = null;
        try {
            result = _DelegateMethods.get(methodKey).invoke(_Delegate, args);
        }
        catch (InvocationTargetException e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
        catch (Throwable t) {
            System.err.println(t);
            t.printStackTrace();
        }
        return result;
    }

    /**
     * Creates a {@link IBaseInvokeRunnable}.
     * 
     * @param proxy The proxy instance that the method was invoked on
     * 
     * @param method The {@link Method} instance corresponding to the interface method invoked on the proxy instance.
     *            The declaring class of the Method object will be the interface that the method was declared in, which
     *            may be a superinterface of the proxy interface that the proxy class inherits the method through.
     * 
     * @param args An array of objects containing the values of the arguments passed in the method invocation on the
     *            proxy instance, or null if interface method takes no arguments. Arguments of primitive types are
     *            wrapped in instances of the appropriate primitive wrapper class, such as {@link Integer} or
     *            {@link Boolean}.
     * 
     * @return A {@link IBaseInvokeRunnable}.
     * 
     * @see #baseInvoke(Object, Method, Object[])
     * 
     */
    protected IBaseInvokeRunnable createBaseInvokeRunnable(final Object proxy, final Method method, final Object[] args) {

        return new IBaseInvokeRunnable() {

            private Throwable _Error;
            private Object _Result;

            @Override
            public Throwable getError() {
                return _Error;
            }

            @Override
            public Object getResult() {
                return _Result;
            }

            @Override
            public void run() {
                try {
                    _Result = baseInvoke(proxy, method, args);
                }
                catch (Throwable t) {
                    _Error = t;
                }
            }
        };
    }

    /**
     * Creates a unique String based on the specified method.
     * 
     * @param method The {@link Method}.
     * @return The unique key {@link String}.
     * 
     * @see #baseGetMethodKey(Method)
     */
    protected String getMethodKey(Method method) {
        return baseGetMethodKey(method);
    }

    /**
     * Interface for a {@link Runnable} that calls
     * {@link DelegatingInvocationHandler#baseInvoke(Object, Method, Object[]) baseInvoke}
     * 
     * @author Mark Masse
     */
    public interface IBaseInvokeRunnable extends Runnable {

        /**
         * Returns the {@link Throwable} thrown by the
         * {@link DelegatingInvocationHandler#baseInvoke(Object, Method, Object[]) baseInvoke} method.
         * 
         * @return The {@link Throwable} or <code>null</code> if nothing was thrown.
         */
        Throwable getError();

        /**
         * Returns the result of the {@link DelegatingInvocationHandler#baseInvoke(Object, Method, Object[]) baseInvoke}
         * method.
         * 
         * @return The result.
         */
        Object getResult();
    }

}