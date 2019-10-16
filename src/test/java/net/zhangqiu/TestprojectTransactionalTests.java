package net.zhangqiu;

import net.zhangqiu.service.database.entity.EntityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.IHookCallBack;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

@SpringBootTest
public class TestprojectTransactionalTests extends AbstractTestNGSpringContextTests {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    EntityContext entityContext;

    protected String strProjectCode;
    protected String datasourceName;

    @BeforeClass(alwaysRun = true)
    public void beforeTest(){
        strProjectCode = "testproject";
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult){
        DataSourceTransactionManager dataSourceTransactionManager = null;
        TransactionStatus transactionStatus = null;
        try{
            datasourceName = entityContext.getDefaultDatasourceNameMap().get(strProjectCode);
            dataSourceTransactionManager = applicationContext.getBean(entityContext.getDataSourceTransactionManagerName(strProjectCode,datasourceName), DataSourceTransactionManager.class);
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionStatus = dataSourceTransactionManager.getTransaction(defaultTransactionDefinition);
            super.run(callBack,testResult);
            //dataSourceTransactionManager.commit(transactionStatus);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            dataSourceTransactionManager.rollback(transactionStatus);
        }
    }
}
