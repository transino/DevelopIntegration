package net.zhangqiu.service.database.interfaces;

import javax.sql.DataSource;

import net.zhangqiu.service.database.entity.EntityDatasource;

public interface ConnectionPool {
	public DataSource getDataSource(EntityDatasource entityDatasource);
}
