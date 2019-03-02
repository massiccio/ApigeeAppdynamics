package org.massiccio.apigee.appdynamics.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.massiccio.fakeservice.FakeService;

class ScopeFilterTest {
	
	ScopeFilter test;
	

	@BeforeEach
	void setUp() throws Exception {
		test = new ScopeFilter();
	}

	@AfterEach
	void tearDown() throws Exception {
		test = null;
	}

	@Test
	void testTest1() throws MalformedObjectNameException {
		// valid
		String validName = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V2$r$1,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(validName);
		assertTrue(test.test(oname));
	}
	
	@Test
	void testTest2() throws MalformedObjectNameException {
		// no revision, still valid
		String validName = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$a$MyApi_V2,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(validName);
		assertTrue(test.test(oname));
	}
	
	@Test
	void testTest3() throws MalformedObjectNameException {
		// $o is missing
		String invalidName = "METRICS.PREVIOUS:type=inboundtraffic,scope=MyOrg$e$MyEnv$a$MyApi_V2$r$1,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(invalidName);
		assertFalse(test.test(oname));
	}
	
	@Test
	void testTest4() throws MalformedObjectNameException {
		// $e is missing
		String invalidName = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$MyEnv$a$MyApi_V2$r$1,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(invalidName);
		assertFalse(test.test(oname));
	}
	
	@Test
	void testTest5() throws MalformedObjectNameException {
		// $a is missing
		String invalidName = "METRICS.PREVIOUS:type=inboundtraffic,scope=o$MyOrg$e$MyEnv$MyApi_V2$r$1,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(invalidName);
		assertFalse(test.test(oname));
	}
	
	@Test
	void testTest6() {
		assertFalse(test.test(null));
	}
	
	@Test
	void testTest7() throws MalformedObjectNameException {
		String invalidName = "METRICS.PREVIOUS:type=inboundtraffic,scope=,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(invalidName);
		assertFalse(test.test(oname));
	}
	
	@Test
	void testTest8() throws MalformedObjectNameException {
		String invalidName = "METRICS.PREVIOUS:type=inboundtraffic,scope= ,name=ResponseCount";
		ObjectName oname = ObjectName.getInstance(invalidName);
		assertFalse(test.test(oname));
	}

	
	@Test
	void test9() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.INVALID_SCOPE_1);
		assertFalse(test.test(oname));
	}
	
	@Test
	void test10() throws MalformedObjectNameException {
		ObjectName oname = ObjectName.getInstance(FakeService.NAME_1);
		assertTrue(test.test(oname));
	}
}
