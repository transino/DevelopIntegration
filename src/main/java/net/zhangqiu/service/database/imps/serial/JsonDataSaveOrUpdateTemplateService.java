package net.zhangqiu.service.database.imps.serial;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.entity.result.SaveOrUpdateResult;

import net.zhangqiu.service.database.interfaces.CheckHandler;

@Service
public abstract class JsonDataSaveOrUpdateTemplateService extends EntityDataTemplateService {
	
	@Autowired
    EntityContext entityContext;
	@Autowired
	@Qualifier("jsonDataCheckService")
	CheckHandler checkHandler;
	
	private void initEntityDataParam(String datasourceName, EntityDataParam entityData){
		
	}
	
	protected SaveOrUpdateResult getCheckResult(String strProjectCode, String datasourceName, EntityDataParam entityData) throws Exception{
		initEntityDataParam(datasourceName,entityData);
		SaveOrUpdateResult saveOrUpdateResult = new SaveOrUpdateResult();
		entityData.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, entityData));
		String entityTableKey = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityData.getCheckTableName());
		CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(entityTableKey);
		
		if(checkRuleTable != null){
			DataResult dataResult = checkHandler.check(checkRuleTable, entityData.getData());
			if(!dataResult.isCheck()){
				saveOrUpdateResult.setSuccess(false);
				saveOrUpdateResult.setMessage("error");
				saveOrUpdateResult.setDataResult(dataResult);
			}
			else{
				saveOrUpdateResult.setSuccess(true);
				saveOrUpdateResult.setMessage("success");
				saveOrUpdateResult.setDataResult(dataResult);
			}
		}
		else{
			DataResult dataResult = new DataResult();
			Map<String,String> data = new HashMap<String,String>();
			for(Map.Entry<String,Object> entry : entityData.getData().entrySet()){
				if(entry.getValue() == null){
					data.put(entry.getKey(), null);
				}
				else{
					data.put(entry.getKey(), entry.getValue().toString());
				}
				
			}
			dataResult.setData(data);
			saveOrUpdateResult.setDataResult(dataResult);
		}
		
		EntityTable entityTable  = entityContext.getEntityTableMap().get(entityTableKey);
		if(entityTable != null){
			for(EntityColumn entityColumn : entityTable.getEntityColumnList()){
				if(!saveOrUpdateResult.getDataResult().getColumnResultMap().containsKey(entityColumn.getColumnName())){
					saveOrUpdateResult.getDataResult().getColumnResultMap().put(entityColumn.getColumnName(), null);
				}
			}
		}
		
		return saveOrUpdateResult;
	}
}
