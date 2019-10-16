package net.zhangqiu.service.database.entity;

import net.zhangqiu.service.database.interfaces.InitDataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EntityConfig {
	
	 @Autowired
	 ApplicationContext applicationContext; 
	 
	 /*
	 @Autowired
	 EntityContext entityContext;
	 
	 @Autowired
	 @Qualifier("apacheTomcatPool")
	 ConnectionPool connectionPool;
	 */
	 
	 @Bean
	 Runnable dynamicConfiguration() throws Exception { 
		 /*
	     ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext; 
	     DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)configurableApplicationContext.getBeanFactory(); 
	     for(EntityDatasource entityDatasource : entityContext.getEntityDatasourceMap().values()){
	    	 DataSource dataSource = connectionPool.getDataSource(entityDatasource);
			 defaultListableBeanFactory.registerSingleton(entityDatasource.getDataSourceName(),dataSource);
			 JdbcTemplate jdbcTemplate = new JdbcTemplate();
			 jdbcTemplate.setDataSource(dataSource);
			 defaultListableBeanFactory.registerSingleton(entityContext.getJdbcTemplateName(entityDatasource.getDataSourceName()),jdbcTemplate);
			 DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
			 dataSourceTransactionManager.setDataSource(dataSource);
			 defaultListableBeanFactory.registerSingleton(entityContext.getDataSourceTransactionManagerName(entityDatasource.getDataSourceName()),dataSourceTransactionManager);
			 TransactionTemplate transactionTemplate = new TransactionTemplate();
			 transactionTemplate.setTransactionManager(dataSourceTransactionManager);
			 defaultListableBeanFactory.registerSingleton(entityContext.getTransactionTemplateName(entityDatasource.getDataSourceName()),transactionTemplate);
		 }
	     */
	     String [] initDataBaseHandlerNames = applicationContext.getBeanNamesForType(InitDataBaseHandler.class);
	     if(initDataBaseHandlerNames != null){
	    	 for(int i=0;i<initDataBaseHandlerNames.length;i++){
	    		 InitDataBaseHandler initDataBaseHandler = applicationContext.getBean(initDataBaseHandlerNames[0], InitDataBaseHandler.class);
	    		 initDataBaseHandler.init();
	    	 }
	     }
	     
	     //entityContext.initDatasourceSql();

	     return null;
	 } 
}
