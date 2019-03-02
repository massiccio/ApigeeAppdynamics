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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.massiccio.apigee.appdynamics.filters.NameFilter;
import org.massiccio.apigee.appdynamics.filters.ScopeFilter;
import org.massiccio.apigee.appdynamics.filters.TypeFilter;
import org.massiccio.fakeservice.FakeService;

class MetricsProcessorTest {

	private static final String JMX_VALUE = "service:jmx:rmi:///jndi/rmi://127.0.0.1:12346/jmxrmi";

	private static FakeService service;

	private static JMXConnector jmxc;

	private static MBeanServerConnection mbsc;

	private MetricsProcessor test;

	@BeforeAll
	static void setupBeforeClass() throws Exception {
		System.setProperty(Utilities.JMX_URL, JMX_VALUE);
		service = new FakeService();

		jmxc = Utilities.connect();
		mbsc = jmxc.getMBeanServerConnection();
	}

	@BeforeEach
	void setUp() throws Exception {
		service.register();

		test = new MetricsProcessor(mbsc);
	}

	@AfterEach
	void tearDown() throws Exception {
		this.test = null;
		service.unregister();
	}

	@SuppressWarnings({ "unused", "static-method" })
	@Test
	void testApigeeMsgProcJmxMonitor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MetricsProcessor(null);
		});
	}

	@SuppressWarnings("static-method")
	@Test
	void testApigeeMsgProcJmxMonitor1() throws Exception {
		JMXConnector jmxc = null;
		try {
			jmxc = Utilities.connect();
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			MetricsProcessor mp = new MetricsProcessor(mbsc);
			mp.processObjectNames(service.getMap().values());
		} finally {
			closeJmxc(jmxc);
		}
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetValue() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.NAME_1);
		long value = Utilities.getValue(mbsc, oname);
		long expected = value + 1L;
		assertEquals(expected, Utilities.getValue(mbsc, oname));
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetValueInvalid() throws MalformedObjectNameException {
		ObjectName oname = ObjectName
		        .getInstance("InvalidDomain:type=invalid,scope=invalid,name=invalid");
		assertEquals(Long.MIN_VALUE, Utilities.getValue(mbsc, oname));
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetValueNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			Utilities.getValue(mbsc, null);
		});
	}

	@Test
	void testCreateMetrics() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.NAME_1);
		this.test.createMetrics(oname);
	}

	@Test
	void testCreateMetricsScopeUnknown() throws MalformedObjectNameException {
		String name = "METRICS.PREVIOUS:type=faults,scope=o$MyAPI1$1$UNKNOWN,name=PolicyErrors";
		ObjectName oname = ObjectName.getInstance(name);
		assertNull(this.test.createMetrics(oname));
	}

	@Test
	void testCreateMetricsIgnoreValue() throws MalformedObjectNameException {
		String name = "METRICS.PREVIOUS:type=faults,scope=o$MyAPI1$1,name1=PolicyErrors";
		ObjectName oname = ObjectName.getInstance(name);
		assertNull(this.test.createMetrics(oname));
	}

	@Test
	void testCreateMetricsIgnoreName() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.NO_ENV_3);
		this.test.createMetrics(oname);
	}

	@Test
	void testCreateMetricsResponseCount() throws MalformedObjectNameException {
		String name = FakeService.NAME_2;
		ObjectName oname = ObjectName.getInstance(name);
		assertNotNull(this.test.createMetrics(oname));
	}

	@Test
	void testCreateMetricsRequestCountInvalid() throws MalformedObjectNameException {
		String name = FakeService.NO_ENV_1;
		ObjectName oname = ObjectName.getInstance(name);
		assertNull(this.test.createMetrics(oname));
	}

	@Test
	void testCreateMetricsResponsesSent_5XXInvalid() throws MalformedObjectNameException {
		String name = FakeService.NO_ENV_2;
		ObjectName oname = ObjectName.getInstance(name);
		assertNull(this.test.createMetrics(oname));
	}

	@Test
	void testCreateMetricsRevisionInvalid() throws MalformedObjectNameException {
		String name = FakeService.NO_ENV_3;
		ObjectName oname = ObjectName.getInstance(name);
		assertNull(this.test.createMetrics(oname));
	}

	@Test
	void testProcessMsgProcMBeans() {
		List<String> result = this.test.processObjectNames(service.getMap().values());
		assertFalse(result.isEmpty());
	}

	private static final void closeJmxc(JMXConnector jmxc) {
		if (jmxc != null) {
			try {
				jmxc.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	@SuppressWarnings("static-method")
	@Test
	void testProcessMsgProcMBeans1() throws Exception {
		JMXConnector jmxc = null;
		try {
			jmxc = Utilities.connect();
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			MetricsProcessor test1 = new MetricsProcessor(mbsc);
			List<String> result = test1.processObjectNames(service.getMap().values());
			assertFalse(result.isEmpty());
		} finally {
			closeJmxc(jmxc);
		}
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetMBeansEmptyDomain() {
		assertEquals(Collections.emptySet(), Utilities.getMBeans(mbsc, " "));
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetMBeansNullDomain() {
		assertEquals(Collections.emptySet(), Utilities.getMBeans(mbsc, null));
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetMBeans() {
		String domain = "METRICS.PREVIOUS:*";
		assertEquals(service.getMap().size(), Utilities.getMBeans(mbsc, domain).size());

		Set<ObjectName> set = new HashSet<>(service.getMap().values());
		assertEquals(set, Utilities.getMBeans(mbsc, domain));
	}

	@SuppressWarnings("static-method")
	@Test
	void testGetMBeansInvalidDomain() {
		String domain = "METRICS.PREVIOUS";
		assertEquals(Collections.emptySet(), Utilities.getMBeans(mbsc, domain));
	}

	@SuppressWarnings("static-method")
	@Test
	void testMain1() {
		String[] args = { "test|" };
		service.unregister();
		Launcher.main(args);
	}

	@SuppressWarnings("static-method")
	@Test
	void testMain() {
		String[] args = {};
		Launcher.main(args);
	}

	@SuppressWarnings("static-method")
	@Test
	void testFilterPipeline1() throws MalformedObjectNameException {
		List<ObjectName> names = new ArrayList<>();
		names.add(ObjectName.getInstance(FakeService.NAME_1));

		List<ObjectName> filteredList = names.stream()
		        .filter(new TypeFilter().and(new NameFilter()).and(new ScopeFilter()))
		        .collect(Collectors.toList());
		assertEquals(1, filteredList.size());
	}

	@SuppressWarnings("static-method")
	@Test
	void testFilterPipeline2() throws MalformedObjectNameException {
		List<ObjectName> names = new ArrayList<>();
		names.add(ObjectName.getInstance(FakeService.NO_ENV_1));
		names.add(ObjectName.getInstance(FakeService.INVALID_SCOPE_1));

		List<ObjectName> filteredList = names.stream()
		        .filter(new TypeFilter().and(new NameFilter()).and(new ScopeFilter()))
		        .collect(Collectors.toList());
		assertTrue(filteredList.isEmpty());
	}

	@SuppressWarnings("static-method")
	@Test
	void testFilterPipeline3() throws MalformedObjectNameException {
		List<ObjectName> names = new ArrayList<>();
		names.add(ObjectName.getInstance(FakeService.NAME_1));
		names.add(ObjectName.getInstance(FakeService.NAME_2));

		names.add(ObjectName.getInstance(FakeService.NO_ENV_1));
		names.add(ObjectName.getInstance(FakeService.NO_ENV_2));
		names.add(ObjectName.getInstance(FakeService.NO_ENV_3));

		names.add(ObjectName.getInstance(FakeService.INVALID_SCOPE_1));
		names.add(ObjectName.getInstance(FakeService.INVALID_SCOPE_2));
		names.add(ObjectName.getInstance(FakeService.INVALID_SCOPE_3));

		names.add(ObjectName.getInstance(FakeService.INVALID_TYPE_1));

		List<ObjectName> filteredList = names.stream()
		        .filter(new TypeFilter().and(new NameFilter()).and(new ScopeFilter()))
		        .collect(Collectors.toList());
		assertEquals(2, filteredList.size());
	}
}
