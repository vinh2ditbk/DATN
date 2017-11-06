<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page session="true"%>

<!DOCTYPE html >
<html>
	<body>

		<div class="col-md-4">
			<div id="MainMenu">
				<div class="list-group panel">
					<a href="#admin-menu" class="list-group-item list-group-item-success" data-toggle="collapse" data-parent="#MainMenu">Quản lý Admin<i class="fa fa-caret-down"></i></a>
					<div class="collapse" id="admin-menu">
						<a href="#" class="list-group-item">Thông tin chung</a>
						<a href="#" class="list-group-item">Chỉnh sửa TT</a>
						<a href="#" class="list-group-item">Thay đổi mật khẩu</a>
					</div>

					<a href="#admin-teacher" class="list-group-item list-group-item-success" data-toggle="collapse" data-parent="#MainMenu">Quản lý giảng viên<i class="fa fa-caret-down"></i></a>
					<div class="collapse" id="admin-teacher">
						<a href="#" class="list-group-item">Thông tin giảng viên</a>
						<a href="/admin/create" class="list-group-item">Thêm giảng viên</a>
						<a href="#" class="list-group-item">Chỉnh sửa giảng viên</a>
						<a href="#" class="list-group-item">Xóa giảng viên</a>
					</div>
					<c:if test="${pageContext.request.userPrincipal.name != null}">
						<a href="javascript:formSubmit()" class="list-group-item list-group-item-success" data-parent="#MainMenu">Thoát</a>
					</c:if>
				</div>
			</div>
		</div>

	</body>
</html>
