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

import java.nio.charset.Charset;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;
//import org.apache.zookeeper.common.PathUtils;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.fusesource.ide.zk.zookeeper.PathUtils;

/**
 * A ZooKeeper node (called znode for short). Each unique znode instance corresponds to a ZooKeeper path.
 * 
 * @see ZooKeeperConnection#getZnode(String)
 * @see ZooKeeper#exists(String, boolean)
 * @see ZooKeeper#getData(String, boolean, Stat)
 * @see ZooKeeper#getChildren(String, boolean)
 * @see ZooKeeper#getACL(String, Stat)
 * 
 * @author Mark Masse
 */
public final class Znode {

    /**
     * The maximum znode data size (1 MB).
     */
    public static final long MAX_DATA_SIZE = 1024 * 1024; // 1MB

    /**
     * The znode path separator character '/'.
     */
    public static final char PATH_SEPARATOR_CHAR = '/';

    /**
     * String rendition of the znode path separator character "/".
     */
    public static final String PATH_SEPARATOR_STRING = String.valueOf(PATH_SEPARATOR_CHAR);

    /**
     * The root znode path String "/".
     */
    public static final String ROOT_PATH = PATH_SEPARATOR_STRING;
    public static final String STAT_DESCRIPTION_AVERSION = "The number of changes to the ACL of this znode.";
    public static final String STAT_DESCRIPTION_CTIME = "The time when this znode was created.";

    public static final String STAT_DESCRIPTION_CVERSION = "The number of changes to the children of this znode.";
    public static final String STAT_DESCRIPTION_CZXID = "The zxid of the change that caused this znode to be created.";

    public static final String STAT_DESCRIPTION_DATA_LENGTH = "The length of the data field of this znode.";
    public static final String STAT_DESCRIPTION_EPHEMERAL_OWNER = "The session id of the owner of this znode if the znode is an ephemeral node.";

    public static final String STAT_DESCRIPTION_MTIME = "The time when this znode was last modified.";
    public static final String STAT_DESCRIPTION_MZXID = "The zxid of the change that last modified this znode.";

    public static final String STAT_DESCRIPTION_NUM_CHILDREN = "The number of children of this znode.";
    public static final String STAT_DESCRIPTION_VERSION = "The number of changes to the data of this znode.";

    public static final String STAT_NAME_AVERSION = "aversion";
    public static final String STAT_NAME_CTIME = "ctime";

    public static final String STAT_NAME_CVERSION = "cversion";
    public static final String STAT_NAME_CZXID = "czxid";

    public static final String STAT_NAME_DATA_LENGTH = "dataLength";
    public static final String STAT_NAME_EPHEMERAL_OWNER = "ephemeralOwner";

    public static final String STAT_NAME_MTIME = "mtime";
    public static final String STAT_NAME_MZXID = "mzxid";

    public static final String STAT_NAME_NUM_CHILDREN = "numChildren";
    public static final String STAT_NAME_VERSION = "version";

    /**
     * The /zookeeper path.
     */
    public static final String ZNODE_PATH_ZOOKEEPER = ROOT_PATH + "zookeeper";

    /**
     * The /zookeeper/quota path.
     */
    public static final String ZNODE_PATH_ZOOKEEPER_QUOTA = ZNODE_PATH_ZOOKEEPER + PATH_SEPARATOR_STRING + "quota";

    /**
     * The character set used to convert znode data Strings to/from bytes.
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Tests the validity of the specified znode path.
     * 
     * @param absolutePath The znode path.
     * @param isSequential The sequential flag.
     * @throws IllegalArgumentException If the path is invalid.
     * 
     * @see {@link PathUtils#validatePath(String, boolean)}
     */
    public static final void validatePath(String absolutePath, boolean isSequential) throws IllegalArgumentException {
        PathUtils.validatePath(absolutePath, isSequential);
    }

    /**
     * Returns the absolute path formed by concatenating the specified parent and relative paths.
     * 
     * @param parentPath The parent path.
     * @param relativePath The relative path.
     * @return The absoulte path.
     */
    public static final String getAbsolutePath(String parentPath, String relativePath) {

        String absolutePath;
        if (parentPath.equals(ROOT_PATH)) {
            absolutePath = ROOT_PATH + relativePath;
        }
        else {
            absolutePath = parentPath + PATH_SEPARATOR_STRING + relativePath;
        }

        return absolutePath;
    }

    /**
     * Splits the specified absolute path, returning the parent path part.
     * 
     * @param absolutePath The absolute path to split.
     * @return The parent path.
     */
    public static final String getParentPath(String absolutePath) {
        return splitPath(absolutePath)[0];
    }

    /**
     * Splits the specified absolute path, returning the relative (child) path part.
     * 
     * @param absolutePath The absolute path to split.
     * @return The relative path.
     */
    public static final String getRelativePath(String absolutePath) {
        return splitPath(absolutePath)[1];
    }

    /**
     * Returns <code>true</code> if the specified absolute path starts with {@link #ZNODE_PATH_ZOOKEEPER}.
     * 
     * @param absolutePath The path to test.
     * @return <code>true</code> if the specified absolute path starts with {@link #ZNODE_PATH_ZOOKEEPER}.
     */
    public static boolean isSystemPath(String absolutePath) {
        return absolutePath.startsWith(ZNODE_PATH_ZOOKEEPER);
    }

    /**
     * Splits the specified absolute path, returning an array with two elements. Array element <code>[0]</code> contains
     * the absolute parent path and element <code>[1]</code> contains the relative child path.
     * 
     * @param absolutePath The path to split.
     * @return A {@link String} array containing the two split path elements.
     */
    public static final String[] splitPath(String absolutePath) {
        String[] splitPath = new String[2];

        if (absolutePath.equals(ROOT_PATH)) {
            splitPath[0] = null;
            splitPath[1] = absolutePath;
        }
        else {
            int lastSeparatorIndex = absolutePath.lastIndexOf(PATH_SEPARATOR_CHAR);
            if (lastSeparatorIndex == 0) {
                splitPath[0] = ROOT_PATH;
            }
            else {
                splitPath[0] = absolutePath.substring(0, lastSeparatorIndex);
            }

            splitPath[1] = absolutePath.substring(lastSeparatorIndex + 1);
        }

        return splitPath;
    }

    private List<ACL> _Acl;
    private boolean _AclReadable;
    private List<String> _Children;
    private boolean _ChildrenReadable;
    private byte[] _Data;
    private boolean _DataReadable;
    private String _DataString;
    private boolean _Ephemeral;
    private final String _ParentPath;
    private final String _Path;
    private final String _RelativePath;
    private boolean _Sequential;
    private Stat _Stat;

    /**
     * Constructor.
     * 
     * @param path The znode path {@link String}.
     * 
     * @see ZooKeeperConnection#getZnode(String)
     */
    public Znode(String path) {
        _Path = path;

        String[] splitPath = splitPath(path);
        _ParentPath = splitPath[0];
        _RelativePath = splitPath[1];
    }

    /**
     * Returns the acl.
     * 
     * @return The acl
     */
    public List<ACL> getAcl() {
        return _Acl;
    }

    /**
     * Returns the children.
     * 
     * @return The children.
     */
    public List<String> getChildren() {
        return _Children;
    }

    /**
     * Returns the data.
     * 
     * @return The data.
     */
    public byte[] getData() {
        return _Data;
    }

    /**
     * Returns the znode data as a {@link String}.
     * 
     * @return The znode data as a {@link String}.
     */
    public String getDataAsString() {
        if (_DataString == null) {

            if (_Data != null) {
                _DataString = new String(_Data, CHARSET);
            }
            else {
                _DataString = "";
            }
        }

        return _DataString;
    }

    /**
     * Returns the parent path.
     * 
     * @return The parent path.
     */
    public String getParentPath() {
        return _ParentPath;
    }

    /**
     * Returns the path.
     * 
     * @return The path.
     */
    public String getPath() {
        return _Path;
    }

    /**
     * Returns the relative path without a leading "/".
     * 
     * @return The relative path.
     */
    public String getRelativePath() {
        return _RelativePath;
    }

    /**
     * Returns the stat.
     * 
     * @return The stat.
     */
    public Stat getStat() {
        return _Stat;
    }

    /**
     * Returns <code>true</code> if the znode ACL is readable.
     * 
     * @return The <code>true</code> if the znode ACL is readable.
     */
    public boolean isAclReadable() {
        return _AclReadable;
    }

    /**
     * Returns <code>true</code> if the znode children list is readable.
     * 
     * @return The <code>true</code> if the znode children list is readable.
     */
    public boolean isChildrenReadable() {
        return _ChildrenReadable;
    }

    /**
     * Returns <code>true</code> if the znode data is readable.
     * 
     * @return The <code>true</code> if the znode data is readable.
     */
    public boolean isDataReadable() {
        return _DataReadable;
    }

    /**
     * Returns <code>true</code> if the znode is ephemeral.
     * 
     * @return <code>true</code> if the znode is ephemeral.
     */
    public boolean isEphemeral() {
        return _Ephemeral;
    }

    /**
     * Returns <code>true</code> if this znode has no children.
     * 
     * @return <code>true</code> if this znode has no children.
     */
    public boolean isLeaf() {
        List<String> children = getChildren();
        return (children == null || getChildren().size() == 0);
    }

    /**
     * Returns <code>true</code> if the znode is sequential.
     * 
     * @return <code>true</code> if the znode is sequential.
     */
    public boolean isSequential() {
        return _Sequential;
    }

    /**
     * Sets the {@link ACL} {@link List}.
     * 
     * @param acl the ACL to set
     */
    public void setAcl(List<ACL> acl) {
        _Acl = acl;
    }

    /**
     * Sets the ACL readable flag value.
     * 
     * @param aclReadable the ACL readable flag value. <code>true</code> if the znode ACL is readable.
     */
    public void setAclReadable(boolean aclReadable) {
        _AclReadable = aclReadable;
    }

    /**
     * Sets the children as a {@link List} of {@link String} paths.
     * 
     * @param children The children to set.
     */
    public void setChildren(List<String> children) {
        _Children = children;
    }

    /**
     * Sets the children readable flag value.
     * 
     * @param childrenReadable the children readable flag value. <code>true</code> if the znode children list is readable.
     */
    public void setChildrenReadable(boolean childrenReadable) {
        _ChildrenReadable = childrenReadable;
    }

    /**
     * Sets the data.
     * 
     * @param data The data to set.
     */
    public void setData(byte[] data) {
        _Data = data;
        _DataString = null;
    }

    /**
     * Sets the data readable flag value.
     * 
     * @param dataReadable the data readable flag value. <code>true</code> if the znode data is readable.
     */
    public void setDataReadable(boolean dataReadable) {
        _DataReadable = dataReadable;
    }

    /**
     * Sets the ephemeral flag.
     * 
     * @param ephemeral The ephemeral flag value.
     */
    public void setEphemeral(boolean ephemeral) {
        _Ephemeral = ephemeral;
    }

    /**
     * Sets the sequential flag that is used when creating new znodes.
     * 
     * @param sequential the sequential flag value to set.
     */
    public void setSequential(boolean sequential) {
        _Sequential = sequential;
    }

    /**
     * Sets the {@link Stat}.
     * 
     * @param stat The stat to set.
     */
    public void setStat(Stat stat) {
        _Stat = stat;
        setEphemeral(stat.getEphemeralOwner() > 0);
    }

    @Override
    public String toString() {
        return "Znode [" + (_Path != null ? "Path=" + _Path + ", " : "")
                + (_DataString != null ? "DataString=" + _DataString : "") + "]";
    }

}
