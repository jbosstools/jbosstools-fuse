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

package org.fusesource.ide.zk.zookeeper.viewers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.zk.zookeeper.data.ZooKeeperServerDescriptor;
import org.fusesource.ide.zk.core.EclipseCoreActivator;
import org.fusesource.ide.zk.core.viewers.BaseElementType;
import org.fusesource.ide.zk.core.viewers.IElementType;
import org.fusesource.ide.zk.jmx.data.JmxConnectionDescriptor;


/**
 * {@link IElementType} for {@link ZooKeeperServerDescriptor}.
 * 
 * @author Mark Masse
 */
public class ZooKeeperServerDescriptorElementType extends BaseElementType {

    public static final String COLUMN_TITLE_HOST = "Host";
    public static final String COLUMN_TITLE_PORT = "Port";
    public static final String COLUMN_TITLE_JMX_URL = "JMX URL";

    private static final int[] COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.RIGHT, SWT.LEFT };

    private static final String[] COLUMN_TITLES = new String[] { COLUMN_TITLE_HOST, COLUMN_TITLE_PORT,
            COLUMN_TITLE_JMX_URL };

    private static final int[] COLUMN_WIDTHS = new int[] { SWT.DEFAULT, 50, SWT.DEFAULT };

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:Host | 1:Port | 2:JMX URL

        ZooKeeperServerDescriptor server = (ZooKeeperServerDescriptor) element;
        switch (columnIndex) {

        case 0:
            return server.getHost();

        case 1:
            return String.valueOf(server.getPort());

        case 2:
            JmxConnectionDescriptor jmxConnectionDescriptor = server.getJmxConnectionDescriptor();
            if (jmxConnectionDescriptor == null) {
                return "";
            }
            return String.valueOf(jmxConnectionDescriptor.getJmxServiceUrl());
        }
        return null;
    }

    @Override
    public int[] getColumnAlignments() {
        return COLUMN_ALIGNMENTS;
    }

    @Override
    public String[] getColumnTitles() {
        return COLUMN_TITLES;
    }

    @Override
    public int[] getColumnWidths() {
        return COLUMN_WIDTHS;
    }

    @Override
    public Image getImage(Object element) {
        return EclipseCoreActivator.getManagedImage(EclipseCoreActivator.IMAGE_KEY_OBJECT_SERVER);
    }

    @Override
    public String getText(Object element) {
        ZooKeeperServerDescriptor server = (ZooKeeperServerDescriptor) element;
        return server.getHost() + ":" + server.getPort();
    }

}
