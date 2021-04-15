package logicanalyser.config;

import java.net.URL;
import java.util.Optional;

import com.google.common.base.Preconditions;

import logicanalyser.SeverityRating;

public class Details {
	public final String description;
	public final SeverityRating severity;
	
	public final Optional<URL> resourceLink;
	public final Optional<String> suggestion;
	
	public Details(String description, SeverityRating severity) {
		this(description, severity, null, null);
	}
	
	public Details(String description, SeverityRating severity, URL resourceLink) {
		this(description, severity, resourceLink, null);
	}
	
	public Details(String description, SeverityRating severity, URL resourceLink, String suggestion) {
		Preconditions.checkNotNull(description);
		Preconditions.checkNotNull(severity);
		
		this.description = description;
		this.severity = severity;
		this.resourceLink = Optional.ofNullable(resourceLink);
		this.suggestion = Optional.ofNullable(suggestion);
	}
}
