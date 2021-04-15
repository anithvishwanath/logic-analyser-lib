package logicanalyser;

import java.io.Serializable;

/**
 * MetricBase is an abstract class that represents statistics about a file.
 */
public abstract class MetricBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	/**
	 * Creates a new metric
	 * @param name The name of the metric
	 */
	protected MetricBase(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the metric
	 * @return A name representing the metric
	 */
	public String getName() {
		return name;
	}
}
