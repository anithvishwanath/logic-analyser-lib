package rulesets.html.doctype;

import java.util.List;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.languages.Context;

/**
 * Checks if the {@code <!DOCTYPE>} tag is present at the
 * begining of the document
 */
class DoctypePresent extends Rule<Document> {

	public DoctypePresent() {
		super(Document.class, "doctype");
	}

	@Override
	public void check(Context context, Document target) {
		List<Node> nodes = target.childNodes();

		for (int i = 0; i < nodes.size(); i++) {
			Node docTag1 = nodes.get(i);
			if (docTag1 instanceof Comment) {
				continue;
			} else if (docTag1 instanceof Element) {
				context.addMarker(docTag1);
				return;
			} else if (docTag1 instanceof TextNode) {
				if (((TextNode) docTag1).isBlank()) {
					continue;
				} else {
					context.addMarker(docTag1);
					return;
				}
			} else if (docTag1 instanceof DocumentType) {
				break;
			}
		}
	}
	
	@Override
	public Details defaultDetails() {
		return new Details(
			"The Doctype tag must be present and be the first element",
			SeverityRating.CriticalError
		);
	}
}
