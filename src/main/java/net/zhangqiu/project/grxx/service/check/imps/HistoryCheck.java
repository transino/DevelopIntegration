package net.zhangqiu.project.grxx.service.check.imps;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckMap;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.database.dao.BaseDao;
import net.zhangqiu.service.database.dao.SelectByIdDao;
import net.zhangqiu.service.database.entity.EntityContext;

@Component
public class HistoryCheck extends BaseDao implements CheckMap{

	@Autowired
    EntityContext entityContext;
	@Autowired
    SelectByIdDao selectByIdDao;

	public CommonCheckResult check(String[] params,
                                   Map<String, Object> dataMap, Map<String, String> ruleColumnMap,
                                   Map<String, String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		/*
		JdbcTemplate jdbcTemplate = getJdbcTemplate(checkRuleTable.getDataSourceName());
		String sql = "SELECT ZHZT FROM GR_GRXX_JC WHERE JRJGDM = '"+dataMap.get("JRJGDM").toString()+"' AND YWH = '"+dataMap.get("YWH").toString()+"' AND JSYHKRQ = '"+dataMap.get("JSYHKRQ").toString()+"'";
		Map<String,Object> selectMap = jdbcTemplate.queryForMap(sql);
		if(selectMap != null){
			if(dataMap.get("YWZL").toString().equals("1")){
				if(selectMap.get("ZHZT").toString().equals("3")){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("业务种类为“贷款”时，数据库中账户记录前最近一个月的账户状态必须为“结清”以外的值");
				}
			}
			else if(dataMap.get("YWZL").toString().equals("2")){
				if(selectMap.get("ZHZT").toString().equals("4")){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("业务种类为“信用卡”时，账户记录和数据库中账户记录最近一个月的账户状态不能同为“销户”");
				}
			}
		}
		else{
			commonCheckResult.setMessage("success");
		}
		*/
		return commonCheckResult;
	}
}
