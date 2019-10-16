package net.zhangqiu.service.database.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UpdateSqlDao extends BaseDao{
	
	public int updateSql(String strProjectCode,String datasourceName,String sql,Object[] objects){
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		return jdbcTemplate.update(sql,objects);
	}
	
	public int updateSql(String strProjectCode,String datasourceName,String sql){
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		return jdbcTemplate.update(sql);
	}
}
