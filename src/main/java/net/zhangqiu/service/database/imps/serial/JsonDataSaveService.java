package net.zhangqiu.service.database.imps.serial;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.result.ColumnResult;
import net.zhangqiu.service.database.dao.SelectByIdDao;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.SnowFlakeGenerator;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.entity.result.SaveOrUpdateResult;
import net.zhangqiu.service.database.interfaces.CheckHandler;
import net.zhangqiu.utils.StrUtils;

@Service
public class JsonDataSaveService extends JsonDataSaveOrUpdateTemplateService{

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    UpdateSqlDao updateSqlDao;
	@Autowired
    SelectByIdDao selectByIdDao;
	@Autowired
    EntityContext entityContext;
	@Autowired
	@Qualifier("jsonDataCheckService")
	CheckHandler checkHandler;
	@Autowired
    SnowFlakeGenerator snowFlakeGenerator;

	@Override
	public SaveOrUpdateResult logicService(String strProjectCode, String datasourceName, EntityDataParam entityData) throws Exception {
		
		SaveOrUpdateResult saveOrUpdateResult = super.getCheckResult(strProjectCode,datasourceName, entityData);
		if(!saveOrUpdateResult.isSuccess()){
			return saveOrUpdateResult;
		}
		String key = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
		EntityTable entityTable  = entityContext.getEntityTableMap().get(key);
		if(entityTable == null){
			saveOrUpdateResult.setSuccess(false);
			saveOrUpdateResult.setMessage("表名Key（"+key+"）不存在，请确认是否在*_table.json文件中定义，区分大小写");
			return saveOrUpdateResult;
		}
		if(entityTable.isAutoGenerateId()){
			for(String string : entityTable.getIdColumnSet()){
				long id = snowFlakeGenerator.nextId();
				entityData.getData().put(string, id);
				saveOrUpdateResult.getDataResult().getData().put(string, String.valueOf(id));
			}
		}
		else{
			String selectSql = entityContext.getEntityTableSelectByIdSql(strProjectCode, datasourceName, entityData);
			Object[] objects = entityContext.getEntityTableIdValue(strProjectCode,datasourceName, entityData);
			Map<String,Object> data = (Map<String,Object>)selectByIdDao.query(strProjectCode,datasourceName,entityData.getTableName(), selectSql, objects);
			if(data.keySet().size() > 0){
				if(entityTable.getIdColumnSet().size() == 1){
					String columnName = entityTable.getIdColumnSet().toArray()[0].toString();
					ColumnResult columnResult = new ColumnResult();
					columnResult.setCheck(false);
					columnResult.setDescription("主键重复");
					saveOrUpdateResult.setMessage("主键重复");
					saveOrUpdateResult.getDataResult().getColumnResultMap().put(columnName, columnResult);
				}
				else{
					ColumnResult columnResult = new ColumnResult();
					columnResult.setCheck(false);
					String description = "";
					for(String columnName : entityTable.getIdColumnSet()){
						for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
							if(entityColumn.getColumnName().equals(columnName)){
								if(!StrUtils.isEmpty(entityColumn.getColumnDescription())){
									description += entityColumn.getColumnDescription()+ "，";
								}
								else{
									description += entityColumn.getColumnName() + "，";
								}
								break;
							}
						}
					}
					if(!StrUtils.isEmpty(description)){
						description = description.substring(0, description.length() -1);
					}
					columnResult.setDescription("主键（"+description+"）重复");
					saveOrUpdateResult.setMessage("主键（"+description+"）重复");
					for(String targetColumnName : entityTable.getIdColumnSet()){
						saveOrUpdateResult.getDataResult().getColumnResultMap().put(targetColumnName, columnResult);
					}
				}
				saveOrUpdateResult.setSuccess(false);
				return saveOrUpdateResult;
			}
		}
		if(entityTable.getLogicIdColumnSet() != null && entityTable.getLogicIdColumnSet().size() > 0){
			String strCondtion = " WHERE 1=1";
			for(String columnName : entityTable.getLogicIdColumnSet()){
				strCondtion += " AND " + columnName + "=?";
			}
			Object[] objects = new Object[entityTable.getLogicIdColumnSet().size()];
			int i=0;
			for(String columnName : entityTable.getLogicIdColumnSet()){
				objects[i] = entityData.getData().get(columnName);
				i++;
			}
			String selectSql = "SELECT * FROM " + entityTable.getTableName() + strCondtion;
			Map<String,Object> data = (Map<String,Object>)selectByIdDao.query(strProjectCode,datasourceName,entityData.getTableName(), selectSql, objects);
			if(data.keySet().size() > 0){
				if(entityTable.getLogicIdColumnSet().size() == 1){
					String columnName = entityTable.getLogicIdColumnSet().toArray()[0].toString();
					ColumnResult columnResult = new ColumnResult();
					columnResult.setCheck(false);
					columnResult.setDescription("逻辑主键重复");
					saveOrUpdateResult.setMessage("逻辑主键重复");
					saveOrUpdateResult.getDataResult().getColumnResultMap().put(columnName, columnResult);
				}
				else{
					ColumnResult columnResult = new ColumnResult();
					columnResult.setCheck(false);
					String description = "";
					for(String columnName : entityTable.getLogicIdColumnSet()){
						for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
							if(entityColumn.getColumnName().equals(columnName)){
								if(!StrUtils.isEmpty(entityColumn.getColumnDescription())){
									description += entityColumn.getColumnDescription()+ "，";
								}
								else{
									description += entityColumn.getColumnName() + "，";
								}
								break;
							}
						}
					}
					if(!StrUtils.isEmpty(description)){
						description = description.substring(0, description.length() -1);
					}
					columnResult.setDescription("逻辑主键（"+description+"）重复");
					saveOrUpdateResult.setMessage("逻辑主键（"+description+"）重复");
					for(String targetColumnName : entityTable.getLogicIdColumnSet()){
						saveOrUpdateResult.getDataResult().getColumnResultMap().put(targetColumnName, columnResult);
					}
				}
				saveOrUpdateResult.setSuccess(false);
				return saveOrUpdateResult;
			}
		}
		
		String sql = entityContext.getEntityTableInsertSql(strProjectCode,datasourceName, entityData);
		Object[] objects = entityContext.getEntityTableInsertValue(strProjectCode,datasourceName, entityData);
		updateSqlDao.updateSql(strProjectCode,datasourceName, sql, objects);
		saveOrUpdateResult.setSuccess(true);
		saveOrUpdateResult.setMessage("success");
        entityContext.addOrUpdateCachedata(strProjectCode,datasourceName,entityData);
		return saveOrUpdateResult;
	}
}
