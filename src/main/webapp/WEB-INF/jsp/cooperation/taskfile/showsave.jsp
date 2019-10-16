<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>任务文件</title>
<script type="text/javascript">
function submitclick() {
	document.form.action = "save";
    document.form.submit();
}
function deleteclick() {
	document.form.action = "delete";
    document.form.submit();
}
function inittableclick() {
	document.form.action = "initTable";
    document.form.submit();
}
</script>
</head>
<body>
    <form name="form" method="post" enctype="multipart/form-data">
        <fieldset>
        	<legend>提交任务文件</legend>
        	<p id="buttons">
                <a href="/cooperation">首页</a>
            </p>
            <p>
                <label>项目编号：</label> <input type="text" id="strProjectCode" name="strProjectCode" tabindex="1">
            </p>
            <p>
                <label>任务编号：</label> <input type="text" id="strTaskCode" name="strTaskCode" tabindex="2">
            </p>
            <p>
                <label>选择文件：</label> <input type="file" id="file" name="file" tabindex="3">
            </p>
            <p id="buttons">
                <input id="reset" type="reset" tabindex="4" value="取消">
                <input id="submit" type="submit" tabindex="4" value="删除任务文件" onclick="deleteclick()">
                <input id="submit" type="submit" tabindex="4" value="初始化任务表" onclick="inittableclick()">
                <input id="submit" type="submit" tabindex="5" value="提交" onclick="submitclick()">
            </p>
        </fieldset>
    </form>
</body>
</html>