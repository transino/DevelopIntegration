package net.zhangqiu.service.check.imps.condition;

import java.util.Map;

import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.interfaces.Condition;
import net.zhangqiu.utils.StrUtils;

@Component
public class IfEqualsCondition implements Condition{

	public CommonCheckResult check(String[] params,Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		
		String value1 = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, params[0]);
		String value2 = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, params[2]);
		String value1Description = StrUtils.getDescription(ruleColumnMap, dataMap, columnDescriptionMap, params[0],checkRuleTable.getColumnValueMap());
		String value2Description = StrUtils.getDescription(ruleColumnMap, dataMap, columnDescriptionMap, params[2],checkRuleTable.getColumnValueMap());

		if(params[1].equals("=")){
			if(!value1.equals(value2)){
				commonCheckResult.setSuccess(false);
			}
			else{
				commonCheckResult.setMessage("如果"+value1Description+"等于" + value2Description);
			}
		}
		else if(params[1].equals("!=")){
			if(value1.equals(value2)){
				commonCheckResult.setSuccess(false);
			}
			else{
				commonCheckResult.setMessage("如果"+value1Description+"不等于" + value2Description);
			}
		}
		return commonCheckResult;
	}

}
