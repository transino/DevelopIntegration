package net.zhangqiu.service.check.entity;

import java.util.Map;

public class CheckRule {
	private String match;
	private int index;
	private String rule;
	private String description;
	private boolean optional;
	private int level;
	private Map<String,String> ruleColumnMap;
	private Map<String,String> descriptionColumnMap;
	
	public CheckRule(){
		this(null,null);
	}

	public CheckRule(String rule){
		this(rule,null);
	}
	public CheckRule(String rule,String description){
		this(rule,description,0);
	}
	public CheckRule(String rule,int level){
		this(rule,null,level);
	}
	public CheckRule(String rule,String description,int level){
		this.rule = rule;
		this.description = description;
		this.level = level;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public Map<String, String> getRuleColumnMap() {
		return ruleColumnMap;
	}

	public void setRuleColumnMap(Map<String, String> ruleColumnMap) {
		this.ruleColumnMap = ruleColumnMap;
	}

	public Map<String, String> getDescriptionColumnMap() {
		return descriptionColumnMap;
	}

	public void setDescriptionColumnMap(Map<String, String> descriptionColumnMap) {
		this.descriptionColumnMap = descriptionColumnMap;
	}
}
