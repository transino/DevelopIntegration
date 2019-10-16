<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>executesql</title>
<script type="text/javascript">
function submitclick() {
	document.form.action = "executeSql";
    document.form.submit();
}
</script>
</head>
<body>
    <form name="form" method="post">
        <fieldset>
        	<legend>ExecuteSql</legend>
        	<p id="buttons">
                <a href="/cooperation">首页</a>
            </p>
            <p>
                <label>数据源：</label>
                <select name="datasourceNameKey" id="datasourceNameKey">
	                <c:forEach items="${datasourcelist}" var="datasource">
	  	                <option value="${datasource}" <c:if test="${currentdatasource==datasource}">selected</c:if>>
		                ${datasource}
		                </option>
	                </c:forEach>
                </select>
            </p>
            <p>
                <label>JsonData：</label>
            </p>
            <p>
                <textarea id="jsonData" name="jsonData" style="width: 1000px; height: 300px"></textarea>
            </p>
            <p id="buttons">
                <input id="reset" type="reset" tabindex="4" value="取消">
                <input id="submit" type="submit" tabindex="5" value="执行" onclick="submitclick()">
            </p>
        </fieldset>
    </form>
</body>
</html>