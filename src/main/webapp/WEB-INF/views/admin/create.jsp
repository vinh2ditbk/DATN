<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html >
<html>
	<head>
		<title>Quản trị</title>
		<link type="text/css" href="/css/bootstrap.css" rel="stylesheet" />
		<link type="text/css" href="/css/app.css" rel="stylesheet" />
	</head>
<body>
	<h1>Title : ${title}</h1>
	<c:if test="${pageContext.request.userPrincipal.name != null}">
		<% if (!request.isUserInRole("ADMIN")) { %>
           <% response.sendRedirect("/403"); %>
        <% } %>
	</c:if>

	<c:if test="${pageContext.request.userPrincipal.name == null}">
		<% response.sendRedirect("/login"); %>
	</c:if>
	<form class="form-create" name='createForm' action="/teacher/group" method="POST">
    		<table class="table table-bordered">
    			<tbody>
    				<tr>
    					<td>Tên nhóm thi:</td>
    					<td><input type="text" name="groupid" placeholder="Tên nhóm thi"/></td>
    				</tr>
    				<tr>
    					<td>Duyệt file excel:</td>
    					<td><input type="file" name="file" accept=".xls,.xlsx"/></td>
    				</tr>
    			</tbody>
    		</table>

    		<button class="btn btn-primary type="submit">Tạo TK giảng viên</button>
    	</form>

	<script type="application/javascript" src="js/jquery.js"></script>
	<script type="application/javascript" src="js/bootstrap.js"></script>
</body>
</html>