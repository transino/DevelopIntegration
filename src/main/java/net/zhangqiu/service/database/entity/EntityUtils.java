package net.zhangqiu.service.database.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.zhangqiu.service.check.entity.CheckRule;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.CheckRuleTableFile;
import net.zhangqiu.service.database.entity.param.EntityDataComponent;
import net.zhangqiu.service.database.entity.param.EntityDataComponentParam;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

public class EntityUtils {

	public static EntityTableFile convertToEntityTableFile(Object entityTableFileJson){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		map.put("entityTableList", EntityTable.class);
		map.put("entityColumnList", EntityColumn.class);
		EntityTableFile entityTableFile = (EntityTableFile) JsonUtils.jsonToObject(entityTableFileJson, EntityTableFile.class, map);
		return entityTableFile;
	}
	
	public static void entityTableFileToUpperCase(EntityTableFile entityTableFile){
		for(EntityTable entityTable : entityTableFile.getEntityTableList()){
			for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
				entityColumn.setColumnName(entityColumn.getColumnName().toUpperCase());
			}
			Set<String> idColumnSet = new HashSet<String>();
			for(String string : entityTable.getIdColumnSet()){
				idColumnSet.add(string.toUpperCase());
			}
			entityTable.setIdColumnSet(idColumnSet);
			
			Set<String> logicIdColumnSet = new HashSet<String>();
			for(String string : entityTable.getLogicIdColumnSet()){
				logicIdColumnSet.add(string.toUpperCase());
			}
			entityTable.setLogicIdColumnSet(logicIdColumnSet);
		}
	}
	
	public static void entityTableToUpperCase(EntityTable entityTable){
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			entityColumn.setColumnName(entityColumn.getColumnName().toUpperCase());
		}
		Set<String> idColumnSet = new HashSet<String>();
		for(String string : entityTable.getIdColumnSet()){
			idColumnSet.add(string.toUpperCase());
		}
		entityTable.setIdColumnSet(idColumnSet);
		
		Set<String> logicIdColumnSet = new HashSet<String>();
		for(String string : entityTable.getLogicIdColumnSet()){
			logicIdColumnSet.add(string.toUpperCase());
		}
		entityTable.setLogicIdColumnSet(logicIdColumnSet);
	}
	
	public static EntityTableFile convertToEntityTableFile(String entityTableFileString){
		Object entityTableFileJson = JsonUtils.stringToJson(entityTableFileString);
		return convertToEntityTableFile(entityTableFileJson);
	}
	
	public static EntityDatasourceSqlFile convertToEntityDatasourceSqlFile(Object entityDatasourceFileJson){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		map.put("entityDatasourceSqlList", EntityDatasourceSql.class);
		EntityDatasourceSqlFile entityDatasourceSqlFile = (EntityDatasourceSqlFile) JsonUtils.jsonToObject(entityDatasourceFileJson, EntityDatasourceSqlFile.class, map);
		return entityDatasourceSqlFile;
	}
	
	public static EntityDatasourceSqlFile convertToEntityDatasourceSqlFile(String entityDatasourceSqlFileString){
		Object entityDatasourceFileJson = JsonUtils.stringToJson(entityDatasourceSqlFileString);
		return convertToEntityDatasourceSqlFile(entityDatasourceFileJson);
	}
	
	public static EntityDatasourceFile convertToEntityDatasourceFile(Object entityDatasourceFileJson){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		map.put("entityDatasourceList", EntityDatasource.class);
		EntityDatasourceFile entityDatasourceFile = (EntityDatasourceFile) JsonUtils.jsonToObject(entityDatasourceFileJson, EntityDatasourceFile.class,map);
		return entityDatasourceFile;
	}
	
	public static EntityDatasourceFile convertToEntityDatasourceFile(String entityDatasourceFileString){
		Object entityDatasourceFileJson = JsonUtils.stringToJson(entityDatasourceFileString);
		return convertToEntityDatasourceFile(entityDatasourceFileJson);
	}
	
	public static EntityDataParam convertToEntityData(Object entityDataJson){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		map.put("data", Map.class);
		EntityDataParam entityData = (EntityDataParam) JsonUtils.jsonToObject(entityDataJson, EntityDataParam.class,map);
		//entityDataParamToUpperCase(entityData);
		return entityData;
	}
	
	/*
	private static void entityDataParamToUpperCase(EntityDataParam entityData){
		Map<String,Object> upperDataMap = new HashMap<String,Object>();
		for(Map.Entry<String,Object> entry : entityData.getData().entrySet()){
			upperDataMap.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		entityData.setData(upperDataMap);
	}
	*/
	public static EntityDataParam convertToEntityData(String entityDataString){
		Object entityDataJson = JsonUtils.stringToJson(entityDataString);
		return convertToEntityData(entityDataJson);
	}
	
	public static EntityDataComponentParam convertToEntityDataComponent(Object entityDataComponentJson){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		map.put("entityDataComponentList", EntityDataComponent.class);
		map.put("objectMapList", Map.class);
		EntityDataComponentParam entityDataComponentParam = (EntityDataComponentParam) JsonUtils.jsonToObject(entityDataComponentJson,EntityDataComponentParam.class,map);
		
		return entityDataComponentParam;
	}
	
	public static EntityDataComponentParam convertToEntityDataComponent(String entityDataComponentString){
		Object entityDataJson = JsonUtils.stringToJson(entityDataComponentString);
		return convertToEntityDataComponent(entityDataJson);
	}

	private static CheckRuleTable convertToCheckRuleTable(Object checkRuleTableObject){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		Object checkRuleTableJson = JsonUtils.objectToJson(checkRuleTableObject);
		map.put("checkRuleList", CheckRule.class);
		CheckRuleTable checkRuleTable = (CheckRuleTable) JsonUtils.jsonToObject(checkRuleTableJson, CheckRuleTable.class, map);
		return checkRuleTable;
	}
	
	public static CheckRuleTableFile convertToCheckRuleTableFile(Object checkRuleTableJson){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		map.put("checkRuleTableList", CheckRuleTable.class);
		map.put("checkRuleList", CheckRule.class);
		map.put("checkRuleTableMap", Map.class);
		CheckRuleTableFile checkRuleTableFile = (CheckRuleTableFile) JsonUtils.jsonToObject(checkRuleTableJson, CheckRuleTableFile.class, map);
		
		for(CheckRuleTable checkRuleTable : checkRuleTableFile.getCheckRuleTableList()){
			Map<String, CheckRuleTable> checkRuleTableMap = checkRuleTable.getCheckRuleTableMap();
			checkRuleTable.setCheckRuleTableMap(new HashMap<String, CheckRuleTable>());
			for(Map.Entry<String, CheckRuleTable> entry : checkRuleTableMap.entrySet()){
				CheckRuleTable newCheckRuleTable = convertToCheckRuleTable(entry.getValue());
				checkRuleTable.getCheckRuleTableMap().put(entry.getKey(), newCheckRuleTable);
			}
		}
		return checkRuleTableFile;
	}
	
	public static void checkRuleTableFileToUpperCase(CheckRuleTableFile checkRuleTableFile){
		for(CheckRuleTable checkRuleTable : checkRuleTableFile.getCheckRuleTableList()){
			for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
				checkRule.setRule(StrUtils.paramToUpperCase(checkRule.getRule(), "@COLUMN"));
				checkRule.setDescription(StrUtils.paramToUpperCase(checkRule.getDescription(), "@COLUMN"));
				checkRule.setMatch(StrUtils.paramToUpperCase(checkRule.getMatch(), "@COLUMN"));
			}
		}
	}
	
	public static void checkRuleTableToUpperCase(CheckRuleTable checkRuleTable){
		for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
			checkRule.setRule(StrUtils.paramToUpperCase(checkRule.getRule(), "@COLUMN"));
			checkRule.setDescription(StrUtils.paramToUpperCase(checkRule.getDescription(), "@COLUMN"));
			checkRule.setMatch(StrUtils.paramToUpperCase(checkRule.getMatch(), "@COLUMN"));
		}
	}
	
	public static CheckRuleTableFile convertToCheckRuleTableFile(String checkRuleTableString){
		Object checkRuleTableJson = JsonUtils.stringToJson(checkRuleTableString);
		return convertToCheckRuleTableFile(checkRuleTableJson);
	}
		
	public final static String Column_String = "string";
	public final static String Column_Date = "date";
	public final static String Column_Boolean = "boolean";
	public final static String Column_Table = "table";
	public static boolean isSupportColumnType(String strColumnType){
		String lowerColumnType = strColumnType.toLowerCase();
		if(lowerColumnType.equals(Column_String) || lowerColumnType.equals(Column_Date) || lowerColumnType.equals(Column_Boolean)|| lowerColumnType.equals(Column_Table)){
			return true;
		}
		return false;
	}
	
	public final static String Database_Oracle = "oracle";
	public final static String Database_Mysql = "mysql";
	public final static String Database_Db2 = "db2";
	public final static String Database_Sqlserver = "sqlserver";
	public static boolean isSupportDatabaseType(String strDatabaseType){
		String lowerDatabaseType = strDatabaseType.toLowerCase();
		if(lowerDatabaseType.equals(Database_Oracle) || lowerDatabaseType.equals(Database_Mysql) || lowerDatabaseType.equals(Database_Db2) || lowerDatabaseType.equals(Database_Sqlserver)){
			return true;
		}
		return false;
	}
}
