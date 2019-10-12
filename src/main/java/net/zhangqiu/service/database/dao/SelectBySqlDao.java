package net.zhangqiu.service.database.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SelectBySqlDao extends BaseDao {

    public List<Map<String,Object>> query(String strProjectCode, String datasourceName, String sql, Object[] args){
        JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
        return jdbcTemplate.queryForList(sql,args);
    }

    public List<Map<String,Object>> query(String strProjectCode, String datasourceName, String sql){
        JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
        return jdbcTemplate.queryForList(sql);
    }

    public int queryForInt(String strProjectCode,String datasourceName,String sql,Object[] args){
        JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
        return jdbcTemplate.queryForObject(sql,args,Integer.class);
    }
}
