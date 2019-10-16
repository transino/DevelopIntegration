package net.zhangqiu.service.check.imps.check;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.utils.StrUtils;

import org.springframework.stereotype.Component;

@Component
public class NotEmptyCheck implements Check{
	public CommonCheckResult check(String value,String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		
		value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);

		if(!value.equals("")){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("应为空");
		}
		return commonCheckResult;
	}
}
