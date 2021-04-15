package logicanalyser.languages;

import logicanalyser.Interval;
import logicanalyser.Marker;

/**
 * Provides a way to produce markers given some target.
 * 
 */
public interface Context {
	/**
	 * Creates and adds a new marker for the given target.
	 * The marker information will be loaded from the
	 * configuration using the current rules ID.
	 * 
	 * @param target The target that was the cause of the marker.
	 * @throws IllegalArgumentException Thrown if the target is not of an 
	 * 		accepted type
	 */
	void addMarker(Object target);
	
	/**
	 * Creates and adds a new marker for the given target.
	 * The marker information will be loaded from the
	 * configuration using the specified ID
	 * 
	 * @param target The target that was the cause of the marker.
	 * @param overrideId The ID for the marker information
	 * @throws IllegalArgumentException Thrown if the target is not of an 
	 * 		accepted type
	 */
	void addMarker(Object target, String overrideId);
	
	/**
	 * Finds the location of the given target
	 * @param target The target to find
	 * @return The Interval containing the location
	 * @throws IllegalArgumentException Thrown if the target is not of an 
	 * 		accepted type
	 */
	Interval locate(Object target);
	
	/**
	 * Adds a pre-existing marker to the results.
	 * No information in the marker will be updated.
	 * These cannot be configured through the configuration
	 * system.
	 * @param marker The marker to add
	 */
	void addMarker(Marker marker);
}
