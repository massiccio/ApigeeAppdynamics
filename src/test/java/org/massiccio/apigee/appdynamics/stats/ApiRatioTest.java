package org.massiccio.apigee.appdynamics.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiRatioTest {

	private static final String PREFIX = "myprefix";

	private ApiRatio test;

	@BeforeEach
	void setUp() throws Exception {
		test = new ApiRatio(PREFIX);
	}

	@AfterEach
	void tearDown() throws Exception {
		//
	}

	@SuppressWarnings({ "static-method", "unused" })
	@Test
	void testInvalidAPI1() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ApiRatio(null);
		});
	}

	@SuppressWarnings({ "static-method", "unused" })
	@Test
	void testInvalidAPI2() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ApiRatio(" ");
		});
	}

	@Test
	void testSetResponseCount() {
		this.test.setResponseCount(1L);
		assertEquals(1L, this.test.getResponseCount());
	}

	@Test
	void testSetResponseCountInvalid() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseCount(-1L);
		});
	}

	@Test
	void testSetResponseSentXXX() {
		this.test.setResponseSentXXX("500", 1L);
	}

	@Test
	void testSetResponseSentXXXNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseSentXXX(null, 1L);
		});
	}

	@Test
	void testSetResponseSentXXXEmpty() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseSentXXX(" ", 1L);
		});
	}

	@Test
	void testSetResponseSentXXXNegative() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setResponseSentXXX("500", -1L);
		});
	}

	@Test
	void testGetResponseCount() {
		assertEquals(0L, this.test.getResponseCount());
		this.test.setResponseCount(2L);
		assertEquals(2L, this.test.getResponseCount());
	}

	@Test
	void testSetRequestCount() {
		assertEquals(0L, this.test.getRequestCount());
		this.test.setRequestCount(2L);
		assertEquals(2L, this.test.getRequestCount());
	}

	@Test
	void testSetRequestCountInvalid() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.test.setRequestCount(-1L);
		});
	}

	@Test
	void testGetRequestCount() {
		assertEquals(0L, this.test.getRequestCount());
	}

	@Test
	void testGetPrefix() {
		assertEquals(PREFIX, this.test.getPrefix());
	}

	@Test
	void testToStringEmpty() {
		assertEquals(ApiRatio.EMPTY_STRING, this.test.toString());
	}

	@Test
	void testToStringEmpty1() {
		this.test.setResponseCount(1L);
		assertNotEquals(ApiRatio.EMPTY_STRING, this.test.toString());
	}

	@Test
	void testToString() {
		this.test.setResponseCount(1L);
		this.test.setResponseSentXXX("200", 1L);
		this.test.setResponseSentXXX("500", 1L);
		assertNotEquals("", this.test.toString());
	}

	@Test
	void testToStringPending() {
		this.test.setRequestCount(4L);
		this.test.setResponseCount(1L);
		this.test.setResponseSentXXX("200", 1L);
		this.test.setResponseSentXXX("500", 1L);
		assertNotEquals("", this.test.toString());
	}
}
