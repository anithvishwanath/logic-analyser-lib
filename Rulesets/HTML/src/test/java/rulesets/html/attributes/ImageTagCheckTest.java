package rulesets.html.attributes;

import static org.junit.Assert.*;

import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.mockito.Mockito.*;

import logicanalyser.languages.Context;

public class ImageTagCheckTest {
	@Test
	public void rejectsAbsentAltTag() {
		Element element = new Element("img");
		element.attr("src", "img/test.png");
		
		Context context = mock(Context.class);
		
		ImageTagCheck rule = new ImageTagCheck();
		rule.check(context, element);
		
		verify(context).addMarker(element);
	}
	
	@Test
	public void acceptsAltTag() {
		Element element = new Element("img");
		element.attr("src", "img/test.png");
		element.attr("alt", "title");
		
		Context context = mock(Context.class);
		
		ImageTagCheck rule = new ImageTagCheck();
		rule.check(context, element);
		
		verifyZeroInteractions(context);
	}
}
