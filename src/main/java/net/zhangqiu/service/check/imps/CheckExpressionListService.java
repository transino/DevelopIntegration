package net.zhangqiu.service.check.imps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.check.interfaces.CheckExpression;
import net.zhangqiu.service.check.interfaces.CheckExpressionList;

@Service
public class CheckExpressionListService implements CheckExpressionList{

	@Autowired
	CheckExpression checkExpression;
	@Autowired
	ApplicationContext applicationContext;
	
	class CheckThread implements Callable<DataListResult>{

		private CheckRuleTable checkRuleTable;
		private List<Map<String, Object>> dataMapList;
		private DataListResultParam dataListResultParam;

		public CheckThread(CheckRuleTable checkRuleTable, List<Map<String, Object>> dataMapList, DataListResultParam dataListResultParam){
			this.checkRuleTable = checkRuleTable;
			this.dataMapList = dataMapList;
			this.dataListResultParam = dataListResultParam;
		}

		public DataListResult call() throws Exception {
			DataListResult dataListResult = new DataListResult();
			for(Map<String, Object> dataMap : dataMapList){
				if(!dataListResultParam.isMaxDataResultBreak() || dataListResult.getDataResultList().size() < dataListResultParam.getMaxDataResult()){
					DataResult dataResult = checkExpression.check(checkRuleTable, dataMap, dataListResultParam.getDataResultParam());
					if(!dataResult.isCheck()){
						dataListResult.setCheck(false);
						dataListResult.setCheckFailCount(dataListResult.getCheckFailCount()+1);
					}
					else{
						dataListResult.setCheckSuccessCount(dataListResult.getCheckSuccessCount()+1);
					}
					if(!dataResult.isForceCheck()){
						dataListResult.setForceCheck(false);
						dataListResult.setForceCheckFailCount(dataListResult.getForceCheckFailCount()+1);
					}
					else{
						dataListResult.setForceCheckSuccessCount(dataListResult.getForceCheckSuccessCount()+1);
					}
					if(dataResult.isException()){
						dataListResult.setException(true);
						dataListResult.setExceptionCount(dataListResult.getExceptionCount()+1);
					}
					dataListResult.setCheckCount(dataListResult.getCheckCount()+1);
					if(dataListResultParam.getResultLevel() == 0){
						if(!dataResult.isCheck()){
							dataListResult.getDataResultList().add(dataResult);
						}
					}
					else if(dataListResultParam.getResultLevel() == 1){
						if(!dataResult.isForceCheck()){
							dataListResult.getDataResultList().add(dataResult);
						}
					}
					else if(dataListResultParam.getResultLevel() == 2){
						if(!dataResult.isException()){
							dataListResult.getDataResultList().add(dataResult);
						}
					}
					else if(dataListResultParam.getResultLevel() == 99){
						dataListResult.getDataResultList().add(dataResult);
					}
				}
				else{
					break;
				}
			}
			return dataListResult;
		}
	}

	public void check(DataListResult dataListResult, CheckRuleTable checkRuleTable, List<Map<String, Object>> dataMapList, DataListResultParam dataListResultParam) throws Exception {
		
		if(dataListResultParam.isMaxDataResultBreak() && dataListResult.getDataResultList().size() >= dataListResultParam.getMaxDataResult()){
			return;
		}
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future<DataListResult>> futureList = new ArrayList<Future<DataListResult>>();
		
		int maxThread = dataListResultParam.getProcessorCount();
		for(int i=0;i<maxThread;i++){
			int startIndet = i*(dataMapList.size()/maxThread);
			int endIndex = (i+1)*(dataMapList.size()/maxThread);
			if(i == maxThread-1){
				endIndex = dataMapList.size();
			}
			List<Map<String, Object>> threadMap = dataMapList.subList(startIndet, endIndex);;
			if(threadMap.size() > 0){
				CheckThread checkThread = new CheckThread(checkRuleTable,threadMap,dataListResultParam);
				futureList.add(executorService.submit(checkThread));
			}
		}
		
		for(Future<DataListResult> future : futureList){
			DataListResult threadResult = future.get();
			
			if(!threadResult.isCheck()){
				dataListResult.setCheck(false);
			}
			if(!threadResult.isForceCheck()){
				dataListResult.setForceCheck(false);
			}
			if(threadResult.isException()){
				dataListResult.setException(true);
			}
			dataListResult.setCheckCount(dataListResult.getCheckCount() + threadResult.getCheckCount());
			dataListResult.setCheckFailCount(dataListResult.getCheckFailCount() + threadResult.getCheckFailCount());
			dataListResult.setCheckSuccessCount(dataListResult.getCheckSuccessCount() + threadResult.getCheckSuccessCount());
			dataListResult.setForceCheckFailCount(dataListResult.getForceCheckFailCount() + threadResult.getForceCheckFailCount());
			dataListResult.setForceCheckSuccessCount(dataListResult.getForceCheckSuccessCount() + threadResult.getForceCheckSuccessCount());
			dataListResult.setExceptionCount(dataListResult.getExceptionCount() + threadResult.getExceptionCount());
			if(!dataListResultParam.isMaxDataResultBreak() || dataListResult.getDataResultList().size() < dataListResultParam.getMaxDataResult()){
				dataListResult.getDataResultList().addAll(threadResult.getDataResultList());
			}
			if(dataListResultParam.isMaxDataResultBreak() && dataListResult.getDataResultList().size() > dataListResultParam.getMaxDataResult()){
				List<DataResult> removeList = dataListResult.getDataResultList().subList(dataListResultParam.getMaxDataResult(),dataListResult.getDataResultList().size()-1);
				dataListResult.getDataResultList().retainAll(removeList);
			}
		}
		executorService.shutdown();
	}
}
