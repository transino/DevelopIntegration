package net.zhangqiu.project.grxx.service.check.imps;

import java.util.Map;

import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckMap;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.utils.StrUtils;

@Component
public class HkztCheck implements CheckMap{
	
	public CommonCheckResult check(String[] params,Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		if(dataMap.get("KHRQ") == null || dataMap.get("HKZT") == null || dataMap.get("JSYHKRQ")==null){
			return commonCheckResult;
		}
		String KHRQ = dataMap.get("KHRQ").toString();
		String HKZT = dataMap.get("HKZT").toString();
		String JSYHKRQ = dataMap.get("JSYHKRQ").toString();
		
		if(HKZT.length()==24){
			int KHRQYear = Integer.valueOf(KHRQ.substring(0,4));
			int KHRQMonth = Integer.valueOf(KHRQ.substring(4,6));
			int JSYHKRQYear = Integer.valueOf(JSYHKRQ.substring(0,4));
			int JSYHKRQMonth = Integer.valueOf(JSYHKRQ.substring(4,6));

			int mothDiff = (JSYHKRQYear - KHRQYear)*12 + (JSYHKRQMonth - KHRQMonth);
			
			if(mothDiff >= 0 ){
				if(mothDiff >= 23) {
					if(HKZT.substring(1).trim().indexOf("/") > -1) {
						commonCheckResult.setSuccess(false);
						commonCheckResult.setMessage("“24个月（账户）还款状态”数据项值中，“开户日期”所在月之后各月还款状态不能为“/”。");
					}
				} 
				else {
					String left = null;
					String right = null;
					if(mothDiff == 0) {
						left = HKZT.substring(0, HKZT.length()-1);
					} else {
						left = HKZT.substring(0, HKZT.length()-mothDiff-1);
						right = HKZT.substring(HKZT.length()-mothDiff-1);//右边包括开户日期当月
					}
					
					if(!StrUtils.isEmpty(left)) {
						String regex = "([/]{"+left.length()+"})";
						if(!left.matches(regex)) {
							commonCheckResult.setSuccess(false);
							commonCheckResult.setMessage("“24个月（账户）还款状态”数据项值中，“开户日期”所在月之前各月还款状态必须为“/”。");
						}
					}
					
					if(!StrUtils.isEmpty(right)) {
						if(right.indexOf("/") > -1) {
							commonCheckResult.setSuccess(false);
							commonCheckResult.setMessage("“24个月（账户）还款状态”数据项值中，“开户日期”所在月及之后各月还款状态不能为“/”。");
						}
					}
				}
			}
		}
		return commonCheckResult;
	}
}
