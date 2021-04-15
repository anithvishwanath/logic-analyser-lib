package logicanalyser.languages.css;

import org.w3c.dom.css.CSSRule;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;

import logicanalyser.MetricBase;
import logicanalyser.MetricMap;
import logicanalyser.MetricProducer;
import logicanalyser.MetricValue;
import logicanalyser.languages.MetricContext;
import logicanalyser.util.CountingMap;

public class PropertyCount extends MetricProducer<CascadingStyleSheet> {

	public PropertyCount() {
		super(CascadingStyleSheet.class);
	}

	@Override
	public String getMetricName() {
		return "css.propertycount";
	}

	@Override
	public MetricBase produce(MetricContext context, CascadingStyleSheet target) {
		CountingMap<String> tagCount = new CountingMap<>();
		for (CSSStyleRule rule : target.getAllStyleRules()) {
			for (CSSDeclaration declaration : rule.getAllDeclarations()) {
				tagCount.increment(declaration.getProperty());
			}
		}
		
		return new MetricMap(
			getMetricName(),
			tagCount.toMap(
				MetricValue::new
			)
		);
	}
}
