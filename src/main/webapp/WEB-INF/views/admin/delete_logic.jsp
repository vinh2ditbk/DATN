<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page session="true"%>

<!DOCTYPE html >

<html>
	<head>
		<title>Quản trị - Xóa tài khoản giảng viên tạm thời</title>
		<%@ include file="../head_tag.jsp"%>
	</head>
	<body>

		<%@ include file="../header.jsp"%>

		<div class="container-fluid" style="margin-top: 10px">
			<div class="row">
				<%@ include file="menu.jsp"%>

				<div class="col-md-8">
					<div class="bs-example well">
						<form class="form-create" name='createForm' action="/admin/delete_logic" method="POST">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
							<label for="sel1">Chọn giảng viên:</label>
							<select class="form-control" id="sel1" name="teacher">
								<c:forEach items="${teachers}" var="teacher" varStatus="itr">
									<option>${teacher}</option>
								</c:forEach>
							</select>

							<div style="margin-top: 5px">
								<button type="submit" class="btn btn-primary">Xóa</button>
								<button type="submit" class="btn btn-primary" style="float: right"><a id="bt-login" href="/admin">Hủy</a></button>
							</div>

							<c:if test="${success != null && success}">
								<div style="color: blue">Xóa logic giảng viên thành công</div>
							</c:if>
							<c:if test="${success != null && !success}">
								<div style="color: red"><c:out value="${error_message}"/></div>
							</c:if>

						</form>
					</div>
				</div>
			</div>
		</div>

		<%@ include file="../footer.jsp"%>

		<%@ include file="condition.jsp"%>

	</body>
</html>