package rulesets.html.placement;

import org.jsoup.nodes.Element;

import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * A Rule that checks that block elements are not placed within inline elements
 */
class BlockElementPlacement extends Rule<Element> {
	private static final String DESCRIPTION_ID = "placement.block-inside-inline";
	
	public BlockElementPlacement() {
		super(Element.class, DESCRIPTION_ID);
	}

	@Override
	public void check(Context context, Element target) {
		// HTML Tags are within the document 'element' which is 'inline'
		if (target.tag().isInline() || target.tagName().equalsIgnoreCase("html")) {
			return;
		}
		
		if (target.parent().tag().isInline()) {
			context.addMarker(target);
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"Block elements should not be within inline elements",
			SeverityRating.CriticalError
		);
	}
}
