package logicanalyser.languages.css;

import com.google.common.collect.ImmutableMap;
import com.helger.css.decl.CascadingStyleSheet;

import logicanalyser.MetricMap;
import logicanalyser.MetricProducer;
import logicanalyser.MetricValue;
import logicanalyser.languages.MetricContext;

public class LanguageBreakdown extends MetricProducer<CascadingStyleSheet> {
	public LanguageBreakdown() {
		super(CascadingStyleSheet.class);
	}

	@Override
	public String getMetricName() {
		return "languages";
	}

	@Override
	public MetricMap produce(MetricContext context, CascadingStyleSheet target) {
		return new MetricMap(
			getMetricName(),
			ImmutableMap.<String, MetricValue>builder()
				.put("css", new MetricValue(100))
				.build()
		);
	}
}
