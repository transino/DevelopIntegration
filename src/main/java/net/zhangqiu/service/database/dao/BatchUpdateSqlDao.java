package net.zhangqiu.service.database.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BatchUpdateSqlDao extends BaseDao{
		
	public void batchUpdateSql(String strProjectCode,String datasourceName,String sql,final List<Object[]> objectList) throws SQLException{
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement, int index) throws SQLException {
				Object[] objects = objectList.get(index);
				for(int i=0;i<objects.length;i++){
				    Object value = objects[i];
				    if(value instanceof java.util.Date){
                        value = new java.sql.Timestamp(((java.util.Date)value).getTime());
                    }
					preparedStatement.setObject(i+1, value);
				}
			}
			public int getBatchSize() {
				return objectList.size();
			}
		});
	}
	
/*	public void batchUpdateSql(String sql,final List<Object[]> objectList) throws SQLException{
		jdbcTemplate.batchUpdate(sql,objectList);
	}*/
}
