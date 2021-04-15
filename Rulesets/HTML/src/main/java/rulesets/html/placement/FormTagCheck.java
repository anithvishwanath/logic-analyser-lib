package rulesets.html.placement;

import org.jsoup.nodes.Element;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

class FormTagCheck extends Rule<Element> {

	public FormTagCheck() {
		super(Element.class, "placement.form");
	}

	@Override
	public void check(Context context, Element target) {
		if (target.tagName().equals("table")) {
			Element e = target.select("table").first().children().first();

			if (e.tagName().equals("form")) {
				context.addMarker(target);
			}
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"Form tags must not be placed directly inside a table element",
			SeverityRating.CriticalError
		);
	}
}
