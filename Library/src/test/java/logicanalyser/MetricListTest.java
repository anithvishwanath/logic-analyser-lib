package logicanalyser;

import static org.junit.Assert.*;
import org.junit.Test;

public class MetricListTest {
	@Test
	public void assignsValuesCorrectly() {
		MetricValue value1 = new MetricValue(2);
		MetricValue value2 = new MetricValue(3);
		
		MetricList metric = new MetricList("metric.name", value1, value2);
		
		assertEquals("metric.name", metric.getName());
		assertEquals(2, metric.size());
		assertEquals(value1, metric.getValue(0));
		assertEquals(value2, metric.getValue(1));
	}
	
	@Test(expected = NullPointerException.class)
	public void rejectsNullValue() {
		MetricList metric = new MetricList("metric.name", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void rejectsInconsistentValueSize() {
		MetricValue value1 = new MetricValue(2);
		MetricValue value2 = new MetricValue(3, 5);
		
		MetricList metric = new MetricList("metric.name", value1, value2);
	}
}
