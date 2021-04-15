package logicanalyser.languages.html;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.common.net.MediaType;

import logicanalyser.Interval;
import logicanalyser.MetricMap;
import logicanalyser.MetricProducer;
import logicanalyser.MetricValue;
import logicanalyser.languages.MetricContext;
import logicanalyser.util.CountingMap;

public class LanguageBreakdown extends MetricProducer<Document> {
	public LanguageBreakdown() {
		super(Document.class);
	}

	@Override
	public String getMetricName() {
		return "languages";
	}

	@Override
	public MetricMap produce(MetricContext context, Document target) {
		Elements scriptTags = target.select("script");
		Elements styleTags = target.select("style");
		
		CountingMap<MediaType> count = new CountingMap<>();
		
		int size = 0;
		
		for (Element script : scriptTags) {
			if (script.hasAttr("src")) {
				continue;
			}
			
			Interval interval = innerInterval(script);
			
			MediaType type;
			if (script.hasAttr("type")) {
				type = MediaType.parse(script.attr("type"));
			} else {
				type = MediaType.JAVASCRIPT_UTF_8;
			}
			
			type = type.withoutParameters();
			
			count.add(type, interval.length());
			size += interval.length();
		}
		
		for (Element style : styleTags) {
			Interval interval = innerInterval(style);

			MediaType type = MediaType.CSS_UTF_8;
			type = type.withoutParameters();
			
			count.add(type, interval.length());
			size += interval.length();
		}
		
		int totalSize = target.position().end;
		
		size = totalSize - size;
		
		count.add(MediaType.HTML_UTF_8.withoutParameters(), size);
		
		toPercentages(count, totalSize);
		
		return new MetricMap(
			getMetricName(),
			count.toMap(
				MediaType::subtype,
				MetricValue::new
			)
		);
	}
	
	private Interval innerInterval(Element element) {
		int start = Integer.MAX_VALUE;
		int end = 0;
		
		for (Node node : element.childNodes()) {
			start = Math.min(node.position().start, start);
			end = Math.max(node.position().end, end);
		}
		
		return new Interval(start, end);
	}
	
	private void toPercentages(CountingMap<MediaType> map, int totalSize) {
		Map<MediaType, Float> values = map.toMap(value -> value / (float)totalSize * 100);
		
		TreeMap<Float, MediaType> ordered = new TreeMap<>((f1, f2) -> -Float.compare(f1, f2));
		
		int sum = 0;
		for (MediaType type : values.keySet()) {
			float percentage = values.get(type);
			int intPercentage = (int)percentage;
			sum += intPercentage;
			map.set(type, intPercentage);
			ordered.put(percentage - intPercentage, type);
		}
		
		Iterator<MediaType> it = ordered.values().iterator();
		for (int i = sum; i < 100 && it.hasNext(); ++i) {
			map.increment(it.next());
		}
	}
}
