package logicanalyser.languages;

import java.util.List;

import com.google.common.collect.Lists;

import logicanalyser.Interval;
import logicanalyser.Marker;
import logicanalyser.Rule;
import logicanalyser.SeverityRating;
import logicanalyser.config.Details;
import logicanalyser.config.RuleConfiguration;

public abstract class BaseContext implements Context {
	private final List<Marker> markers;
	private final RuleConfiguration details;
	
	private Rule<?> currentRule;
	
	public BaseContext(RuleConfiguration details) {
		this.details = details;
		markers = Lists.newArrayList();
	}
	
	public List<Marker> getMarkers() {
		return markers;
	}
	
	@Override
	public void addMarker(Marker marker) {
		markers.add(marker);
	}
	
	@Override
	public void addMarker(Object target) {
		Interval location = locate(target);
		Details info = getDetails();
		String locationDetails = generateLocationInfo(target, location);
		
		Marker marker = new Marker(location, info, locationDetails);
		markers.add(marker);
	}
	
	@Override
	public void addMarker(Object target, String overrideId) {
		Interval location = locate(target);
		Details info = getDetails(overrideId);
		String locationDetails = generateLocationInfo(target, location);
		
		Marker marker = new Marker(location, info, locationDetails);
		markers.add(marker);
	}
	
	protected Details getDetails(String id) {
		Details information = details.forRule(id);
		if (information == null) {
			return new Details(
				"Unknown rule id " + id,
				SeverityRating.Informational
			);
		} else {
			return information;
		}
	}
	
	protected Details getDetails() {
		Details information = details.forRule(currentRule);
		if (information == null) {
			return currentRule.defaultDetails();
		} else {
			return information;
		}
	}
	
	protected String generateLocationInfo(Object target, Interval interval) {
		return null;
	}
	
	public void setRule(Rule<?> currentRule) {
		this.currentRule = currentRule;
	}
}
