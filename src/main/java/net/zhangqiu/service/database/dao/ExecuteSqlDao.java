package net.zhangqiu.service.database.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExecuteSqlDao extends BaseDao{

	public void executeSql(String strProjectCode,String datasourceName,String sql){
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		jdbcTemplate.execute(sql);
	}
	
}
