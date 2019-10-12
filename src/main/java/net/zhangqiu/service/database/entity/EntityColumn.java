package net.zhangqiu.service.database.entity;

public class EntityColumn {
	private String columnName;
	private String columnDescription;
	private String columnType;
    private String showColumn;

    public String getShowColumn() {
        return showColumn;
    }

    public void setShowColumn(String showColumn) {
        this.showColumn = showColumn;
    }

    public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnDescription(String columnDescription) {
		this.columnDescription = columnDescription;
	}
	public String getColumnDescription() {
		return columnDescription;
	}
	
	public EntityColumn(){
		
	}
	public EntityColumn(String columnName){
		this(columnName,"",EntityUtils.Column_String);		
	}
	public EntityColumn(String columnName,String columndescription){
		this(columnName,columndescription,EntityUtils.Column_String);
	}
	public EntityColumn(String columnName,String columnDescription,String columnType){
		this.columnName = columnName;
		this.setColumnDescription(columnDescription);
		this.columnType = columnType;
	}
	
}
