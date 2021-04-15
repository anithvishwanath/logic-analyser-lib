package logicanalyser;

import static org.junit.Assert.*;
import org.junit.Test;

public class IntervalTest {
	@Test
	public void intervalAssignedCorrectly() {
		Interval interval = new Interval(0, 10);
		assertEquals(0, interval.start);
		assertEquals(10, interval.end);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void intervalEndIsAfterStart() {
		Interval interval = new Interval(5, 4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void intervalStartIsNotNegative() {
		Interval interval = new Interval(-1, 3);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void intervalStartIsNotEnd() {
		Interval interval = new Interval(4, 4);
	}
	
	@Test
	public void lengthIsCorrect() {
		Interval interval = new Interval(3, 10);
		assertEquals(7, interval.length());
	}
}
