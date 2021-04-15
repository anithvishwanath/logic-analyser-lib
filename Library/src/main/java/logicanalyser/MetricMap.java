package logicanalyser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * MetricMap provides a mapping between key and MetricValue.
 * Allows things like counting of objects within a category
 */
public class MetricMap extends MetricBase implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Map<String, MetricValue> values;
	
	/**
	 * Creates a new MetricList
	 * @param name The name of the metric
	 * @param values A map of keys to MetricValues. The values are all expected to have the same size
	 * @throws IllegalArgumentException Thrown if the values are incorrectly
	 *		sized, or contain nulls
	 */
	public MetricMap(String name, Map<String, MetricValue> values) {
		super(name);
		
		if (values.size() > 0) {
			int size = -1;
			for (MetricValue value : values.values()) {
				if (value == null) {
					throw new IllegalArgumentException("Values contains null");
				}
				
				if (size != -1 && value.size() != size) {
					throw new IllegalArgumentException("Values contain multiple value sizes");
				}
				
				size = value.size();
			}
		}
		
		this.values = Collections.unmodifiableMap(values);
	}
	
	/**
	 * Gets all the values within this metric
	 * @return An unmodifiable collection of values
	 */
	public Collection<MetricValue> getValues() {
		return values.values();
	}
	
	/**
	 * Gets all the keys within this metric
	 * @return An unmodifiable set of keys
	 */
	public Set<String> getKeys() {
		return values.keySet();
	}
	
	/**
	 * Gets the size of values
	 * @return number of values
	 */
	public int size() {
		return values.size();
	}
	
	/**
	 * Retrieves a value by a key
	 * @param key The key of the value
	 * @return The index element of the value
	 */
	public MetricValue getValue(String key) {
		return values.get(key);
	}
}
