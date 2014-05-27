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

package org.fusesource.ide.zk.zookeeper.data;

import java.io.File;
import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.fusesource.ide.zk.core.ui.FileEditor;

/**
 * Represents the authentication information associated with a ZooKeeper connection.
 * 
 * @see ZooKeeper#addAuthInfo(String, byte[])
 * 
 * @author Mark Masse
 */
public final class AuthInfo implements Comparable<AuthInfo> {

    private final String _AuthString;
    private final String _Scheme;
    private final Type _Type;

    /**
     * Constructor.
     * 
     * @param type The {@link Type}.
     * @param scheme The scheme.
     * @param auth The authentication {@link String}. In the case of the {@link Type#File}, this will be a file path.
     *            For {@link Type#Text}, this will be the authentication value.
     * 
     * @see ZooKeeper#addAuthInfo(String, byte[])
     */
    public AuthInfo(Type type, String scheme, String authString) {
        _Type = type;
        _Scheme = scheme;
        _AuthString = authString;
    }

    @Override
    public int compareTo(AuthInfo o) {
        int result = getType().name().compareTo(o.getType().name());
        if (result == 0) {
            result = getScheme().compareTo(o.getScheme());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuthInfo other = (AuthInfo) obj;
        if (_AuthString == null) {
            if (other._AuthString != null)
                return false;
        }
        else if (!_AuthString.equals(other._AuthString))
            return false;
        if (_Scheme == null) {
            if (other._Scheme != null)
                return false;
        }
        else if (!_Scheme.equals(other._Scheme))
            return false;
        if (_Type == null) {
            if (other._Type != null)
                return false;
        }
        else if (!_Type.equals(other._Type))
            return false;
        return true;
    }

    /**
     * Returns the byte array containing the authentication value.
     * 
     * @param type The {@link Type}.
     * @param scheme The scheme.
     * @return The authentication value. In the case of the {@link Type#File}, this array will contain the file's contents.
     *            For {@link Type#Text}, this will be the authentication {@link String} value's {@link String#getBytes() bytes}.
     * 
     * @see ZooKeeper#addAuthInfo(String, byte[])
     */
    public byte[] getAuth() throws IOException {
        Type type = getType();
        String authString = getAuthString();
        if (type.equals(Type.Text)) {
            return authString.getBytes();
        }
        else if (type.equals(Type.File)) {
            return FileEditor.readFile(new File(authString));
        }

        return null;
    }

    /**
     * Returns the auth String.
     * 
     * @return The auth String
     */
    public String getAuthString() {
        return _AuthString;
    }

    /**
     * Returns the scheme.
     * 
     * @return The scheme
     */
    public String getScheme() {
        return _Scheme;
    }

    /**
     * Returns the type.
     * 
     * @return The type
     */
    public Type getType() {
        return _Type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_AuthString == null) ? 0 : _AuthString.hashCode());
        result = prime * result + ((_Scheme == null) ? 0 : _Scheme.hashCode());
        result = prime * result + ((_Type == null) ? 0 : _Type.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AuthInfo [" + (_Type != null ? "Type=" + _Type + ", " : "")
                + (_Scheme != null ? "Scheme=" + _Scheme + ", " : "")
                + (_AuthString != null ? "AuthString=" + _AuthString : "") + "]";
    }

    /**
     * The AuthInfo auth value type.
     * 
     * @author Mark Masse
     */
    public static enum Type {
        /**
         * Indicates that the authentication {@link String} value identifies a file (full path).
         */
        File,
        
        /**
         * Indicates that the authentication {@link String} value contains the actual authentication data.
         */
        Text;
    }
}
