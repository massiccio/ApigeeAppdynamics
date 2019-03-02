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
