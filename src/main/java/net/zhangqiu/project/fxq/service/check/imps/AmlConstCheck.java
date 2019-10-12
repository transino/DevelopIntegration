package net.zhangqiu.project.fxq.service.check.imps;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.interfaces.Check;

import net.zhangqiu.service.database.entity.EntityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AmlConstCheck implements Check {

    @Autowired
    EntityContext entityContext;
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String, String> ruleColumnMap, Map<String, String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();

		String[] values =dataMap.get(ruleColumnMap.get(value)).toString().split(",",-1);
		for(String val : values){
            if(!entityContext.getConstMap().get("aml").get(params[0]).containsKey(val)){
                commonCheckResult.setSuccess(false);
                String message = "值域应为" + entityContext.getConstMap().get("aml").get(params[0]).values().toString();
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
