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
import org.testng.annotations.BeforeTest;

@SpringBootTest
public class TestprojectSpringbootTests extends AbstractTestNGSpringContextTests {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    EntityContext entityContext;

    protected String strProjectCode;
    protected String datasourceName;

    @BeforeTest(alwaysRun = true)
    public void beforeTest(){
        strProjectCode = "testproject";
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult){
        datasourceName = entityContext.getDefaultDatasourceNameMap().get(strProjectCode);
        super.run(callBack,testResult);
    }
}
