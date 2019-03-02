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


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.massiccio.fakeservice.FakeService;

class LauncherTest {
	
	private static FakeService service;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// we create only one service, however before each test all MBeans are
		// re-created
		service = new FakeService();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		//
	}

	@SuppressWarnings("static-method")
	@BeforeEach
	void setUp() throws Exception {
		service.register();
	}

	@SuppressWarnings("static-method")
	@AfterEach
	void tearDown() throws Exception {
		service.unregister();
	}
}
