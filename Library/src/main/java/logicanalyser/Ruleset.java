package logicanalyser;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;

import logicanalyser.languages.LanguageBase;

/**
 * A Ruleset is a collection of rules that are grouped together to form a category.
 */
public interface Ruleset {
	/**
	 * Gets the category name of this ruleset
	 * @return The name
	 */
	String getCategoryName();
	
	/**
	 * Gets the language this ruleset is run with
	 * @return The language type
	 */
	Class<? extends LanguageBase> getLanguage();
	
	/**
	 * Gets the rules that are contained within this ruleset
	 * @return An unmodifiable list of rules
	 */
	List<Rule<?>> getContainedRules();
	
	/**
	 * Gets only the rules in this ruleset, that accept the given type
	 * @param <T> The type accepted by the rule
	 * @param type The type accepted by the rule
	 * @return An iterable of the rules
	 */
	default <T> Iterable<Rule<T>> getRulesUsing(Class<T> type) {
		ArrayList<Rule<T>> filtered = new ArrayList<>();
		for (Rule<?> rule : getContainedRules()) {
			if (rule.getTarget().equals(type)) {
				filtered.add((Rule<T>)rule);
			}
		}
		
		return filtered;
	}
}
