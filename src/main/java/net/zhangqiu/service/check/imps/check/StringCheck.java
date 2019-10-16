package net.zhangqiu.service.check.imps.check;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

@Component
public class StringCheck implements Check {

	Pattern chinesePattern = Pattern.compile("[\u4e00-\u9fa5]");
	Pattern repeatPattern = Pattern.compile(".*([a-zA-Z]).*\\1.*");
	Pattern upperPattern = Pattern.compile("[A-Z]*");
	Pattern specialPattern = Pattern.compile("[^～  ！ ◎ ＃ ￥ ％  ……  ※  ×  ＋  ＝   ：  , “   ” ？《   》  ，  。  、~ ! @ $ % ^ & * + | \\  / ; \\ }]+");

	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		
		if(params[0].equals("chinese")){
			byte[] buf = value.getBytes();
			boolean haveChn = false;
			for (byte b : buf) {
				if(b<0){
					haveChn = true;
					break;
				}
			}
			if(!haveChn){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("应含中文");
			}
		}
		else if(params[0].equals("notchinese")){
			Matcher matcher = chinesePattern.matcher(value);
     		if(matcher.find()){
     			commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("不应含中文");
			}
		}
		else if(params[0].equals("repeat")){
			Matcher matcher = repeatPattern.matcher(value);
			if(matcher.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("不能有重复字母");
			}
		}
		else if(params[0].equals("upper")){
			Matcher matcher = upperPattern.matcher(value);
			if(!matcher.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("应为大写字母");
			}
		}
		else if(params[0].equals("special")){
			Matcher matcher = specialPattern.matcher(value);
			if(matcher.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("不能有特殊字符");
			}
		}
		
		return commonCheckResult;
	}

}
