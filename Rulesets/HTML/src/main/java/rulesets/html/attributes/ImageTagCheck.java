package rulesets.html.attributes;

import org.jsoup.nodes.Element;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Ensures that image tags have alt attributes for when
 * images do not load.
 */
class ImageTagCheck extends Rule<Element> {

	public ImageTagCheck() {
		super(Element.class, "attributes.img-alt");
	}

	@Override
	public void check(Context context, Element target) {
		if (target.tagName().equals("img")) {
			if (!target.hasAttr("alt")) {
				context.addMarker(target);
			}
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"Image tags should provide alternate text",
			SeverityRating.Error
		);
	}
}
