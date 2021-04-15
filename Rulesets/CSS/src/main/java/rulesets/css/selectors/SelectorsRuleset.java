package rulesets.css.selectors;

import java.util.List;

import com.google.common.collect.ImmutableList;

import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.languages.CSSAnalyser;
import logicanalyser.languages.LanguageBase;

public class SelectorsRuleset implements Ruleset {
	private final List<Rule<?>> rules;
	
	public SelectorsRuleset() {
		rules = ImmutableList.<Rule<?>>builder()
			.add(new OverspecificSelectors())
			.add(new IDSelectors())
			.build();
	}
	
	@Override
	public String getCategoryName() {
		return "Selectors";
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
