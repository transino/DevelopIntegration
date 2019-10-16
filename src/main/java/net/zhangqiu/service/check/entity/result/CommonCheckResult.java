package net.zhangqiu.service.check.entity.result;

public class CommonCheckResult {
	private boolean success;
	private String message;
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
	public CommonCheckResult(){
		success = true;
		message = "";
	}
}
