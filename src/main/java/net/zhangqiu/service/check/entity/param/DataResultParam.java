package net.zhangqiu.service.check.entity.param;

public class DataResultParam {
	private boolean checkRule;
	private boolean ruleExpression;
	private boolean descriptionExpression;
	private boolean description;
	private boolean optional;
	private boolean columnResultOnlyFalse;
	public boolean isCheckRule() {
		return checkRule;
	}
	public void setCheckRule(boolean checkRule) {
		this.checkRule = checkRule;
	}
	public boolean isRuleExpression() {
		return ruleExpression;
	}
	public void setRuleExpression(boolean ruleExpression) {
		this.ruleExpression = ruleExpression;
	}
	public boolean isDescriptionExpression() {
		return descriptionExpression;
	}
	public void setDescriptionExpression(boolean descriptionExpression) {
		this.descriptionExpression = descriptionExpression;
	}
	public boolean isDescription() {
		return description;
	}
	public void setDescription(boolean description) {
		this.description = description;
	}
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	public void setColumnResultOnlyFalse(boolean columnResultOnlyFalse) {
		this.columnResultOnlyFalse = columnResultOnlyFalse;
	}
	public boolean isColumnResultOnlyFalse() {
		return columnResultOnlyFalse;
	}
	public DataResultParam(){
		checkRule = true;
		ruleExpression = true;
		descriptionExpression = true;
		description = true;
		optional = true;
		columnResultOnlyFalse = true;
	}
}
