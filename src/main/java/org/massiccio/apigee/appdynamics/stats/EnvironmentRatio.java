package org.massiccio.apigee.appdynamics.stats;

/**
 * Class used to compute environment statistics.
 */
public class EnvironmentRatio extends ApiRatio {

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Creates a new EnvironmentRatio object.
     *
     * @param api The organization and environment, e.g.,
     * inboundtraffic|MyOrg|MyEnv
     */
	public EnvironmentRatio(String environment) {
        super(environment);
    }

    // =========================================================================
    // Methods
    // =========================================================================

	public void setResponseCount(long responseCount) {
        // environment => we have to aggregate the responses generated by each API
		if (responseCount < 0L) {
			throw new IllegalArgumentException("Epected positive argument, received: " + responseCount);
		}
        super.responseCount += responseCount;
    }

    @Override
    public void setRequestCount(long requestCount) {
        // environment => we have to aggregate the requests hitting each API
    	if (requestCount < 0L) {
			throw new IllegalArgumentException("Epected positive argument, received: " + requestCount);
		}
        this.requestCount += requestCount;
    }

    @Override
    public void setResponseSentXXX(String responseSentXXX, long value) {
        // aggregate all responses of type 2XX, 3XX, etc.
    	if (value < 0L) {
			throw new IllegalArgumentException("Epected positive argument, received: " + value);
		}
        super.map.merge(responseSentXXX, value, Long::sum);
    }
}