package net.zhangqiu.service.database.dao;

import net.zhangqiu.TestprojectTransactionalTests;
import net.zhangqiu.service.database.entity.EntityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UpdateSqlDaoTest extends TestprojectTransactionalTests {

    @Autowired
    UpdateSqlDao updateSqlDao;

    @Autowired
    EntityContext entityContext;

    @Test(groups = {"TestprojectSpringbootTransactional"})
    public void updateSql(){
        String field1 = String.valueOf(entityContext.getNextId());
        String field2 = String.valueOf(entityContext.getNextId());
        String sql = "INSERT INTO Testproject_Testtable VALUES('"+field1+"','"+field2+"','')";
        int result = updateSqlDao.updateSql(this.strProjectCode,this.datasourceName,sql);
        Assert.assertEquals(result,1);

        field1 = String.valueOf(entityContext.getNextId());
        field2 = String.valueOf(entityContext.getNextId());
        sql = "INSERT INTO Testproject_Testtable VALUES(?,?,'')";
        Object[] args = new Object[]{field1,field2};
        result = updateSqlDao.updateSql(this.strProjectCode,this.datasourceName,sql,args);
        Assert.assertEquals(result,1);
    }

}
