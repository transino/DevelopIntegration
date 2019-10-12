package net.zhangqiu.service.database.imps.batch;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.CheckRule;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckAll;
import net.zhangqiu.service.check.interfaces.CheckBatch;
import net.zhangqiu.service.check.interfaces.CheckExpressionList;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckTextParam;
import net.zhangqiu.service.database.interfaces.ListMapHandler;
import net.zhangqiu.service.database.interfaces.QueryForListMapDataHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.StrUtils;

@Service
public class DatabaseToFileService extends EntityDatabaseTextTemplateService {
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    EntityContext entityContext;
	@Autowired
	@Qualifier("grxxCallbackHandlerDao")
	QueryForListMapDataHandler queryForListMapDataHandler;
	@Autowired
	@Qualifier("checkExpressionListService")
	CheckExpressionList checkExpressionList;
	@Autowired
	protected ApplicationContext applicationContext;

	class TextFileDataHandler implements ListMapHandler {
	
		private BufferedWriter bufferedWriter;
		private String regex;
		private CheckRuleTable checkRuleTable;
		private DataListResultParam dataListResultParam;
		private DataListResult dataListResult;
		private CheckBatch checkBatch;
		public TextFileDataHandler(BufferedWriter bufferedWriter, String regex, CheckRuleTable checkRuleTable, DataListResultParam dataListResultParam, DataListResult dataListResult, CheckBatch checkBatch){
			this.bufferedWriter = bufferedWriter;
			this.regex = regex;
			this.checkRuleTable = checkRuleTable;
			this.dataListResultParam = dataListResultParam;
			this.dataListResult = dataListResult;
			this.checkBatch = checkBatch;
		}
		
		public void handleData(List<Map<String, Object>> dataMapList)throws Exception {
			
			if(checkRuleTable != null){
				if(checkBatch != null){
					checkBatch.check(dataListResult, checkRuleTable, dataMapList, dataListResultParam);
				}
				checkExpressionList.check(dataListResult,checkRuleTable, dataMapList,dataListResultParam);
			}

			for(Map<String, Object> map : dataMapList){
				String string = "";
				for(Object object : map.values()){
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
			DataListResultParam dataListResultParam = new DataListResultParam();
			dataListResultParam.setMaxDataResult(100);
			dataListResultParam.setMaxDataResultBreak(false);
			DataListResult dataListResult = new DataListResult();
			
			String key = entityDatabaseText.getTableNameKey();
			CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(key);

			CheckBatch checkBatch = null;
			if(checkRuleTable != null){
				for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
					if(checkRule.getLevel() == 4){
						String expressionParam = StrUtils.getExpressionParam(checkRule.getRule(), "@CHECK");
						CheckAll checkAll = applicationContext.getBean(expressionParam, CheckAll.class);
						checkAll.check(dataListResult, checkRuleTable, dataListResultParam);
					}
				}
				for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
					if(checkRule.getLevel() == 3){
						String expressionParam = StrUtils.getExpressionParam(checkRule.getRule(), "@CHECK");
						checkBatch = applicationContext.getBean(expressionParam, CheckBatch.class);
					}
				}
			}
			
			TextFileDataHandler textFileDataHandler = new TextFileDataHandler(bufferedWriter,entityDatabaseText.getRegex(),checkRuleTable,dataListResultParam,dataListResult,checkBatch);
			queryForListMapDataHandler.queryForHandler(textFileDataHandler,entityDatabaseText.getCacheLine(),strProjectCode,datasourceName,tableName,entityDatabaseText.getSql(), null);

			System.out.println(dataListResult.getCheckCount());
			System.out.println(dataListResult.getExceptionCount());
			System.out.println(dataListResult.getDataResultList().size());
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
