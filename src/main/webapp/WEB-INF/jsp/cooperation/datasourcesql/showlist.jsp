<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>datasourcesql</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script type="text/javascript">
function datasourcechange() {
	document.form.action = "cooperation/datasourcesql/showList";
    document.form.submit();
}

</script>
  </head>
  <body>
    <form name="form" method="post" >
    <fieldset>
        <legend>数据源SQL日志</legend>
        <p id="buttons">
            <a href="/cooperation">首页</a>
        </p>
        <p>
                <label>数据源：</label>
                <select name="datasourceNameKey" id="datasourceNameKey" onchange="datasourcechange()">
	                <c:forEach items="${datasourcelist}" var="datasource">
	  	                <option value="${datasource}" <c:if test="${currentdatasource==datasource}">selected</c:if>>
		                ${datasource}
		                </option>
	                </c:forEach>
                </select>
        </p>
        <c:forEach items="${datasourcesqllist}" var="datasourcesql">
           <p>${datasourcesql.STRSQLID}|${datasourcesql.STRSTATE}|<a href="cooperation/datasourcesql/delete?strSqlId=${datasourcesql.STRSQLID}&datasourceNameKey=${currentdatasource}">删除</a></p>
        </c:forEach>
    </fieldset>
    </form>
  </body>
</html>
