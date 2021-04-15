package rulesets.css.values;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Checks for duplicate CSS rules. Duplicate CSS rules can be combined into
 * a single rule with multiple selectors.
 */
class DuplicateRules extends Rule<CascadingStyleSheet> {
	public DuplicateRules() {
		super(CascadingStyleSheet.class, "values.duplicate-rules");
	}
	
	@Override
	public void check(Context context, CascadingStyleSheet target) {
		for (CSSStyleRule primary : target.getAllStyleRules()) {
			for (CSSStyleRule secondary : target.getAllStyleRules()) {
				if (primary == secondary) {
					continue;
				}
				
				if (areContentsEqual(primary, secondary)) {
					context.addMarker(primary);
				}
			}
		}
	}
	
	private boolean areContentsEqual(CSSStyleRule rule1, CSSStyleRule rule2) {
		if (rule1.getDeclarationCount() != rule2.getDeclarationCount()) {
			return false;
		}
		
		// Equals ignoring order
		for (CSSDeclaration declaration : rule1.getAllDeclarations()) {
			if (!rule2.getAllDeclarations().contains(declaration)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"Multiple rules contain the same declarations.",
			SeverityRating.Warning
		);
	}
}
