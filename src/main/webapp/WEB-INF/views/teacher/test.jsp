<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page session="true"%>

<!DOCTYPE html >

<html>
	<head>
		<title>Giảng viên - Tạo đề thi cho từng nhóm</title>
		<%@ include file="../head_tag.jsp"%>
	</head>
	<body>

		<%@ include file="../header.jsp"%>

		<div class="container-fluid" style="margin-top: 10px">
			<div class="row">
				<%@ include file="menu.jsp"%>

				<div class="col-md-8">
					<div class="bs-example well">
						<form class="form-create" name='createForm' action="/teacher/test" method="POST" enctype="multipart/form-data" >
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

							<div class="form-group">
								<label for="sel1">Chọn nhóm thi:</label>
								<select class="form-control" id="sel1" name="group">
									<c:forEach items="${groups}" var="group" varStatus="itr">
										<option>${group}</option>
									</c:forEach>
								</select>
							</div>

							<div class="form-group">
								<label for="inputTimer">Thời gian làm bài (phút):</label>
								<input type="input" class="form-control" id="inputTimer" name="timer" placeholder="Thời gian làm bài (phút)">
							</div>

							<div class="form-group">
								<label for="inputfile">Upload đề thi bằng file word:</label>
								<input type="file" class="form-control" id="inputfile" name="file" placeholder="Duyệt file word" accept=".doc,.docx">
							</div>

							<div>
								<button type="submit" class="btn btn-primary">Tạo</button>
								<button type="submit" class="btn btn-primary" style="float: right"><a id="bt-login" href="/teacher">Hủy</a></button>
							</div>
							<c:if test="${success != null && success}">
								<div style="color: blue">Thêm đề thi thành công</div>
							</c:if>
							<c:if test="${success != null && !success}">
								<div style="color: red"><c:out value="${error_message}"/></div>
							</c:if>
						</form>
					</div>
				</div>
			</div>
		</div>

		<%@ include file="condition.jsp"%>

		<%@ include file="../footer.jsp"%>

	</body>
</html>