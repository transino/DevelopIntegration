package net.zhangqiu.service.database.entity.param;

public class EntityDatabaseCheckParam {
	
	private String datasourceNameKey;
	private String tableNameKey;
	private String sql;
	private int cacheLine;
	private int maxDataResult;
	private int processorCount;
	private boolean maxDataResultBreak;
	private int resultLevel;
	private boolean checkRule;
	private boolean ruleExpression;
	private boolean descriptionExpression;
	private boolean description;
	private boolean optional;
	private boolean columnResultOnlyFalse;
	
	public void setDatasourceNameKey(String datasourceNameKey) {
		this.datasourceNameKey = datasourceNameKey;
	}
	public String getDatasourceNameKey() {
		return datasourceNameKey;
	}
	public void setTableNameKey(String tableNameKey) {
		this.tableNameKey = tableNameKey;
	}
	public String getTableNameKey() {
		return tableNameKey;
	}
	public int getCacheLine() {
		return cacheLine;
	}
	public void setCacheLine(int cacheLine) {
		this.cacheLine = cacheLine;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public int getMaxDataResult() {
		return maxDataResult;
	}
	public void setMaxDataResult(int maxDataResult) {
		this.maxDataResult = maxDataResult;
	}
	public int getProcessorCount() {
		return processorCount;
	}
	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}
	public boolean isMaxDataResultBreak() {
		return maxDataResultBreak;
	}
	public void setMaxDataResultBreak(boolean maxDataResultBreak) {
		this.maxDataResultBreak = maxDataResultBreak;
	}
	public int getResultLevel() {
		return resultLevel;
	}
	public void setResultLevel(int resultLevel) {
		this.resultLevel = resultLevel;
	}
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
	public EntityDatabaseCheckParam(){
		cacheLine = 1000;
		maxDataResult = 100;
		processorCount = Runtime.getRuntime().availableProcessors();
		maxDataResultBreak = true;
		resultLevel = 0;
		checkRule = true;
		ruleExpression = true;
		descriptionExpression = true;
		description = true;
		optional = true;
		columnResultOnlyFalse = true;
	}
}
