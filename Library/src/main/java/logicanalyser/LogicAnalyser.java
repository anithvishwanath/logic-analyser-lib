package logicanalyser;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;
import java.util.Set;

import logicanalyser.config.RuleConfiguration;
import logicanalyser.languages.CSSAnalyser;
import logicanalyser.languages.HTMLAnalyser;
import logicanalyser.languages.LanguageBase;

/**
 * The LogicAnalyser class provides access to all functionalities of this library.
 */
public class LogicAnalyser {
	private final Set<LanguageBase> installedLanguages;
	private final SetMultimap<LanguageBase, Ruleset> installedRulesets;
	private RuleConfiguration configuration;
	
	public LogicAnalyser() {
		installedLanguages = Sets.newHashSet();
		installedRulesets = HashMultimap.create();
		
		// Install included languages
		registerLanguageProcessor(new HTMLAnalyser());
		registerLanguageProcessor(new CSSAnalyser());
	}
	
	/**
	 * Analyses the given content finding errors and generating statistics about it. 
	 * @param type The MIME type of the content.
	 * @param content The content itself
	 * @return A report containing the error and statistic information
	 * @throws IllegalArgumentException Thrown if no language processor is able to handle the given MIME type
	 */
	public Report analyseContent(MediaType type, String content) throws IllegalArgumentException {
		LanguageBase language = getLanguageFor(type);
		if (language == null) {
			throw new IllegalArgumentException("No handler for " + type);
		}
		
		// TODO: Here is where you would remove rulesets that are not active.
		
		return language.process(type, content, installedRulesets.get(language), this);
	}
	
	/**
	 * Checks if a MIME type is supported by the available language processors.
	 * @param type The MIME type to check
	 * @return True if {@link #analyseContent(MediaType, String)} will succeed.
	 */
	public boolean canAnalyseContent(MediaType type) {
		for (LanguageBase language : installedLanguages) {
			if (language.doesAccept(type)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the language processor for the given type
	 * @param type The MIME type to check
	 * @return The language processor or null
	 */
	public LanguageBase getLanguageFor(MediaType type) {
		for (LanguageBase language : installedLanguages) {
			if (language.doesAccept(type)) {
				return language;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves all supported MIME types by the installed languages
	 * @return A set containing MIME types
	 */
	public Set<MediaType> getSupportedTypes() {
		ImmutableSet.Builder<MediaType> builder = ImmutableSet.builder();
		
		for (LanguageBase language : installedLanguages) {
			builder.addAll(language.getAcceptedTypes());
		}
		
		return builder.build();
	}
	
	/**
	 * Registers a new language processor to the system.
	 * @param language The language processor instance
	 * @throws IllegalArgumentException Thrown if the new language
	 * 		is already registered
	 */
	public void registerLanguageProcessor(LanguageBase language) {
		Preconditions.checkNotNull(language);
		
		Preconditions.checkArgument(!installedLanguages.contains(language));
		Preconditions.checkArgument(
			!installedLanguages
				.stream()
				.anyMatch(
					lang -> lang.getName().equals(language.getName()
				)
			)
		);
					
		installedLanguages.add(language);
	}
	
	/**
	 * Unregisters an installed language processor
	 * @param language The language processor instance to remove
	 */
	public void deregisterLanguageProcessor(LanguageBase language) {
		Preconditions.checkNotNull(language);
		
		if (installedLanguages.remove(language)) {
			installedRulesets.removeAll(language);
		}
	}
	
	/**
	 * Retrieves a language processor by its type
	 * @param type The type of the language processor
	 * @return The language processor
	 */
	private LanguageBase getLanguage(Class<? extends LanguageBase> type) {
		for (LanguageBase language : installedLanguages) {
			if (type.equals(language.getClass())) {
				return language;
			}
		}
		
		return null;
	}
	
	/**
	 * Registers a new ruleset to the system.
	 * Rulesets that have the same category name will appear as one ruleset with the combined rules from each.
	 * @param ruleset The ruleset to register.
	 * @throws IllegalStateException Thrown if the language specified by the ruleset is not loaded
	 */
	public void registerRuleset(Ruleset ruleset) throws IllegalStateException {
		Preconditions.checkNotNull(ruleset);
		
		LanguageBase targetLanguage = getLanguage(ruleset.getLanguage());
		if (targetLanguage == null) {
			throw new IllegalStateException("Target Language is not installed");
		}
		
		installedRulesets.put(targetLanguage, ruleset);
	}
	
	/**
	 * Unregisters an installed ruleset
	 * @param ruleset The ruleset to remove
	 */
	public void deregisterRuleset(Ruleset ruleset) {
		Preconditions.checkNotNull(ruleset);
		
		LanguageBase targetLanguage = getLanguage(ruleset.getLanguage());
		if (targetLanguage == null) {
			throw new IllegalArgumentException("Target Language is not installed");
		}
		
		installedRulesets.remove(targetLanguage, ruleset);
	}
	
	/**
	 * Applies the configuration stored in RuleConfiguration.
	 * It will be consulted to find the messages for each rule
	 * @param config The configuration
	 */
	public void applyRuleConfiguration(RuleConfiguration config) {
		this.configuration = config;
	}
	
	/**
	 * Retrieves the current rule configuration object. This might
	 * be either the supplied one in 
	 * {@link #applyRuleConfiguration(RuleConfiguration)}
	 * or the default one.
	 * @return The rule configuration
	 */
	public RuleConfiguration getRuleConfiguration() {
		if (configuration == null) {
			return RuleConfiguration.DEFAULT;
		} else {
			return configuration;
		}
	}
}
