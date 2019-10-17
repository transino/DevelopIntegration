package net.zhangqiu.service.database.entity;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.CheckRule;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.CheckRuleTableFile;
import net.zhangqiu.service.database.dao.BatchUpdateSqlDao;
import net.zhangqiu.service.database.dao.ExecuteSqlDao;
import net.zhangqiu.service.database.dao.SelectAllDao;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.interfaces.ConnectionPool;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class EntityContext {
	
	private SnowFlakeGenerator snowFlakeGenerator;
	public EntityContext(){
		snowFlakeGenerator = new SnowFlakeGenerator(1,1);
	}
	public long getNextId(){
		return snowFlakeGenerator.nextId();
	}
	
	private static Logger logger = LoggerFactory.getLogger(EntityContext.class);
		
	@Autowired
	EnvironmentContext environmentContext;

	private Map<String,String> defaultDatasourceNameMap;
	public void setDefaultDatasourceNameMap(Map<String,String> defaultDatasourceNameMap) {
		this.defaultDatasourceNameMap = defaultDatasourceNameMap;
	}
	public Map<String,String> getDefaultDatasourceNameMap() {
		return defaultDatasourceNameMap;
	}

	public String getCurrentDatasourceName(String strProjectCode,String datasourceName){
		if(StrUtils.isEmpty(datasourceName)){
			return defaultDatasourceNameMap.get(strProjectCode);
		}
		else{
			return datasourceName;
		}
	}
	
	private final String transactionTemplateName =  "TransactionTemplate";
	private final String jdbcTemplateName = "JdbcTemplate";
	private final String dataSourceTransactionManagerName = "DataSourceTransactionManager";
	public String getDatasourceBeanName(String strProjectCode,String datasourceName){
		return strProjectCode + fileNameSplit + datasourceName;
	}
	public String getTransactionTemplateName(String strProjectCode,String datasourceName){
		return strProjectCode + fileNameSplit +datasourceName+ fileNameSplit + transactionTemplateName;
	}
	public String getJdbcTemplateName(String strProjectCode,String datasourceName){
		return strProjectCode + fileNameSplit +datasourceName+ fileNameSplit + jdbcTemplateName;
	}
	public String getDataSourceTransactionManagerName(String strProjectCode,String datasourceName){
		return strProjectCode + fileNameSplit +datasourceName+ fileNameSplit + dataSourceTransactionManagerName;
	}

	private String datasourceFilePath;
	private String taskFilePath;
	public void setDatasourceFilePath(String datasourceFilePath) {
		this.datasourceFilePath = datasourceFilePath;
	}
	public String getDatasourceFilePath() {
		return datasourceFilePath;
	}
	public void setTaskFilePath(String taskFilePath) {
		this.taskFilePath = taskFilePath;
	}
	public String getTaskFilePath() {
		return taskFilePath;
	}
	
	private final String fileNameSplit = "_";
	private final String datasourceFileName = fileNameSplit + "datasource.json";
	private final String taskDatasourceSqlFileName = fileNameSplit + "datasourcesql.json";
	private final String taskTableFileName = fileNameSplit + "table.json";
	private final String taskTableCheckFileName = fileNameSplit + "tablecheck.json";
	private final String taskTxtSqlFileName = fileNameSplit + "sql.sql";
	private final String taskTxtLineSqlFileName = fileNameSplit + "linesql.sql";

	public String getDatasourceFilePath(String strProjectCode){
		return environmentContext.getResourcesPath() + strProjectCode + File.separator + strProjectCode + datasourceFileName;
	}
	public String getDatasourceFileName(String strProjectCode){
		return strProjectCode + datasourceFileName;
	}
	public String getTaskTableFilePath(String strProjectCode,String strTaskCode){
		return environmentContext.getResourcesPath() + strProjectCode + File.separator + strTaskCode + File.separator + strTaskCode + taskTableFileName;
	}
	public String getTaskTableFileName(String strTaskCode){
		return strTaskCode + taskTableFileName;
	}
	public String getTaskDatasourceSqlFilePath(String strProjectCode,String strTaskCode){
		return environmentContext.getResourcesPath() + strProjectCode + File.separator + strTaskCode + File.separator + strTaskCode + taskDatasourceSqlFileName;
	}
	public String getTaskDatasourceSqlFileName(String strTaskCode){
		return strTaskCode + taskDatasourceSqlFileName;
	}
	public String getTaskTableCheckFilePath(String strProjectCode,String strTaskCode){
		return environmentContext.getResourcesPath() + strProjectCode + File.separator + strTaskCode + File.separator + strTaskCode + taskTableCheckFileName;
	}
	public String getTaskTableCheckFileName(String strTaskCode){
		return strTaskCode + taskTableCheckFileName;
	}
	public String getTaskTxtPath(String strProjectCode,String strTaskCode,String fileName){
		return environmentContext.getResourcesPath() + strProjectCode + File.separator + strTaskCode + File.separator + fileName;
	}
	public String getTaskClassPath(String strProjectCode,String strTaskCode,String fileName){
		return strProjectCode + File.separator + strTaskCode + File.separator + fileName;
	}
	
	public String getTaskKey(String strProjectCode,String strTaskCode){
		return strProjectCode + fileNameSplit + strTaskCode;
	}

    private Map<String,List<Map<String,Object>>> cachedataMap;
    private Map<String,Map<String,Map<String,Object>>> cachedataKeyMap;
    private Map<String,Map<String,Map<String,String>>> cachedataKeyNameMap;
    private Map<String,Map<String,Map<String,String>>> constMap;

    public Map<String,Map<String,Map<String,String>>> getConstMap() {
        return constMap;
    }

    public void setConstMap(Map<String,Map<String,Map<String,String>>> constMap) {
        this.constMap = constMap;
    }

    public Map<String, List<Map<String, Object>>> getCachedataMap() {
        return cachedataMap;
    }

    public void setCachedataMap(Map<String, List<Map<String, Object>>> cachedataMap) {
        this.cachedataMap = cachedataMap;
    }

    public Map<String, Map<String, Map<String, Object>>> getCachedataKeyMap() {
        return cachedataKeyMap;
    }

    public void setCachedataKeyMap(Map<String, Map<String, Map<String, Object>>> cachedataKeyMap) {
        this.cachedataKeyMap = cachedataKeyMap;
    }
    public Map<String, Map<String, Map<String, String>>> getCachedataKeyNameMap() {
        return cachedataKeyNameMap;
    }

    public void setCachedataKeyNameMap(Map<String, Map<String, Map<String, String>>> cachedataKeyNameMap) {
        this.cachedataKeyNameMap = cachedataKeyNameMap;
    }

    @PostConstruct
	public void init() throws Exception{
		
		projectEntityDatasourceMap = new HashMap<String,Map<String,EntityDatasource>>();
		entityDatasourceMap = new LinkedHashMap<String,EntityDatasource>();
		entityTableMap = new LinkedHashMap<String,EntityTable>();
		txtSqlMap = new HashMap<String,Map<String,String>>();
		txtLineSqlMap = new HashMap<String,Map<String,List<String>>>();
		entityDatasourceSqlListMap = new LinkedHashMap<String,List<EntityDatasourceSql>>();
		entityTableInsertMap = new HashMap<String,String>();
		entityTableUpdateByIdMap = new HashMap<String,String>(); 
		entityTableDeleteByIdMap = new HashMap<String,String>();
		entityTableDeleteAllMap = new HashMap<String,String>(); 
		entityTableSelectByIdMap = new HashMap<String,String>();  
		entityTableSelectAllMap = new HashMap<String,String>();  
		checkRuleTableMap = new HashMap<String,CheckRuleTable>();
		defaultDatasourceNameMap = new HashMap<String,String>();
        cachedataMap = new HashMap<String, List<Map<String, Object>>>();
        cachedataKeyMap = new HashMap<String, Map<String, Map<String, Object>>>();
        cachedataKeyNameMap =  new HashMap<String, Map<String, Map<String, String>>>();
        constMap = new HashMap<String, Map<String, Map<String, String>>>();

		try{
			initEntityDatasourceFile();
			initEntityTaskFile();
			initDatasourceSql();
            initCachedata();
		}
		catch(Exception ex){
            logger.error("初始化异常，检查异常逻辑是否完整，请联系服务管理员",ex);
		}
	}
	
	private Map<String,Map<String,EntityDatasource>> projectEntityDatasourceMap;

	public void setProjectEntityDatasourceMap(
			Map<String,Map<String,EntityDatasource>> projectEntityDatasourceMap) {
		this.projectEntityDatasourceMap = projectEntityDatasourceMap;
	}
	public Map<String,Map<String,EntityDatasource>> getProjectEntityDatasourceMap() {
		return projectEntityDatasourceMap;
	}
	
	private Map<String,EntityDatasource> entityDatasourceMap;
	
	public void setEntityDatasourceMap(Map<String,EntityDatasource> entityDatasourceMap) {
		this.entityDatasourceMap = entityDatasourceMap;
	}
	public Map<String,EntityDatasource> getEntityDatasourceMap() {
		return entityDatasourceMap;
	}

	@Autowired
	ApplicationContext applicationContext; 
	@Autowired
	@Qualifier("apacheTomcatPool")
    ConnectionPool connectionPool;
	private void initEntityDatasourceFile() throws Exception{
        File entityProjectFiles = new File(EntityContext.class.getClassLoader().getResource("").getFile());
		if(entityProjectFiles.exists()){
			File[] projectFiles = entityProjectFiles.listFiles();
			for(File projectFile : projectFiles){
				if(projectFile.isDirectory()){
					String strProjectCode = projectFile.getName();
					try{
						File[] datasourceFiles = projectFile.listFiles();
						for(File datasourceFile : datasourceFiles){
							if(datasourceFile.getName().endsWith(datasourceFileName)){
								Map<String,EntityDatasource> projectEntityDatasource = new HashMap<String,EntityDatasource>();
								String entityDatasourceString = FileUtils.readFile(datasourceFile.getPath(),environmentContext.getEncoding());
								EntityDatasourceFile entityDatasourceFile = EntityUtils.convertToEntityDatasourceFile(entityDatasourceString);

								if(environmentContext.getDefaultDatasourceNameMap().containsKey(strProjectCode)){
									defaultDatasourceNameMap.put(strProjectCode, environmentContext.getDefaultDatasourceNameMap().get(strProjectCode));
								}
								for(EntityDatasource entityDatasource : entityDatasourceFile.getEntityDatasourceList()){
									String key = entityDatasource.getDataSourceName();
									if(projectEntityDatasource.containsKey(key)){
										logger.error("数据源名重复，" + key);
									}
									else{
										projectEntityDatasource.put(key, entityDatasource);
										entityDatasourceMap.put(getEntityDatasourceKey(strProjectCode,entityDatasource.getDataSourceName()), entityDatasource);
										registeDatasource(strProjectCode,entityDatasource);
									}
									if(!environmentContext.getDefaultDatasourceNameMap().containsKey(strProjectCode)){
										if(entityDatasource.isDefaultDatasource()){
											if(!defaultDatasourceNameMap.containsKey(strProjectCode)){
												defaultDatasourceNameMap.put(strProjectCode, entityDatasource.getDataSourceName());
											}
											else{
												logger.error("多个默认数据源，" + key);
											}
										}
									}
								}
								projectEntityDatasourceMap.put(strProjectCode, projectEntityDatasource);
							}
						}
					}
					catch (Exception ex){
						logger.error("初始化项目数据源异常："+strProjectCode + "，请联系项目管理员",ex);
					}
				}
			}
		}
	}

	private void registeDatasource(String strProjectCode,EntityDatasource entityDatasource){
		if(!applicationContext.containsBean(getDatasourceBeanName(strProjectCode, entityDatasource.getDataSourceName()))){
			ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext; 
		    DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)configurableApplicationContext.getBeanFactory(); 
			DataSource dataSource = connectionPool.getDataSource(entityDatasource);
			defaultListableBeanFactory.registerSingleton(getDatasourceBeanName(strProjectCode, entityDatasource.getDataSourceName()),dataSource);
			JdbcTemplate jdbcTemplate = new JdbcTemplate();
			jdbcTemplate.setDataSource(dataSource);
			defaultListableBeanFactory.registerSingleton(getJdbcTemplateName(strProjectCode,entityDatasource.getDataSourceName()),jdbcTemplate);
			DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
			dataSourceTransactionManager.setDataSource(dataSource);
			defaultListableBeanFactory.registerSingleton(getDataSourceTransactionManagerName(strProjectCode,entityDatasource.getDataSourceName()),dataSourceTransactionManager);
			TransactionTemplate transactionTemplate = new TransactionTemplate();
			transactionTemplate.setTransactionManager(dataSourceTransactionManager);
			defaultListableBeanFactory.registerSingleton(getTransactionTemplateName(strProjectCode,entityDatasource.getDataSourceName()),transactionTemplate);
		}
	}
	
	private Map<String,EntityTable> entityTableMap;
	private Map<String,CheckRuleTable> checkRuleTableMap;
	private Map<String,List<EntityDatasourceSql>> entityDatasourceSqlListMap;
	private Map<String,Map<String,String>> txtSqlMap;
	private Map<String,Map<String,List<String>>> txtLineSqlMap;
	public void setEntityTableMap(Map<String,EntityTable> entityTableMap) {
		this.entityTableMap = entityTableMap;
	}
	public Map<String,EntityTable> getEntityTableMap() {
		return entityTableMap;
	}
	
	public void setCheckRuleTableMap(Map<String,CheckRuleTable> checkRuleTableMap) {
		this.checkRuleTableMap = checkRuleTableMap;
	}
	public Map<String,CheckRuleTable> getCheckRuleTableMap() {
		return checkRuleTableMap;
	}
	public void setEntityDatasourceSqlListMap(
			Map<String,List<EntityDatasourceSql>> entityDatasourceSqlListMap) {
		this.entityDatasourceSqlListMap = entityDatasourceSqlListMap;
	}
	public Map<String,List<EntityDatasourceSql>> getEntityDatasourceSqlListMap() {
		return entityDatasourceSqlListMap;
	}
	public void setTxtSqlMap(Map<String,Map<String,String>> txtSqlMap) {
		this.txtSqlMap = txtSqlMap;
	}
	public Map<String,Map<String,String>> getTxtSqlMap() {
		return txtSqlMap;
	}
	public void setTxtLineSqlMap(Map<String,Map<String,List<String>>> txtLineSqlMap) {
		this.txtLineSqlMap = txtLineSqlMap;
	}
	public Map<String,Map<String,List<String>>> getTxtLineSqlMap() {
		return txtLineSqlMap;
	}
	
	public String getEntityDatasourceKey(String strProjectCode,String dataSourceName){
		String key = strProjectCode;
		if(StrUtils.isEmpty(dataSourceName)){
			dataSourceName = defaultDatasourceNameMap.get(strProjectCode);
		}
		key = key + "." + dataSourceName;
		return key;
	}
	
	public String getStrProjectCodeFromDatasourceKey(String key){
		return key.split("\\.")[0];
	}
	
	public String getDatasourceNameFromDatasourceKey(String key){
		return key.split("\\.")[1];
	}

	public String getEntityTableKey(String strProjectCode,String dataSourceName,String tableName){
		String key = strProjectCode;
		if(StrUtils.isEmpty(dataSourceName)){
			dataSourceName = defaultDatasourceNameMap.get(strProjectCode);
		}
		key = key + "." + dataSourceName + "." + tableName;
		return key;
	}
	
	public String getStrProjectCodeFromTableKey(String key){
		return key.split("\\.")[0];
	}
	
	public String getDatasourceNameFromTableKey(String key){
		return key.split("\\.")[1];
	}
	
	public String getTableNameFromTableKey(String key){
		return key.split("\\.")[2];
	}

	private void initEntityTaskFile() throws Exception{
		File entityProjectFiles = new File(environmentContext.getResourcesPath());
		if(entityProjectFiles.exists()){
			File[] projectFiles = entityProjectFiles.listFiles();
			for(File projectFile : projectFiles){
				if(projectFile.isDirectory()){
					String strProjectCode = projectFile.getName();
					File[] taskListFiles = projectFile.listFiles();
					for(File taskListFile : taskListFiles){
						if(taskListFile.isDirectory()){
							try{
								File[] taskFiles = taskListFile.listFiles();
								for(File taskFile : taskFiles){
									if(taskFile.getName().endsWith(taskTableFileName)){
										String entityTableString = FileUtils.readFile(taskFile.getPath(),environmentContext.getEncoding());
										EntityTableFile entityTableFile = EntityUtils.convertToEntityTableFile(entityTableString);
										for(EntityTable entityTable : entityTableFile.getEntityTableList()){
											String dataSourceName = entityTable.getDataSourceName();
											String key = getEntityTableKey(strProjectCode,dataSourceName,entityTable.getTableName());
											if(entityTableMap.containsKey(key)){
												logger.error("表名重复，" + key);
											}
											else{
												initStandardSql(strProjectCode,entityTable);
												//EntityUtils.entityTableToUpperCase(entityTable);
                                                for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
                                                    if(StrUtils.isEmpty(entityColumn.getColumnDescription())){
                                                        entityColumn.setColumnDescription(entityColumn.getColumnName());
                                                    }
                                                    entityTable.getEntityColumnMap().put(entityColumn.getColumnName(),entityColumn);
                                                }
												entityTableMap.put(key, entityTable);
											}
										}
									}
									else if(taskFile.getName().endsWith(taskTableCheckFileName)){
										String entityTableCheckString = FileUtils.readFile(taskFile.getPath(),environmentContext.getEncoding());
										CheckRuleTableFile checkRuleTableFile = EntityUtils.convertToCheckRuleTableFile(entityTableCheckString);
										for(CheckRuleTable checkRuleTable : checkRuleTableFile.getCheckRuleTableList()){
											String dataSourceName = checkRuleTable.getDataSourceName();
											String key = getEntityTableKey(strProjectCode,dataSourceName, checkRuleTable.getTableName());
											if(checkRuleTableMap.containsKey(key)){
												logger.error("表名重复，" + key);
											}
											else{
												//EntityUtils.checkRuleTableToUpperCase(checkRuleTable);
												initCheckRuleTable(strProjectCode,dataSourceName,checkRuleTable);
												checkRuleTableMap.put(key, checkRuleTable);
											}
										}
									}
									else if(taskFile.getName().endsWith(taskDatasourceSqlFileName)){
										String entityDatasourceSqlString = FileUtils.readFile(taskFile.getPath(),environmentContext.getEncoding());
										EntityDatasourceSqlFile entityDatasourceSqlFile = EntityUtils.convertToEntityDatasourceSqlFile(entityDatasourceSqlString);
										entityDatasourceSqlListMap.put(getTaskKey(projectFile.getName(),taskListFile.getName()), entityDatasourceSqlFile.getEntityDatasourceSqlList());
									}
									else if(taskFile.getName().endsWith(taskTxtSqlFileName)){
										String taskTxtSqlFileString = FileUtils.readFile(taskFile.getPath(),environmentContext.getEncoding());
										Map<String,String> fileMap = null;
										if(txtSqlMap.containsKey(getTaskKey(projectFile.getName(),taskListFile.getName()))){
											fileMap = txtSqlMap.get(getTaskKey(projectFile.getName(),taskListFile.getName()));
										}
										else{
											fileMap = new HashMap<String,String>();
										}
										fileMap.put(taskFile.getName(), taskTxtSqlFileString);
										txtSqlMap.put(getTaskKey(projectFile.getName(),taskListFile.getName()), fileMap);
									}
									else if(taskFile.getName().endsWith(taskTxtLineSqlFileName)){
										List<String> taskTxtLineSqlFileStringList = FileUtils.readLineFile(taskFile.getPath(),environmentContext.getEncoding());
										Map<String,List<String>> fileMap = null;
										if(txtLineSqlMap.containsKey(getTaskKey(projectFile.getName(),taskListFile.getName()))){
											fileMap = txtLineSqlMap.get(getTaskKey(projectFile.getName(),taskListFile.getName()));
										}
										else{
											fileMap = new HashMap<String,List<String>>();
										}
										fileMap.put(taskFile.getName(), taskTxtLineSqlFileStringList);
										txtLineSqlMap.put(getTaskKey(projectFile.getName(),taskListFile.getName()), fileMap);
									}
								}
							}
							catch (Exception ex){
                                logger.error("初始化任务异常："+taskListFile.getName() + "，请联系任务责任人",ex);
							}
						}
					}
				}
			}
		}
	}
	
	private void initCheckRuleTable(String strProjectCode,String dataSourceName,CheckRuleTable checkRuleTable){
		String key = getEntityTableKey(strProjectCode,dataSourceName, checkRuleTable.getTableName());
		for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
			Map<String,String> ruleColumnMap = StrUtils.getParamMap(checkRule.getRule(), "@COLUMN");
			Map<String,String> descriptionColumnMap = StrUtils.getParamMap(checkRule.getDescription(), "@COLUMN");
			if(StrUtils.isEmpty(checkRule.getMatch())){
				checkRule.setMatch(StrUtils.getFirstParam(checkRule.getRule(), "@COLUMN"));
			}
			checkRule.setRuleColumnMap(ruleColumnMap);
			checkRule.setDescriptionColumnMap(descriptionColumnMap);
		}
		for(EntityColumn entityColumn : entityTableMap.get(key).getEntityColumnList()){
			checkRuleTable.getColumnDescriptionMap().put(entityColumn.getColumnName(), entityColumn.getColumnDescription());
			checkRuleTable.getColumnTypeMap().put(entityColumn.getColumnName(),entityColumn.getColumnType());
		}
		for(Map.Entry<String, CheckRuleTable> entry : checkRuleTable.getCheckRuleTableMap().entrySet()){
			initCheckRuleTable(strProjectCode,dataSourceName,entry.getValue());
		}
	}
	
	private Map<String,String> entityTableInsertMap; 
	private Map<String,String> entityTableUpdateByIdMap; 
	private Map<String,String> entityTableDeleteByIdMap; 
	private Map<String,String> entityTableDeleteAllMap;
	private Map<String,String> entityTableSelectByIdMap; 
	private Map<String,String> entityTableSelectAllMap; 
	public void setEntityTableInsertMap(Map<String,String> entityTableInsertMap) {
		this.entityTableInsertMap = entityTableInsertMap;
	}
	public Map<String,String> getEntityTableInsertMap() {
		return entityTableInsertMap;
	}
	public void setEntityTableSelectByIdMap(Map<String,String> entityTableSelectByIdMap) {
		this.entityTableSelectByIdMap = entityTableSelectByIdMap;
	}
	public Map<String,String> getEntityTableSelectByIdMap() {
		return entityTableSelectByIdMap;
	}
	public void setEntityTableSelectAllMap(Map<String,String> entityTableSelectAllMap) {
		this.entityTableSelectAllMap = entityTableSelectAllMap;
	}
	public Map<String,String> getEntityTableSelectAllMap() {
		return entityTableSelectAllMap;
	}
	public void setEntityTableDeleteByIdMap(Map<String,String> entityTableDeleteByIdMap) {
		this.entityTableDeleteByIdMap = entityTableDeleteByIdMap;
	}
	public Map<String,String> getEntityTableDeleteByIdMap() {
		return entityTableDeleteByIdMap;
	}
	public void setEntityTableDeleteAllMap(Map<String,String> entityTableDeleteAllMap) {
		this.entityTableDeleteAllMap = entityTableDeleteAllMap;
	}
	public Map<String,String> getEntityTableDeleteAllMap() {
		return entityTableDeleteAllMap;
	}
	public void setEntityTableUpdateByIdMap(Map<String,String> entityTableUpdateByIdMap) {
		this.entityTableUpdateByIdMap = entityTableUpdateByIdMap;
	}
	public Map<String,String> getEntityTableUpdateByIdMap() {
		return entityTableUpdateByIdMap;
	}
	
	public String getEntityTableInsertSql(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String key = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		return entityTableInsertMap.get(key);
	}
	
	public String getEntityTableSelectByIdSql(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String key = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		return entityTableSelectByIdMap.get(key);
	}
	
	public String getEntityTableUpdateByIdSql(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String key = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		return entityTableUpdateByIdMap.get(key);
	}
	
	public String getEntityTableDeleteByIdSql(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String key = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		return entityTableDeleteByIdMap.get(key);
	}
	
	public String getEntityTableDeleteAllSql(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String key = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		return entityTableDeleteAllMap.get(key);
	}
	
	public String getEntityTableSelectAllSql(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String key = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		return entityTableSelectAllMap.get(key);
	}
	
	private void initStandardSql(String strProjectCode,EntityTable entityTable) throws Exception{
		
		String key = getEntityTableKey(strProjectCode,entityTable.getDataSourceName(),entityTable.getTableName());

		String columnSql = "";
		String valueSql = "";
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			/*
			if(!entityTable.isAutoGenerateId() || !entityTable.getIdColumnSet().contains(entityColumn.getColumnName())){
			    columnSql += entityColumn.getColumnName() + ",";
			    valueSql += "?,";
			}
			*/
			columnSql += entityColumn.getColumnName() + ",";
			valueSql += "?,";
		}
		columnSql = columnSql.substring(0,columnSql.length() - 1);
		valueSql = valueSql.substring(0,valueSql.length() - 1);

		String sql = "INSERT INTO " + entityTable.getTableName() + "(" + columnSql + ") VALUES(" + valueSql + ")";
		entityTableInsertMap.put(key, sql);
		
		String idConditionSql = "";
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			if(entityTable.getIdColumnSet().contains(entityColumn.getColumnName())){
				idConditionSql = entityColumn.getColumnName() + "=?" + " AND ";
			}
		}
		if(StrUtils.isEmpty(idConditionSql)){
			throw new Exception("表：" + entityTable.getTableName() + "未设置或匹配主键，区分大小写");
		}
		idConditionSql = idConditionSql.substring(0,idConditionSql.length() - 5);
		
		String deleteByIdSql = "DELETE FROM " + entityTable.getTableName() + " WHERE " + idConditionSql;
		entityTableDeleteByIdMap.put(key, deleteByIdSql);
		
		String deleteAllSql = "DELETE FROM " + entityTable.getTableName();
		entityTableDeleteAllMap.put(key, deleteAllSql);
		
		String updateColumnSql = "";
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			if(!entityTable.getIdColumnSet().contains(entityColumn.getColumnName())){ 
				updateColumnSql += entityColumn.getColumnName() + "=?,";
			}
		}
		updateColumnSql = updateColumnSql.substring(0,updateColumnSql.length() - 1);
		String updateByIdSql = "UPDATE " + entityTable.getTableName() + " SET " + updateColumnSql + " WHERE " + idConditionSql;
		entityTableUpdateByIdMap.put(key, updateByIdSql);
		
		String selectByIdSql = "SELECT * FROM " + entityTable.getTableName() + " WHERE " + idConditionSql;
		entityTableSelectByIdMap.put(key, selectByIdSql);
		
		String selectByAllSql = "SELECT * FROM " + entityTable.getTableName();
		entityTableSelectAllMap.put(key, selectByAllSql);
	}
	
	public Object convertToType(Object value,String columnType) throws Exception{
		if(columnType.toLowerCase().equals(EntityUtils.Column_String)){
			value = value.toString();
		}
		else if(columnType.toLowerCase().equals(EntityUtils.Column_Date)){
			if(!(value instanceof Date)){
				if(value instanceof String){
				    if(((String) value).length() > 10){
                        SimpleDateFormat simpleDatetimeFormat=new SimpleDateFormat(environmentContext.getDatetimeFormt());
                        value = simpleDatetimeFormat.parse((String)value);
                    }
                    else{
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(environmentContext.getDateFormt());
                        value = simpleDateFormat.parse((String)value);
                    }
				}
				else if(value instanceof Long){
					value = new Date((Long)value);
				}
				else{
					value = JsonUtils.MorphDynaBeanToDate(value);
				}
			}
		}
		else if(columnType.toLowerCase().equals(EntityUtils.Column_Boolean)){
			value = ((Boolean)value);
		}
		else if(columnType.toLowerCase().equals(EntityUtils.Column_Table)){
			if(value instanceof String){
				
			}
		}
		return value;
	}

	public Map<String,Object> getConvertToStandardMap(String strProjectCode,String datasourceName,EntityDataParam entityData) throws Exception{
		String entityTableKey = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		EntityTable entityTable = getEntityTableMap().get(entityTableKey);
		Map<String,Object> map = new HashMap<String,Object>();
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			String key = entityColumn.getColumnName();
			Object value = entityData.getData().get(key);
			if(value != null){
				value = convertToType(value,entityColumn.getColumnType());
			}
			map.put(key, value);
		}
		return map;
	}
	
	public Object[] getEntityTableInsertValue(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String entityTableKey = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		EntityTable entityTable = getEntityTableMap().get(entityTableKey);
		Map<String,Object> map = entityData.getData();
		List<Object> objectList = new ArrayList<Object>();
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			String key = entityColumn.getColumnName();
			/*
			if(!entityTable.isAutoGenerateId() || !entityTable.getIdColumnSet().contains(key)){
				objectList.add(map.get(key));
			}
			*/
			objectList.add(map.get(key));
		}
		return objectList.toArray();
	}
	
	public Object[] getEntityTableUpdateValue(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String entityTableKey = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		EntityTable entityTable = getEntityTableMap().get(entityTableKey);
		Map<String,Object> map = entityData.getData();
		List<Object> objectList = new ArrayList<Object>();
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			String key = entityColumn.getColumnName();
			if(!entityTable.getIdColumnSet().contains(key)){
				objectList.add(map.get(key));
			}
		}
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			String key = entityColumn.getColumnName();
			if(entityTable.getIdColumnSet().contains(key)){
				objectList.add(map.get(key));
			}
		}
		return objectList.toArray();
	}
	
	public Object[] getEntityTableIdValue(String strProjectCode,String datasourceName,EntityDataParam entityData){
		String entityTableKey = getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		EntityTable entityTable = getEntityTableMap().get(entityTableKey);
		Map<String,Object> map = entityData.getData();
		List<Object> objectList = new ArrayList<Object>();
		for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
			String key = entityColumn.getColumnName();
			if(entityTable.getIdColumnSet().contains(key)){
				objectList.add(map.get(key));
			}
		}
		return objectList.toArray();
	}
	
	
	@Autowired
	ExecuteSqlDao executeSqlDao;
	@Autowired
	SelectAllDao selectAllDao;
	@Autowired
    UpdateSqlDao updateSqlDao;
	@Autowired
    BatchUpdateSqlDao batchUpdateSqlDao;
	private void initDatasourceSql() throws Exception{
		for(Map.Entry<String,Map<String,EntityDatasource>> entryProject : projectEntityDatasourceMap.entrySet()){
			String strProjectCode = entryProject.getKey();
			for(Map.Entry<String,EntityDatasource> entry : entryProject.getValue().entrySet()){
				if(entry.getValue().getInitSqlList() != null && entry.getValue().getInitSqlList().length > 0 && entry.getValue().getDataSourceName().equals(defaultDatasourceNameMap.get(strProjectCode))){
					try{
						for(String initSql : entry.getValue().getInitSqlList()){
							executeSqlDao.executeSql(strProjectCode,entry.getValue().getDataSourceName(), initSql);
						}
					}
					catch(Exception ex){
                        logger.warn("数据源:" + entry.getValue().getDataSourceName() + ",创建初始SQL异常,请联系项目管理员,如果表已存在,可忽略");
					}
					try{
						List<Map<String,Object>> datasourceSqlDataList = selectAllDao.query(strProjectCode,entry.getValue().getDataSourceName(),"Cooperation_DatasourceSql", "SELECT * FROM Cooperation_DatasourceSql");
						datasourceSqlDataList = StrUtils.listMapDataToUpperCase(datasourceSqlDataList);
						Map<String,Map<String,Object>> datasourceSqlDataMap = new HashMap<String,Map<String,Object>>();
						for(Map<String,Object> map : datasourceSqlDataList){
							datasourceSqlDataMap.put(map.get("STRSQLID").toString(), map);
						}
						for(Map.Entry<String, List<EntityDatasourceSql>> entrySql : entityDatasourceSqlListMap.entrySet()){
							int i = 0;
							for(EntityDatasourceSql entityDatasourceSql : entrySql.getValue()){
								String datasourceName = entityDatasourceSql.getDataSourceName();
								if(StrUtils.isEmpty(datasourceName)){
									datasourceName = defaultDatasourceNameMap.get(strProjectCode);
								}
								if(datasourceName.equals(entry.getValue().getDataSourceName())){
									int j = 0;
									for(String updateSql : entityDatasourceSql.getUpdateSqlList()){
										String strSqlId = entrySql.getKey() + fileNameSplit + i + fileNameSplit + j;
										
										boolean doDelete = false;
										boolean doInsert = false;
										if(!datasourceSqlDataMap.containsKey(strSqlId)){
											doInsert = true;
										}
										else{
											if(datasourceSqlDataMap.get(strSqlId).get("STRSTATE").equals("0")){
												doDelete = true;
												doInsert = true;
											}
										}
										
										if(doDelete){
											String deleteSql = "DELETE FROM Cooperation_DatasourceSql WHERE strSqlId = ?";
											updateSqlDao.updateSql(strProjectCode,datasourceName, deleteSql, new Object[]{strSqlId});
										}
										if(doInsert){
											String subSql = "";
											try{
												if(updateSql.toUpperCase().startsWith("TXTUTILSQL:")){
													String fileName = updateSql.substring(11);
													String strTaskCode = entrySql.getKey().substring(entrySql.getKey().indexOf(fileNameSplit) + 1);
													ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
												    ClassPathResource classPathResource = new ClassPathResource(getTaskClassPath(strProjectCode,strTaskCode,fileName));
													populator.addScript(classPathResource);
													DatabasePopulatorUtils.execute(populator, applicationContext.getBean(getDatasourceBeanName(strProjectCode, datasourceName), DataSource.class));
												}
												else if(updateSql.toUpperCase().startsWith("TXTSQL:")){
													String fileName = updateSql.substring(7);
													String sql = txtSqlMap.get(entrySql.getKey()).get(fileName);
													executeSqlDao.executeSql(strProjectCode,datasourceName, sql);
												}
												else if(updateSql.toUpperCase().startsWith("TXTLINESQL:")){
													String fileName = updateSql.substring(11);
													List<String> sqlList = txtLineSqlMap.get(entrySql.getKey()).get(fileName);
													for(String sql : sqlList){
														if(!sql.startsWith("--")){
															executeSqlDao.executeSql(strProjectCode,datasourceName, sql);
														}
													}
												}
												else{
													executeSqlDao.executeSql(strProjectCode,datasourceName, updateSql);
												}
												subSql = updateSql;
												if(subSql.length() > 1000){
													subSql = subSql.substring(0,1000) + "...";
												}
												updateSqlDao.updateSql(strProjectCode,datasourceName, "INSERT INTO Cooperation_DatasourceSql VALUES(?,?,?,?,?)",new Object[]{strSqlId,subSql,new Date(),"1","success"});
											}
											catch(Exception ex){
												String message = ex.getMessage();
												if(message!=null && message.length() > 1000){
													message = message.substring(0,1000) + "...";
												}
												updateSqlDao.updateSql(strProjectCode,datasourceName, "INSERT INTO Cooperation_DatasourceSql VALUES(?,?,?,?,?)",new Object[]{strSqlId,subSql,new Date(),"0",message});
											}
										}
										
										j++;
									}
									i++;
								}
							}
						}
					}
					catch(Exception ex){
                        logger.error("初始化数据源各任务SQL异常:"+entry.getValue().getDataSourceName()+"，检查异常逻辑是否完整，请联系服务管理员",ex);
					}
				}
			}
		}
	}

    public String getKey(Map<String,Object> dataMap, Set<String> idColumnSet){
        String key = "";
        for (String id : idColumnSet){
            key =  dataMap.get(id).toString() + "&";
        }
        key = key.substring(0,key.length() -1);
        return key;
    }

    private void initCachedata(){
        for(Map.Entry<String, EntityTable> entry : getEntityTableMap().entrySet()) {
            try {
                if (!StrUtils.isEmpty(entry.getValue().getCacheRule())) {
                    String strProjectCode = getStrProjectCodeFromTableKey(entry.getKey());
                    String datasourceName = getDatasourceNameFromTableKey(entry.getKey());
                    List<Map<String, Object>> listMap = selectAllDao.query(strProjectCode, datasourceName, entry.getValue().getTableName(), "SELECT * FROM " + entry.getValue().getTableName());
                    if(entry.getValue().getCacheRule().equals("table")){

                        cachedataMap.put(entry.getKey(), listMap);
                        Map<String, Map<String, Object>> keyMap = new HashMap<String, Map<String, Object>>();
                        for (Map<String, Object> dataMap : listMap) {
                            String key = getKey(dataMap, entry.getValue().getIdColumnSet());
                            keyMap.put(key, dataMap);
                        }
                        cachedataKeyMap.put(entry.getKey(), keyMap);
                    }
                    else if(entry.getValue().getCacheRule().startsWith("const:")){
                        Map<String, Map<String, String>> projectConstMap = null;
                        if(constMap.containsKey(strProjectCode)){
                            projectConstMap = constMap.get(strProjectCode);
                        }
                        else{
                            projectConstMap = new HashMap<String, Map<String, String>>();
                        }
                        String[] constRules = entry.getValue().getCacheRule().split(":")[1].split(",");
                        for(Map<String,Object> map : listMap){
                            String constNum = map.get(constRules[0]).toString();
                            Map<String,String> constDetailMap = projectConstMap.get(constNum);
                            if(constDetailMap == null){
                                constDetailMap = new HashMap<String,String>();
                            }
                            constDetailMap.put(map.get(constRules[1]).toString(), map.get(constRules[2]).toString());
                            projectConstMap.put(constNum, constDetailMap);
                        }
                        constMap.put(strProjectCode,projectConstMap);
                    }
                }
            }
            catch (Exception ex) {
                logger.error("初始化缓存异常:"+entry.getKey() + ",请联系服务管理员",ex);
            }
        }

        for(Map.Entry<String, EntityTable> entry : getEntityTableMap().entrySet()) {
            try {
                EntityTable entityTable = entry.getValue();
                CheckRuleTable checkRuleTable = getCheckRuleTableMap().get(entry.getKey());
                if(checkRuleTable != null){
                    for (EntityColumn entityColumn : entityTable.getEntityColumnList()) {
                        if (!StrUtils.isEmpty(entityColumn.getShowColumn())) {
                            String strProjectCode = getStrProjectCodeFromTableKey(entry.getKey());
                            if (entityColumn.getShowColumn().startsWith("table:")) {
                                String[] fields = entityColumn.getShowColumn().split(":");
                                String[] showFields = fields[1].split("\\.", -1);
                                String datasourceName = getDatasourceNameFromTableKey(entry.getKey());
                                String key = getEntityTableKey(strProjectCode,datasourceName,showFields[0]);
                                if (cachedataKeyMap.containsKey(key)) {
                                    if(!cachedataKeyNameMap.containsKey(key)){
                                        cachedataKeyNameMap.put(key,new HashMap<String, Map<String, String>>());
                                    }
                                    Map<String, String> valueMap = null;
                                    if(!cachedataKeyNameMap.get(key).containsKey(showFields[1])){
                                        valueMap = new HashMap<String, String>();
                                        for (Map.Entry<String, Map<String, Object>> keyEntry : cachedataKeyMap.get(key).entrySet()) {
                                            valueMap.put(keyEntry.getKey(), keyEntry.getValue().get(showFields[1]).toString());
                                        }
                                    }
                                    else{
                                        valueMap = cachedataKeyNameMap.get(key).get(showFields[1]);
                                    }
                                    checkRuleTable.getColumnValueMap().put(entityColumn.getColumnName(), valueMap);
                                    cachedataKeyNameMap.get(key).put(showFields[1], valueMap);
                                }
                            }
                            else if(entityColumn.getShowColumn().startsWith("const:")){
                                String[] fields = entityColumn.getShowColumn().split(":");
                                String showField = fields[1];
                                checkRuleTable.getColumnValueMap().put(entityColumn.getColumnName(),constMap.get(strProjectCode).get(showField));
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                logger.error("初始化缓存异常:"+entry.getKey()+ ",请联系服务管理员",ex);
            }
        }
    }

    private void refreshCacheData(String strProjectCode,String datasourceName,EntityDataParam entityData){
        String addKey = getEntityTableKey(strProjectCode,datasourceName,entityData.getTableName());
        EntityTable addEntityTable = getEntityTableMap().get(addKey);
        for(Map.Entry<String, EntityTable> entry : getEntityTableMap().entrySet()) {
            EntityTable entityTable = entry.getValue();
            CheckRuleTable checkRuleTable = getCheckRuleTableMap().get(entry.getKey());
            for (EntityColumn entityColumn : entityTable.getEntityColumnList()) {
                if (!StrUtils.isEmpty(entityColumn.getShowColumn())) {
                    if (entityColumn.getShowColumn().startsWith("table:" + entityData.getTableName())) {
                        String[] fields = entityColumn.getShowColumn().split(":");
                        String[] showFields = fields[1].split("\\.", -1);
                        String key = getEntityTableKey(strProjectCode,datasourceName,showFields[0]);
                        if (cachedataKeyMap.containsKey(key)) {
                            Map<String, String> valueMap = new HashMap<String, String>();
                            for (Map.Entry<String, Map<String, Object>> keyEntry : cachedataKeyMap.get(key).entrySet()) {
                                valueMap.put(keyEntry.getKey(), keyEntry.getValue().get(showFields[1]).toString());
                            }
                            if(checkRuleTable != null){
                                checkRuleTable.getColumnValueMap().put(entityColumn.getColumnName(), valueMap);
                            }
                            cachedataKeyNameMap.get(key).put(showFields[1], valueMap);
                        }
                    }
                    else if(entityColumn.getShowColumn().startsWith("const:")){
                        if(addEntityTable.getCacheRule().startsWith("const:")){
                            String[] fields = entityColumn.getShowColumn().split(":");
                            String showField = fields[1];
                            checkRuleTable.getColumnValueMap().put(entityColumn.getColumnName(),constMap.get(strProjectCode).get(showField));
                        }
                    }
                }
            }
        }
    }


    public void addOrUpdateCachedata(String strProjectCode,String datasourceName,EntityDataParam entityData){
	    try{
            String addKey = getEntityTableKey(strProjectCode,datasourceName,entityData.getTableName());
            EntityTable addEntityTable = getEntityTableMap().get(addKey);
            if(addEntityTable != null && !StrUtils.isEmpty(addEntityTable.getCacheRule())){
                if(addEntityTable.getCacheRule().equals("table")){
                    cachedataKeyMap.get(addKey).put(getKey(entityData.getData(), addEntityTable.getIdColumnSet()),entityData.getData());
                    cachedataMap.get(addKey).clear();
                    cachedataMap.get(addKey).addAll(cachedataKeyMap.get(addKey).values());
                }
                else if(addEntityTable.getCacheRule().startsWith("const:")){
                    String[] constRules = addEntityTable.getCacheRule().split(":")[1].split(",");
                    constMap.get(strProjectCode).get(entityData.getData().get(constRules[0])).put(entityData.getData().get(constRules[1]).toString(),entityData.getData().get(constRules[2]).toString());
                }
                refreshCacheData(strProjectCode,datasourceName,entityData);
            }
        }
        catch (Exception ex) {
            logger.error("添加或更新缓存异常:"+entityData.getTableName()+ ",请联系服务管理员",ex);
        }
    }

    public void deleteCachedata(String strProjectCode,String datasourceName,EntityDataParam entityData){
        try{
            String deleteKey = getEntityTableKey(strProjectCode,datasourceName,entityData.getTableName());
            EntityTable deleteEntityTable = getEntityTableMap().get(deleteKey);
            if(deleteEntityTable != null && !StrUtils.isEmpty(deleteEntityTable.getCacheRule())){
                if(deleteEntityTable.getCacheRule().equals("table")){
                    cachedataKeyMap.get(deleteKey).remove(getKey(entityData.getData(), deleteEntityTable.getIdColumnSet()));
                    cachedataMap.get(deleteKey).clear();
                    cachedataMap.get(deleteKey).addAll(cachedataKeyMap.get(deleteKey).values());
                }
                else if(deleteEntityTable.getCacheRule().startsWith("const:")){
                    String[] constRules = deleteEntityTable.getCacheRule().split(":")[1].split(",");
                    constMap.get(strProjectCode).get(entityData.getData().get(constRules[0])).remove(entityData.getData().get(constRules[1]).toString());
                }
                refreshCacheData(strProjectCode,datasourceName,entityData);
            }
        }
        catch (Exception ex) {
            logger.error("删除缓存异常:"+entityData.getTableName()+ ",请联系服务管理员",ex);
        }
    }

}
