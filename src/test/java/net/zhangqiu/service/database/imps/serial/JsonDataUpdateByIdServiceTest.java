package net.zhangqiu.service.database.imps.serial;

import net.zhangqiu.TestprojectTransactionalTests;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.entity.result.SaveOrUpdateResult;
import net.zhangqiu.service.database.interfaces.EntityTransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonDataUpdateByIdServiceTest extends TestprojectTransactionalTests {


    @Autowired
    @Qualifier("jsonDataSaveService")
    EntityTransactionHandler jsonDataSaveService;

    @Autowired
    @Qualifier("jsonDataUpdateByIdService")
    EntityTransactionHandler jsonDataUpdateByIdService;

    @Autowired
    EntityContext entityContext;

    @Test(groups = {"TestprojectSpringbootTransactional"})
    public void logicService() throws Exception{
        EntityDataParam entityData = new EntityDataParam();
        entityData.setTableName("Testproject_Testtable");
        entityData.setCheckTableName("Testproject_Testtable");
        Map<String, Object> map = new HashMap<String, Object>();
        String field1 = String.valueOf(entityContext.getNextId());
        String field2 = String.valueOf(entityContext.getNextId());
        //测试数据入库
        map.put("field1", field1);
        map.put("field2", field2);
        entityData.setData(map);
        SaveOrUpdateResult saveOrUpdateResult = (SaveOrUpdateResult)jsonDataSaveService.logicService(strProjectCode, datasourceName, entityData);
        Assert.assertEquals(saveOrUpdateResult.isSuccess(),true);
        //数据更新
        map.put("field1", field1);
        map.put("field2", String.valueOf(entityContext.getNextId()));
        entityData.setData(map);
        saveOrUpdateResult = (SaveOrUpdateResult)jsonDataUpdateByIdService.logicService(strProjectCode, datasourceName, entityData);
        Assert.assertEquals(saveOrUpdateResult.isSuccess(),true);
    }
}
