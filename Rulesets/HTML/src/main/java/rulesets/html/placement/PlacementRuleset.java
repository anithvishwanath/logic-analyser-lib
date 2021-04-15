package rulesets.html.placement;

import java.util.List;

import com.google.common.collect.ImmutableList;

import logicanalyser.Rule;
import logicanalyser.Ruleset;
import logicanalyser.languages.HTMLAnalyser;
import logicanalyser.languages.LanguageBase;

public class PlacementRuleset implements Ruleset {
	private final List<Rule<?>> rules;
	public PlacementRuleset() {
		rules = ImmutableList.<Rule<?>>builder()
			.add(new BlockElementPlacement())
			.add(new FormTagCheck())
			.build();
	}
	
	@Override
	public String getCategoryName() {
		return "Placement";
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
