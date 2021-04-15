package logicanalyser;

import com.google.common.base.Preconditions;
import java.io.Serializable;

/**
 * MetricSingle contains a list of values related to a file.
 */
public class MetricSingle extends MetricBase implements Serializable {
	private static final long serialVersionUID = 1L;

	private final MetricValue value;
	
	/**
	 * Creates a new metric 
	 * @param name The name of the metric
	 * @param value A list of values from MetricValue
	 */
	public MetricSingle(String name, MetricValue value) {
		super(name);
		Preconditions.checkNotNull(value);
		this.value = value;
	}
	
	/**
	 * Gets the value from Metric Value
	 * @return The values from Metric Value
	 */
	public MetricValue getValue() {
		return value;
	}
}
