package org.massiccio.apigee.appdynamics;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

public class Launcher {

	/** The Apigee domain. */
	private static final String DOMAIN = "METRICS.PREVIOUS";

	private static final String DEFAULT_PREFIX = "name=Custom Metrics|Apigee|";

	/**
	 * Main method. Exit status is 0 if everything is OK, 1 otherwise.
	 * 
	 * @param args The prefix to use. If <code>null</code>, use
	 * {@link #DEFAULT_PREFIX}.
	 */
	public static void main(String[] args) {
		// no args => no prefix
		final String prefix = args.length > 0 ? "name=" + args[0].trim() : DEFAULT_PREFIX;

		JMXConnector jmxc = null;
		try {
			jmxc = Utilities.connect();
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			Set<ObjectName> names = Utilities.getMBeans(mbsc, DOMAIN + ":*");

			if (names.size() > 0) {
				MetricsProcessor monitor = new MetricsProcessor(mbsc);
				List<String> metrics = monitor.processObjectNames(names);
				metrics.stream().
					filter(Utilities::isNotNullOrEmpty).
					map(c -> prefix + c).
					forEach(System.out::print);
			}
		} catch (Exception e) {
			System.exit(1);
		} finally {
			closeConnector(jmxc);
		}
	}

	private static final void closeConnector(JMXConnector jmxc) {
		if (jmxc == null) {
			return;
		}

		try {
			jmxc.close();
		} catch (IOException ignore) {
			// ignore
		}
	}

}
