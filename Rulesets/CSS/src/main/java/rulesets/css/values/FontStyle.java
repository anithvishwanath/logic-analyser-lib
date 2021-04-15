package rulesets.css.values;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.shorthand.CSSShortHandRegistry;
import com.helger.css.property.ECSSProperty;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Checks if a font-family or font declaration has fallback fonts
 */
class FontStyle extends Rule<CSSStyleRule> {
	private static final Set<String> GENERIC_FONTS = ImmutableSet.of(
		"serif",
		"sans-serif",
		"cursive",
		"fantasy",
		"monospace",
		"inherit",
		"initial"
	);
	
	public FontStyle() {
		super(CSSStyleRule.class, "selectors.font-fallback");
	}

	@Override
	public void check(Context context, CSSStyleRule rule) {
		boolean hasFont = false;
		CSSDeclaration first = null;
		for (CSSDeclaration declaration : rule.getAllDeclarations()) {
			if (declaration.getProperty().equals("font-family")) {
				if (hasFont) {
					return;
				}
				
				hasFont = true;
				first = declaration;
				if (hasFallbacks(declaration)) {
					return;
				}
			} else if (declaration.getProperty().equals("font")) {
				// FIXME: There is a bug in the split into pieces method. The fallback fonts do not show up in the resultant list.
//				ICommonsList<CSSDeclaration> parts = CSSShortHandRegistry
//					.getShortHandDescriptor(ECSSProperty.FONT)
//					.getSplitIntoPieces(declaration);
//				
//				for (CSSDeclaration subDeclaration : parts) {
//					if (subDeclaration.getProperty().equals("font-family")) {
//						if (hasFont) {
//							return;
//						}
//
//						hasFont = true;
//						first = declaration;
//						if (hasFallbacks(subDeclaration)) {
//							return;
//						}
//					}
//				}
			}
		}
		
		if (hasFont) {
			context.addMarker(first);	
		}
	}
	
	private boolean hasFallbacks(CSSDeclaration declaration) {
		ICommonsList<CSSExpressionMemberTermSimple> members;
		
		members = declaration.getExpression()
			.getAllSimpleMembers();
		
		//check to see if there is one member 
		if (members.size() == 1) {
			CSSExpressionMemberTermSimple member = members.get(0);
			if (!GENERIC_FONTS.contains(member.getValue())) {
				return false;
			}
		} else if (members.isEmpty()) {
			return false;
		}
		
		return true;
	}

	@Override
	public Details defaultDetails() {
		return new Details(
			"Must provide fallback font to let the browser pick a similar font if the desired font is not available",
			SeverityRating.Error
		);
	}
}
