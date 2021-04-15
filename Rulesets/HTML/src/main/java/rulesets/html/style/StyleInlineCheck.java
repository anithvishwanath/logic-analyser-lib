package rulesets.html.style;

import org.jsoup.nodes.Element;
import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Checks if inline styles are used. Inline styles have the highest priority
 * and will override normal css. The should be used sparingly
 */
class StyleInlineCheck extends Rule<Element> {

	public StyleInlineCheck() {
		super(Element.class, "style.inline-style");
	}

	@Override
	public void check(Context context, Element target) {
		if(target.hasAttr("style"))	{
			context.addMarker(target);
		}
	}

	@Override
	public Details defaultDetails() {
		return new Details(
			"Inline style is not considered to be a good practise, define style and reuse it",
			SeverityRating.Warning
		);
	}
}
