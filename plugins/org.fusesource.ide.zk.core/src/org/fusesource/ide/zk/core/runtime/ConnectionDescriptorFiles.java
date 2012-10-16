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

package org.fusesource.ide.zk.core.runtime;

import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.fusesource.ide.zk.core.data.AbstractConnectionDescriptor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * Manages the persistence of connection descriptors into XML files.
 * 
 * @author Mark Masse
 */
public class ConnectionDescriptorFiles<T extends AbstractConnectionDescriptor<T>> {

    public static final String FILE_EXTENSION = ".xml";
    public static final String XML_TAG_CONNECTION = "Connection";
    public static final String XML_TAG_NAME = "Name";
    public static final String XML_TAG_VERSION = "Version";

    private final File _Directory;

    private final Map<String, IConnectionDescriptorXmlSerializer<T>> _Serializers;

    private final String _XmlWriteVersion;

    public ConnectionDescriptorFiles(File directory, String xmlWriteVersion) {
        _Directory = directory;
        _XmlWriteVersion = xmlWriteVersion;
        _Serializers = new HashMap<String, IConnectionDescriptorXmlSerializer<T>>();
    }

    public void addSerializer(String xmlVersion, IConnectionDescriptorXmlSerializer<T> serializer) {
        _Serializers.put(xmlVersion, serializer);
    }

    public boolean delete(T connectionDescriptor) {
        createDirectory();

        File file = getConnectionFile(connectionDescriptor);
        if (!file.exists()) {
            return true;
        }

        return file.delete();
    }

    public boolean exists(String name) {
        return getNames().contains(name);
    }

    /**
     * Returns the directory.
     * 
     * @return The directory
     */
    public final File getDirectory() {
        return _Directory;
    }

    public Set<String> getNames() {
        createDirectory();

        File[] connectionFiles = getConnectionFiles();
        if (connectionFiles == null) {
            return Collections.emptySet();
        }

        TreeSet<String> names = new TreeSet<String>();
        for (File connectionFile : connectionFiles) {

            FileReader reader = null;
            try {
                reader = new FileReader(connectionFile);
                XMLMemento connectionMemento = XMLMemento.createReadRoot(reader);
                String name = connectionMemento.getString(XML_TAG_NAME);
                names.add(name);
            }
            catch (FileNotFoundException e) {
                // TODO: Log
                e.printStackTrace();
            }
            catch (WorkbenchException e) {
                // TODO: Log
                e.printStackTrace();
            }
            finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException e) {
                    // TODO: Log
                    e.printStackTrace();
                }
            }
        }

        return names;
    }

    /**
     * Returns the xmlWriteVersion.
     * 
     * @return The xmlWriteVersion
     */
    public final String getXmlWriteVersion() {
        return _XmlWriteVersion;
    }

    public T load(String name) {
        File connectionFile = getConnectionFile(name);
        return load(connectionFile);
    }

    public Set<T> loadAll() {
        createDirectory();

        File[] connectionFiles = getConnectionFiles();
        if (connectionFiles == null || connectionFiles.length == 0) {
            return Collections.emptySet();
        }

        Set<T> connectionSet = new TreeSet<T>();
        for (File connectionFile : connectionFiles) {

            T connectionDescriptor = load(connectionFile);
            if (connectionDescriptor != null) {
                connectionSet.add(connectionDescriptor);
            }
        }

        return connectionSet;
    }

    public void save(T connectionDescriptor) {

        createDirectory();

        XMLMemento memento = toXml(connectionDescriptor);

        File file = getConnectionFile(connectionDescriptor);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            memento.save(writer);
        }
        catch (IOException e) {
            // TODO: Log
            e.printStackTrace();
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException e) {
                // TODO: Log
                e.printStackTrace();
            }
        }
    }

    protected void createDirectory() {
        File dir = getDirectory();
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    protected T fromXml(XMLMemento memento) {
        String version = memento.getString(XML_TAG_VERSION);
        IConnectionDescriptorXmlSerializer<T> serializer = _Serializers.get(version);
        return serializer.fromXml(memento);
    }

    protected File getConnectionFile(String name) {
        return new File(getDirectory(), name + FILE_EXTENSION);
    }

    protected File getConnectionFile(T connectionDescriptor) {
        return getConnectionFile(connectionDescriptor.getName());
    }

    protected File[] getConnectionFiles() {
        return _Directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(FILE_EXTENSION);
            }
        });
    }

    protected T load(File connectionFile) {

        T connectionDescriptor = null;
        FileReader reader = null;

        try {
            reader = new FileReader(connectionFile);
            XMLMemento connectionMemento = XMLMemento.createReadRoot(reader);
            connectionDescriptor = fromXml(connectionMemento);
        }
        catch (FileNotFoundException e) {
            // TODO: Log
            e.printStackTrace();
        }
        catch (WorkbenchException e) {
            // TODO: Log
            e.printStackTrace();
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
                // TODO: Log
                e.printStackTrace();
            }
        }

        return connectionDescriptor;
    }

    protected XMLMemento toXml(T connectionDescriptor) {

        String xmlWriteVersion = getXmlWriteVersion();
        IConnectionDescriptorXmlSerializer<T> serializer = _Serializers.get(xmlWriteVersion);

        XMLMemento memento = XMLMemento.createWriteRoot(XML_TAG_CONNECTION);
        memento.putString(XML_TAG_VERSION, xmlWriteVersion);
        memento.putString(XML_TAG_NAME, connectionDescriptor.getName());

        serializer.toXml(connectionDescriptor, memento);

        return memento;

    }

}
