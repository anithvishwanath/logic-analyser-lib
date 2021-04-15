package logicanalyser.languages;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import com.helger.css.CSSSourceArea;
import com.helger.css.CSSSourceLocation;
import com.helger.css.ECSSVersion;
import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ICSSExpressionMember;
import com.helger.css.reader.CSSReader;

import logicanalyser.Interval;
import logicanalyser.InvalidContentException;
import logicanalyser.LogicAnalyser;
import logicanalyser.Marker;
import logicanalyser.MetricBase;
import logicanalyser.MetricSingle;
import logicanalyser.MetricValue;
import logicanalyser.Report;
import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.config.RuleConfiguration;
import logicanalyser.languages.css.LanguageBreakdown;
import logicanalyser.languages.css.PropertyCount;

public class CSSAnalyser extends LanguageBase {
	private static final Set<Class<?>> ACCEPTED_RULE_TYPES = ImmutableSet.of(
		String.class, 
		CascadingStyleSheet.class, 
		CSSStyleRule.class, 
		CSSDeclaration.class
	);
	
	public CSSAnalyser() {
		registerMetric(new LanguageBreakdown());
		registerMetric(new PropertyCount());
	}
	
	@Override
	public String getName() {
		return "CSS";
	}

	@Override
	public boolean doesAccept(MediaType contentType) {
		return MediaType.CSS_UTF_8.is(contentType);
	}
	
	@Override
	public Iterable<MediaType> getAcceptedTypes() {
		return Arrays.asList(MediaType.CSS_UTF_8);
	}

	@Override
	public Report process(MediaType contentType, String content, Iterable<Ruleset> rulesets, LogicAnalyser analyser)
			throws IllegalArgumentException {
		
		CascadingStyleSheet stylesheet = CSSReader.readFromString(content, ECSSVersion.LATEST);
		
		if (stylesheet == null) {
			throw new InvalidContentException("Cannot interpret as CSS");
		}
		
		List<Marker> markers = generateMarkers(stylesheet, content, rulesets, analyser);
		List<MetricBase> metrics = generateMetrics(stylesheet, content, markers);
		metrics.addAll(generateMarkerMetrics(markers));
		
		return new Report(metrics, markers);
	}
	
	private List<Marker> generateMarkers(CascadingStyleSheet stylesheet, String content, Iterable<Ruleset> rulesets, LogicAnalyser analyser) {
		CSSContext context = new CSSContext(content, analyser.getRuleConfiguration());
		
		Iterable<Rule<CSSStyleRule>> styleRules = Collections.emptyList();
		Iterable<Rule<CSSDeclaration>> propertyRules = Collections.emptyList();
		
		for (Ruleset ruleset : rulesets) {
			for (Rule<String> rule : ruleset.getRulesUsing(String.class)) {
				context.setRule(rule);
				rule.check(context, content);
			}
			
			for (Rule<CascadingStyleSheet> rule : ruleset.getRulesUsing(CascadingStyleSheet.class)) {
				context.setRule(rule);
				rule.check(context, stylesheet);
			}
			
			styleRules = Iterables.concat(styleRules, ruleset.getRulesUsing(CSSStyleRule.class));
			propertyRules = Iterables.concat(propertyRules, ruleset.getRulesUsing(CSSDeclaration.class));
		}
		
		for (CSSStyleRule cssRule : stylesheet.getAllStyleRules()) {
			for (Rule<CSSStyleRule> rule : styleRules) {
				context.setRule(rule);
				rule.check(context, cssRule);
			}
			
			for (CSSDeclaration declaration : cssRule.getAllDeclarations()) {
				for (Rule<CSSDeclaration> rule : propertyRules) {
					context.setRule(rule);
					rule.check(context, declaration);
				}
			}
		}
		
		return context.getMarkers();
	}
	
	private List<MetricBase> generateMetrics(CascadingStyleSheet stylesheet, String content, List<Marker> markers) {
		List<MetricBase> metrics = Lists.newArrayList();
		
		CSSMetricContext context = new CSSMetricContext(content, stylesheet, markers);
		
		metrics.addAll(generateMetrics(context, content));
		metrics.addAll(generateMetrics(context, stylesheet));
		
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

	@Override
	public boolean canRulesAccept(Class<?> type) {
		return ACCEPTED_RULE_TYPES.contains(type);
	}

	@Override
	public Class<?> getContextType() {
		return ICSSSourceLocationAware.class;
	}
	
	private static class CSSContext extends BaseContext {
		private final String content;
		private final String[] lines;
		
		public CSSContext(String content, RuleConfiguration details) {
			super(details);
			
			this.content = content;
			this.lines = content.split("\n");
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
		public Interval locate(Object target) {
			Preconditions.checkArgument(target instanceof ICSSSourceLocationAware, "CSS Analyser only accepts ICSSSourceLocationAware as the context target");
			
			if (target instanceof ICSSSourceLocationAware) {
				CSSSourceLocation source = ((ICSSSourceLocationAware)target).getSourceLocation();
				CSSSourceArea start = source.getFirstTokenArea();
				CSSSourceArea end = source.getLastTokenArea();
				
				int offset = 0;
				// Bugs in the location:
				if (target instanceof CSSDeclaration) {
					offset = -7;
				} else if (target instanceof ICSSExpressionMember) {
					offset = -7;
				}
				
				if (start == null) {
					return new Interval(0, 1);
				}
				
				if (end != null) {
					return new Interval(
						toCharacterPosition(start.getTokenBeginLineNumber(), start.getTokenBeginColumnNumber())+offset,
						toCharacterPosition(end.getTokenEndLineNumber(), end.getTokenEndColumnNumber()+1)+offset
					);
				} else {
					return new Interval(
						toCharacterPosition(start.getTokenBeginLineNumber(), start.getTokenBeginColumnNumber())+offset,
						toCharacterPosition(start.getTokenEndLineNumber(), start.getTokenEndColumnNumber()+1)+offset
					);
				}
			}
			return new Interval(0, 1);
		}
		
		private int toCharacterPosition(int line, int column) {
			int index = 0;
			for (int i = 0; i < line - 1; ++i) {
				index += lines[i].length() + 1; // +1 for newline
			}
			
			index += column - 1;
			return index;
		}
	}
	
	public class CSSMetricContext implements MetricContext {
		private final String content;
		private final String[] lines;
		private final List<Marker> markers;
		
		public CSSMetricContext(String content, CascadingStyleSheet stylesheet, List<Marker> markers) {
			this.content = content;
			this.markers = markers;
			
			lines = content.split("\n");
		}
		
		@Override
		public MediaType getLanguageAt(int index) {
			return MediaType.CSS_UTF_8;
		}

		@Override
		public MediaType getLanguageAtLine(int line) {
			return MediaType.CSS_UTF_8;
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
			return markers;
		}
		
	}
}
