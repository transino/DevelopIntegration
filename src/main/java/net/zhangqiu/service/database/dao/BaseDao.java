package net.zhangqiu.service.database.dao;

import net.zhangqiu.service.database.entity.EntityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseDao {
	@Autowired
	protected ApplicationContext applicationContext;
	@Autowired
    EntityContext entityContext;
	
	protected JdbcTemplate getJdbcTemplate(String strProjectCode,String datasourceName){
		return applicationContext.getBean(entityContext.getJdbcTemplateName(strProjectCode,datasourceName),JdbcTemplate.class);
	}
}
