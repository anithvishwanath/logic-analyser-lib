package logicanalyser;

import java.io.Serializable;

/**
 * MetricList contains a list of metric values
 */
public class MetricList extends MetricBase implements Serializable {
	private static final long serialVersionUID = 1L;

	private final MetricValue[] values;
	
	/**
	 * Creates a new MetricList
	 * @param name The name of the metric
	 * @param values A list of values from MetricValue
	 * @throws IllegalArgumentException Thrown if the values are incorrectly
	 *		sized, or contain nulls
	 */
	public MetricList(String name, MetricValue... values) {
		super(name);
		
		if (values.length > 0) {
			int size = values[0].size();
			for (MetricValue value : values) {
				if (value == null) {
					throw new IllegalArgumentException("Values contains null");
				}
				
				if (value.size() != size) {
					throw new IllegalArgumentException("Values contain multiple value sizes");
				}
			}
		}			
		this.values = values;
	}
	
	/**
	 * Gets the value for this metric from MetricValue[]
	 * @return A value from MetricValue[]
	 */
	public MetricValue[] getValues() {
		return values;
	}

	/**
	 * Gets the size of values
	 * @return Length of values
	 */
	public int size() {
		return values.length;
	}
	
	/**
	 * Gets the index element in values
	 * @param index The index of the value, up to {@link #size()} -1
	 * @return The index element of the value
	 */
	public MetricValue getValue(int index) {
		return values[index];
	}
}
