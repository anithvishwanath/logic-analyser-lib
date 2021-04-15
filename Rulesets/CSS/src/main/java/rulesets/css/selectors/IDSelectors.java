package rulesets.css.selectors;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.ICSSSelectorMember;
import logicanalyser.languages.Context;
import logicanalyser.config.Details;
import logicanalyser.SeverityRating;
import logicanalyser.Rule;

/**
 * Suggests that they dont use IDs as selectors as they have a higher priority.
 */
class IDSelectors extends Rule<CSSStyleRule> {

	public IDSelectors() {
		super(CSSStyleRule.class, "selectors.id");
	}

	@Override
	public void check(Context context, CSSStyleRule rule) {
		boolean found = false;
		for (CSSSelector selector : rule.getAllSelectors()) {
			for (ICSSSelectorMember member : selector.getAllMembers()) {
				if (member instanceof CSSSelectorSimpleMember) {
					CSSSelectorSimpleMember simpleMember = (CSSSelectorSimpleMember)member;

					if (simpleMember.isHash()) {
						context.addMarker(simpleMember);
						found = true;
						break;
					}
				}
			}
			
			if (found) {
				break;
			}
		}
	}

	@Override
	public Details defaultDetails() {
		return new Details(
			"ID have higher priority over other selectors and cannot be undone, it is more preferrable to use .class selector",
			SeverityRating.Warning
		);
	}
}


