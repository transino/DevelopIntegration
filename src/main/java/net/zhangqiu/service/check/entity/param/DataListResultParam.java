package net.zhangqiu.service.check.entity.param;

public class DataListResultParam {
	private int maxDataResult;
	private int processorCount;
	private boolean maxDataResultBreak;
	private int resultLevel;
	private DataResultParam dataResultParam;
	
	public int getMaxDataResult() {
		return maxDataResult;
	}

	public void setMaxDataResult(int maxDataResult) {
		this.maxDataResult = maxDataResult;
	}

	public int getProcessorCount() {
		return processorCount;
	}

	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}
	
	public void setMaxDataResultBreak(boolean maxDataResultBreak) {
		this.maxDataResultBreak = maxDataResultBreak;
	}

	public boolean isMaxDataResultBreak() {
		return maxDataResultBreak;
	}

	
	public void setResultLevel(int resultLevel) {
		this.resultLevel = resultLevel;
	}

	public int getResultLevel() {
		return resultLevel;
	}


	public DataResultParam getDataResultParam() {
		return dataResultParam;
	}

	public void setDataResultParam(DataResultParam dataResultParam) {
		this.dataResultParam = dataResultParam;
	}

	public DataListResultParam(){
		maxDataResult = 100;
		processorCount = Runtime.getRuntime().availableProcessors();
		maxDataResultBreak = true;
		resultLevel = 0;
		dataResultParam = new DataResultParam();
	}

	
}
