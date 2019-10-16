package net.zhangqiu.service.database.imps.batch;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.ListQueryCallbackHandlerDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckTextParam;
import net.zhangqiu.service.database.interfaces.ListDataHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.StrUtils;

@Service
public class DatabaseToFile1Service extends EntityDatabaseTextTemplateService {

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    EntityContext entityContext;
	@Autowired
	ListQueryCallbackHandlerDao queryCallbackHandlerDao;

	class TextFileDataHandler implements ListDataHandler{
	
		private String regex;
		private BufferedWriter bufferedWriter;
		public TextFileDataHandler(BufferedWriter bufferedWriter,String regex){
			this.bufferedWriter = bufferedWriter;
			this.regex = regex;
		}
		
		public void handleData(List<List<Object>> dataList)throws Exception {
			for(List<Object> list : dataList){
				String string = "";
				for(Object object : list){
					String text = "";
					if(object != null){
						text = object.toString();
					}
					string += text + regex;
				}
				string = string.substring(0,string.length() -1);
				bufferedWriter.write(string + System.getProperty("line.separator"));
			}
		}
	}
	
	@Override
	public CommonServiceResult logicService(EntityDatabaseCheckTextParam entityDatabaseText)throws Exception {
		final String strProjectCode = entityContext.getStrProjectCodeFromTableKey(entityDatabaseText.getTableNameKey());
		final String datasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseText.getTableNameKey());
		final String tableName = entityContext.getTableNameFromTableKey(entityDatabaseText.getTableNameKey());
		
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter  = null;
		BufferedWriter bufferedWriter = null;
		try{
			fileOutputStream = new FileOutputStream(entityDatabaseText.getResource());
			if(StrUtils.isEmpty(entityDatabaseText.getEncoding())){
				outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			}
			else{
				outputStreamWriter = new OutputStreamWriter(fileOutputStream,entityDatabaseText.getEncoding());
			}
			bufferedWriter = new BufferedWriter(outputStreamWriter);

			queryCallbackHandlerDao.queryForHandler(new TextFileDataHandler(bufferedWriter,entityDatabaseText.getRegex()),entityDatabaseText.getCacheLine(),strProjectCode,datasourceName,"SELECT * FROM " + tableName, null);
		}
		finally{
			if(bufferedWriter != null){
				bufferedWriter.close();
			}
			if(outputStreamWriter != null){
				outputStreamWriter.close();
			}
			if(fileOutputStream != null){
				fileOutputStream.close();
			}
		}
		commonServiceResult.setSuccess(true);
		commonServiceResult.setMessage("success");
		return commonServiceResult;
	}

}
