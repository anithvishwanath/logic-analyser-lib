package logicanalyser.languages.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import logicanalyser.MetricMap;
import logicanalyser.MetricProducer;
import logicanalyser.MetricValue;
import logicanalyser.languages.MetricContext;
import logicanalyser.util.CountingMap;

public class TagTypes extends MetricProducer<Document> {
	public TagTypes() {
		super(Document.class);
	}
	
	@Override
	public String getMetricName() {
		return "html.tagtypes";
	}
	
	@Override
	public MetricMap produce(MetricContext context, Document target) {
		CountingMap<String> count = new CountingMap<>();
		
		for (Element element : target.select("*")) {
			if (element == target) {
				continue;
			}
			
			if (element.isBlock()) {
				count.increment("block");
			} else {
				count.increment("inline");
			}
		}
		
		return new MetricMap(
			getMetricName(), 
			count.toMap(MetricValue::new)
		);
	}
}
