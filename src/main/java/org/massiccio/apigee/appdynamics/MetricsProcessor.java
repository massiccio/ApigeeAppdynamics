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
package org.massiccio.apigee.appdynamics;

import static org.massiccio.apigee.appdynamics.Utilities.REQUEST_COUNT;
import static org.massiccio.apigee.appdynamics.Utilities.RESPONSE_COUNT;
import static org.massiccio.apigee.appdynamics.filters.NameFilter.NAME;
import static org.massiccio.apigee.appdynamics.filters.ScopeFilter.SCOPE;
import static org.massiccio.apigee.appdynamics.filters.TypeFilter.FAULTS;
import static org.massiccio.apigee.appdynamics.filters.TypeFilter.INBOUND_TRAFFIC;
import static org.massiccio.apigee.appdynamics.filters.TypeFilter.TYPE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.massiccio.apigee.appdynamics.filters.NameFilter;
import org.massiccio.apigee.appdynamics.filters.ScopeFilter;
import org.massiccio.apigee.appdynamics.filters.TypeFilter;
import org.massiccio.apigee.appdynamics.stats.ApiRatio;
import org.massiccio.apigee.appdynamics.stats.EnvironmentRatio;

/**
 * Queries the Apigee message processor running on the same host and gather
 * metrics via JMX and reports them to AppDynamics.
 */
public class MetricsProcessor {

	// =========================================================================
	// Constants and static block
	// =========================================================================


	/** Ignore metrics containing this string. */
	private static final String UNKNOWN = "UNKNOWN";
	
	private static final Pattern SCOPE_PATTERN;
	
	static {
		String regex = "o\\$(\\S+)\\$e\\$(\\S+)\\$a\\$([^$]+)(\\$r\\$\\d+)?";
		SCOPE_PATTERN = Pattern.compile(regex);
	}

	// =========================================================================
	// Instance fields
	// =========================================================================

	/** The JMX connection. */
	private MBeanServerConnection mbsc;

	/** Hash map storing the ratios for each API. */
	private Map<String, ApiRatio> apiRatios;

	/** Hash map storing the ratios for each environment. */
	private Map<String, EnvironmentRatio> envRatios;

	// =========================================================================
	// Constructor
	// =========================================================================

	MetricsProcessor(MBeanServerConnection mbsc) {
		if (mbsc == null) {
			throw new IllegalArgumentException();
		}
		this.mbsc = mbsc;
		apiRatios = new HashMap<>();
		envRatios = new HashMap<>();
	}

	// =========================================================================
	// Methods
	// =========================================================================

	private final void buildInboundTrafficMetrics(String org, String env, 
			String api, String metricName, long value) {
		
		String fullApiPath = String.format("%s|%s|%s|%s|", INBOUND_TRAFFIC, org, env, api);
		String fullEnvPath = String.format("%s|%s|%s|", INBOUND_TRAFFIC, org, env);
		
		ApiRatio apiRatio = this.apiRatios.get(fullApiPath);
		if (apiRatio == null) {
			apiRatio = new ApiRatio(fullApiPath);
			this.apiRatios.put(fullApiPath, apiRatio);
		}

		EnvironmentRatio envRatio = this.envRatios.get(fullEnvPath);
		if (envRatio == null) {
			envRatio = new EnvironmentRatio(fullEnvPath);
			this.envRatios.put(fullEnvPath, envRatio);
		}

		if (RESPONSE_COUNT.equals(metricName)) {
			apiRatio.setResponseCount(value);
			envRatio.setResponseCount(value);
		} else if (REQUEST_COUNT.equals(metricName)) {
			apiRatio.setRequestCount(value);
			envRatio.setRequestCount(value);
		} else {
			apiRatio.setResponseSentXXX(metricName, value);
			envRatio.setResponseSentXXX(metricName, value);
		}
	}

	final String createMetrics(ObjectName oname) {
		String result = null;
		
		// e.g., inboundtraffic
		final String type = oname.getKeyProperty(TYPE);
		// o$<organization>$e$<environment>$a$<api_including_version>$r$<revision>
		final String scope = oname.getKeyProperty(SCOPE);
		// e.g., RequestsReceived_OPTIONS
		final String metricName = oname.getKeyProperty(NAME);
		final long value = Utilities.getValue(this.mbsc, oname);

		// ignore scopes containing the UNKNOWN word
		// and metrics whose value is invalid
		if ((scope.contains(UNKNOWN)) || (value == Utilities.IGNORE_VALUE)) {
			return result;
		}

		Matcher m = SCOPE_PATTERN.matcher(scope);
		if (m.matches()) {
			String org = m.group(1);
			String env = m.group(2);
			String api = m.group(3);			
			
			// compute ratios for inbound traffic
			if (INBOUND_TRAFFIC.equals(type) && Utilities.isValidName(metricName)) {
				buildInboundTrafficMetrics(org, env, api, metricName, value);
				result = String.format("%s|%s|%s|%s|%s, value=%d\n", INBOUND_TRAFFIC, org, env, api, metricName, value);
			} else if (FAULTS.equals(type)) {
				result = String.format("%s|%s|%s|%s|%s, value=%d\n", FAULTS, org, env, api, metricName, value);
			}
		}
		return result;
	}


	final List<String> processObjectNames(Collection<ObjectName> names) {
		// remove unneeded stuff, see the filter classes
		List<ObjectName> filteredList = names.stream()
		        .filter(new TypeFilter().and(new NameFilter()).and(new ScopeFilter()))
		        .collect(Collectors.toList());

		if (filteredList.isEmpty()) { // only invalid metrics, pass
			return Collections.emptyList();
		}

		// create gauge metrics
		List<String> result = filteredList.stream().map(oname -> createMetrics(oname))
		        .filter(Utilities::isNotNullOrEmpty).collect(Collectors.toList());

		// ratio metrics
		apiRatios.values().forEach(x -> result.addAll(x.getMetrics()));
		envRatios.values().forEach(x -> {
			result.addAll(x.getMetrics());
			final String prefix = x.getPrefix();			
			final String responseCount = String.format("%s%s, value=%d\n",
					prefix, RESPONSE_COUNT, x.getResponseCount());
			final String requestCount = String.format("%s%s, value=%d\n", 
					prefix, REQUEST_COUNT, x.getRequestCount());
			
			result.add(responseCount);
			result.add(requestCount);
		});
		return result;
	}

}
