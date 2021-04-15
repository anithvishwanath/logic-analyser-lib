package rulesets.html.doctype;

import java.util.List;

import com.google.common.collect.ImmutableList;

import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.languages.HTMLAnalyser;
import logicanalyser.languages.LanguageBase;

public class DoctypeRuleSet implements Ruleset{
	private final List<Rule<?>> rules;
	public DoctypeRuleSet() {
		rules = ImmutableList.<Rule<?>>builder()
			.add(new DoctypePresent())
			.build();
	}
	public String getCategoryName() {
		return "Doctype";
	}

	public Class<? extends LanguageBase> getLanguage() {
		return HTMLAnalyser.class;
	}

	public List<Rule<?>> getContainedRules() {
		return rules;
	}
}
