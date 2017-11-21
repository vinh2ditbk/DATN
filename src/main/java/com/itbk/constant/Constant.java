package com.itbk.constant;

/**
 * Created by PC on 11/9/2017.
 */
public final class Constant {

	public final class SeparateTest {
		public static final String SEPARATE_QUESTION = "###";
		public static final String SEPARATE_ANSWER = "XXX";
	}

	public final class NoDataInsert {
		public static final String NO_DATA_INSERT = "FFFFFF";
	}

	public final class RoleType {
		public static final String ROLE_ADMIN = "ROLE_ADMIN";
		public static final String ROLE_TEACHER = "ROLE_TEACHER";
		public static final String ROLE_STUDENT = "ROLE_STUDENT";
	}

	public final class Pattern {
		public static final String PATTERN_USERNAME = "\\w*";
		public static final String PATTERN_PASS = "\\w*";
	}

	public final class ErrorMessage {
		public static final String ERROR_FORMAT_USERNAME = "Tài khoản bạn nhập không đúng định dạng (định dạng đúng là các ký tự a-z hoặc A-Z hoặc dấu '_')";
		public static final String ERROR_FORMAT_PASS = "Mật khẩu bạn nhập không đúng định dạng (định dạng đúng là các ký tự a-z hoặc A-Z hoặc dấu '_')";
		public static final String ERROR_PASS_INCORRECT = "Mật khẩu bạn nhập không đúng";
		public static final String ERROR_RE_PASS_INCORRECT = "Mật khẩu bạn nhập lại không khớp với mật khẩu trước đó";
		public static final String ERROR_EMPTY_INPUT = "Bạn chưa nhập dữ liệu";
		public static final String ERROR_EXISTED_USERNAME = "Tên tài khoản đã tồn tại";
	}
}