package rulesets.css.values;

import java.util.List;
import java.util.Set;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.shorthand.CSSPropertyWithDefaultValue;
import com.helger.css.decl.shorthand.CSSShortHandRegistry;
import com.helger.css.property.ECSSProperty;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Detects the use of properties that have fully expanded a shorthand property.
 * For example:
 * Using padding-left: 0; padding-right: 0; padding-top: 0; padding-bottom: 0
 * Can be collapsed to:
 * padding: 0;
 */
class ExpandedProperties extends Rule<CSSStyleRule> {
	private static final Set<ECSSProperty> SHORTHAND_PROPERTIES = 
		CSSShortHandRegistry.getAllShortHandProperties();
	
	public ExpandedProperties() {
		super(CSSStyleRule.class, "values.expanded-properties");
	}
	
	@Override
	public void check(Context context, CSSStyleRule rule) {
		for (ECSSProperty shorthand : SHORTHAND_PROPERTIES) {
			if (hasProperty(rule, shorthand)) {
				continue;
			}
			
			List<CSSPropertyWithDefaultValue> subProperties;
			subProperties = CSSShortHandRegistry
				.getShortHandDescriptor(shorthand)
				.getAllSubProperties();

			CSSDeclaration first = null;
			boolean hasAll = true;
			for (CSSPropertyWithDefaultValue subProperty : subProperties) {
				if (!hasProperty(rule, subProperty.getProperty().getProp())) {
					hasAll = false;
					break;
				}
				
				if (first == null) {
					first = rule.getDeclarationOfPropertyName(subProperty.getProperty().getPropertyName());
				}
			}
			
			if (hasAll) {
				context.addMarker(first);
			}
		}
	}
	
	private boolean hasProperty(CSSStyleRule rule, ECSSProperty property) {
		return rule.getDeclarationOfPropertyName(property.getName()) != null;
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"All sub properties have been expanded, these can be collapsed",
			SeverityRating.Informational
		);
	}
}
