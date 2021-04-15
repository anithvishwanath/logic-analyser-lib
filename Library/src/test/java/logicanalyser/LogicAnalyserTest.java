package logicanalyser;

import com.google.common.net.MediaType;
import logicanalyser.languages.LanguageBase;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.hasItems;

@RunWith(Enclosed.class)
public class LogicAnalyserTest {
	public static class Registrations {
		private LogicAnalyser analyser;
		
		@Before
		public void setup() {
			analyser = new LogicAnalyser();
		}
		
		@Test
		public void registerRuleset() {
			Ruleset ruleset = mock(Ruleset.class);
			LanguageBase language = mock(LanguageBase.class);
			
			// Answer is required due to generic issues with thenReturn
			when(ruleset.getLanguage()).then(invocation -> language.getClass());
			when(language.getName()).thenReturn("Test");
			
			analyser.registerLanguageProcessor(language);
			analyser.registerRuleset(ruleset);
		}
		
		@Test(expected=IllegalStateException.class)
		public void registerRulesetWithoutLanguage() {
			Ruleset ruleset = mock(Ruleset.class);
			// Answer is required due to generic issues with thenReturn
			when(ruleset.getLanguage()).then(invocation -> LanguageBase.class);
			
			analyser.registerRuleset(ruleset);
		}
		
		@Test(expected=IllegalArgumentException.class)
		public void registerDuplicateLanguage() {
			LanguageBase language = mock(LanguageBase.class);
			
			when(language.getName()).thenReturn("Test");
			
			analyser.registerLanguageProcessor(language);
			analyser.registerLanguageProcessor(language);
		}
		
		@Test
		public void deregisterLanguage() {
			LanguageBase language = mock(LanguageBase.class);
			
			when(language.getName()).thenReturn("Test");
			
			analyser.registerLanguageProcessor(language);
			analyser.deregisterLanguageProcessor(language);
		}
	}
	
	public static class Execution {
		private LogicAnalyser analyser;
		private LanguageBase jsLanguage;
		private LanguageBase jsonLanguage;
			
		@Before
		public void setup() {
			analyser = new LogicAnalyser();
			
			jsLanguage = mock(JSLang.class);
			when(jsLanguage.doesAccept(any(MediaType.class))).thenReturn(false);
			when(jsLanguage.doesAccept(MediaType.JAVASCRIPT_UTF_8)).thenReturn(true);
			when(jsLanguage.getName()).thenReturn("JS");
			
			when(jsLanguage.canRulesAccept(any())).thenReturn(false);
			when(jsLanguage.canRulesAccept(String.class)).thenReturn(true);
			
			jsonLanguage = mock(JSONLang.class);
			when(jsonLanguage.doesAccept(any(MediaType.class))).thenReturn(false);
			when(jsonLanguage.doesAccept(MediaType.JSON_UTF_8)).thenReturn(true);
			
			when(jsonLanguage.canRulesAccept(any())).thenReturn(false);
			when(jsonLanguage.canRulesAccept(String.class)).thenReturn(true);
			when(jsonLanguage.getName()).thenReturn("JSON");
			
			analyser.registerLanguageProcessor(jsLanguage);
			analyser.registerLanguageProcessor(jsonLanguage);
		}
		
		@Test
		public void languageDelegationToHTML() {
			analyser.analyseContent(MediaType.JAVASCRIPT_UTF_8, "content");
			verify(jsLanguage, times(1)).process(eq(MediaType.JAVASCRIPT_UTF_8), eq("content"), any(), eq(analyser));
			verify(jsonLanguage, never()).process(any(), any(), any(), any());
		}
		
		@Test
		public void languageDelegationToCSS() {
			analyser.analyseContent(MediaType.JSON_UTF_8, "content");
			verify(jsonLanguage, times(1)).process(eq(MediaType.JSON_UTF_8), eq("content"), any(), eq(analyser));
			verify(jsLanguage, never()).process(any(), any(), any(), any());
		}
		
		@Test
		public void mediaTypeSupport() {
			assertTrue(analyser.canAnalyseContent(MediaType.JSON_UTF_8));
			assertTrue(analyser.canAnalyseContent(MediaType.JAVASCRIPT_UTF_8));
			
			assertFalse(analyser.canAnalyseContent(MediaType.CSV_UTF_8));
			assertFalse(analyser.canAnalyseContent(MediaType.PNG));
		}
		
		@Test
		public void languageSupport() {
			assertEquals(jsLanguage, analyser.getLanguageFor(MediaType.JAVASCRIPT_UTF_8));
			assertEquals(jsonLanguage, analyser.getLanguageFor(MediaType.JSON_UTF_8));
		}
		
		@Test
		public void rulesetPassthrough() {
			Ruleset ruleset1 = mock(Ruleset.class);
			
			// Answer is required due to generic issues with thenReturn
			when(ruleset1.getLanguage()).then(invocation -> jsLanguage.getClass());
			
			analyser.registerRuleset(ruleset1);
			
			analyser.analyseContent(MediaType.JAVASCRIPT_UTF_8, "content");
			
			verify(jsLanguage).process(eq(MediaType.JAVASCRIPT_UTF_8), eq("content"), argThat(hasItems(ruleset1)), eq(analyser));
		}
		
		private static abstract class JSLang extends LanguageBase {}
		private static abstract class JSONLang extends LanguageBase {}
	}
}
