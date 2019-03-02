package org.massiccio.apigee.appdynamics.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.massiccio.apigee.appdynamics.Utilities;

/**
 * Class used to compute API statistics.
 */
public class ApiRatio {

    // =========================================================================
    // Constants
    // =========================================================================

    /** Empty string. */
    static final String EMPTY_STRING = "";

    // =========================================================================
    // Instance fields
    // =========================================================================

    /** The prefix. */
    private final String prefix;

    /**
     * The hash map containing {ResponsesSent_5XX -> 0, ResponsesSent_2XX -> 10,
     * ...}
     */
    protected HashMap<String, Long> map;

    /** The total number of responses for this API. */
    protected long responseCount;

    /** The total number of responses for this environment. */
    protected long requestCount;

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Creates a new ApiRatio object.
     *
     * @param api The API, e.g., inboundtraffic|MyOrg|MyEnv|MyApi|
     */
    public ApiRatio(String api) {
    	if (api == null || api.trim().isEmpty()) {
    		throw new IllegalArgumentException("Invalid api, expected valid string, was: '" + api + "'");
    	}
        this.prefix = api;
        map = new HashMap<>();
        responseCount = 0L;
        requestCount = 0L;
    }

    // =========================================================================
    // Methods
    // =========================================================================

    public void setResponseCount(long responseCount) {
    	if (responseCount < 0L) {
    		throw new IllegalArgumentException("Expected value > 0, input was: " + responseCount);
    	}
        this.responseCount = responseCount;
    }

    public void setResponseSentXXX(String responseSentXXX, long value) {
      if (!Utilities.isNotNullOrEmpty(responseSentXXX)) {
    		throw new IllegalArgumentException("Invalid code, expected valid string, was: '" + responseSentXXX + "'");
    	}
    	if (value < 0L) {
    		throw new IllegalArgumentException("Expected value > 0, input was: " + value);
    	}
        this.map.put(responseSentXXX, value);
    }

    public long getResponseCount() {
        return this.responseCount;
    }

    public void setRequestCount(long requestCount) {
    	if (requestCount < 0L) {
    		throw new IllegalArgumentException("Expected value > 0, input was: " + requestCount);
    	}
        this.requestCount = requestCount;
    }

    public long getRequestCount() {
        return this.requestCount;
    }

    public String getPrefix() {
		return prefix;
	}

    public List<String> getMetrics() {
    	if (this.responseCount == 0) {
    		return Collections.emptyList();
    	}

    	List<String> result = this.map.entrySet().stream().map(e -> {
    		double ratio = ((double) e.getValue()) / this.responseCount;
    		long ratioPct = Math.round(ratio * 100L);
    		String metric = String.format("%s%s_pct, value=%d\n", prefix, e.getKey(), ratioPct);
    		return metric;
    	}).collect(Collectors.toCollection(ArrayList::new));

    	final long pending = this.requestCount - responseCount;
        if (pending <= 0L || this.requestCount == 0L) {
        	String metric = String.format("%sPendingRequests_pct,  value=0\n", prefix);
    		result.add(metric);
        } else {
            double ratio = pending / this.requestCount;
            long ratioPct = Math.round(ratio * 100L);
        	String metric = String.format("%sPendingRequests_pct,  value=%d\n", prefix, ratioPct);
    		result.add(metric);
        }

    	return result;
    }


    @Deprecated
    public String toString() {
    	if (this.responseCount == 0) {
    		return EMPTY_STRING;
    	}

        StringBuilder sb = new StringBuilder();
        this.map.forEach((k, v) -> {
            // AppDynamics does NOT accept floating point numbers
            double ratio = ((double) v) / this.responseCount;
            long ratioPct = Math.round(ratio * 100L);
            sb.append(this.prefix).append(k).append("_pct, value=").append(ratioPct).append("\n");
        });
        // Compute pending requests.
        // Useful to set up health rules based on pending requests.
        final long pending = this.requestCount - responseCount;
        // pending could be negative in a period where there are more
        // completions than arrivals
        // In reality this should not happen as normally the request timeout
        // is less than 1 minute
        if (pending <= 0L || this.requestCount == 0L) {
            sb.append(this.prefix).append("PendingRequests_pct,  value=").append(0).append("\n");
        } else {
            double ratio = pending / this.requestCount;
            long ratioPct = Math.round(ratio * 100L);
            sb.append(this.prefix).append("PendingRequests_pct,  value=").append(ratioPct).append("\n");
        }

        return sb.toString();
    }

}
