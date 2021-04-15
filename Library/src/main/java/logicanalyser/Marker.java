package logicanalyser;

import com.google.common.base.Preconditions;

import logicanalyser.config.Details;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * A Marker represents some piece of information that is attached has a location
 * attached to it. This information is usually error messages, but can be
 * informational text too.
 */
public class Marker implements Serializable, Comparable<Marker> {
	private static final long serialVersionUID = 1L;

	private final Interval location;
	private final SeverityRating severity;
	private final String description;

	// Optional elements
	private String locationSpecifier;
	private URL moreInformationLink;
	private String suggestedFix;

	/**
	 * Creates a new Marker
	 * @param location The location of the marker
	 * @param severity The severity rating for the marker
	 * @param description The information this marker contains
	 */
	public Marker(Interval location, SeverityRating severity, String description) {
		this(location, severity, description, null, null, null);
	}
	
	/**
	 * Creates a new Marker
	 * @param location The location of the marker
	 * @param severity The severity rating for the marker
	 * @param description The information this marker contains
	 * @param locationSpecifier Some information that can help the user understand where the problem was.
	 * 		This might be some of the surrounding tags or code.
	 */
	public Marker(Interval location, SeverityRating severity, String description, String locationSpecifier) {
		this(location, severity, description, locationSpecifier, null, null);
	}

	/**
	 * Creates a new Marker with the addition information
	 * @param location The location of the marker
	 * @param severity The severity rating for the marker
	 * @param description The information this marker contains
	 * @param locationSpecifier Some information that can help the user understand where the problem was.
	 * 		This might be some of the surrounding tags or code.
	 * @param moreInfoLink A URL where more information can be found, optional
	 * @param suggestion A suggestion that can be shown to the user, optional
	 */
	public Marker(Interval location, SeverityRating severity, String description, String locationSpecifier, URL moreInfoLink, String suggestion) {
		Preconditions.checkNotNull(location);
		Preconditions.checkNotNull(severity);
		Preconditions.checkNotNull(description);

		this.location = location;
		this.severity = severity;
		this.description = description;
		this.locationSpecifier = locationSpecifier;
		this.moreInformationLink = moreInfoLink;
		this.suggestedFix = suggestion;
	}
	
	/**
	 * Creates a new marker with the given location and information
	 * @param location The location of the marker
	 * @param details The details object that holds the marker info
	 * @param locationSpecifier Additional optional text that helps locate the marker
	 */
	public Marker(Interval location, Details details, String locationSpecifier) {
		Preconditions.checkNotNull(location);
		Preconditions.checkNotNull(details);
		
		this.location = location;
		this.severity = details.severity;
		this.description = details.description;
		this.moreInformationLink = details.resourceLink.orElse(null);
		this.suggestedFix = details.suggestion.orElse(null);
		this.locationSpecifier = locationSpecifier;
	}

	/**
	 * Gets the location of this Marker
	 * @return An Interval
	 */
	public Interval getLocation() {
		return location;
	}

	/**
	 * Gets the severity rating of this Marker
	 * @return The rating
	 */
	public SeverityRating getSeverity() {
		return severity;
	}

	/**
	 * Gets the information contained in the Marker
	 * @return The information
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the optional text that provides a hint at the location of the error
	 * @return An Optional that might or might not contain the text
	 */
	public Optional<String> getLocationSpecifier() {
		return Optional.ofNullable(locationSpecifier);
	}
	
	/**
	 * Adds some additional information to help locate where the error was.
	 * This could be some of the surrounding code or tags where the
	 * problem occurred.
	 * @param locationSpecifier The additional information
	 */
	public void setLocationSpeicifer(String locationSpecifier) {
		this.locationSpecifier = locationSpecifier;
	}

	/**
	 * Gets the optional URL that links to information related to this Marker.
	 * @return An Optional that might or might not contain the URL
	 */
	public Optional<URL> getMoreInformationLink() {
		return Optional.ofNullable(moreInformationLink);
	}

	/**
	 * Sets the URL to provide links to more information about the Marker
	 * @param url The new URL to set
	 */
	public void setMoreInformationLink(URL url) {
		this.moreInformationLink = url;
	}

	/**
	 * Gets the optional suggestion for this Marker
	 * @return An Optional that might or might not contain the suggestion
	 */
	public Optional<String> getSuggestedFix() {
		return Optional.ofNullable(suggestedFix);
	}

	/**
	 * Sets the suggestion for this Marker
	 * @param fix The suggestion
	 */
	public void setSuggestedFix(String fix) {
		this.suggestedFix = fix;
	}

	@Override
	public String toString() {
		return String.format("%s Marker at %s", severity.name(), location);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 61 * hash + location.hashCode();
		hash = 61 * hash + severity.hashCode();
		hash = 61 * hash + description.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Marker) {
			Marker other = (Marker) obj;

			if (!location.equals(other.location)) {
				return false;
			}
			if (severity != other.severity) {
				return false;
			}
			if (!description.equals(other.description)) {
				return false;
			}
			if (!Objects.equals(suggestedFix, other.suggestedFix)) {
				return false;
			}
			if (!Objects.equals(moreInformationLink, other.moreInformationLink)) {
				return false;
			}
			return true;
		}

		return false;
	}
	
	@Override
	public int compareTo(Marker o) {
		return location.compareTo(o.location);
	}
}
