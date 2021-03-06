package com.itbk.service;

import com.itbk.constant.Constant;
import com.itbk.model.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;

/**
 * Created by PC on 11/9/2017.
 */

@Component
public class HandleFileExelService {

	@Autowired
	private StudentService studentService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	GroupService groupService;

	@Autowired
	TeacherService teacherService;

	public boolean readFileExel (MultipartFile file, Teacher teacher, Group group, boolean isExistedGroup) {
		try {
			Workbook workbook = new HSSFWorkbook(file.getInputStream());
			Sheet sheet = null;
			if(isExistedGroup) {
				sheet = workbook.getSheetAt(0);
				readBySheet(sheet, group, isExistedGroup);
			} else {
				int numberOfSheet = workbook.getNumberOfSheets();
				for(int i = 0; i < numberOfSheet; i++) {
					System.out.println("chay toi day i = " + i);
					sheet = workbook.getSheetAt(i);
					Group groupNew = new Group();
					groupNew.setTeacher(teacher);
					groupNew.setName("N" + sheet.getSheetName());
					groupService.saveGroup(groupNew);
					readBySheet(sheet, groupNew, isExistedGroup);
				}
			}

			workbook.close();
			return true;
		} catch (Exception e) {
			System.out.println("Errors: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public void readBySheet(Sheet sheet, Group group, boolean isExistedGroup) {
		String string = Constant.NoDataInsert.NO_DATA_INSERT;
		String nameTeacher = "";

		for (Row row : sheet) {
			if (row.getRowNum() == 0) continue; //skip first row
			Student student = new Student();
			for (Cell cell : row) {
				switch (cell.getCellTypeEnum()) {
					case STRING:
						string = cell.toString();
						break;
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							string = cell.toString();
						} else {
							string = NumberToTextConverter.toText(cell.getNumericCellValue());
						}
						break;
				}
				switch (cell.getColumnIndex()) {
					case 1:
						student.setIdB(string);
						break;
					case 2:
						student.setName(string);
						break;
					case 3:
						student.setDateOfBirth(string);
						break;
					case 4:
						student.setClassStd(string);
						break;
					case 5:
						if (row.getRowNum() == 1) {
							nameTeacher = string;
						}
						break;
				}
				student.setTeacher(nameTeacher);
				if (isExistedGroup) { // read only a sheet case
					student.setGroup(group);
				} else { // read all sheets case
					student.setGroup(group);
				}
			}

			studentService.save(student);
			if (userService.findByUserName(student.getIdB()) == null) {
				User user = new User();
				user.setUsername(student.getIdB());
				user.setPassword(passwordEncoder.encode(convertPassword(student.getDateOfBirth())));
				HashSet<Role> roles = new HashSet<>();
				roles.add(roleService.findByName(Constant.RoleType.ROLE_STUDENT));
				user.setRoles(roles);
				userService.saveUser(user);
			}
		}
	}
	
	public String convertPassword(String password) {
		String []arrayPass = password.split("-");
		if(arrayPass[1].equals("Jan")) arrayPass[1] = "01";
		if(arrayPass[1].equals("Feb")) arrayPass[1] = "02";
		if(arrayPass[1].equals("Mar")) arrayPass[1] = "03";
		if(arrayPass[1].equals("Apr")) arrayPass[1] = "04";
		if(arrayPass[1].equals("May")) arrayPass[1] = "05";
		if(arrayPass[1].equals("Jun")) arrayPass[1] = "06";
		if(arrayPass[1].equals("Jul")) arrayPass[1] = "07";
		if(arrayPass[1].equals("Aug")) arrayPass[1] = "08";
		if(arrayPass[1].equals("Sep")) arrayPass[1] = "09";
		if(arrayPass[1].equals("Oct")) arrayPass[1] = "10";
		if(arrayPass[1].equals("Nov")) arrayPass[1] = "11";
		if(arrayPass[1].equals("Dec")) arrayPass[1] = "12";
		
		return arrayPass[0] + arrayPass[1] + arrayPass[2];
	}
}
