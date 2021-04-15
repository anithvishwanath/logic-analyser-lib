package logicanalyser;

import logicanalyser.languages.MetricContext;

/**
 * A producer that takes some target and makes metrics from it
 * @param <E> The type of target. The options are the same as Rules
 */
public abstract class MetricProducer<E> {
	private final Class<E> type;
	
	public MetricProducer(Class<E> type) {
		this.type = type;
	}
	
	/**
	 * The target type that is expected in produce
	 * @return The target type
	 */
	public Class<E> getTarget() {
		return type;
	}
	
	/**
	 * The name of the metric this produces
	 * @return The name
	 */
	public abstract String getMetricName();
	
	/**
	 * Produces a metric from the given target
	 * @param context Extra information that may be useful
	 * @return A metric
	 */
	public abstract MetricBase produce(MetricContext context, E target);
}
