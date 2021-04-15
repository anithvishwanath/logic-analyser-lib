package logicanalyser.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import logicanalyser.SeverityRating;

public class XMLConfigLoader {
	private final InputStream stream;
	private final boolean closeAfterwards;
	
	public XMLConfigLoader(File file) throws FileNotFoundException {
		stream = new FileInputStream(file);
		closeAfterwards = true;
	}
	
	public XMLConfigLoader(InputStream stream) {
		this.stream = stream;
		closeAfterwards = false;
	}
	
	private Schema loadSchema() {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			return factory.newSchema(XMLConfigLoader.class.getResource("/configuration.xsd"));
		} catch (SAXException e) {
			// Schema should be present and valid always
			throw new AssertionError(e);
		}
	}
	
	public RuleConfiguration load() throws IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(true);
		factory.setSchema(loadSchema());
		factory.setIgnoringElementContentWhitespace(true);
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(stream);
			
			RuleConfiguration config = new RuleConfiguration();
			
			NodeList rules = document.getElementsByTagName("rule");
			for (int i = 0; i < rules.getLength(); ++i) {
				config.add(parseRule((Element)rules.item(i)));
			}
			
			return config;
		} catch (ParserConfigurationException e) {
			throw new AssertionError(e);
		} finally {
			if (closeAfterwards) {
				stream.close();
			}
		}
	}
	
	private RuleDetails parseRule(Element ruleElement) {
		String id = ruleElement.getElementsByTagName("id").item(0).getTextContent();
		String description = ruleElement.getElementsByTagName("description").item(0).getTextContent();
		String severityString = ruleElement.getElementsByTagName("severity").item(0).getTextContent();
		
		SeverityRating rating;
		switch (severityString.toLowerCase()) {
		case "info":
			rating = SeverityRating.Informational;
			break;
		case "warning":
			rating = SeverityRating.Warning;
			break;
		default:
		case "error":
			rating = SeverityRating.Error;
			break;
		case "critical":
			rating = SeverityRating.CriticalError;
			break;
		}
		
		NodeList urlNode = ruleElement.getElementsByTagName("url");
		NodeList suggestionNode = ruleElement.getElementsByTagName("suggestion");
		
		URL url = null;
		String suggestion = null;
		
		if (urlNode.getLength() > 0) {
			try {
				url = new URI(urlNode.item(0).getTextContent()).toURL();
			} catch (MalformedURLException e) {
				// Schema validation should have taken care of this
				throw new AssertionError(e);
			} catch (URISyntaxException e) {
				// Schema validation should have taken care of this
				throw new AssertionError(e);
			}
		}
		
		if (suggestionNode.getLength() > 0) {
			suggestion = suggestionNode.item(0).getTextContent();
		}
		
		return new RuleDetails(id, description, rating, url, suggestion);
	}
}
