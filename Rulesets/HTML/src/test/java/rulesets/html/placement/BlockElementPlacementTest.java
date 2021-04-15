package rulesets.html.placement;

import static org.junit.Assert.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import static org.mockito.Mockito.*;

import logicanalyser.languages.Context;

public class BlockElementPlacementTest {
	@Test
	public void paragraphInsideSpanFails() {
		Element span = new Element("span");
		Element paragraph = new Element("p");
		span.appendChild(paragraph);
		
		BlockElementPlacement rule = new BlockElementPlacement();
		Context context = mock(Context.class);
		
		rule.check(context, paragraph);
		
		verify(context).addMarker(paragraph);
	}
	
	@Test
	public void spanInsideParagraphSucceeds() {
		Element span = new Element("span");
		Element paragraph = new Element("p");
		paragraph.appendChild(span);
		
		BlockElementPlacement rule = new BlockElementPlacement();
		Context context = mock(Context.class);
		
		rule.check(context, span);
		
		verifyZeroInteractions(context);
	}
	
	@Test
	public void htmlTagPasses() {
		// The document is not considered a block element, but is still an element.
		// Dont want the <html> tag to be reported as an error
		Document document = new Document("/");
		Element html = new Element("html");
		document.appendChild(html);
		
		BlockElementPlacement rule = new BlockElementPlacement();
		Context context = mock(Context.class);
		
		rule.check(context, html);
		
		verifyZeroInteractions(context);
	}
}
