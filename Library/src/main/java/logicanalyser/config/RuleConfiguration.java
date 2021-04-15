package logicanalyser.config;

import java.util.Map;

import com.google.common.collect.Maps;

import logicanalyser.Rule;

public class RuleConfiguration {
	public static final RuleConfiguration DEFAULT = new RuleConfiguration();
	
	private final Map<String, RuleDetails> details;
	
	public RuleConfiguration() {
		details = Maps.newHashMap();
	}
	
	public RuleDetails forRule(String id) {
		return details.get(id);
	}
	
	public RuleDetails forRule(Rule<?> rule) {
		return forRule(rule.getRuleName());
	}
	
	public void add(RuleDetails details) {
		this.details.put(details.id, details);
	}
}
