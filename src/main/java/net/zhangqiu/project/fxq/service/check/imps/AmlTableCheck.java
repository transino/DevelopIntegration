package net.zhangqiu.project.fxq.service.check.imps;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.database.entity.EntityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AmlTableCheck implements Check {
    @Autowired
    EntityContext entityContext;

    public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String, String> ruleColumnMap, Map<String, String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
        CommonCheckResult commonCheckResult = new CommonCheckResult();

        String strProjectCode = "aml";
        String datasourceName = entityContext.getDefaultDatasourceNameMap().get("strProjectCode");
        String tableName = params[0].split("\\.")[0];
        String fieldName = params[0].split("\\.")[1];
        String key = entityContext.getEntityTableKey(strProjectCode,datasourceName,tableName);

        String[] values =dataMap.get(ruleColumnMap.get(value)).toString().split(",",-1);
        for(String val : values){
            if(!entityContext.getCachedataKeyNameMap().get(key).get(fieldName).containsKey(val)){
                commonCheckResult.setSuccess(false);
                String message = "值域应为" + entityContext.getCachedataKeyNameMap().get(key).get(fieldName).values().toString();
                if(message.length() > 100){
                    message = message.substring(0,100) + "...";
                }
                commonCheckResult.setMessage(message);
                break;
            }
        }
        return commonCheckResult;
    }
}
