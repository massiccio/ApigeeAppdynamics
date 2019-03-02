package org.massiccio.apigee.appdynamics.filters;

import java.util.function.Predicate;

import javax.management.ObjectName;

/**
 * Filter for the <i>name</i> property.
 */
public class NameFilter implements Predicate<ObjectName> {
	

	public static final String NAME = "name";

	/**
	 * Returns <code>false</code> if the object name has property "name" whose value is
	 * <ul>
	 * <li>LastUpdated
	 * <li>LastLatencyUpdateTime
	 * <li>ErrorResponsesSent_4XX
	 * <li>ErrorResponsesSent_5XX
	 * </ul>
	 * 
	 * Return <code>true</code> otherwise.
	 */
	@Override
	public boolean test(ObjectName t) {
		if (t == null) {
			return true;
		}

		final String name = t.getKeyProperty(NAME);

		return !("LastUpdated".equals(name)
				|| "LastLatencyUpdateTime".equals(name)
				|| "ErrorResponsesSent_4XX".equals(name)
				|| "ErrorResponsesSent_5XX".equals(name));

	}

}
