package org.massiccio.apigee.appdynamics.filters;

import static org.junit.jupiter.api.Assertions.*;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.massiccio.apigee.appdynamics.filters.NameFilter;

class NameFilterTest {
	
	private NameFilter test;

	@BeforeEach
	void setUp() throws Exception {
		test = new NameFilter();
	}

	@AfterEach
	void tearDown() throws Exception {
		//
	}

	@Test
	void testTestLastUpdated() throws MalformedObjectNameException {
		ObjectName t = ObjectName.getInstance("mydomain", "name", "LastUpdated");
		assertFalse(this.test.test(t));
	}
	
	@Test
	void testTestLastLatencyUpdateTime() throws MalformedObjectNameException {
		ObjectName t = ObjectName.getInstance("mydomain", "name", "LastLatencyUpdateTime");
		assertFalse(this.test.test(t));
	}
	
	@Test
	void testTestErrorResponsesSent_4XX() throws MalformedObjectNameException {
		ObjectName t = ObjectName.getInstance("mydomain", "name", "ErrorResponsesSent_4XX");
		assertFalse(this.test.test(t));
	}
	
	@Test
	void testTestErrorResponsesSent_5XX() throws MalformedObjectNameException {
		ObjectName t = ObjectName.getInstance("mydomain", "name", "ErrorResponsesSent_5XX");
		assertFalse(this.test.test(t));
	}

	@Test
	void testTestNull() {
		assertTrue(this.test.test(null));
	}
	
	@Test
	void testTestFalse() throws MalformedObjectNameException, NullPointerException {
		ObjectName t = ObjectName.getInstance("d:type=Foo,name=Bling");
		assertTrue(this.test.test(t));
	}
}
