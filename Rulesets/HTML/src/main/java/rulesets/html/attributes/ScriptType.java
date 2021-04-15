package rulesets.html.attributes;

import org.jsoup.nodes.Element;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Checks if a script tag has the type attribute.
 * The tag is only needed if the script is not
 * JavaScript.
 */
class ScriptType extends Rule<Element> {
	public ScriptType() {
		super(Element.class, "attributes.script-type");
	}

	@Override
	public void check(Context context, Element target) {
		if (target.tagName().equals("script")) {
			if (!target.hasAttr("type")) {
				context.addMarker(target);
			}
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"If not using javascript, make sure you add the type. e.g. application/ecmascript",
			SeverityRating.Informational
		);
	}
}
