package logicanalyser;

import logicanalyser.config.Details;
import logicanalyser.languages.Context;
import logicanalyser.languages.LanguageBase;

/**
 * Represents some kind of rule that is defined for a language.
 * @param <E> The type of object that is used to check the rule
 */
public abstract class Rule<E> {
	/**
	 * The type of element
	 */
	private final Class<E> type;
	
	/**
	 * The name of the rule
	 */
	private String ruleName;
	
	/**
	 * Description of the rule
	 */
	private String description;

	/**
	 * Constructs the rule for the given type.
	 * @param type The type of object being checked
	 * @param name The name of the rule
	 */
	public Rule(Class<E> type, String name) {
		this.type = type;
		this.ruleName = name;
	}
	
	public Class<E> getTarget() {
		return type;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * This is used to return a list of type marker.
	 * All markers should be output to the context
	 * @param context The context var, provides access to creating markers and locating targets.
	 * 		Check the corresponding {@link LanguageBase} implementation
	 * 		to find out what target the context requires. This is 
	 * 		the return value of {@link LanguageBase#getContextType()}
	 * @param target The target to check for rule violations
	 */
	public abstract void check(Context context, E target);
	
	/**
	 * Retrieves the default details of this rule.
	 * This will be used if no configuration has been set 
	 * for the rule id.
	 * @return The details
	 */
	public abstract Details defaultDetails();
}
