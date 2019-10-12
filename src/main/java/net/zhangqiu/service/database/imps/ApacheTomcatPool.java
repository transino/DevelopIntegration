package net.zhangqiu.service.database.imps;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.database.entity.EntityDatasource;
import net.zhangqiu.service.database.interfaces.ConnectionPool;


@Component
public class ApacheTomcatPool implements ConnectionPool {
	public DataSource getDataSource(EntityDatasource entityDatasource) {
		DataSource dataSource = new DataSource();
		dataSource.setUrl(entityDatasource.getUrl());
		dataSource.setUsername(entityDatasource.getUserName());
		dataSource.setPassword(entityDatasource.getPassword());
		dataSource.setDriverClassName(entityDatasource.getDriverClassName());
		
		//dataSource.setTestOnBorrow(true);
		
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationInterval(60000);
		dataSource.setTestWhileIdle(true);
		//线程等待时间
		dataSource.setTimeBetweenEvictionRunsMillis(60000);

		//是否打印日志
		//dataSource.setLogAbandoned(true);
		//是否自动回收超时连接
		//dataSource.setRemoveAbandoned(true);
		//超时时间(以秒数为单位)
		//dataSource.setRemoveAbandonedTimeout(180);
	
   	    return dataSource;
	}

}
