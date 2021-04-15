package rulesets.html.style;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Checks if a paragraph is using multiple sequential
 * line break tags to provide extra spacing. This behaviour
 * suggests that they want to display a new paragraph, so they
 * should just end the paragraph tag and start a new one.
 */
class LineBreakCheck extends Rule<Element>{

	public LineBreakCheck() {
		super(Element.class, "linebreak");
	}

	@Override
	public void check(Context context, Element target) {
		if (!target.tagName().equals("p")) {
			return;
		}
		
		boolean wasLineBreak = false;
		for (Node child : target.childNodes()) {
			// Text is allowed between the line breaks
			if (child instanceof TextNode) {
				TextNode text = (TextNode)child;
				if (!text.isBlank()) {
					wasLineBreak = false;
				}
			}
			
			if (child instanceof Element) {
				Element element = (Element)child;
				
				if (element.tagName().equals("br")) {
					if (wasLineBreak) {
						// There have been 2 in a row
						context.addMarker(element);
						break; // Dont trigger if there are even more
					} else {
						wasLineBreak = true;
					}
				} else {
					wasLineBreak = false;
				}
			}
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"Do not use multiple sequential line break tags",
			SeverityRating.Error
		);
	}

}

