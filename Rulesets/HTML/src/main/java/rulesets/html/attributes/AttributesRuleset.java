package rulesets.html.attributes;

import java.util.List;

import com.google.common.collect.ImmutableList;

import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.languages.HTMLAnalyser;
import logicanalyser.languages.LanguageBase;

public class AttributesRuleset implements Ruleset {
	private final List<Rule<?>> rules;
	
	public AttributesRuleset() {
		rules = ImmutableList.<Rule<?>>builder()
			.add(new ImageTagCheck())
			.add(new ScriptType())
			.build();
	}
	@Override
	public String getCategoryName() {
		return "Attributes";
	}

	@Override
	public Class<? extends LanguageBase> getLanguage() {
		return HTMLAnalyser.class;
	}

	@Override
	public List<Rule<?>> getContainedRules() {
		return rules;
	}
}
