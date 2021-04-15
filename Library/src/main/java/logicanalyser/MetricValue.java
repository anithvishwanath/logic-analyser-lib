package logicanalyser;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * MetricValues contains the metrics / statistical information of the file.
 */
public class MetricValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final int[] values;
	
	/**
	 * Creates a new MetricValue 
	 * @param values A list of metric values. There must be at least 1 value
	 */
	public MetricValue(int... values) {
		Preconditions.checkArgument(values.length >= 1, "A MetricValue must have at least 1 value");
		this.values = values;
	}
	
	/**
	 * Gets the first value of the metric 
	 * @return The first element in values[]
	 */
	public int get() {
		return values[0];
	}
	
	/**
	 * Gets the index for the value
	 * @return The index of the value
	 */
	public int get(int index) {
		return values[index];
	}
	
	/**
	 * Gets the size of values
	 * @return Length of values
	 */
	public int size() {
		return values.length;
	}
}
