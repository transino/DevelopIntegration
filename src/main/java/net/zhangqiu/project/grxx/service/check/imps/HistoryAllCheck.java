package net.zhangqiu.project.grxx.service.check.imps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.interfaces.CheckAll;
import net.zhangqiu.service.database.dao.BaseDao;

@Component
public class HistoryAllCheck extends BaseDao implements CheckAll{

	private class DKQueryForPreparedStatementCreator implements PreparedStatementCreator{

		public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
			String sql = "select * from GR_GRXX_JC jc  inner join";
			sql += " (select * from GR_GRXX_JC prejc where prejc.YWZL = '1' and prejc.ZHZT = '3' and prejc.JSYHKRQ = '20180101') prejcjoin on";
			sql += " jc.JRJGDM = prejcjoin.JRJGDM and jc.YWH = prejcjoin.YWH";
			sql += " where jc.YWZL = '1' and jc.JSYHKRQ = '20180101';";
			PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
			return preparedStatement;
		}
		
	}
	
	private class XYKQueryForPreparedStatementCreator implements PreparedStatementCreator{

		public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
			String sql = "select * from GR_GRXX_JC jc  inner join";
			sql += " (select * from GR_GRXX_JC prejc where prejc.YWZL = '2' and prejc.ZHZT = '4' and prejc.JSYHKRQ = '20180101') prejcjoin on";
			sql += " jc.JRJGDM = prejcjoin.JRJGDM and jc.YWH = prejcjoin.YWH";
			sql += " where jc.YWZL = '2' and jc.ZHZT = '4' and jc.JSYHKRQ = '20180101';";
			PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
			return preparedStatement;
		}
		
	}

	public void check(DataListResult dataListResult,
			CheckRuleTable checkRuleTable,
			DataListResultParam dataListResultParam) throws Exception {
		
		JdbcTemplate jdbcTemplate = getJdbcTemplate("grxx",checkRuleTable.getDataSourceName());
		jdbcTemplate.query(new DKQueryForPreparedStatementCreator(), new RowCallbackHandler(){
			public void processRow(ResultSet rs) throws SQLException {
				//"业务种类为“贷款”时，数据库中账户记录前最近一个月的账户状态必须为“结清”以外的值"
			}
		});
		jdbcTemplate.query(new XYKQueryForPreparedStatementCreator(), new RowCallbackHandler(){
			public void processRow(ResultSet rs) throws SQLException {
				//"业务种类为“信用卡”时，账户记录和数据库中账户记录最近一个月的账户状态不能同为“销户”"
			}
		});
	}
}
