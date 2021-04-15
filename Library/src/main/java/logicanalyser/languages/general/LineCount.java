package logicanalyser.languages.general;

import logicanalyser.MetricBase;
import logicanalyser.MetricProducer;
import logicanalyser.MetricSingle;
import logicanalyser.MetricValue;
import logicanalyser.languages.MetricContext;

/**
 * Counts the number of lines in the document
 */
public class LineCount extends MetricProducer<String> {
	public LineCount() {
		super(String.class);
	}
	
	@Override
	public String getMetricName() {
		return "linecount";
	}
	
	@Override
	public MetricBase produce(MetricContext context, String target) {
		String[] lines = target.split("\\n");
		return new MetricSingle(getMetricName(), new MetricValue(lines.length));
	}
}
