package net.zhangqiu.restcontroller;

import net.zhangqiu.service.database.interfaces.JsonDatasourceHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class JsonDataRestController{
	
	@Autowired
	@Qualifier("jsonDataSaveService")
	JsonDatasourceHandler jsonDataSaveService;
	
	@RequestMapping(value = "/jsonDataSave")
	public String jsonDataSave(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataSaveService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataDeleteAllService")
	JsonDatasourceHandler jsonDataDeleteAllService;
	
	@RequestMapping(value = "/jsonDataDeleteAll")
	public String jsonDataDeleteAll(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataDeleteAllService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataDeleteByIdService")
	JsonDatasourceHandler jsonDataDeleteByIdService;
	
	@RequestMapping(value = "/jsonDataDeleteById")
	public String jsonDataDeleteById(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataDeleteByIdService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataSelectAllService")
	JsonDatasourceHandler jsonDataSelectAllService;
	
	@RequestMapping(value = "/jsonDataSelectAll")
	public String jsonDataSelectAll(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataSelectAllService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataSelectByConditionService")
	JsonDatasourceHandler jsonDataSelectByConditionService;
	
	@RequestMapping(value = "/jsonDataSelectByCondition")
	public String jsonDataSelectByCondition(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataSelectByConditionService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataSelectCountByConditionService")
	JsonDatasourceHandler jsonDataSelectCountByConditionService;
	
	@RequestMapping(value = "/jsonDataSelectCountByCondition")
	public String jsonDataSelectCountByCondition(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataSelectCountByConditionService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataExecuteSqlService")
	JsonDatasourceHandler jsonDataExecuteSqlService;
	
	@RequestMapping(value = "/jsonDataExecuteSql")
	public String jsonDataExecuteSql(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataExecuteSqlService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataUpdateSqlService")
	JsonDatasourceHandler jsonDataUpdateSqlService;
	
	@RequestMapping(value = "/jsonDataUpdateSql")
	public String jsonDataUpdateSql(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataUpdateSqlService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataSelectByIdService")
	JsonDatasourceHandler jsonDataSelectByIdService;
	
	@RequestMapping(value = "/jsonDataSelectById")
	public String jsonDataSelectById(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataSelectByIdService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataUpdateByIdService")
	JsonDatasourceHandler jsonDataUpdateByIdService;
	
	@RequestMapping(value = "/jsonDataUpdateById")
	public String jsonDataUpdateById(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataUpdateByIdService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }

    @Autowired
    @Qualifier("jsonDataSelectBySqlService")
    JsonDatasourceHandler jsonDataSelectBySqlService;

    @RequestMapping(value = "/jsonDataSelectBySql")
    public String jsonDataSelectBySql(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
        return jsonDataSelectBySqlService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }

	
	@Autowired
	@Qualifier("jsonDataCheckService")
	JsonDatasourceHandler jsonDataCheckService;
	
	@RequestMapping(value = "/jsonDataCheck")
	public String jsonDataCheck(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataCheckService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }
	
	@Autowired
	@Qualifier("jsonDataComponentService")
	JsonDatasourceHandler jsonDataComponentService;
	
	@RequestMapping(value = "/jsonDataComponent")
	public String jsonDataComponent(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
		return jsonDataComponentService.handleService(indentification,strProjectCode, datasourceName,jsonData,encoding);
    }

    @RequestMapping(value = "/jsonDataComponent",method = RequestMethod.POST)
    public String jsonDataComponent2(@RequestBody Map<String,String> map) throws Exception {
        return jsonDataComponentService.handleService(map.get("indentification"),map.get("strProjectCode"), map.get("datasourceName"),map.get("jsonData"),map.get("encoding"));
    }
}
