package rulesets.css.values;

import java.util.List;

import com.google.common.collect.ImmutableList;

import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.languages.CSSAnalyser;
import logicanalyser.languages.LanguageBase;

public class ValuesRuleset implements Ruleset {
	private final List<Rule<?>> rules;
	
	public ValuesRuleset() {
		rules = ImmutableList.<Rule<?>>builder()
			.add(new FontStyle())
			.add(new DuplicateRules())
			.add(new ExpandedProperties())
			.add(new UnitValueRule())
			.build();
	}
	
	@Override
	public String getCategoryName() {
		return "Values";
	}

	@Override
	public Class<? extends LanguageBase> getLanguage() {
		return CSSAnalyser.class;
	}

	@Override
	public List<Rule<?>> getContainedRules() {
		return rules;
	}
}
