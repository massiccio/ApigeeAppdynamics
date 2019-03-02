package org.massiccio.fakeservice;

import java.util.concurrent.atomic.AtomicInteger;

public class Metric implements MetricMBean {

	private static AtomicInteger counter = new AtomicInteger();
	
	final double id;
	
	public Metric() {
		id = Math.random();
	}
	
	@Override
	public int getValue() {
		return counter.incrementAndGet();
	}
}
