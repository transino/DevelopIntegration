package net.zhangqiu.restcontroller;

import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityDatasource;
import net.zhangqiu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    @Autowired
    EntityContext entityContext;

    @RequestMapping(value = "/getDatabaseType")
    public String getDatabaseType(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName) throws Exception {
        try{
            datasourceName = entityContext.getCurrentDatasourceName(strProjectCode,datasourceName);
            EntityDatasource entityDatasource = entityContext.getProjectEntityDatasourceMap().get(strProjectCode).get(datasourceName);
            return entityDatasource.getStrDatabaseType();
        }
        catch (Exception ex){
            return "";
        }
    }

    @RequestMapping(value = "/getDatasource")
    public String getDatasource(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName) throws Exception {
        try{
            datasourceName = entityContext.getCurrentDatasourceName(strProjectCode,datasourceName);
            EntityDatasource entityDatasource = entityContext.getProjectEntityDatasourceMap().get(strProjectCode).get(datasourceName);
            return JsonUtils.objectToString(entityDatasource);
        }
        catch (Exception ex){
            return "";
        }
    }
}
