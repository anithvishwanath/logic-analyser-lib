package rulesets.html.style;

import java.util.List;

import com.google.common.collect.ImmutableList;

import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.languages.HTMLAnalyser;
import logicanalyser.languages.LanguageBase;

public class StyleRuleset implements Ruleset {
	private final List<Rule<?>> rules;
	
	public StyleRuleset() {
		rules = ImmutableList.<Rule<?>>builder()
			.add(new TextStyleCheck())
			.add(new StyleInlineCheck())
			.add(new LineBreakCheck())
			.build();
	}
	
	public String getCategoryName() {
		return "Style";
	}

	public Class<? extends LanguageBase> getLanguage() {
		return HTMLAnalyser.class;
	}

	public List<Rule<?>> getContainedRules() {
		return rules;
	}
}
