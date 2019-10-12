package net.zhangqiu.project.cooperation.service;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityDatasource;
import net.zhangqiu.service.database.entity.EntityDatasourceFile;
import net.zhangqiu.service.database.entity.EntityDatasourceSql;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

@Service
public class DatasourceFileSaveService {

    private static Logger logger = LoggerFactory.getLogger(DatasourceFileSaveService.class);

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	EntityContext entityContext;
	@Autowired
	UpdateSqlDao updateSqlDao;
	@Autowired
	ApplicationContext applicationContext;
	
	public String handleService(String indentification,String strProjectCode,byte[] fileByte,String encoding) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(StrUtils.isEmpty(strProjectCode)){
				throw new Exception("项目编号不能为空");
			}
			if(fileByte == null || fileByte.length == 0){
				throw new Exception("文件不能为空");
			}

            if(StrUtils.isEmpty(encoding)){
                encoding = environmentContext.getEncoding();
            }

			String jsonString = FileUtils.readZipFile(entityContext.getDatasourceFileName(strProjectCode), fileByte, encoding);
			CommonServiceResult datasourceResult = saveDatasourceFile(strProjectCode,jsonString);
			if(!datasourceResult.isSuccess()){
				return JsonUtils.objectToString(datasourceResult);
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
	
	public String delete(String indentification,String strProjectCode,String encoding) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(StrUtils.isEmpty(strProjectCode)){
				throw new Exception("项目编号不能为空");
			}

			File entityProjectFiles = new File(environmentContext.getResourcesPath());
			if(entityProjectFiles.exists()){
				File[] projectFiles = entityProjectFiles.listFiles();
				for(File projectFile : projectFiles){
					if(projectFile.isDirectory() && projectFile.getName().equals(strProjectCode)){
						for(File datasourceFile : projectFile.listFiles()){
							if(!datasourceFile.isDirectory()){
								String datasourceFileName = entityContext.getDatasourceFileName(strProjectCode);
								if(datasourceFile.getName().equals(datasourceFileName)){
									if(datasourceFile.exists()){
										datasourceFile.delete();
									}
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
	
	public String initTable(String indentification,String strProjectCode,String encoding) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
			if(StrUtils.isEmpty(strProjectCode)){
				throw new Exception("项目编号不能为空");
			}

			String deleteSql = "DELETE FROM Cooperation_DatasourceSql WHERE strSqlId like ?";
			updateSqlDao.updateSql(strProjectCode,entityContext.getDefaultDatasourceNameMap().get(strProjectCode), deleteSql, new Object[]{strProjectCode + "%"});
			
			Map<String,List<EntityDatasourceSql>> entityDatasourceSqlListMap = entityContext.getEntityDatasourceSqlListMap();
			
			for(Map.Entry<String,List<EntityDatasourceSql>> entry : entityDatasourceSqlListMap.entrySet()){
				if(entry.getKey().startsWith(strProjectCode)){
					List<EntityDatasourceSql> entityDatasourceSqlList = entry.getValue();
					for(EntityDatasourceSql entityDatasourceSql :entityDatasourceSqlList){
						if(StrUtils.isEmpty(entityDatasourceSql.getDataSourceName()) || entityDatasourceSql.getDataSourceName().equals(entityContext.getDefaultDatasourceNameMap().get(strProjectCode))){
							for(String initSql : entityDatasourceSql.getInitSqlList()){
								if(initSql.toUpperCase().startsWith("TXTUTILSQL:")){
									String fileName = initSql.substring(11);
									String strTaskCode = entry.getKey().substring(entry.getKey().indexOf("_") + 1);
									ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
								    ClassPathResource classPathResource = new ClassPathResource(strProjectCode + File.separator + strTaskCode + File.separator + fileName);
									populator.addScript(classPathResource);
									DatabasePopulatorUtils.execute(populator, applicationContext.getBean(entityContext.getDatasourceBeanName(strProjectCode, entityContext.getDefaultDatasourceNameMap().get(strProjectCode)), DataSource.class));
								}
								else if(initSql.toUpperCase().startsWith("TXTSQL:")){
									String fileName = initSql.substring(7);
									String sql = entityContext.getTxtSqlMap().get(entry.getKey()).get(fileName);
									updateSqlDao.updateSql(strProjectCode,entityContext.getDefaultDatasourceNameMap().get(strProjectCode), sql);
								}
								else if(initSql.toUpperCase().startsWith("TXTLINESQL:")){
									String fileName = initSql.substring(11);
									List<String> sqlList = entityContext.getTxtLineSqlMap().get(entry.getKey()).get(fileName);
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
	
	private CommonServiceResult saveDatasourceFile(String strProjectCode,String jsonString) throws Exception{
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		try{
		    if(!StrUtils.isEmpty(jsonString)){
                EntityDatasourceFile entityDatasourceFile = EntityUtils.convertToEntityDatasourceFile(jsonString);
                for(EntityDatasource entityDatasource : entityDatasourceFile.getEntityDatasourceList()){
                    String key = entityDatasource.getDataSourceName();
                    if(StrUtils.isEmpty(entityDatasource.getDataSourceName())){
                        throw new Exception("数据源名不能为空");
                    }
                    if(StrUtils.isEmpty(entityDatasource.getUrl())){
                        throw new Exception("连接不能为空，" + key);
                    }
                    if(StrUtils.isEmpty(entityDatasource.getUserName())){
                        throw new Exception("用户名不能为空，" + key);
                    }
                    if(StrUtils.isEmpty(entityDatasource.getPassword())){
                        throw new Exception("密码不能为空，" + key);
                    }
                    if(StrUtils.isEmpty(entityDatasource.getDriverClassName())){
                        throw new Exception("驱动不能为空，" + key);
                    }
                    if(StrUtils.isEmpty(entityDatasource.getStrDatabaseType())){
                        throw new Exception("数据库类型不能为空，" + key);
                    }
                    if(!EntityUtils.isSupportDatabaseType(entityDatasource.getStrDatabaseType())){
                        throw new Exception("不支持数据库类型，" + entityDatasource.getStrDatabaseType());
                    }
                    //String fileString = JsonUtils.objectToString(entityDatasourceFile);
                    FileUtils.saveFile(entityContext.getDatasourceFilePath(strProjectCode), jsonString, environmentContext.getEncoding());
                    commonServiceResult.setSuccess(true);
                    commonServiceResult.setMessage("success");
                }
            }
		}
		catch(Exception ex){
            logger.error("",ex);
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
		}
		return commonServiceResult;
	}
	
	public String saveDatasourceFile(String indentification,String strProjectCode,String jsonString,String encoding) throws Exception{
		if(StrUtils.isEmpty(encoding)){
			encoding = environmentContext.getEncoding();
		}
		jsonString = URLDecoder.decode(jsonString, encoding);
		return JsonUtils.objectToString(saveDatasourceFile(strProjectCode,jsonString));
	}
}
