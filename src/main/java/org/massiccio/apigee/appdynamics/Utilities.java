package org.massiccio.apigee.appdynamics;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public final class Utilities {

	/** Property used to specify the JMX URL. */
	public static final String JMX_URL = "jmx.url";
	
	/** Value assigned to metrics whose value cannot be retrieved. */
	static final long IGNORE_VALUE = Long.MIN_VALUE;

	/** The metric identifying the response count. */
	static final String RESPONSE_COUNT = "ResponseCount";

	/** The metric identifying the request count. */
	static final String REQUEST_COUNT = "RequestCount";

	/** Metrics whose ratio shall be computed. */
	private static final HashSet<String> RATIO_METRICS = new HashSet<>();

	static {
		RATIO_METRICS.add("ResponsesSent_1XX");
		RATIO_METRICS.add("ResponsesSent_2XX");
		RATIO_METRICS.add("ResponsesSent_3XX");
		RATIO_METRICS.add("ResponsesSent_4XX");
		RATIO_METRICS.add("ResponsesSent_5XX");
	}
	
	
	private Utilities() {
		// avoid instantiation
	}
	
	
	public static boolean isNotNullOrEmpty (String str) {
	    return (str != null && !str.trim().isEmpty());
	}

	/**
	 * Gets all the MBeans for the specified domain.
	 * 
	 * @param domain The domain.
	 * @return A set of ObectNames for the specified domain, possibly empty, but
	 *         not <code>null</code>.
	 */
	static Set<ObjectName> getMBeans(MBeanServerConnection mbsc, String domain) {

		Set<ObjectName> result = Collections.emptySet();

		if (domain == null || domain.trim().isEmpty()) {
			return result;
		}

		// Query MBean names
		try {
			ObjectName pattern = ObjectName.getInstance(domain);
			result = new TreeSet<>(mbsc.queryNames(pattern, null));
		} catch (MalformedObjectNameException | NullPointerException
				| IOException ignore) {
			//
		}
		return result;
	}

	/**
	 * Gets the value of the metric identified by the ObjectName passed as
	 * argument.
	 * 
	 * @param oname The ObjectName identifying the metric
	 * @return The value of the metric, or {@value #IGNORE_VALUE} if an error
	 *         occurred while retrieving the value.
	 */
	static long getValue(MBeanServerConnection mbsc, ObjectName oname) {
		if (oname == null) {
			throw new IllegalArgumentException("The argument cannot be null");
		}
		try {
			return Long.valueOf(mbsc.getAttribute(oname, "Value").toString());
		} catch (NumberFormatException | AttributeNotFoundException
				| InstanceNotFoundException | MBeanException
				| ReflectionException | IOException ignore) {
			return IGNORE_VALUE;
		}
	}

	/**
	 * Connects to the JMX connector using {@link #JMX_URL}.
	 * 
	 * @return The JMX API connector.
	 */
	static JMXConnector connect() throws Exception {
		String jmxUrl = System.getProperty(JMX_URL);
		JMXServiceURL url = new JMXServiceURL(jmxUrl);
		return JMXConnectorFactory.connect(url, null);
	}

	/**
	 * Check whether the name is valid.
	 * 
	 * @return <code>true</code> if valid, <code>false</code> otherwise.
	 */
	static final boolean isValidName(String name) {
		return ((RATIO_METRICS.contains(name) || RESPONSE_COUNT.equals(name)
				|| REQUEST_COUNT.equals(name)));
	}
}
