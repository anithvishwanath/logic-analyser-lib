package logicanalyser.languages;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.net.MediaType;

import logicanalyser.InvalidContentException;
import logicanalyser.LogicAnalyser;
import logicanalyser.MetricBase;
import logicanalyser.MetricProducer;
import logicanalyser.Report;
import logicanalyser.Ruleset;
import logicanalyser.languages.general.*;

/**
 * Represents a language that can be processed by the system.
 */
public abstract class LanguageBase {
	private SetMultimap<Class<?>, MetricProducer<?>> metricProducers;
	
	public LanguageBase() {
		metricProducers = HashMultimap.create();
		
		// Register the general ones
		registerMetric(new Indentation());
		registerMetric(new LineCount());
	}
	
	/**
	 * Gets the name of this language for display purposes
	 * @return The name
	 */
	public abstract String getName();
	
	/**
	 * Checks if this language can accept the given media type
	 * @param contentType The MIME type of the content.
	 * @return True if this language checker can check the given media type
	 */
	public abstract boolean doesAccept(MediaType contentType);
	
	/**
	 * Retrieves all the MIME types this language can process
	 * @return Some iterable containing the types
	 */
	public abstract Iterable<MediaType> getAcceptedTypes();
	
	/**
	 * Processes the given content, checking all rulesets.
	 * @param contentType The MIME type of the content.
	 * @param content The content itself.
	 * @param rulesets The Rulesets that will be checked. These must be rulesets for this language.
	 * @param analyser The LogicAnalyser instance that ran this
	 * @return A Report of all issues with the content, and all the statistics of the content.
	 * @throws IllegalArgumentException Thrown if the {@code contentType} is not valid. See {@link #doesAccept(MediaType)}
	 * @throws InvalidContentException Thrown if the {@code content} cannot be interpreted.
	 */
	public abstract Report process(MediaType contentType, String content, Iterable<Ruleset> rulesets, LogicAnalyser analyser) throws IllegalArgumentException, InvalidContentException;
	
	/**
	 * Checks if a rule can use the given type as their target
	 * @param type The type to use
	 * @return True if the type is supported for Rule
	 */
	public abstract boolean canRulesAccept(Class<?> type);
	
	/**
	 * Gets the type that is accepted through the context
	 * @return The type
	 */
	public abstract Class<?> getContextType();
	
	/**
	 * Registers a new metric
	 * @param producer The producer for this metric
	 * @throws IllegalArgumentException Thrown if the metric already exists, 
	 * 		or does not have an accepted target type as check 
	 * 		with {@link #canRulesAccept(Class)}
	 */
	public void registerMetric(MetricProducer<?> producer) throws IllegalArgumentException {
		Preconditions.checkNotNull(producer);
		Preconditions.checkArgument(canRulesAccept(producer.getTarget()));
		
		Preconditions.checkArgument(
			!metricProducers.get(producer.getTarget())
			.stream()
			.anyMatch(p -> p.getMetricName().equals(producer.getMetricName()))
		);
		
		metricProducers.put(producer.getTarget(), producer);
	}
	
	/**
	 * Deregisters a metric
	 * @param producer The producer for the metric
	 */
	public void deregisterMetric(MetricProducer<?> producer) {
		metricProducers.remove(producer.getTarget(), producer);
	}
	
	/**
	 * Retrieves metric producers for a given target type
	 * @param type The type they accept
	 * @return An unmodifiable collection of the producers
	 */
	public <T> Collection<MetricProducer<T>> getMetricProducersFor(Class<T> type) {
		return Collections.unmodifiableCollection((Collection)metricProducers.get(type));
	}
	
	/**
	 * Produce metrics for the given target
	 * @param context The context
	 * @param target The target
	 * @return A list of produced metrics
	 */
	public List<MetricBase> generateMetrics(MetricContext context, Object target) {
		Collection<MetricProducer<Object>> producers = getMetricProducersFor((Class<Object>)target.getClass());
		
		List<MetricBase> metrics = Lists.newArrayListWithExpectedSize(producers.size());
		for (MetricProducer<Object> producer : producers) {
			MetricBase metric = producer.produce(context, target);
			if (metric != null) {
				metrics.add(metric);
			}
		}
		
		return metrics;
	}
}
