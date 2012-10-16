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
import org.fusesource.ide.zk.zookeeper.ZooKeeperActivator;
import org.fusesource.ide.zk.zookeeper.data.AuthInfo;
import org.fusesource.ide.zk.core.viewers.BaseElementType;
import org.fusesource.ide.zk.core.viewers.IElementType;

import java.util.Arrays;


/**
 * {@link IElementType} for {@link AuthInfo}.
 * 
 * @author Mark Masse
 */
public class AuthInfoElementType extends BaseElementType {

    public static final String COLUMN_TITLE_TYPE = "Type";
    public static final String COLUMN_TITLE_SCHEME = "Scheme";
    public static final String COLUMN_TITLE_AUTH = "Auth";

    private static final int[] COLUMN_ALIGNMENTS = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };

    private static final String[] COLUMN_TITLES = new String[] { COLUMN_TITLE_TYPE, COLUMN_TITLE_SCHEME,
            COLUMN_TITLE_AUTH };

    private static final int[] COLUMN_WIDTHS = new int[] { SWT.DEFAULT, SWT.DEFAULT, SWT.DEFAULT };

    @Override
    public int getChildCount(Object parent) {
        return 0;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // 0:Type | 1:Scheme | 2:Auth

        AuthInfo authInfo = (AuthInfo) element;
        switch (columnIndex) {

        case 0:
            return authInfo.getType().name();

        case 1:
            return authInfo.getScheme();

        case 2:
            AuthInfo.Type type = authInfo.getType();
            String authString = authInfo.getAuthString();
            if (type == AuthInfo.Type.File) {
                return authString;
            }
            else if (type == AuthInfo.Type.Text) {
                char[] hiddenText = new char[authString.length()];
                Arrays.fill(hiddenText, '*');
                return new String(hiddenText);
            }
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
    public Object getChildElement(Object parent, int index) {
        return null;
    }

    @Override
    public Image getImage(Object element) {
        return ZooKeeperActivator.getManagedImage(ZooKeeperActivator.IMAGE_KEY_OBJECT_AUTH);
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        AuthInfo authInfo = (AuthInfo) element;
        String text = authInfo.getScheme() + ":" + authInfo.getAuthString();
        return text;
    }

}
