package org.fusesource.ide.fabric.activemq;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.fabric.activemq.messages";

	public static String TreeBrokers;

	public static String CreateQueueAction;
	public static String CreateQueueActionToolTip;
	public static String CreateQueueDialogTitle;
	public static String CreateQueueDialogMessage;

	public static String CreateTopicAction;
	public static String CreateTopicActionToolTip;
	public static String CreateTopicDialogTitle;
	public static String CreateTopicDialogMessage;
	
	public static String DeleteQueueAction;
	public static String DeleteQueueActionToolTip;
	public static String DeleteQueueDialogTitle;
	public static String DeleteQueueDialogMessage;

	public static String DeleteTopicAction;
	public static String DeleteTopicActionToolTip;
	public static String DeleteTopicDialogTitle;
	public static String DeleteTopicDialogMessage;
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}