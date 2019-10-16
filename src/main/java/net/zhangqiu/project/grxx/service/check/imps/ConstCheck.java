package net.zhangqiu.project.grxx.service.check.imps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.database.dao.SelectAllDao;
import net.zhangqiu.service.database.interfaces.InitDataBaseHandler;

@Component
public class ConstCheck implements Check,InitDataBaseHandler{
	
	@Autowired
    SelectAllDao selectAllDao;

	private Map<String,Map<String,String>> constMap;

	public void init() throws Exception{
		//需要配置v3full数据源，暂时屏蔽
		/* 
		constMap = new HashMap<String,Map<String,String>>(); 
		List<Map<String,Object>> listMap = selectAllDao.query("v3full", "select * from systemcodevalue;");
		for(Map<String,Object> map : listMap){
			String strSetCode = map.get("strSetCode").toString();
			Map<String,String> setCodeMap = constMap.get(strSetCode);
			if(setCodeMap == null){
				setCodeMap = new HashMap<String,String>();
			}
			setCodeMap.put(map.get("strCode").toString(), map.get("strValue").toString());
			constMap.put(strSetCode, setCodeMap);
		}
		*/
	}

	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		value = dataMap.get(ruleColumnMap.get(value)).toString();
		
		if(!constMap.get(params[0]).containsKey(value)){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("值域应为" + constMap.get(params[0]).keySet().toString());
		}
		
		return commonCheckResult;
	}

}
