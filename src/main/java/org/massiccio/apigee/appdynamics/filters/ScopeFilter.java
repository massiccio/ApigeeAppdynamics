/**
 * Copyright 2019 michele@apache.org
 * 
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
