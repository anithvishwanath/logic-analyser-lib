package logicanalyser;

import static org.junit.Assert.*;
import org.junit.Test;

public class MetricValueTest {
	@Test
	public void assignsValuesCorrectly() {
		MetricValue value = new MetricValue(0, 1, 2);
		
		assertEquals(3, value.size());
		
		assertEquals(0, value.get(0));
		assertEquals(1, value.get(1));
		assertEquals(2, value.get(2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void rejectsEmptyValue() {
		MetricValue value = new MetricValue();
	}
}
