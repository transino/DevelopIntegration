package net.zhangqiu.service.database.imps.serial;

import java.util.Map;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.SelectByConditionDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityDatasource;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonDataSelectByConditionService extends EntityDataTemplateService{
	@Autowired
	EnvironmentContext environmentContext;

	@Autowired
    EntityContext entityContext;
	
	@Autowired
	SelectByConditionDao selectByConditionDao;

	@Override
	public CommonServiceResult logicService(String strProjectCode,String datasourceName,EntityDataParam entityData) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		String sql = entityContext.getEntityTableSelectAllSql(strProjectCode,datasourceName,entityData);
		if(StrUtils.isEmpty(sql)){
			String key = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage("表名Key（"+key+"）不存在，请确认是否在*_table.json文件中定义，区分大小写");
			return commonServiceResult;
		}
		if(!StrUtils.isEmpty(entityData.getStrCondition())){
			sql += " WHERE " + entityData.getStrCondition();
		}
		String byPage = "";
		EntityDatasource entityDatasource = entityContext.getProjectEntityDatasourceMap().get(strProjectCode).get(datasourceName);
		Map<String,Object> map = entityData.getData();
		boolean hasCONST_LIMIT = false;
		if(map != null){
			for(Map.Entry<String, Object> entry : map.entrySet()){
				if(entry.getKey().toUpperCase().equals("CONST_LIMIT")){
					hasCONST_LIMIT = true;
					break;
				}
			}
		}
		
		int objectsSize = 0;
		if(entityData.getData() != null){
			objectsSize = entityData.getData().size();
		}
		if(hasCONST_LIMIT){
			objectsSize = objectsSize -1;
		}
		Object[] objects = new Object[objectsSize];
		
		if(map != null){
			for(Map.Entry<String, Object> entry : map.entrySet()){
				if(!entry.getKey().toUpperCase().equals("CONST_LIMIT")){
					int indexOf = entry.getKey().indexOf("_");
					int index = Integer.valueOf(entry.getKey().substring(0, indexOf));
					objects[index] = entityContext.convertToType(entry.getValue(),entry.getKey().substring(indexOf+1));
				}
				else{
					if(entityDatasource.getStrDatabaseType().equals(EntityUtils.Database_Mysql)){
						byPage = " LIMIT " + entry.getValue();
						sql += byPage;
					}
					else if(entityDatasource.getStrDatabaseType().equals(EntityUtils.Database_Oracle)){
						Integer startIndex;
						Integer endIndex;
						if(entry.getValue().toString().indexOf(",") > -1){
							startIndex = Integer.valueOf(entry.getValue().toString().split(",")[0]);
							endIndex = Integer.valueOf(entry.getValue().toString().split(",")[0]) + Integer.valueOf(entry.getValue().toString().split(",")[1]);
						}
						else{
							startIndex = 0;
							endIndex = Integer.valueOf(entry.getValue().toString());
						}
						sql = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM ("+sql+") A WHERE ROWNUM <= "+endIndex+") WHERE RN > " + startIndex.toString();
					    //strSql="SELECT "+strSelectField+" FROM ( SELECT A.*,ROWNUM ROWNUMBER FROM (SELECT "+strSelectField+" FROM "+strViewName+") A ) WHERE ROWNUMBER BETWEEN "+(ShowParamManager.getFirstResult()+1) +" AND "+(ShowParamManager.getFirstResult()+PageSize)+"";
					}
					else if(entityDatasource.getStrDatabaseType().equals(EntityUtils.Database_Sqlserver)){
						Integer startIndex;
						Integer endIndex;
						if(entry.getValue().toString().indexOf(",") > -1){
							startIndex = Integer.valueOf(entry.getValue().toString().split(",")[0]);
							endIndex = Integer.valueOf(entry.getValue().toString().split(",")[0]) + Integer.valueOf(entry.getValue().toString().split(",")[1]);
						}
						else{
							startIndex = 0;
							endIndex = Integer.valueOf(entry.getValue().toString());
						}
						if(entry.getValue().toString().indexOf(",") > -1){
							String entityTableKey = entityContext.getEntityTableKey(strProjectCode, datasourceName, entityData.getTableName());
							String id = entityContext.getEntityTableMap().get(entityTableKey).getIdColumnSet().toArray()[0].toString();
							sql = "SELECT TOP "+Integer.valueOf(entry.getValue().toString().split(",")[1])+" * FROM "+entityData.getTableName()+" WHERE "+id+" NOT IN (SELECT TOP "+startIndex+" "+id+" FROM "+entityData.getTableName()+" WHERE "+entityData.getStrCondition()+") AND "+ entityData.getStrCondition();
							//sql = "SELECT * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY " + id + " ASC) AS 'ROWNUMBER', * FROM " + entityData.getTableName() + " WHERE " + entityData.getStrCondition() + ") AS View_TableName WHERE ROWNUMBER BETWEEN "+ (startIndex+1) +" AND "+ (endIndex);
						}
						else{
							sql = "SELECT TOP " + endIndex + " * FROM " + entityData.getTableName() + " WHERE " + entityData.getStrCondition();
						}
						//strSql="select "+strSelectField+" from ( SELECT ROW_NUMBER() OVER (ORDER BY " + primaryKeyName + " ASC) AS 'ROWNUMBER', * FROM " + strViewName+ ") as View_AutoDTO_DB_DBXX_JC where ROWNUMBER between "+ (ShowParamManager.getFirstResult()+1) +" and "+ (ShowParamManager.getFirstResult()+PageSize) +"";
					}
					//strSql="select "+strSelectField+" from (select rownumber() over(order by "+primaryKeyName+") as ROWNUMBER,"+strSelectField +"  from "+strViewName+" )as a where a.ROWNUMBER between "+ (ShowParamManager.getFirstResult()+1) +" and "+ (ShowParamManager.getFirstResult()+PageSize) +"";
				}
			}
		}

		Object data = selectByConditionDao.query(strProjectCode,datasourceName,entityData.getTableName(), sql,objects);
		commonServiceResult.setData(data);
		commonServiceResult.setSuccess(true);
		commonServiceResult.setMessage("success");
		return commonServiceResult;
	}
}
