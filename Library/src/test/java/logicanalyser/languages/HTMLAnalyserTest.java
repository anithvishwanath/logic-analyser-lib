package logicanalyser.languages;

import com.google.common.net.MediaType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import logicanalyser.Interval;
import logicanalyser.LogicAnalyser;
import logicanalyser.Marker;
import logicanalyser.Report;
import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.config.RuleConfiguration;

import org.hamcrest.CoreMatchers;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class HTMLAnalyserTest {
	public static class Types {
		private HTMLAnalyser analyser;

		@Before
		public void setup() {
			analyser = new HTMLAnalyser();
		}

		@Test
		public void ensureCorrectTypes() {
			assertTrue(analyser.doesAccept(MediaType.HTML_UTF_8));
			assertFalse(analyser.doesAccept(MediaType.CSS_UTF_8));

			assertTrue(analyser.canRulesAccept(Element.class));
			assertTrue(analyser.canRulesAccept(Document.class));
			assertTrue(analyser.canRulesAccept(String.class));
			assertFalse(analyser.canRulesAccept(Integer.class));

			assertEquals(Element.class, analyser.getContextType());
		}
	}
	
	public static class Processing {
		private HTMLAnalyser analyser;

		@Before
		public void setup() {
			analyser = new HTMLAnalyser();
		}
		
		@Test
		public void ensureRulesAreInvokedCorrectly() {
			Rule<Element> elementRule = mock(Rule.class);
			Rule<Document> documentRule = mock(Rule.class);
			Rule<String> stringRule = mock(Rule.class);
			
			when(elementRule.getTarget()).thenReturn(Element.class);
			when(documentRule.getTarget()).thenReturn(Document.class);
			when(stringRule.getTarget()).thenReturn(String.class);

			LogicAnalyser logicAnalyser = mock(LogicAnalyser.class);
			when(logicAnalyser.getRuleConfiguration())
				.thenReturn(RuleConfiguration.DEFAULT);

			Ruleset ruleset = new TestRuleset() {
				@Override
				public List<Rule<?>> getContainedRules() {
					return Arrays.asList(
						elementRule,
						documentRule,
						stringRule
					);
				}
			};
			
			String rawDocument = "<html><head></head><body></body></html>";

			analyser.process(MediaType.HTML_UTF_8, rawDocument, Arrays.asList(ruleset), logicAnalyser);

			verify(elementRule, atLeastOnce()).check(any(), any());
			verify(documentRule, times(1)).check(any(), any());
			verify(stringRule, times(1)).check(any(), eq(rawDocument));	
		}
		
		@Test
		public void markersAreReturnedInReport() {
			LogicAnalyser logicAnalyser = mock(LogicAnalyser.class);
			when(logicAnalyser.getRuleConfiguration())
				.thenReturn(RuleConfiguration.DEFAULT);
			
			Marker stringMarker = mock(Marker.class);
			when(stringMarker.getSeverity())
				.thenReturn(SeverityRating.Error);
			when(stringMarker.getLocation())
				.thenReturn(new Interval(0,1));
			Marker elementMarker = mock(Marker.class);
			when(elementMarker.getSeverity())
				.thenReturn(SeverityRating.Error);
			when(elementMarker.getLocation())
				.thenReturn(new Interval(0,1));
			Marker documentMarker = mock(Marker.class);
			when(documentMarker.getSeverity())
				.thenReturn(SeverityRating.Error);
			when(documentMarker.getLocation())
				.thenReturn(new Interval(0,1));
			
			Rule<Element> elementRule = new Rule<Element>(Element.class, "1") {
				@Override
				public void check(Context context, Element target) {
					context.addMarker(elementMarker);
				}
				
				@Override
				public Details defaultDetails() {
					return new Details("", SeverityRating.Error);
				}
			};
			Rule<Document> documentRule = new Rule<Document>(Document.class, "1") {
				@Override
				public void check(Context context, Document target) {
					context.addMarker(documentMarker);
				}
				
				@Override
				public Details defaultDetails() {
					return new Details("", SeverityRating.Error);
				}
			};
			Rule<String> stringRule = new Rule<String>(String.class, "1") {
				@Override
				public void check(Context context, String target) {
					context.addMarker(stringMarker);
				}
				
				@Override
				public Details defaultDetails() {
					return new Details("", SeverityRating.Error);
				}
			};
			
			Ruleset ruleset = new TestRuleset() {
				@Override
				public List<Rule<?>> getContainedRules() {
					return Arrays.asList(
						elementRule,
						documentRule,
						stringRule
					);
				}
			};
			
			String rawDocument = "<html><head></head><body></body></html>";

			Report report = analyser.process(MediaType.HTML_UTF_8, rawDocument, Arrays.asList(ruleset), logicAnalyser);

			assertThat(report.getAllMarkers(), CoreMatchers.hasItems(
				elementMarker,
				documentMarker, 
				stringMarker
			));
		}
		
		// Mockito cannot handle defendor methods, so this is needed
		private static abstract class TestRuleset implements Ruleset {
			@Override
			public String getCategoryName() {
				return "Test";
			}

			@Override
			public Class<? extends LanguageBase> getLanguage() {
				return HTMLAnalyser.class;
			}
		}
	}
}
