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
package org.massiccio.apigee.appdynamics.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnvironmentRatioTest {
	
	private static final String ENVIRONMENT = "myenvironment";
	
	private EnvironmentRatio test;

	@BeforeEach
	void setUp() throws Exception {
		test = new EnvironmentRatio(ENVIRONMENT);
	}

	@AfterEach
	void tearDown() throws Exception {
		//
	}

	@Test
	void testSetResponseCount() {
		this.test.setResponseCount(1L);
		assertEquals(1L, this.test.getResponseCount());
		this.test.setResponseCount(1L); // sum
		assertEquals(2L, this.test.getResponseCount());
	}
	
	@Test
	void testSetResponseCountInvalid() {
		this.test.setResponseCount(1L);
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseCount(-1L);
		});		
	}
	
	@Test
	void testSetResponseCountInvalid1() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseCount(-1L);
		});
		
	}

	@Test
	void testSetResponseSentXXX() {
		String key = "500";
		this.test.setResponseSentXXX(key, 1L);
		this.test.setResponseSentXXX(key, 1L);
		assertEquals(2L, this.test.map.get(key).longValue());
	}
	
	@Test
	void testSetResponseSentXXXNegative() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseSentXXX("500", -1L);
		});
	}

	@Test
	void testSetRequestCount() {
		this.test.setRequestCount(1L);
		assertEquals(1L, this.test.getRequestCount());
		this.test.setRequestCount(1L); // sum
		assertEquals(2L, this.test.getRequestCount());
	}
	
	@Test
	void testSetRequestCountInvalid() {
		this.test.setRequestCount(1L);
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setRequestCount(-1L);
		});
	}
	
	@Test
	void testSetRequestCountInvalid1() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setRequestCount(-1L);
		});
	}

	@SuppressWarnings({ "unused", "static-method" })
	@Test
	void testEnvironmentRatioNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new EnvironmentRatio(null);
		});
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	@Test
	void testEnvironmentRatioEmpty() {
		assertThrows(IllegalArgumentException.class, () -> {
			new EnvironmentRatio(" ");
		});
	}

}
