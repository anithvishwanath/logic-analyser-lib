package rulesets.html.style;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Suggests the use of {@code <em>} and {@code <strong>} instead
 * of {@code <i>} and {@code <b>}
 */
class TextStyleCheck extends Rule<Element> {

	public TextStyleCheck() {
		super(Element.class, "style.format-tags");
	}

	@Override
	public void check(Context context, Element target) {
		if (target.tagName().equals("head")) {
			Elements test = target.select("head").first().children();
			for (Element e : test) {
				if (e.tagName().equals("b") || e.tagName().equals("i")) {
					// TODO: This needs to be elsewhere
					context.addMarker(target, "placement.head-bad-tags");
				}
			}

		} else if (target.tagName().equals("body")) {
			Elements test1 = target.select("body").first().children();
			for (Element e : test1) {
				if (e.tagName().equals("b") || e.tagName().equals("i")) {
					context.addMarker(target);
				}
			}

		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"<strong> and <em> should be used instead of <b> and <i>",
			SeverityRating.Warning
		);
	}
}
