package net.zhangqiu.service.database.imps.serial;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.SelectAllDao;
import net.zhangqiu.service.database.dao.SelectByConditionDao;
import net.zhangqiu.service.database.dao.SelectBySqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityDatasource;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JsonDataSelectBySqlService extends EntityDataTemplateService{
    @Autowired
    EnvironmentContext environmentContext;

    @Autowired
    EntityContext entityContext;

    @Autowired
    SelectBySqlDao selectBySqlDao;

    @Override
    public CommonServiceResult logicService(String strProjectCode,String datasourceName,EntityDataParam entityData) throws Exception {
        CommonServiceResult commonServiceResult = new CommonServiceResult();
        String sql = entityData.getStrCondition();
        if(StrUtils.isEmpty(sql)){
            commonServiceResult.setSuccess(false);
            commonServiceResult.setMessage("SQL不能为空");
            return commonServiceResult;
        }

        Map<String,Object> map = entityData.getData();
        Object result = null;
        if(map != null && map.size() > 0){
            int objectsSize = entityData.getData().size();
            Object[] objects = new Object[objectsSize];
            for(Map.Entry<String, Object> entry : map.entrySet()){
                int indexOf = entry.getKey().indexOf("_");
                int index = Integer.valueOf(entry.getKey().substring(0, indexOf));
                objects[index] = entityContext.convertToType(entry.getValue(),entry.getKey().substring(indexOf+1));
            }
            result = selectBySqlDao.query(strProjectCode,datasourceName, sql, objects);
        }
        else{
            result = selectBySqlDao.query(strProjectCode,datasourceName, sql);
        }
        commonServiceResult.setSuccess(true);
        commonServiceResult.setMessage("success");
        commonServiceResult.setData(result);
        return commonServiceResult;
    }
}
