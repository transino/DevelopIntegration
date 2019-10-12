package net.zhangqiu.service.entity.result;

public class CommonServiceResult {
	private boolean success;
	private String message;
	private Object data;
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
	public CommonServiceResult(){
		success = true;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getData() {
		return data;
	}
}
