package logicanalyser.languages.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import logicanalyser.Marker;
import logicanalyser.MetricBase;
import logicanalyser.MetricMap;
import logicanalyser.MetricProducer;
import logicanalyser.MetricValue;
import logicanalyser.languages.HTMLAnalyser.HTMLMetricContext;
import logicanalyser.languages.MetricContext;
import logicanalyser.util.CountingMap;

/**
 * This metric counts each tag that is present in the 
 * document model.
 */
public class TagCounter extends MetricProducer<Document> {
	public TagCounter() {
		super(Document.class);
	}
	
	@Override
	public String getMetricName() {
		return "html.tagcount";
	}

	@Override
	public MetricBase produce(MetricContext context, Document document) {
		CountingMap<String> count = new CountingMap<>();
		CountingMap<String> errors = new CountingMap<>();
		CountingMap<String> warnings = new CountingMap<>();
		
		for (Element element : document.select("*")) {
			if (element == document) {
				continue;
			}
			
			count.increment(element.tagName());
		}
		
		for (Marker marker : context.getMarkers()) {
			CountingMap<String> target;
			switch (marker.getSeverity()) {
			case Warning:
				target = warnings;
				break;
			case Error:
			case CriticalError:
				target = errors;
				break;
			default:
				continue;
			}
			
			Node node = ((HTMLMetricContext)context).getNodeAt(marker.getLocation().start);
			
			while (!(node instanceof Element) && node != document) {
				node = node.parent();
			}
			
			if (node == document) {
				continue;
			}
			
			Element errorElement = (Element)node;
			target.increment(errorElement.tagName());
		}
		
		return new MetricMap(
			getMetricName(),
			count.toMapWithKey((key, value) -> {
				return new MetricValue(
					value, 
					warnings.get(key),
					errors.get(key)
				);
			})
		);
	}
}
