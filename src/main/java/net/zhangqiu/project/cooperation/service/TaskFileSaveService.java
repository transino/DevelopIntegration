package net.zhangqiu.project.cooperation.service;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.CheckRule;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.CheckRuleTableFile;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityDatasourceSql;
import net.zhangqiu.service.database.entity.EntityDatasourceSqlFile;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.EntityTableFile;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

@Service
public class TaskFileSaveService{

    private static Logger logger = LoggerFactory.getLogger(TaskFileSaveService.class);
	
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    EntityContext entityContext;
	@Autowired
    UpdateSqlDao updateSqlDao;
	@Autowired
	ApplicationContext applicationContext;

	public String handleService(String indentification,String strProjectCode,String strTaskCode,byte[] fileByte,String encoding){
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(StrUtils.isEmpty(strProjectCode)){
				throw new Exception("项目编号不能为空");
			}
			if(StrUtils.isEmpty(strTaskCode)){
				throw new Exception("任务编号不能为空");
			}
			if(fileByte == null || fileByte.length == 0){
				throw new Exception("文件不能为空");
			}

			if(StrUtils.isEmpty(encoding)){
                encoding = environmentContext.getEncoding();
            }
			
			String jsonTableString = FileUtils.readZipFile(entityContext.getTaskTableFileName(strTaskCode), fileByte, encoding);
			CommonServiceResult tableResult = saveTableFile(strProjectCode,strTaskCode,jsonTableString);
			if(!tableResult.isSuccess()){
				return JsonUtils.objectToString(tableResult);
			}
			
			String jsonDatasourceSqlString = FileUtils.readZipFile(entityContext.getTaskDatasourceSqlFileName(strTaskCode), fileByte, encoding);
			CommonServiceResult datasourceResult = saveDatasourceSqlFile(strProjectCode,strTaskCode,jsonDatasourceSqlString);
			if(!datasourceResult.isSuccess()){
				return JsonUtils.objectToString(datasourceResult);
			}

			String jsonTableCheckString = FileUtils.readZipFile(entityContext.getTaskTableCheckFileName(strTaskCode), fileByte, encoding);
			CommonServiceResult checkRuleResult = saveCheckRuleTableFile(strProjectCode,strTaskCode,jsonTableCheckString);
			if(!checkRuleResult.isSuccess()){
				return JsonUtils.objectToString(checkRuleResult);
			}
			
			Map<String,String> txtSqlStringMap= FileUtils.readListZipFile("_sql.sql", fileByte, encoding);
			for(Map.Entry<String, String> entry : txtSqlStringMap.entrySet()){
				CommonServiceResult txtSqlResult = saveTxtSqlFile(strProjectCode,strTaskCode,entry.getKey(),entry.getValue());
				if(!txtSqlResult.isSuccess()){
					return JsonUtils.objectToString(txtSqlResult);
				}
			}
			
			Map<String,String> txtLineSqlStringMap = FileUtils.readListZipFile("_linesql.sql", fileByte, encoding);
			for(Map.Entry<String, String> entry : txtLineSqlStringMap.entrySet()){
				CommonServiceResult txtLineSqlResult = saveTxtLineSqlFile(strProjectCode,strTaskCode,entry.getKey(),entry.getValue());
				if(!txtLineSqlResult.isSuccess()){
					return JsonUtils.objectToString(txtLineSqlResult);
				}
			}
			
			entityContext.init();
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return JsonUtils.objectToString(commonServiceResult);
	}
	
	public String delete(String indentification,String strProjectCode,String strTaskCode,String encoding) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(StrUtils.isEmpty(strProjectCode)){
				throw new Exception("项目编号不能为空");
			}
			if(StrUtils.isEmpty(strTaskCode)){
				throw new Exception("任务编号不能为空");
			}

			File entityProjectFiles = new File(environmentContext.getResourcesPath());
			if(entityProjectFiles.exists()){
				File[] projectFiles = entityProjectFiles.listFiles();
				for(File projectFile : projectFiles){
					if(projectFile.isDirectory() && projectFile.getName().equals(strProjectCode)){
						for(File datasourceFile : projectFile.listFiles()){
							if(datasourceFile.isDirectory() && datasourceFile.getName().equals(strTaskCode)){
								for(File taskFile : datasourceFile.listFiles()){
									if(taskFile.exists()){
										taskFile.delete();
									}
								}
								if(datasourceFile.exists()){
									datasourceFile.delete();
								}
							}
						}
					}
				}
			}
			
			entityContext.init();
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return JsonUtils.objectToString(commonServiceResult);
	}
	
	public String initTable(String indentification,String strProjectCode,String strTaskCode,String encoding) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(StrUtils.isEmpty(strProjectCode)){
				throw new Exception("项目编号不能为空");
			}
			if(StrUtils.isEmpty(strTaskCode)){
				throw new Exception("任务编号不能为空");
			}
			
			String key = entityContext.getTaskKey(strProjectCode, strTaskCode);
			String deleteSql = "DELETE FROM Cooperation_DatasourceSql WHERE strSqlId like ?";
			updateSqlDao.updateSql(strProjectCode,entityContext.getDefaultDatasourceNameMap().get(strProjectCode), deleteSql, new Object[]{key + "%"});
			
			Map<String,List<EntityDatasourceSql>> entityDatasourceSqlListMap = entityContext.getEntityDatasourceSqlListMap();
			List<EntityDatasourceSql> entityDatasourceSqlList = entityDatasourceSqlListMap.get(key);

			for(EntityDatasourceSql entityDatasourceSql :entityDatasourceSqlList){
				if(StrUtils.isEmpty(entityDatasourceSql.getDataSourceName()) || entityDatasourceSql.getDataSourceName().equals(entityContext.getDefaultDatasourceNameMap().get(strProjectCode))){
					for(String initSql : entityDatasourceSql.getInitSqlList()){
						if(initSql.toUpperCase().startsWith("TXTUTILSQL:")){
							String fileName = initSql.substring(11);
							ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
						    ClassPathResource classPathResource = new ClassPathResource(strProjectCode + File.separator + strTaskCode + File.separator + fileName);
							populator.addScript(classPathResource);
							DatabasePopulatorUtils.execute(populator, applicationContext.getBean(entityContext.getDatasourceBeanName(strProjectCode, entityContext.getDefaultDatasourceNameMap().get(strProjectCode)), DataSource.class));
						}
						else if(initSql.toUpperCase().startsWith("TXTSQL:")){
							String fileName = initSql.substring(7);
							String sql = entityContext.getTxtSqlMap().get(entityContext.getTaskKey(strProjectCode, strTaskCode)).get(fileName);
							updateSqlDao.updateSql(strProjectCode,entityContext.getDefaultDatasourceNameMap().get(strProjectCode), sql);
						}
						else if(initSql.toUpperCase().startsWith("TXTLINESQL:")){
							String fileName = initSql.substring(11);
							List<String> sqlList = entityContext.getTxtLineSqlMap().get(entityContext.getTaskKey(strProjectCode, strTaskCode)).get(fileName);
							for(String sql : sqlList){
								updateSqlDao.updateSql(strProjectCode,entityContext.getDefaultDatasourceNameMap().get(strProjectCode), sql);
							}
						}
						else{
							updateSqlDao.updateSql(strProjectCode,entityContext.getDefaultDatasourceNameMap().get(strProjectCode), initSql);
						}
					}
				}
			}

			entityContext.init();
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return JsonUtils.objectToString(commonServiceResult);
	}
	
	private CommonServiceResult saveTableFile(String strProjectCode, String strTaskCode, String jsonTableString) throws Exception{
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(!StrUtils.isEmpty(jsonTableString)){
				EntityTableFile entityTableFile = EntityUtils.convertToEntityTableFile(jsonTableString);
				for(EntityTable entityTable : entityTableFile.getEntityTableList()){
					String key = entityContext.getEntityTableKey(strProjectCode,entityTable.getDataSourceName(),entityTable.getTableName());
					if(StrUtils.isEmpty(entityTable.getTableName())){
						throw new Exception("表名不能为空");
					}
					if(entityTable.getEntityColumnList().size() == 0){
						throw new Exception("表中没有字段，"+ key);
					}
					Set<String> columnSet = new HashSet<String>();
					for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
						if(StrUtils.isEmpty(entityColumn.getColumnName())){
							throw new Exception("字段名不能为空，" + key);
						}
						if(StrUtils.isEmpty(entityColumn.getColumnType())){
							throw new Exception("字段类型不能为空，" + key + "：" + entityColumn.getColumnName());
						}
						if(!EntityUtils.isSupportColumnType(entityColumn.getColumnType())){
							throw new Exception("不支持字段类型，" + key + "：" + entityColumn.getColumnName() + "：" + entityColumn.getColumnType());
						}
						if(columnSet.contains(entityColumn.getColumnName())){
							throw new Exception("字段名重复，" + key + "：" + entityColumn.getColumnName());
						}
						else{
							columnSet.add(entityColumn.getColumnName());
						}
					}
				}
				//String fileTableString = JsonUtils.objectToString(entityTableFile);
				FileUtils.saveFile(entityContext.getTaskTableFilePath(strProjectCode, strTaskCode), jsonTableString,environmentContext.getEncoding());
			}
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return commonServiceResult;
	}
	
	public String saveTableFile(String indentification,String strProjectCode,String strTaskCode,String jsonTableString,String encoding) throws Exception{
		if(StrUtils.isEmpty(encoding)){
			encoding = environmentContext.getEncoding();
		}
		jsonTableString = URLDecoder.decode(jsonTableString, encoding);
		return JsonUtils.objectToString(saveTableFile(strProjectCode,strTaskCode,jsonTableString));
	}
	
	private CommonServiceResult saveDatasourceSqlFile(String strProjectCode, String strTaskCode, String jsonDatasourceSqlString) throws Exception{
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(!StrUtils.isEmpty(jsonDatasourceSqlString)){
				EntityDatasourceSqlFile entityDatasourceSqlFile = EntityUtils.convertToEntityDatasourceSqlFile(jsonDatasourceSqlString);
				if(entityDatasourceSqlFile.getEntityDatasourceSqlList().size() == 0){
					throw new Exception("没有数据源SQL");
				}
				for(EntityDatasourceSql entityDatasourceSql : entityDatasourceSqlFile.getEntityDatasourceSqlList()){
					if(StrUtils.isEmpty(entityDatasourceSql.getDataSourceName())){
						throw new Exception("数据源不能为空");
					}
					if(entityDatasourceSql.getInitSqlList() == null || entityDatasourceSql.getInitSqlList().length == 0){
						throw new Exception("初始SQL不能为空");
					}
					if(entityDatasourceSql.getUpdateSqlList() == null || entityDatasourceSql.getUpdateSqlList().length == 0){
						throw new Exception("更新SQL不能为空");
					}
				}

				//String fileDatasourceSqlString = JsonUtils.objectToString(entityDatasourceSqlFile);
				FileUtils.saveFile(entityContext.getTaskDatasourceSqlFilePath(strProjectCode, strTaskCode), jsonDatasourceSqlString,environmentContext.getEncoding());
			}
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return commonServiceResult;
	}
	
	public String saveDatasourceSqlFile(String indentification,String strProjectCode,String strTaskCode,String jsonDatasourceSqlString,String encoding) throws Exception{
		if(StrUtils.isEmpty(encoding)){
			encoding = environmentContext.getEncoding();
		}
		jsonDatasourceSqlString = URLDecoder.decode(jsonDatasourceSqlString, encoding);
		return JsonUtils.objectToString(saveDatasourceSqlFile(strProjectCode,strTaskCode,jsonDatasourceSqlString));
	}
	
	private CommonServiceResult saveCheckRuleTableFile(String strProjectCode, String strTaskCode, String jsonTableCheckString) throws Exception{
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(!StrUtils.isEmpty(jsonTableCheckString)){
				CheckRuleTableFile checkRuleTableFile = EntityUtils.convertToCheckRuleTableFile(jsonTableCheckString);
				if(checkRuleTableFile.getCheckRuleTableList().size() == 0){
					throw new Exception("没有表校验规则");
				}
				for(CheckRuleTable checkRuleTable : checkRuleTableFile.getCheckRuleTableList()){
					String key = checkRuleTable.getTableName();
					if(StrUtils.isEmpty(key)){
						throw new Exception("表名不能为空");
					}
					if(checkRuleTable.getCheckRuleList().size() == 0){
						throw new Exception("没有校验规则，"+ key);
					}
					for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
						if(StrUtils.isEmpty(checkRule.getRule())){
							throw new Exception("规则不能为空，"+ key);
						}
					}
				}

				//String fileCheckString = JsonUtils.objectToString(checkRuleTableFile);
				FileUtils.saveFile(entityContext.getTaskTableCheckFilePath(strProjectCode, strTaskCode), jsonTableCheckString,environmentContext.getEncoding());
			}
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return commonServiceResult;
	}
	
	public String saveCheckRuleTableFile(String indentification,String strProjectCode,String strTaskCode,String jsonTableCheckString,String encoding) throws Exception{
		if(StrUtils.isEmpty(encoding)){
			encoding = environmentContext.getEncoding();
		}
		jsonTableCheckString = URLDecoder.decode(jsonTableCheckString, encoding);
		return JsonUtils.objectToString(saveCheckRuleTableFile(strProjectCode,strTaskCode,jsonTableCheckString));
	}
	
	private CommonServiceResult saveTxtSqlFile(String strProjectCode, String strTaskCode, String fileName, String txtSqlString) throws Exception{
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(!StrUtils.isEmpty(txtSqlString)){
				FileUtils.saveFile(entityContext.getTaskTxtPath(strProjectCode, strTaskCode,fileName), txtSqlString,environmentContext.getEncoding());
			}
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
		    logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return commonServiceResult;
	}
	
	private CommonServiceResult saveTxtLineSqlFile(String strProjectCode, String strTaskCode, String fileName, String txtLineSqlString) throws Exception{
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(!StrUtils.isEmpty(txtLineSqlString)){
				FileUtils.saveFile(entityContext.getTaskTxtPath(strProjectCode, strTaskCode, fileName), txtLineSqlString,environmentContext.getEncoding());
			}
			commonServiceResult.setSuccess(true);
			commonServiceResult.setMessage("success");
		}
		catch(Exception ex){
		    logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return commonServiceResult;
	}
}
