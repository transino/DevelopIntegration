package net.zhangqiu.service.database.imps;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.database.entity.EntityDatasource;
import net.zhangqiu.service.database.interfaces.ConnectionPool;

@Component
public class DbcpPool implements ConnectionPool {
	
	public DataSource getDataSource(EntityDatasource entityDatasource) {
		 BasicDataSource basicDataSource = new BasicDataSource();
    	 basicDataSource.setUrl(entityDatasource.getUrl());
    	 basicDataSource.setUsername(entityDatasource.getUserName());
    	 basicDataSource.setPassword(entityDatasource.getPassword());
    	 basicDataSource.setDriverClassName(entityDatasource.getDriverClassName());
    	 basicDataSource.setInitialSize(10);
    	 basicDataSource.setMaxTotal(50);
    	 return basicDataSource;
	}

}
