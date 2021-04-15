package logicanalyser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Reports provide statistics and errors about an analysed file.
 */
public class Report implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Map<String, MetricBase> metrics;
	private final List<Marker> markers;
	
	/**
	 * Creates a new report with the given metrics and markers
	 * @param metrics Any Iterable containing the metrics
	 * @param markers Any Iterable containing the markers
	 */
	public Report(Iterable<MetricBase> metrics, Iterable<Marker> markers) {
		ImmutableMap.Builder<String, MetricBase> mapBuilder = ImmutableMap.builder();
		
		for (MetricBase metric : metrics) {
			mapBuilder.put(metric.getName(), metric);
		}
		
		this.metrics = mapBuilder.build();
		
		List<Marker> tempMarkers = Lists.newArrayList(markers);
		Collections.sort(tempMarkers);
		this.markers = ImmutableList.copyOf(tempMarkers);
	}
	
	/**
	 * Retrieves the metric with the given name
	 * @param name The name of the metric, case sensitive
	 * @return The metric or null
	 */
	public MetricBase getMetric(String name) {
		return metrics.get(name);
	}
	
	/**
	 * Checks if a metric is present
	 * @param name The name of the metric, case sensitive
	 * @return True if the metric exists
	 */
	public boolean hasMetric(String name) {
		return metrics.containsKey(name);
	}
	
	/**
	 * Retrieves all metrics in this report
	 * @return An unmodifiable collection
	 */
	public Collection<MetricBase> getAllMetrics() {
		return metrics.values();
	}
	
	/**
	 * Gets all the markers present in this report of any type
	 * @return An unmodifiable list of markers
	 */
	public List<Marker> getAllMarkers() {
		return markers;
	}
	
	/**
	 * Retrieves only the markers with the specific type in this report
	 * @param rating The severity rating of the markers to retrieve
	 * @return An unmodifiable list of markers
	 */
	public List<Marker> getAllMarkers(SeverityRating rating) {
		return ImmutableList.copyOf(
			Iterables.filter(
				markers, 
				e -> e.getSeverity() == rating
			)
		);
	}
	
	/**
	 * Retrieves only error markers (both Error and CriticalError) in this report
	 * @return An unmodifiable list of markers
	 */
	public List<Marker> getErrorMarkers() {
		return ImmutableList.copyOf(
			Iterables.filter(
				markers, 
				e -> e.getSeverity() == SeverityRating.CriticalError || 
					 e.getSeverity() == SeverityRating.Error
			)
		);
	}
	
	/**
	 * Retrieves only the warning markers in this report
	 * @return An unmodifiable list of markers
	 */
	public List<Marker> getWarningMarkers() {
		return getAllMarkers(SeverityRating.Warning);
	}
	
	/**
	 * Retrieves only the informational markers in this report
	 * @return An unmodifiable list of markers
	 */
	public List<Marker> getInfoMarkers() {
		return getAllMarkers(SeverityRating.Informational);
	}
}
