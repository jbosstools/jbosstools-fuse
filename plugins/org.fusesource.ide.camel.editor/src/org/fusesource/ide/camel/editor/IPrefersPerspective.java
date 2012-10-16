package org.fusesource.ide.camel.editor;

/**
 * @author lhein
 */
public interface IPrefersPerspective {
	/**
     * @return the preferred perspective of this part or null if no perspective
     *         is preferred.
     */
    String getPreferredPerspectiveId();
}
