/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.fusesource.ide.launcher.Activator;

/**
 * @author lhein
 */
public class SecureStorageUtil {
	
	private SecureStorageUtil(){
		/*Hide constructor*/
	}
    
    public static String getFromSecureStorage(String baseKey, ILaunchConfiguration launch, String key) {
        try {
        	ISecurePreferences node = getNode(baseKey, launch);
            String val = node.get(key, null);
            if (val == null) {
            	return null;
            }
            return new String(EncodingUtils.decodeBase64(val));
        } catch(IOException | StorageException e) {
        	Activator.getLogger().error(e);
        	return null;
        }
    }

    public static void storeInSecureStorage(String baseKey, ILaunchConfiguration launch, String key, String val ) throws StorageException, UnsupportedEncodingException {
        ISecurePreferences node = getNode(baseKey, launch);
        if( val == null ) {
        	node.put(key, val, true);
        } else {
        	node.put(key, EncodingUtils.encodeBase64(val.getBytes(StandardCharsets.UTF_8)), true /* encrypt */);
        }
    }

    private static ISecurePreferences getNode(String baseKey, ILaunchConfiguration launch) 
    		throws UnsupportedEncodingException {
		String secureKey = new StringBuilder(baseKey)
			.append(launch.getName())
			.append(Path.SEPARATOR).toString();

		ISecurePreferences root = SecurePreferencesFactory.getDefault();
		String encoded = URLEncoder.encode(secureKey, StandardCharsets.UTF_8.name());
		return root.node(encoded);
    }
}
