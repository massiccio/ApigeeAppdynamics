package org.massiccio.apigee.appdynamics.filters;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.massiccio.apigee.appdynamics.filters.TypeFilter;
import org.massiccio.fakeservice.FakeService;

class TypeFilterTest {
	
	private TypeFilter test;


	@BeforeEach
	void setUp() throws Exception {
		this.test = new TypeFilter();
	}

	@AfterEach
	void tearDown() throws Exception {
		//
	}

	@Test
	void testTestNull() {
		assertFalse(this.test.test(null));
	}
	
	@Test
	void testTestFalse() throws MalformedObjectNameException, NullPointerException {
		ObjectName t = ObjectName.getInstance("d:type=Foo,name=Bling");
		assertFalse(this.test.test(t));
	}
	
	@Test
	void testTestFaults() throws MalformedObjectNameException {
		ObjectName t = ObjectName.getInstance("mydomain", "type", "faults");
		assertTrue(this.test.test(t));
	}
	
	@Test
	void testTestInboundTraffic() throws MalformedObjectNameException {
		ObjectName t = ObjectName.getInstance("mydomain", "type", "inboundtraffic");
		assertTrue(this.test.test(t));
	}

	@Test
	void testTestProperties1() throws MalformedObjectNameException {
		Hashtable<String, String> props = new Hashtable<>();
		props.put("key1", "value1");
		props.put("type", "faults");
		ObjectName t = ObjectName.getInstance("mydomain", props);
		assertTrue(this.test.test(t));
	}
	
	@Test
	void testTestProperties2() throws MalformedObjectNameException {
		Hashtable<String, String> props = new Hashtable<>();
		props.put("key1", "value1");
		props.put("type", "inboundtraffic");
		ObjectName t = ObjectName.getInstance("mydomain", props);
		assertTrue(this.test.test(t));
	}
	
	
	@Test
	void invalidType() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.INVALID_TYPE_1);
		assertFalse(test.test(oname));
	}
	
	@Test
	void valid() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.NAME_1);
		assertTrue(test.test(oname));
	}
}
