package logicanalyser.config;

import java.net.URL;

import logicanalyser.SeverityRating;

public class RuleDetails extends Details {
	public final String id;
	
	public RuleDetails(String id, String description, SeverityRating severity) {
		super(description, severity, null, null);
		this.id = id;
	}
	
	public RuleDetails(String id, String description, SeverityRating severity, URL resourceLink) {
		super(description, severity, resourceLink, null);
		this.id = id;
	}
	
	public RuleDetails(String id, String description, SeverityRating severity, URL resourceLink, String suggestion) {
		super(description, severity, resourceLink, suggestion);
		this.id = id;
	}
}
