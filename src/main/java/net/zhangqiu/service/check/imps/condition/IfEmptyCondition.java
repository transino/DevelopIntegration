package net.zhangqiu.service.check.imps.condition;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.interfaces.Condition;
import net.zhangqiu.utils.StrUtils;

import org.springframework.stereotype.Component;

@Component
public class IfEmptyCondition implements Condition {
	
	public CommonCheckResult check(String[] params,Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
				
		String value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, params[0]);
		String valueDescription = StrUtils.getDescription(ruleColumnMap, dataMap, columnDescriptionMap, params[0],checkRuleTable.getColumnValueMap());
		
		if(params[1].equals("empty")){
			if(!StrUtils.isEmpty(value)){
				commonCheckResult.setSuccess(false);
			}
			else{
				commonCheckResult.setMessage("如果"+valueDescription+"为空");
			}
		}
		else if(params[1].equals("notempty")){
			if(StrUtils.isEmpty(value)){
				commonCheckResult.setSuccess(false);
			}
			else{
				commonCheckResult.setMessage("如果"+valueDescription+"不为空");
			}
		}
		return commonCheckResult;
	}
}
