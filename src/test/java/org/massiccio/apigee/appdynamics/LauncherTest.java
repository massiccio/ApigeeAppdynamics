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
