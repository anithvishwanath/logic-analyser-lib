package rulesets.css;

import logicanalyser.LogicAnalyser;
import rulesets.css.selectors.SelectorsRuleset;
import rulesets.css.values.ValuesRuleset;


public class AllCSSRulesets {
	public static void installInto(LogicAnalyser analyser) {
		analyser.registerRuleset(new SelectorsRuleset());
		analyser.registerRuleset(new ValuesRuleset());
	}
}
