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
