package net.zhangqiu.service.check.imps.condition;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.imps.check.CompareCheck;
import net.zhangqiu.service.check.interfaces.Condition;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IfCompareCondition  implements Condition {

	@Autowired
	CompareCheck compareCheck;
	
	public CommonCheckResult check(String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		boolean paramResult = false;
		for(int i=2;i<params.length;i++){
			if(i%4 == 2){
				String[] checkParam = new String[2];
				checkParam[0] = params[i-1];
				checkParam[1] = params[i];
				CommonCheckResult checkResult = compareCheck.check(params[i-2], checkParam,dataMap,ruleColumnMap,columnDescriptionMap,checkRuleTable);
				
				String valueDescription = StrUtils.getDescription(ruleColumnMap, dataMap, columnDescriptionMap, params[i-2],checkRuleTable.getColumnValueMap());
				
				if(i == 2){
					paramResult = checkResult.isSuccess();
					commonCheckResult.setMessage(commonCheckResult.getMessage() + valueDescription + checkResult.getMessage().replace("应", ""));
				}
				else{
					if(params[i-3].equals("and")){
						paramResult = paramResult && checkResult.isSuccess();
						commonCheckResult.setMessage(commonCheckResult.getMessage() + "并且" + valueDescription + checkResult.getMessage().replace("应", ""));
					}
					else if(params[i-3].equals("or")){
						paramResult = paramResult || checkResult.isSuccess();
						commonCheckResult.setMessage(commonCheckResult.getMessage() + "或则" + valueDescription + checkResult.getMessage().replace("应", ""));
					}
				}
			}
		}
		
		if(paramResult){
			commonCheckResult.setMessage("如果" + commonCheckResult.getMessage());
		}
		else{
			commonCheckResult.setSuccess(false);
		}
		
		return commonCheckResult;
	}

}
