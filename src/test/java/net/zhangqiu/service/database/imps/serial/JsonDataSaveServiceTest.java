package net.zhangqiu.service.database.imps.serial;

import java.util.HashMap;
import java.util.Map;

import net.zhangqiu.TestprojectSpringbootTests;
import net.zhangqiu.TestprojectTransactionalTests;
import net.zhangqiu.service.database.entity.result.SaveOrUpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.interfaces.EntityTransactionHandler;

public class JsonDataSaveServiceTest extends TestprojectTransactionalTests {
	
	@Autowired
	@Qualifier("jsonDataSaveService")
	EntityTransactionHandler jsonDataSaveService;

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
        map.put("field1", field1);
        map.put("field2", "");
        entityData.setData(map);
        //测试逻辑检验
        SaveOrUpdateResult saveOrUpdateResult = (SaveOrUpdateResult)jsonDataSaveService.logicService(strProjectCode, datasourceName, entityData);
        Assert.assertEquals(saveOrUpdateResult.getDataResult().isCheck(),false);
        Assert.assertEquals(saveOrUpdateResult.isSuccess(),false);
        //测试数据入库
        map.put("field1", field1);
        map.put("field2", field2);
        entityData.setData(map);
        saveOrUpdateResult = (SaveOrUpdateResult)jsonDataSaveService.logicService(strProjectCode, datasourceName, entityData);
        Assert.assertEquals(saveOrUpdateResult.isSuccess(),true);
        //测试主机重复校验
        saveOrUpdateResult = (SaveOrUpdateResult)jsonDataSaveService.logicService(strProjectCode, datasourceName, entityData);
        Assert.assertEquals(saveOrUpdateResult.isSuccess(),false);
    }
}
