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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages a set of data models.
 * <p>
 * DataModelManagers can be instantiated and used directly. All DataModel-related operations are routed through this
 * class. Specifically, client code should never call the {@link IDataModelSource} interface methods directly.
 * </p>
 * <p>
 * The {@link DataModelManager} class acts as a go-between for a {@link DataModel} instance and its
 * {@link IDataModelSource}. In fact, the DataModel's management-oriented methods are delegated through this class to
 * the source.
 * </p>
 * <p>
 * This class is not meant to be extended. In fact it has been declared <code>final</code> which should make it really
 * difficult to subclass.
 * </p>
 * 
 * @see DataModel
 * @see IDataModelSource
 * @see DataModelSource
 * @see DataModelManagerEvent
 * @see GenericDataModelEvent
 * @see IDataModelManagerEventListener
 * @see IGenericDataModelManagerEventListener
 * 
 * @author Mark Masse
 */
public final class DataModelManager<M extends DataModel<M, K, D>, K extends Comparable<K>, D> {

    private boolean _Destroyed;
    private CopyOnWriteArrayList<IDataModelManagerEventListener<M, K, D>> _EventListeners;
    private CopyOnWriteArrayList<IGenericDataModelManagerEventListener> _GenericEventListeners;
    private final ConcurrentHashMap<K, M> _Models;
    private final IDataModelSource<M, K, D> _Source;

    /**
     * Constructor.
     * 
     * @param source The data model source.
     */
    public DataModelManager(IDataModelSource<M, K, D> source) {
        _Source = source;
        _Models = new ConcurrentHashMap<K, M>();
    }

    /**
     * Adds a data model type-specific event listener.
     * 
     * @param listener the event listener to add.
     */
    public void addEventListener(IDataModelManagerEventListener<M, K, D> listener) {

        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        if (_EventListeners == null) {
            _EventListeners = new CopyOnWriteArrayList<IDataModelManagerEventListener<M, K, D>>();
        }

        if (!_EventListeners.contains(listener)) {
            _EventListeners.add(listener);
        }
    }

    /**
     * Adds an untyped data model event listener.
     * 
     * @param listener the event listener to add.
     */
    public void addGenericEventListener(IGenericDataModelManagerEventListener listener) {

        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        if (_GenericEventListeners == null) {
            _GenericEventListeners = new CopyOnWriteArrayList<IGenericDataModelManagerEventListener>();
        }

        if (!_GenericEventListeners.contains(listener)) {
            _GenericEventListeners.add(listener);
        }
    }

    /**
     * Destroys this manager along with all of its managed models.
     */
    public void destroy() {

        if (isDestroyed()) {
            return;
        }

        List<M> managedModels = getManagedModels();

        _Destroyed = true;

        _Models.clear();

        if (_EventListeners != null) {
            DataModelManagerEvent<M, K, D> event = new DataModelManagerEvent<M, K, D>(this, null);
            for (IDataModelManagerEventListener<M, K, D> listener : _EventListeners) {
                listener.dataModelManagerDestroyed(event);
            }

            _EventListeners.clear();
        }

        if (_GenericEventListeners != null) {
            GenericDataModelManagerEvent event = new GenericDataModelManagerEvent(this, null);
            for (IGenericDataModelManagerEventListener listener : _GenericEventListeners) {
                listener.dataModelManagerDestroyed(event);
            }

            _GenericEventListeners.clear();
        }

        for (M model : managedModels) {
            model.destroy();
        }

    }

    /**
     * Destroys all of the managed models.
     */
    public void destroyManagedModels() {

        if (isDestroyed()) {
            return;
        }

        List<M> managedModels = getManagedModels();

        _Models.clear();

        for (M model : managedModels) {
            model.destroy();
        }

    }

    /**
     * Returns the set of keys associated with the specified criteria.
     * 
     * @param criteria An object that qualifies the request for keys. The manager delegates to the source to interpret
     *            this criteria.
     * @return The set of keys associated with the criteria.
     */
    public Set<K> findKeys(Object criteria) throws DataModelSourceException {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        return _Source.findKeys(criteria);
    }

    /**
     * Returns the list of currently managed models associated with the specified criteria.
     * 
     * @param criteria An object that qualifies the request for models. The manager delegates to the source to interpret
     *            this criteria.
     * @return The list of managed models associated with the criteria.
     * @see #findKeys(Object)
     */
    public List<M> findManagedModels(Object criteria) throws DataModelSourceException {

        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        Set<K> keySet = findKeys(criteria);
        if (keySet == null) {
            return null;
        }

        return getManagedModels(keySet);
    }

    /**
     * Returns the list of (possibly newly created) models associated with the specified criteria.
     * 
     * @param criteria An object that qualifies the request for models. The manager delegates to the source to interpret
     *            this criteria.
     * @return The list of models associated with the criteria.
     * @see #findKeys(Object)
     */
    public List<M> findModels(Object criteria) throws DataModelSourceException {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        Set<K> keySet = findKeys(criteria);
        if (keySet == null) {
            return null;
        }
        return getModels(keySet);
    }

    /**
     * Returns the set of all possible model keys.
     * 
     * @return The set of all possible model keys.
     */
    public Set<K> getKeys() throws DataModelSourceException {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        return _Source.getKeys();
    }

    /**
     * Returns the set of currently managed model keys.
     * 
     * @return The set of currently managed model keys.
     */
    public Set<K> getManagedKeys() {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        return Collections.unmodifiableSet(new TreeSet<K>(_Models.keySet()));
    }

    /**
     * Returns the managed model associated with the specified key or <code>null</code> if no such model is currently
     * managed.
     * 
     * @param key The model's key.
     * @return The managed model associated with the specified key.
     */
    public M getManagedModel(K key) {

        if (isManagedKey(key)) {
            return _Models.get(key);
        }

        return null;
    }

    /**
     * Returns the number of models that are currently being managed.
     * 
     * @return The managed model count.
     */
    public int getManagedModelCount() {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        return _Models.size();
    }

    /**
     * Returns the list of all models that are currently being managed.
     * 
     * @return The managed models.
     */
    public List<M> getManagedModels() {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        Set<K> keys = getManagedKeys();
        List<M> models = new ArrayList<M>(keys.size());
        for (K key : keys) {
            models.add(_Models.get(key));
        }

        return models;
    }

    /**
     * Returns the list of currently managed models associated with the specified keys.
     * 
     * @param keySet The set of model keys.
     * @return the list of currently managed models associated with the specified keys.
     */
    public List<M> getManagedModels(Set<K> keySet) {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        if (keySet == null) {
            throw new IllegalArgumentException("null keys");
        }

        List<M> models = new ArrayList<M>(keySet.size());
        for (K key : keySet) {
            M model = getManagedModel(key);
            if (model != null) {
                models.add(model);
            }
        }
        return models;

    }

    /**
     * Returns the model associated with the specified key. A new model will be created if one does not already exist.
     * 
     * @param key The key.
     * @return The model associated with the key.
     */
    public M getModel(K key) throws DataModelSourceException {

        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        M model = getManagedModel(key);
        if (model != null) {
            return model;
        }

        model = _Source.createModel(key);

        if (model == null) {
            return null;
        }

        if (model.isDestroyed()) {
            return null;
        }

        addModel(key, model);

        return model;
    }

    /**
     * Returns the list of all models that the source is capable of creating.
     * 
     * @return All of the models.
     * @see #getKeys()
     */
    public List<M> getModels() throws DataModelSourceException {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        Set<K> keySet = getKeys();
        return getModels(keySet);
    }

    /**
     * Returns the list of models associated with the specified keys.
     * 
     * @param keySet The set of model keys.
     * @return the list of models associated with the specified keys.
     */
    public List<M> getModels(Set<K> keySet) throws DataModelSourceException {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        if (keySet == null) {
            throw new IllegalArgumentException("null keys");
        }

        List<M> models = new ArrayList<M>(keySet.size());
        for (K key : keySet) {
            M model = getModel(key);
            if (model != null) {
                models.add(model);
            }
        }
        return models;
    }

    /**
     * Returns the source specified during construction.
     * 
     * @return The {@link IDataModelSource} specified during construction.
     */
    public IDataModelSource<M, K, D> getSource() {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        return _Source;
    }

    /**
     * Inserts new data into the source. Note that this method does not automatically result in model creation.
     * 
     * @param key The key associated with the data.
     * @param data The data to insert.
     * @see #getModel(Comparable)
     * @see #updateModelData(DataModel)
     */
    public void insertData(K key, D data) throws DataModelSourceException {
        _Source.insertData(key, data);
    }

    /**
     * Returns <code>true</code> if this model has been destroyed.
     * 
     * @return <code>true</code> if this model has been destroyed.
     * @see #destroy()
     */
    public boolean isDestroyed() {
        return _Destroyed;
    }

    /**
     * Returns <code>true</code> if the specified key is mapped to a currently managed model.
     * 
     * @param key The key to inquire about.
     * @return <code>true</code> if the specified key is mapped to a currently managed model.
     */
    public boolean isManagedKey(K key) {
        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        return _Models.containsKey(key);
    }

    /**
     * Refreshes all of the managed models.
     * 
     * @see DataModel#refreshData()
     */
    public void refreshManagedModels() throws DataModelSourceException {

        if (isDestroyed()) {
            return;
        }

        List<M> managedModels = getManagedModels();

        for (M model : managedModels) {
            refreshModelData(model);
        }

    }

    /**
     * Removes a data model type-specific event listener. Note that this method does nothing if the manager is destroyed
     * or if the listener has not previously been added.
     * 
     * @param listener the event listener to remove.
     * @see #isDestroyed()
     * @see #addEventListener(IDataModelManagerEventListener)
     */
    public void removeEventListener(IDataModelManagerEventListener<M, K, D> listener) {

        if (isDestroyed()) {
            return;
        }

        if (_EventListeners == null) {
            return;
        }

        if (!_EventListeners.contains(listener)) {
            return;
        }

        _EventListeners.remove(listener);
    }

    /**
     * Removes an untyped data model event listener. Note that this method does nothing if the manager is destroyed or
     * if the listener has not previously been added.
     * 
     * @param listener the event listener to remove.
     * @see #isDestroyed()
     * @see #addGenericEventListener(IGenericDataModelManagerEventListener)
     */
    public void removeGenericEventListener(IGenericDataModelManagerEventListener listener) {

        if (isDestroyed()) {
            return;
        }

        if (_GenericEventListeners == null) {
            return;
        }

        if (!_GenericEventListeners.contains(listener)) {
            return;
        }

        _GenericEventListeners.remove(listener);
    }

    @Override
    public String toString() {
        return "DataModelManager [ManagedModelCount=" + getManagedModelCount() + ", "
                + (getSource() != null ? "Source=" + getSource() + ", " : "") + "Destroyed=" + isDestroyed() + "]";
    }

    /**
     * Internal method called by {@link DataModel#deleteData()}.
     * 
     * @param model The model owning the data to be deleted.
     * @see DataModel#deleteData()
     */
    void deleteModelData(M model) throws DataModelSourceException {
        verifyIsManagedModel(model);
        _Source.deleteData(model.getKey());
        model.destroy();
    }

    /**
     * Internal method called by {@link DataModel#refreshData()}.
     * 
     * @param model The model to refresh.
     * @see DataModel#refreshData()
     */
    void refreshModelData(M model) throws DataModelSourceException {

        verifyIsManagedModel(model);

        D data = _Source.getData(model.getKey());
        if (data == null) {
            model.destroy();
            return;
        }

        model.setData(data);
    }

    /**
     * Removes the model associated with the key. This method is not meant to be invoked directly but rather it should
     * only be called from the Model's destroy method.
     * 
     * @param key The model's key.
     * @see DataModel#destroy()
     */
    void removeModel(K key) {

        if (key == null || !_Models.containsKey(key)) {
            return;
        }

        M model = _Models.get(key);
        _Models.remove(key);

        if (_EventListeners != null) {
            DataModelManagerEvent<M, K, D> event = new DataModelManagerEvent<M, K, D>(this, model);
            for (IDataModelManagerEventListener<M, K, D> listener : _EventListeners) {
                listener.dataModelManagerDataModelRemoved(event);
            }
        }

        if (_GenericEventListeners != null) {
            GenericDataModelManagerEvent event = new GenericDataModelManagerEvent(this, model);
            for (IGenericDataModelManagerEventListener listener : _GenericEventListeners) {
                listener.dataModelManagerDataModelRemoved(event);
            }
        }

    }

    /**
     * Internal method called by {@link DataModel#updateData()}.
     * 
     * @param model The model to update.
     * @see DataModel#updateData()
     */
    void updateModelData(M model) throws DataModelSourceException {
        _Source.updateData(model);
        refreshModelData(model);
    }

    private void addModel(K key, M model) {

        if (isDestroyed()) {
            throw new IllegalStateException("DataModelManager is destroyed.");
        }

        _Models.put(key, model);
        model.setManager(this);

        if (_EventListeners != null) {
            DataModelManagerEvent<M, K, D> event = new DataModelManagerEvent<M, K, D>(this, model);
            for (IDataModelManagerEventListener<M, K, D> listener : _EventListeners) {
                listener.dataModelManagerDataModelAdded(event);
            }
        }

        if (_GenericEventListeners != null) {
            GenericDataModelManagerEvent event = new GenericDataModelManagerEvent(this, model);
            for (IGenericDataModelManagerEventListener listener : _GenericEventListeners) {
                listener.dataModelManagerDataModelAdded(event);
            }
        }
    }

    private void verifyIsManagedModel(M model) {
        if (model == null) {
            throw new IllegalArgumentException("null model");
        }

        if (model.isDestroyed()) {
            throw new IllegalArgumentException("model is destroyed");
        }

        K key = model.getKey();

        if (!_Models.containsKey(key)) {
            throw new IllegalArgumentException("model is not managed here");
        }

        if (_Models.get(key) != model) {
            throw new IllegalArgumentException("specified model does not match the managed one for key: " + key);
        }
    }
}
