package logicanalyser.languages;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import logicanalyser.Interval;
import logicanalyser.InvalidContentException;
import logicanalyser.LogicAnalyser;
import logicanalyser.Marker;
import logicanalyser.MetricBase;
import logicanalyser.MetricProducer;
import logicanalyser.MetricSingle;
import logicanalyser.MetricValue;
import logicanalyser.Report;
import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.config.RuleConfiguration;
import logicanalyser.languages.html.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

public class HTMLAnalyser extends LanguageBase {
	private static final MediaType ACCEPTED_TYPE = MediaType.HTML_UTF_8;
	private static final Set<Class<?>> ACCEPTED_RULE_TYPES = ImmutableSet.of(String.class, Document.class, Element.class);
	
	public HTMLAnalyser() {
		registerMetric(new TagCounter());
		registerMetric(new TagTypes());
		registerMetric(new LanguageBreakdown());
	}
	
	@Override
	public String getName() {
		return "HTML";
	}

	@Override
	public boolean doesAccept(MediaType contentType) {
		return ACCEPTED_TYPE.is(contentType);
	}
	
	@Override
	public Iterable<MediaType> getAcceptedTypes() {
		return Arrays.asList(ACCEPTED_TYPE);
	}

	@Override
	public boolean canRulesAccept(Class<?> type) {
		return ACCEPTED_RULE_TYPES.contains(type);
	}

	@Override
	public Report process(MediaType contentType, String content, Iterable<Ruleset> rulesets, LogicAnalyser analyser) throws IllegalArgumentException {
		Preconditions.checkArgument(doesAccept(contentType), "Invalid content type");
		
		//Document document = Jsoup.parse(content, "", Parser.xmlParser());
		Document document = Jsoup.parse(content, "", Parser.htmlParser());
		
		if (document.childNodes().isEmpty() || document.children().isEmpty()) {
			throw new InvalidContentException("Cannot interpret as HTML");
		}
		
		List<Marker> markers = generateMarkers(document, content, rulesets, analyser);
		List<MetricBase> metrics = generateMetrics(document, content, markers);
		metrics.addAll(generateMarkerMetrics(markers));
		
		Report report = new Report(metrics, markers);
		return report;
	}
	
	@Override
	public Class<Element> getContextType() {
		return Element.class;
	}
	
	private List<Marker> generateMarkers(Document document, String content, Iterable<Ruleset> rulesets, LogicAnalyser analyser) {
		Iterable<Rule<Element>> allElementRules = Collections.emptyList();
		ElementContext context = new ElementContext(content, document, analyser.getRuleConfiguration());
		
		for (Ruleset ruleset : rulesets) {
			allElementRules = Iterables.concat(allElementRules, ruleset.getRulesUsing(Element.class));
			Iterable<Rule<Document>> documentRules = ruleset.getRulesUsing(Document.class);
			Iterable<Rule<String>> rawRules = ruleset.getRulesUsing(String.class);
			
			for (Rule<Document> rule : documentRules) {
				context.setRule(rule);
				rule.check(context, document);
			}
			
			for (Rule<String> rule : rawRules) {
				context.setRule(rule);
				rule.check(context, content);
			}
		}
		
		// So we dont have to traverse the tree for each ruleset, we do it here for all rulesets
		for (Element element : document.select("*")) {
			for (Rule<Element> rule : allElementRules) {
				context.setRule(rule);
				rule.check(context, element);
			}
		}
		
		return context.getMarkers();
	}
	
	private List<MetricBase> generateMetrics(Document document, String content, List<Marker> markers) {
		List<MetricBase> metrics = Lists.newArrayList();
		
		HTMLMetricContext context = new HTMLMetricContext(markers, document, content);
		
		metrics.addAll(generateMetrics(context, content));
		metrics.addAll(generateMetrics(context, document));
		
		Collection<MetricProducer<Element>> elementMetrics = getMetricProducersFor(Element.class);
		if (!elementMetrics.isEmpty()) {
			for (Element element : document.select("*")) {
				for (MetricProducer<Element> producer : elementMetrics) {
					MetricBase metric = producer.produce(context, element);
					if (metric != null) {
						metrics.add(metric);
					}
				}
			}
		}
		
		return metrics;
	}
	
	private List<MetricBase> generateMarkerMetrics(List<Marker> markers) {
		List<MetricBase> metrics = Lists.newArrayList();
		int errors = 0;
		int warnings = 0;
		
		for (Marker marker : markers) {
			switch (marker.getSeverity()) {
			case Warning:
				++warnings;
				break;
			case Error:
			case CriticalError:
				++errors;
				break;
			default:
				break;
			} 
		}
		
		metrics.add(new MetricSingle("errorcount", new MetricValue(errors)));
		metrics.add(new MetricSingle("warningcount", new MetricValue(warnings)));
		
		return metrics;
	}
	
	private class ElementContext extends BaseContext {
		private final String content;
		private final Document document;
		
		public ElementContext(String content, Document document, RuleConfiguration details) {
			super(details);
			this.content = content;
			this.document = document;
		}
		
		@Override
		protected String generateLocationInfo(Object target, Interval position) {
			final int PREAMBLE_SIZE = 10;
			final int FOLLOWUP_SIZE = 20;
			
			String preamble = content.substring(Math.max(0, position.start - PREAMBLE_SIZE), position.start);
			String targetText = content.substring(position.start, position.end);
			String followUp = content.substring(position.end, Math.min(content.length(), position.end + FOLLOWUP_SIZE));
			
			String extraLocation = String.format(
				"%s[%s]%s",
				escape(preamble),
				escape(targetText),
				escape(followUp)
			);
			
			return extraLocation;
		}
		
		private String escape(String text) {
			return text
				.replace("\n", "\\n")
				.replace("\t", "\\t")
				.replace("\r", "\\r");
		}
		
		@Override
		public Interval locate(Object rawTarget) {
			Preconditions.checkArgument(rawTarget instanceof Element, "HTML Analyser only accepts Element as the context target");
			
			Element target = (Element)rawTarget;
			// Attempt to locate the element
			
			if (target.position() != null) {
				return new Interval(target.position().start, target.position().end);
			} else {
				return new Interval(0, 1);
			}
		}
	}
	
	public class HTMLMetricContext implements MetricContext {
		private final List<Marker> markers;
		private final String content;
		private final Document doc;
		
		private final String[] lines;
		private final int[] lineStarts;
		
		public HTMLMetricContext(List<Marker> markers, Document doc, String content) {
			this.markers = markers;
			this.doc = doc;
			this.content = content;
			
			lines = content.split("\n");
			lineStarts = new int[lines.length];
			int offset = 0;
			for (int i = 0; i < lines.length; ++i) {
				String line = lines[i];
				lineStarts[i] = offset;
				offset += line.length() + 1;
			}
		}
		
		public int getIndexOfLine(int line) {
			return lineStarts[line];
		}
		
		public Node getNodeAt(int index) {
			List<Node> toSearch = doc.childNodes();
			
			Node best = null;
			while (!toSearch.isEmpty()) {
				Node last = null;
				Node target = null;
				for (Node node : toSearch) {
					if (index < node.position().start) {
						if (last == null) {
							target = node;
						} else {
							target = last;
						}
						break;
					} else if (index < node.position().end) {
						target = node;
						break;
					}
					last = node;
				}
				
				if (target == null) {
					target = last;
				}
				
				best = target;
				if (target.childNodeSize() > 0) {
					toSearch = target.childNodes();
				} else {
					break;
				}
			}
			
			if (best != null) {
				return best;
			} else {
				return doc.childNode(0);
			}
		}
		
		private MediaType getLanguage(Node node) {
			if (node instanceof TextNode) {
				if (node.parent() instanceof Element) {
					Element parent = (Element)node.parent();
					if (parent.tagName().equals("script")) {
						if (parent.hasAttr("type")) {
							try {
								return MediaType.parse(parent.attr("type"));
							} catch (IllegalArgumentException e) {
							}
						}
						
						return MediaType.JAVASCRIPT_UTF_8;
					} else if (parent.tagName().equals("style")) {
						return MediaType.CSS_UTF_8;
					}
				}
			}
			
			return MediaType.HTML_UTF_8;
		}
		
		@Override
		public MediaType getLanguageAt(int index) {
			Node node = getNodeAt(index);
			return getLanguage(node);
		}

		@Override
		public MediaType getLanguageAtLine(int line) {
			int start = getIndexOfLine(line);
			int length = lines[line].length();
			
			return getLanguageAt(start + length / 2);
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public String[] getLines() {
			return lines;
		}

		@Override
		public int getLineCount() {
			return lines.length;
		}

		@Override
		public List<Marker> getMarkers() {
			return Collections.unmodifiableList(markers);
		}
	}
}
