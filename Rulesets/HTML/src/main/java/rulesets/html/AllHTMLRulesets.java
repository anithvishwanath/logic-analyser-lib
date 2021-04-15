package rulesets.html;

import logicanalyser.LogicAnalyser;
import rulesets.html.attributes.AttributesRuleset;
import rulesets.html.doctype.DoctypeRuleSet;
import rulesets.html.placement.PlacementRuleset;
import rulesets.html.style.StyleRuleset;

public class AllHTMLRulesets {
	public static void installInto(LogicAnalyser analyser) {
		analyser.registerRuleset(new AttributesRuleset());
		analyser.registerRuleset(new DoctypeRuleSet());
		analyser.registerRuleset(new StyleRuleset());
		analyser.registerRuleset(new PlacementRuleset());
	}
}
