<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>databasecheckupdate</title>
<script type="text/javascript">
function submitclick() {
	document.form.action = "databaseCheckUpdate";
    document.form.submit();
}
</script>
</head>
<body>
    <form name="form" method="post">
        <fieldset>
        	<legend>校验更新数据库</legend>
        	<p id="buttons">
                <a href="/cooperation">首页</a>
            </p>
            <p>
                <label>表：</label>
                <select name="tableNameKey" id="tableNameKey">
	                <c:forEach items="${tablelist}" var="table">
	  	                <option value="${table}">
		                ${table}
		                </option>
	                </c:forEach>
                </select>
                <label>缓存条数：</label>
                <select name="cacheLine" id="cacheLine">
                    <option value="100" selected >100</option>
                    <option value="200" >200</option>
                    <option value="500" >500</option>
                    <option value="1000" >1000</option>
                    <option value="2000" >2000</option>
                    <option value="5000"  >5000</option>
                    <option value="10000" >10000</option>
                </select>
                <label>线程数：</label>
                <select name="processorCount" id="processorCount">
                    <option value="1" selected >1</option>
                    <option value="2" >2</option>
                    <option value="5" >5</option>
                    <option value="10" >10</option>
                    <option value="20" >20</option>
                </select>
            </p>
            <p>
                <label>SQL：</label>
            </p>
            <p>
                <textarea id="sql" name="sql" style="width: 800px; height: 200px"></textarea>
            </p>
            <p>
                <label>UpdateJson：</label>
            </p>
            <p>
                <textarea id="updateJson" name="updateJson" style="width: 800px; height: 50px"></textarea>
            </p>
            <p id="buttons">
                <input id="reset" type="reset" tabindex="4" value="取消">
                <input id="submit" type="submit" tabindex="5" value="校验更新" onclick="submitclick()">
            </p>
        </fieldset>
    </form>
</body>
</html>