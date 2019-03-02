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
