package net.zhangqiu.restcontroller;

import net.sf.json.JSONObject;
import net.zhangqiu.TestprojectTransactionalTests;
import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
public class JsonDataRestControllerTest extends TestprojectTransactionalTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityContext entityContext;

    @Autowired
    EnvironmentContext environmentContext;

    @Test(groups = {"TestprojectSpringbootTransactional"})
    public void jsonDataSave() throws Exception{

        Map<String,Object> jsonData = new HashMap<String, Object>();
        jsonData.put("tableName","Testproject_Testtable");
        Map<String,String> dataMap = new HashMap<String, String>();
        String field1 = String.valueOf(entityContext.getNextId());
        String field2 = String.valueOf(entityContext.getNextId());
        dataMap.put("field1", field1);
        dataMap.put("field2", field2);
        jsonData.put("data",dataMap);

        String strJsonData = URLEncoder.encode(JsonUtils.objectToString(jsonData),environmentContext.getEncoding());

        MvcResult mvcResult = mockMvc.perform(get("/jsonDataSave")
                .param("strProjectCode",this.strProjectCode)
                .param("jsonData",strJsonData)
        )
                .andReturn();
        String strResult = mvcResult.getResponse().getContentAsString();
        JSONObject resultJsonData = (JSONObject)JsonUtils.stringToJson(strResult);
        Assert.assertEquals(resultJsonData.getBoolean("success"),true);
    }
}
