package org.massiccio.apigee.appdynamics.filters;

import java.util.function.Predicate;

import javax.management.ObjectName;

/**
 * Filter for the <i>scope</i> property.
 */
public class ScopeFilter implements Predicate<ObjectName> {

	public static final String SCOPE = "scope";
	
	private static final String SCOPE_PATTERN = "o\\$\\S+\\$e\\$\\S+\\$a\\$\\S+(\\$r\\$\\d+)?";


	/**
	 * Return <code>true</code> if the object name has a property called "scope"
	 * whose content matches the Apigee pattern.
	 */
	@Override
	public boolean test(ObjectName t) {
		if (t == null) {
    		return false;
		}
		
		final String scope = t.getKeyProperty(SCOPE);
		if (scope == null || scope.trim().isEmpty()) {
			return false;
		}
		
		return scope.matches(SCOPE_PATTERN);
	}

}
