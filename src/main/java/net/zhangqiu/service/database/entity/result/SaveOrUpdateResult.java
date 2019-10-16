package net.zhangqiu.service.database.entity.result;

import net.zhangqiu.service.check.entity.result.DataResult;

public class SaveOrUpdateResult {
	private boolean success;
	private String message;
	private DataResult dataResult;
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setDataResult(DataResult dataResult) {
		this.dataResult = dataResult;
	}
	public DataResult getDataResult() {
		return dataResult;
	}
	public SaveOrUpdateResult(){
		success = true;
	}
}
