package logicanalyser.languages.general;

import com.google.common.net.MediaType;

import logicanalyser.MetricBase;
import logicanalyser.MetricList;
import logicanalyser.MetricProducer;
import logicanalyser.MetricValue;
import logicanalyser.languages.MetricContext;

public class Indentation extends MetricProducer<String> {
	public static final int LANGUAGE_HTML = 0;
	public static final int LANGUAGE_CSS = 1;
	public static final int LANGUAGE_JAVASCRIPT = 2;
	public static final int LANGUAGE_OTHER = 3;
	
	public Indentation() {
		super(String.class);
	}
	
	@Override
	public String getMetricName() {
		return "indentation";
	}

	@Override
	public MetricBase produce(MetricContext context, String content) {
		String[] lines = content.split("\n");
		MetricValue[] values = new MetricValue[lines.length];
		
		for (int i = 0; i < values.length; ++i) {
			String line = lines[i];
			int offset = 0;
			int lineStart = 0;
			int lineEnd = 0;
			boolean hitStart = false;
			for (int c = 0; c < line.length(); ++c) {
				char ch = line.charAt(c);
				if (ch == ' ') {
					++offset;
				} else if (ch == '\t') {
					offset += 4;
				} else {
					if (!hitStart) {
						lineStart = offset;
						hitStart = true;
					}
					++offset;
					lineEnd = offset;
				}
			}
			
			MediaType language = context.getLanguageAtLine(i);
			int type = LANGUAGE_OTHER;
			if (language.is(MediaType.HTML_UTF_8)) {
				type = LANGUAGE_HTML;
			} else if (language.is(MediaType.CSS_UTF_8)) {
				type = LANGUAGE_CSS;
			} else if (language.is(MediaType.JAVASCRIPT_UTF_8)) {
				type = LANGUAGE_JAVASCRIPT;
			}
			
			values[i] = new MetricValue(lineStart, lineEnd, type);
		}
		
		return new MetricList(getMetricName(), values);
	}
}
