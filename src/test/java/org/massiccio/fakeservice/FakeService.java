package org.massiccio.fakeservice;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class FakeService {

	// the revision number is removed from the metric
	// inboundtraffic|my_organization|amy_environment|my_api|ResponsesSent_5XX
	public static final String NAME_1 = "METRICS.PREVIOUS:type=faults,scope=o$FaultyOrg$e$FaultyEnv$a$FaultyApi$r$1,name=PolicyErrors";
	public static final String NAME_2 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V3$r$1,name=ResponsesSent_5XX";
	public static final String NAME_3 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V3$r$1,name=ResponseCount";
	public static final String NAME_4 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V3$r$1,name=RequestCount";
	
	public static final String NAME_5 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V4$r$2,name=ResponsesSent_5XX";
	public static final String NAME_6 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V4$r$2,name=ResponseCount";
	public static final String NAME_7 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V4$r$3,name=RequestCount";

	public static final String INVALID_TYPE_1 = "METRICS.PREVIOUS:type=,scope=o$map-org$e$map-env$a$map-api_V2$r$1,name=ResponseCount";

	// invalid (no $a)
	public static final String INVALID_SCOPE_1 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$invalid_org$e$invalid_env$invalid_api$r$3,name=ResponseCount";
	// no api
	public static final String INVALID_SCOPE_2 = "METRICS.PREVIOUS:type=faults,scope=o$MyOrg$e$MyAPI$r$1,name=TargetErrors";
	public static final String INVALID_SCOPE_3 = "METRICS.PREVIOUS:type=faults,scope=o$MyOrg$a$MyAPI$r$2,name=TargetErrors";

	// no environment
	public static final String NO_ENV_1 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$a$MyAPI1$r$3,name=RequestCount";
	public static final String NO_ENV_2 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$a$MyAPI1$r$3,name=ResponsesSent_5XX";
	public static final String NO_ENV_3 = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$a$MyAPI1$r$3,name=TargetErrors";

	private final MBeanServer server;

	private Map<Metric, ObjectName> map;

	public FakeService() {
		this(false);
	}

	public FakeService(boolean onlyValid) {
		server = ManagementFactory.getPlatformMBeanServer();
		map = new LinkedHashMap<>();

		if (onlyValid) {
			insertOnlyValidNames();
		} else {
			insertAll();
		}
	}

	/** Initialize map with only valid ObjectNames. */
	private void insertOnlyValidNames() {
		try {
			map.put(new Metric(), ObjectName.getInstance(NAME_1));
			
			map.put(new Metric(), ObjectName.getInstance(NAME_2));
			map.put(new Metric(), ObjectName.getInstance(NAME_3));
			map.put(new Metric(), ObjectName.getInstance(NAME_4));

			map.put(new Metric(), ObjectName.getInstance(NAME_5));
			map.put(new Metric(), ObjectName.getInstance(NAME_6));
			map.put(new Metric(), ObjectName.getInstance(NAME_7));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/** Initialize map with all ObjectNames. */
	private void insertAll() {
		try {
			map.put(new Metric(), ObjectName.getInstance(NAME_1));
			
			map.put(new Metric(), ObjectName.getInstance(NAME_2));
			map.put(new Metric(), ObjectName.getInstance(NAME_3));
			map.put(new Metric(), ObjectName.getInstance(NAME_4));
			
			map.put(new Metric(), ObjectName.getInstance(NAME_5));
			map.put(new Metric(), ObjectName.getInstance(NAME_6));
			map.put(new Metric(), ObjectName.getInstance(NAME_7));
			
			map.put(new Metric(), ObjectName.getInstance(INVALID_TYPE_1));
			map.put(new Metric(), ObjectName.getInstance(INVALID_SCOPE_2));
			map.put(new Metric(), ObjectName.getInstance(INVALID_SCOPE_3));
			map.put(new Metric(), ObjectName.getInstance(INVALID_SCOPE_1));
			map.put(new Metric(), ObjectName.getInstance(NO_ENV_1));
			map.put(new Metric(), ObjectName.getInstance(NO_ENV_2));
			map.put(new Metric(), ObjectName.getInstance(NO_ENV_3));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/** Register the ObjectNames. */
	public void register() {
		this.map.entrySet().stream().forEach(entry -> {
			try {
				this.server.registerMBean(entry.getKey(), entry.getValue());
			} catch (MBeanRegistrationException | NotCompliantMBeanException e) {
				throw new IllegalStateException(e);
			} catch (InstanceAlreadyExistsException e) {
				// ignore
			}
		});
	}

	/** Unregister the ObjectNames. */
	public void unregister() {
		this.map.values().forEach(value -> {
			try {
				this.server.unregisterMBean(value);
			} catch (MBeanRegistrationException | InstanceNotFoundException e) {
				// ignore
			}
		});
	}

	/**
	 * Get the map of Metric objects.
	 */
	public Map<Metric, ObjectName> getMap() {
		return this.map;
	}

	public static void main(String[] args) throws InterruptedException {

		new FakeService(true).register();
		
		String port = System.getProperty("com.sun.management.jmxremote.port"); 
		System.out.printf("Exposing metrics on localhost at port [%s]\n", port);

		while (true) {
			Thread.sleep(10 * 1000L);
		}
	}
}
