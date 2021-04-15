package logicanalyser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ReportTest {
	@Test
	public void validConstruction() {
		MetricSingle metric1 = mock(MetricSingle.class);
		when(metric1.getName())
			.thenReturn("metric1");
		MetricList metric2 = mock(MetricList.class);
		when(metric2.getName())
			.thenReturn("metric2");
		
		List<MetricBase> metrics = Arrays.asList(
			metric1,
			metric2
		);
		
		List<Marker> markers = Arrays.asList(
			mock(Marker.class),
			mock(Marker.class)
		);
		
		Report report = new Report(metrics, markers);
		
		assertTrue(report.getAllMarkers().containsAll(markers));
		assertTrue(report.getAllMetrics().containsAll(metrics));
	}
	
	@Test
	public void checkMetricMap() {
		MetricSingle metric1 = mock(MetricSingle.class);
		when(metric1.getName())
			.thenReturn("metric1");
		MetricList metric2 = mock(MetricList.class);
		when(metric2.getName())
			.thenReturn("metric2");
		
		List<MetricBase> metrics = Arrays.asList(
			metric1,
			metric2
		);
		
		Report report = new Report(metrics, Collections.emptyList());
		
		assertTrue(report.hasMetric("metric1"));
		assertEquals(metric1, report.getMetric("metric1"));
	}
	
	@Test
	public void filterMarkers() {
		Marker marker1 = mock(Marker.class);
		when(marker1.getSeverity())
			.thenReturn(SeverityRating.Informational);
		Marker marker2 = mock(Marker.class);
		when(marker2.getSeverity())
			.thenReturn(SeverityRating.Warning);
		Marker marker3 = mock(Marker.class);
		when(marker3.getSeverity())
			.thenReturn(SeverityRating.Error);
		Marker marker4 = mock(Marker.class);
		when(marker4.getSeverity())
			.thenReturn(SeverityRating.CriticalError);
		
		List<Marker> markers = Arrays.asList(
			marker1,
			marker2,
			marker3,
			marker4
		);
		
		Report report = new Report(Collections.emptyList(), markers);
		
		assertEquals(Arrays.asList(marker1), report.getAllMarkers(SeverityRating.Informational));
		assertEquals(Arrays.asList(marker2), report.getAllMarkers(SeverityRating.Warning));
		assertEquals(Arrays.asList(marker3), report.getAllMarkers(SeverityRating.Error));
		assertEquals(Arrays.asList(marker4), report.getAllMarkers(SeverityRating.CriticalError));
		
		assertEquals(Arrays.asList(marker1), report.getInfoMarkers());
		assertEquals(Arrays.asList(marker2), report.getWarningMarkers());
		assertEquals(Arrays.asList(marker3, marker4), report.getErrorMarkers());
	}
}
