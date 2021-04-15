package rulesets.css.selectors;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.ICSSSelectorMember;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Looks for overspecified selectors and selectors that appear to follow the DOM
 */
class OverspecificSelectors extends Rule<CSSStyleRule> {
	// Tweak these values to tune what counts as overspecified
	private static final int SCORE_LIMIT = 10;
	private static final int ID_SCORE = 5;
	private static final int CLASS_SCORE = 3;
	private static final int ELEMENT_SCORE = 5;

	public OverspecificSelectors() {
		super(CSSStyleRule.class, "selectors.overspecified");
	}

	@Override
	public void check(Context context, CSSStyleRule rule) {
		for (CSSSelector selector : rule.getAllSelectors()) {
			
			int score = 0;
			boolean isViolation = false;
			
			for (ICSSSelectorMember member : selector.getAllMembers()) {
				if (member instanceof CSSSelectorSimpleMember) {
					CSSSelectorSimpleMember simpleMember = (CSSSelectorSimpleMember)member;
					if (simpleMember.isHash()) {
						score += ID_SCORE;
					} else if (simpleMember.isClass()) {
						score += CLASS_SCORE;
					} else if (simpleMember.isElementName()) {
						score += ELEMENT_SCORE;
					}
					
					if (score >= SCORE_LIMIT) {
						isViolation = true;
						break;
					}
				}
			}
			
			if (isViolation) {
				context.addMarker(selector);
			}
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"Selectors should be as simple as possible.",
			SeverityRating.Error
		);
	}
}
