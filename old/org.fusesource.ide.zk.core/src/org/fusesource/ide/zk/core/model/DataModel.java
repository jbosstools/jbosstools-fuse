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

package org.fusesource.ide.zk.core.model;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 * As a key component of the library's framework, DataModel wraps a single "data object" and provides management methods
 * and life-cycle events. DataModel instances are managed (cached) by a {@link DataModelManager} instance. The
 * {@link DataModelManager} maps model instances using the model's unique, immutable {@link #getKey() key}. The key
 * class must implement the {@link Comparable} interface. The key class must also implement the
 * {@link Object#hashCode() hashCode} and {@link Object#equals(Object) equals} methods using immutable values, in other
 * words it must be a well-behaved <code>Map</code> key. There are no restrictions on the type of object that can be
 * modeled (as the data portion of the DataModel).
 * </p>
 * <h4>Life Cycle</h4>
 * <p>
 * The typical life-cycle of a DataModel follows a linear sequence of <em>creation</em>, <em>management</em>, and
 * eventual <em>destruction</em>.
 * </p>
 * <h5>Creation</h5>
 * <p>
 * <em>Where do models come from?</em> The process for creating a new model instance is as follows:
 * </p>
 * <p>
 * <ol>
 * <li>A model is requested (by its {@link #getKey() key}) from its {@link DataModelManager#getModel(Comparable)
 * manager}.</li>
 * <li>If the model is not already managed by the {@link DataModelManager manager}, the manager asks the
 * {@link IDataModelSource source} to {@link IDataModelSource#createModel(Comparable) create} a new model.</li>
 * <li>The source creates the model and returns it to the manager.
 * <li>The manager manages (caches) the model and returns it to the requester.</li>
 * <li>The manager
 * {@link IGenericDataModelManagerEventListener#dataModelManagerDataModelAdded(GenericDataModelManagerEvent) announces}
 * the existence of the new model to its
 * {@link DataModelManager#addGenericEventListener(IGenericDataModelManagerEventListener) registered listeners}.</li>
 * </ol>
 * </p>
 * <h5>Management</h5>
 * <p>
 * The manager that owns a model instance will return the instance whenever it is
 * {@link DataModelManager#getModel(Comparable) requested}.
 * </p>
 * <p>
 * A model instance provides the following methods for direct "management":
 * </p>
 * <p>
 * <ul>
 * <li>{@link #updateData()} - Updates the model (after its data has been changed).</li>
 * <li>{@link #refreshData()} - Replaces the model's current data object with data
 * {@link IDataModelSource#getData(Comparable) requested from the source}.</li>
 * <li>{@link #deleteData()} - Deletes the model's data from {@link IDataModelSource#deleteData(Comparable) the source}.
 * This will have the side-effect of destroying the model.</li>
 * </ul>
 * </p>
 * <p>
 * It is important to note that although these methods appear in the model's interface they actually delegate to the
 * manager and ultimately the source to get the job done.
 * </p>
 * <p>
 * DataModel instances support two listener interfaces for receiving life-cycle related events:
 * {@link IDataModelEventListener} and {@link IGenericDataModelEventListener}. The former is model type-specific, where
 * the latter is generic and will work with any DataModel subclass.
 * </p>
 * 
 * <h5>Destruction</h5>
 * <p>
 * When a model is no longer needed it can be destroyed using the {@link #destroy() destroy} method. This method handles
 * the removal of the model from its manager. Unlike the {@link #deleteData() deleteData} method, {@link #destroy()
 * destroy} does not effect the model's data nor does it involve the model's source.
 * </p>
 * <p>
 * Typically a model is "no longer needed" whenever its last listener ("view") has been removed. Thus the default
 * implementation of the {@link #hookAfterLastListenerRemoved() hookAfterLastListenerRemoved} method calls
 * {@link #destroy() destroy}. Subclasses may override this method to keep the model alive without listeners. However
 * doing so could potentially lead to a memory leak since the manager will maintain a reference the model.
 * </p>
 * 
 * @see DataModelManager
 * @see IDataModelSource
 * @see DataModelSource
 * @see DataModelEvent
 * @see GenericDataModelEvent
 * @see IDataModelEventListener
 * @see IGenericDataModelEventListener
 * 
 * @author Mark Masse
 */
public abstract class DataModel<T extends DataModel<T, K, D>, K extends Comparable<K>, D> implements Comparable<T> {

    private D _Data;
    private boolean _Destroyed;
    private final CopyOnWriteArrayList<IDataModelEventListener<T>> _EventListeners;
    private final CopyOnWriteArrayList<IGenericDataModelEventListener> _GenericEventListeners;

    private final GenericDataModelEvent _GenericReusableEvent;

    private final K _Key;
    private DataModelManager<T, K, D> _Manager;
    private final DataModelEvent<T> _ReusableEvent;

    /**
     * Constructor.
     * <p>
     * NOTE: DataModels should only be created by their manager's associated {@link IDataModelSource}.
     * </p>
     * 
     * @param key The unique key to associate with the model.
     */
    public DataModel(K key) {
        _Key = key;

        _ReusableEvent = new DataModelEvent<T>(getThis());
        _GenericReusableEvent = new GenericDataModelEvent(getThis());
        _EventListeners = new CopyOnWriteArrayList<IDataModelEventListener<T>>();
        _GenericEventListeners = new CopyOnWriteArrayList<IGenericDataModelEventListener>();
    }

    /**
     * Adds a model type-specific event listener.
     * 
     * @param listener The model event listener to add.
     */
    public final void addEventListener(IDataModelEventListener<T> listener) {

        if (isDestroyed()) {
            return;
        }

        if (!hasListeners()) {
            hookBeforeFirstListenerAdded();
        }

        if (!_EventListeners.contains(listener)) {
            _EventListeners.add(listener);
        }
    }

    /**
     * Adds a generic (untyped) model event listener.
     * 
     * @param listener The model event listener to add.
     */
    public final void addGenericEventListener(IGenericDataModelEventListener listener) {

        if (isDestroyed()) {
            return;
        }

        if (!hasListeners()) {
            hookBeforeFirstListenerAdded();
        }

        if (!_GenericEventListeners.contains(listener)) {
            _GenericEventListeners.add(listener);
        }
    }

    @Override
    public final int compareTo(T o) {
        return getKey().compareTo(o.getKey());
    }

    /**
     * Deletes the underlying data that is modeled by this DataModel. The implementation delegates to the model's source
     * via the manager.
     * 
     * @throws DataModelSourceException If the source does not support delete.
     * @see IDataModelSource#deleteData(Comparable)
     */
    public final void deleteData() throws DataModelSourceException {
        _Manager.deleteModelData(getThis());
    }

    /**
     * Updates the underlying data that is modeled by this DataModel. The implementation delegates to the model's source
     * via the manager.
     * 
     * @throws DataModelSourceException If the source does not support update.
     * @see IDataModelSource#updateData(DataModel)
     */
    public final void updateData() throws DataModelSourceException {
        _Manager.updateModelData(getThis());
    }

    /**
     * Destroys the model.
     * <p>
     * Sets the destroyed flag to true, notifies listeners (both
     * {@link IDataModelEventListener#dataModelDestroyed(DataModelEvent) typed} and
     * {@link IGenericDataModelEventListener#dataModelDestroyed(GenericDataModelEvent) untyped}) of the destruction,
     * unregisters all listeners and then removes the model from its manager.
     * </p>
     * <p>
     * This method calls {@link #hookAfterDestroyed()} to provide subclasses with an opportunity to clean themselves up
     * before disappearing.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public final void destroy() {

        if (isDestroyed()) {
            return;
        }

        _Destroyed = true;

        CopyOnWriteArrayList<IDataModelEventListener<T>> eventListeners = (CopyOnWriteArrayList<IDataModelEventListener<T>>) _EventListeners
                .clone();
        CopyOnWriteArrayList<IGenericDataModelEventListener> genericEventListeners = (CopyOnWriteArrayList<IGenericDataModelEventListener>) _GenericEventListeners
                .clone();

        _EventListeners.clear();
        _GenericEventListeners.clear();

        _Manager.removeModel(getKey());

        for (IDataModelEventListener<T> listener : eventListeners) {
            listener.dataModelDestroyed(_ReusableEvent);
        }

        for (IGenericDataModelEventListener listener : genericEventListeners) {
            listener.dataModelDestroyed(_GenericReusableEvent);
        }

        hookAfterDestroyed();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Returns the model's data.
     * 
     * @return The data.
     */
    public final D getData() {
        return _Data;
    }

    /**
     * Returns the model's key.
     * 
     * @return The key.
     */
    public final K getKey() {
        return _Key;
    }

    /**
     * Returns the model's manager.
     * 
     * @return The manager.
     */
    public final DataModelManager<T, K, D> getManager() {
        return _Manager;
    }

    /**
     * Returns the conceptual owner model of this model or null if this model has no concept of an owner.
     * <p>
     * The default implementation returns <code>null</code>.
     * </p>
     * 
     * @return The conceptual owner model of this model or null if this model has no concept of an owner.
     */
    public DataModel<?, ?, ?> getOwnerModel() {
        return null;
    }

    /**
     * Returns the conceptual parent model of this model or null if this model has no concept of a parent.
     * <p>
     * The default implementation returns <code>null</code>.
     * </p>
     * 
     * @return The conceptual parent model of this model or null if this model has no concept of a parent.
     */
    public DataModel<?, ?, ?> getParentModel() {
        return null;
    }

    /**
     * Returns this model's manager's source.
     * 
     * @return The source.
     */
    public final IDataModelSource<T, K, D> getSource() {
        return getManager().getSource();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns <code>true</code> if this model has any registered listeners (either {@link IDataModelEventListener
     * typed} or {@link IGenericDataModelEventListener untyped}).
     * 
     * @return <code>true</code> if this model has any registered listeners.
     */
    public boolean hasListeners() {
        return (_EventListeners.size() != 0 || _GenericEventListeners.size() != 0);
    }

    /**
     * Returns the destroyed status.
     * 
     * @return The destroyed status.
     */
    public final boolean isDestroyed() {
        return _Destroyed;
    }

    /**
     * Refreshes the underlying data that is modeled by this DataModel. "Refresh" means that the model's data will be
     * <em>replaced</em> by the object returned from calling the source's {@link IDataModelSource#getData(Comparable)
     * getData} method.
     * 
     * @see IDataModelSource#getData(Comparable)
     */
    public final void refreshData() {
        _Manager.refreshModelData(getThis());
    }

    /**
     * Removes a model type-specific event listener.
     * <p>
     * Note that this method has no effect if the model is already destroyed or the listener has not been previously
     * added.
     * </p>
     * 
     * @param listener The model event listener to remove.
     */
    public final void removeEventListener(IDataModelEventListener<T> listener) {

        if (isDestroyed()) {
            return;
        }

        if (!_EventListeners.contains(listener)) {
            return;
        }

        _EventListeners.remove(listener);

        if (!hasListeners()) {
            hookAfterLastListenerRemoved();
        }
    }

    /**
     * Removes a generic (untyped) model event listener.
     * <p>
     * Note that this method has no effect if the model is already destroyed or the listener has not been previously
     * added.
     * </p>
     * 
     * @param listener The model event listener to remove.
     */
    public final void removeGenericEventListener(IGenericDataModelEventListener listener) {

        if (isDestroyed()) {
            return;
        }

        if (!_GenericEventListeners.contains(listener)) {
            return;
        }

        _GenericEventListeners.remove(listener);

        if (!hasListeners()) {
            hookAfterLastListenerRemoved();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + (_Key != null ? "Key=" + _Key + ", " : "")
                + (_Data != null ? "Data=" + _Data : "") + "]";
    }

    /**
     * Sets the data.
     * 
     * @param data The data to set.
     */
    final void setData(D data) {

        hookBeforeSetData();

        _Data = data;

        hookAfterSetData();

        for (IDataModelEventListener<T> listener : _EventListeners) {
            listener.dataModelDataRefreshed(_ReusableEvent);
        }

        for (IGenericDataModelEventListener listener : _GenericEventListeners) {
            listener.dataModelDataRefreshed(_GenericReusableEvent);
        }

    }

    /**
     * Sets the manager. This method is not meant to be called by subclasses (or anyone else).
     * 
     * @param manager The manager to set.
     */
    final void setManager(DataModelManager<T, K, D> manager) {
        _Manager = manager;
    }

    /**
     * Dispatches the dataModelDataChanged event to all registered listeners.
     */
    protected final void fireDataModelDataChanged() {
        for (IDataModelEventListener<T> listener : _EventListeners) {
            listener.dataModelDataChanged(_ReusableEvent);
        }

        for (IGenericDataModelEventListener listener : _GenericEventListeners) {
            listener.dataModelDataChanged(_GenericReusableEvent);
        }
    }

    /**
     * This is the "getThis trick" described in the Java generics FAQ. Subclasses <b>must</b> implement as
     * <code>return this;</code>.
     * 
     * @return <b>this</b>.
     */
    protected abstract T getThis();

    /**
     * Hook method for subclasses to do some final clean up during destroy. The default implementation does nothing.
     */
    protected void hookAfterDestroyed() {
    }

    /**
     * Hook method for subclasses to override the default behavior of model self-destruction when the last listener is
     * removed. This method normally should not be overridden as it could lead to memory leaks if unused models are left
     * hanging around.
     */
    protected void hookAfterLastListenerRemoved() {
        // No more "views". Self-destruct to avoid keeping unused models around.
        destroy();
    }

    /**
     * Hook method for subclasses to do some initialization after the data is set. The default implementation does
     * nothing.
     */
    protected void hookAfterSetData() {
    }

    /**
     * Hook method for subclasses to do some lazy initialization before the first listener arrives. The default
     * implementation does nothing.
     */
    protected void hookBeforeFirstListenerAdded() {
    }

    /**
     * Hook method for subclasses to do some clean before the data is set. The default implementation does nothing.
     */
    protected void hookBeforeSetData() {
    }

}
