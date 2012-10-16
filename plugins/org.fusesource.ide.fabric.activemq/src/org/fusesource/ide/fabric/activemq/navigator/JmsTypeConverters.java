package org.fusesource.ide.fabric.activemq.navigator;

import java.util.Date;

import javax.jms.DeliveryMode;
import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQDestination;

public class JmsTypeConverters {

	public static Destination toDestination(Object value) {
        if (value instanceof Destination) {
            return (Destination)value;
        }
        if (value instanceof String) {
            String text = (String)value;
            return ActiveMQDestination.createDestination(text, ActiveMQDestination.QUEUE_TYPE);
        }
        if (value instanceof String[]) {
            String text = ((String[])value)[0];
            if (text == null) {
                return null;
            }
            return ActiveMQDestination.createDestination(text, ActiveMQDestination.QUEUE_TYPE);
        }
        return null;
    }

    public static
    Integer toInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value instanceof String) {
            return Integer.valueOf((String)value);
        }
        if (value instanceof String[]) {
            return Integer.valueOf(((String[])value)[0]);
        }
        return null;
    }

    public static Long toLong(Object value) {
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof String) {
            return Long.valueOf((String)value);
        }
        if (value instanceof String[]) {
            return Long.valueOf(((String[])value)[0]);
        }
        return null;
    }

	public static Long toTimestamp(Object value) {
		if (value instanceof Date) {
			Date date = (Date) value;
			return date.getTime();
		} else {
			return toLong(value);
		}
	}

	public static Integer toDeliveryMode(Object value) {
		if (value instanceof String) {
			String text = (String) value;
			if ("NON_PERSISTENT".equalsIgnoreCase(text) || "NON-PERSISTENT".equalsIgnoreCase(text)) {
				return DeliveryMode.NON_PERSISTENT;
			} else if ("PERSISTENT".equalsIgnoreCase(text)) {
				return DeliveryMode.PERSISTENT;
			}
		}
		return toInteger(value);
	}

	public static Boolean toBoolean(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else if (value instanceof String) { 
			String text = (String) value;
			return text.equalsIgnoreCase("false") ? Boolean.FALSE : Boolean.TRUE;
		}
		return null;
	}


}
