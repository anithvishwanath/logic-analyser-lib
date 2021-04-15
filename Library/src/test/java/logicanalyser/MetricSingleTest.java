package logicanalyser;

import org.junit.Test;
import static org.junit.Assert.*;

public class MetricSingleTest {
	@Test
	public void tryValidConstruction() {
		MetricValue value = new MetricValue(10);
		
		MetricSingle metric = new MetricSingle("metric.name", value);
		assertEquals(value, metric.getValue());
		assertEquals("metric.name", metric.getName());
	}
	
	@Test(expected=NullPointerException.class)
	public void rejectsNullValue() {
		MetricSingle metric = new MetricSingle("metric.name", null);
	}
}
