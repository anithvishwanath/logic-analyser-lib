package rulesets.css.values;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSStyleRule;
import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;
/**
 * Checks for numerical property values and in specific for values starting with 0
 * And checks if it followed by px or em  
 */
public class UnitValueRule extends Rule<CSSStyleRule> {

	public UnitValueRule() {
		super(CSSStyleRule.class, "values.Unit-Value");

	}

	@Override
	public void check(Context context, CSSStyleRule rule) {

		String regEx = "0[a-zA-Z]+";
		for (CSSDeclaration decleration : rule.getAllDeclarations()) {
			ICommonsList<CSSExpressionMemberTermSimple> memberList ;
			memberList= decleration.getExpression()
					.getAllSimpleMembers();
			for (CSSExpressionMemberTermSimple memberValue : memberList) {
				if (memberValue instanceof CSSExpressionMemberTermSimple) {
					if (memberValue.getValue().matches(regEx)) {
						context.addMarker(decleration);
					}
				}
			}
		}
	}

	@Override
	public Details defaultDetails() {
		return new Details(
				"Units for property vlaues with 0 must only include numbers", 
				SeverityRating.Error);
	}

}
