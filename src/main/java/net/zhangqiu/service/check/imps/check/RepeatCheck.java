package net.zhangqiu.service.check.imps.check;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckMapList;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.utils.StrUtils;

@Component
public class RepeatCheck implements CheckMapList{
	
	private String tempSplitFlag = ",";

	private String getValue(String[] params,Map<String, Object> dataMap,Map<String, String> ruleColumnMap){
		String value = "";
		for(int i=0;i<params.length;i++){
			value += StrUtils.getMapInnerValue(ruleColumnMap, dataMap, params[i]) + tempSplitFlag;
		}
		return value;
	}
	
	public CommonCheckResult check(String[] params,
			Map<String, Object> dataMap, List<Map<String, Object>> dataMapList,
			Map<String, String> ruleColumnMap,
			Map<String, String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		String value = getValue(params,dataMap,ruleColumnMap);
		
		for(Map<String, Object> map : dataMapList){
			if(map != dataMap){
				String compareValue = getValue(params,map,ruleColumnMap);
				if(value.equals(compareValue)){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("数据重复");
					break;
				}
			}
		}
		
		return commonCheckResult;
	}

}
