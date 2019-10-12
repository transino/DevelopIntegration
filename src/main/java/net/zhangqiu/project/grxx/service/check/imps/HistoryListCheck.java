package net.zhangqiu.project.grxx.service.check.imps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckBatch;
import net.zhangqiu.service.database.dao.BaseDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityDatasource;

@Component
public class HistoryListCheck extends BaseDao implements CheckBatch {

	private class QueryForPreparedStatementCreator implements PreparedStatementCreator{

		public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
			String sql = "SELECT JRJGDM,YWH,ZHZT FROM GR_GRXX_JC WHERE ZHZT IN('3','4') AND JSYHKRQ = '20180101'";
			PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
			return preparedStatement;
		}
		
	}
	
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
    EntityContext entityContext;
	
	public void check(DataListResult dataListResult,
                      CheckRuleTable checkRuleTable,
                      List<Map<String, Object>> dataMapList,
                      DataListResultParam dataListResultParam)
			throws Exception {
		/*
		final Map<String,Map<String, Object>> map = new HashMap<String,Map<String, Object>>();
		for(Map<String, Object> data : dataMapList){
			map.put(data.get("JRJGDM").toString() + data.get("YWH").toString(), data);
		}
		EntityDatasource entityDatasource = entityContext.getEntityDatasourceMap().get(entityContext.getCurrentDatasourceName(checkRuleTable.getDataSourceName()));

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			Class.forName(entityDatasource.getDriverClassName());;
			connection =DriverManager.getConnection(entityDatasource.getUrl()+"?" + "user="+entityDatasource.getUserName()+"&password="+entityDatasource.getPassword());
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement("SELECT JRJGDM,YWH,ZHZT FROM GR_GRXX_JC WHERE ZHZT IN('3','4') AND JSYHKRQ = '20180101'",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
			resultSet = preparedStatement.executeQuery();  
			while (resultSet.next()) {
				String JRJGDM = resultSet.getObject("JRJGDM").toString();
				String YWH = resultSet.getObject("YWH").toString();
				String key = JRJGDM + YWH;
				Map<String,Object> dataMap = map.get(key);
				if(dataMap != null){
					if(dataMap.get("YWZL").equals("1") && resultSet.getObject("ZHZT").equals("3")){
						//"业务种类为“贷款”时，数据库中账户记录前最近一个月的账户状态必须为“结清”以外的值"
					}
					else if(dataMap.get("YWZL").equals("2") && dataMap.get("ZHZT").equals("4") && resultSet.getObject("ZHZT").equals("4")){
						//"业务种类为“信用卡”时，账户记录和数据库中账户记录最近一个月的账户状态不能同为“销户”"
					}
				}
			}
		}
		finally{
			if(resultSet != null){
				resultSet.close();
			}
			if(preparedStatement != null){
				preparedStatement.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		*/
		/*
		JdbcTemplate jdbcTemplate = getJdbcTemplate(checkRuleTable.getDataSourceName());
		jdbcTemplate.query(new QueryForPreparedStatementCreator(), new RowCallbackHandler(){
			public void processRow(ResultSet rs) throws SQLException {
				String JRJGDM = rs.getObject("JRJGDM").toString();
				String YWH = rs.getObject("YWH").toString();
				String key = JRJGDM + YWH;
				if(map.containsKey(key)){
					if(map.get("YWZL").equals("1") && rs.getObject("ZHZT").equals("3")){
						//"业务种类为“贷款”时，数据库中账户记录前最近一个月的账户状态必须为“结清”以外的值"
					}
					else if(map.get("YWZL").equals("2") && rs.getObject("ZHZT").equals("4")){
						//"业务种类为“信用卡”时，账户记录和数据库中账户记录最近一个月的账户状态不能同为“销户”"
					}
				}
			}
		});
		*/
	}

}
