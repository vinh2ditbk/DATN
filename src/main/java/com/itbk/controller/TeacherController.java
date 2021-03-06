package com.itbk.controller;

import com.itbk.constant.Constant;
import com.itbk.dto.Examination;
import com.itbk.model.*;
import com.itbk.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping(value = {"/teacher"})
public class TeacherController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private StudentService studentService;

	@Autowired
	private HandleFileExelService handleFileExelService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private AnswerService answerService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private HandleFileWordService handleFileWordService;

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String infoTeacherGet(HttpServletRequest request, Model model) throws IOException {
		String userName = getUserName();
		if(userName != null) {
			model.addAttribute("username", userName);
		} else {
			return "redirect:/login";
		}
		ArrayList<Group> groups = (ArrayList<Group>)groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(userName).getId());
		int numberOfStudent = 0;
		for(Group group : groups) {
			numberOfStudent += (int)studentService.countStudentByGroupId(group.getId());
		}

		model.addAttribute("countGroup", groups.size());
		model.addAttribute("countStudent", numberOfStudent);

		return "/teacher/info";
	}

	@RequestMapping(value = "/changeinfo", method = RequestMethod.GET)
	public String getChangeInfo() {

		return "/teacher/changeinfo";
	}

	@RequestMapping(value = "/changeinfo", method = RequestMethod.POST)
	public String postChangeInfo(@RequestParam("account") String account, @RequestParam("oldpass") String oldPass,
								 @RequestParam("newpass") String newPass, @RequestParam("renewpass") String reNewPass, Model model) {
		User teacher = userService.findByUserName(getUserName());
		if(account.equals("") || oldPass.equals("") || newPass.equals("") || reNewPass.equals("")) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			return "/teacher/changeinfo";
		}
		else if(!account.matches(Constant.Pattern.PATTERN_USERNAME)) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_FORMAT_USERNAME);
			return "/teacher/changeinfo";
		}
		else if(!passwordEncoder.matches(oldPass, teacher.getPassword())) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_PASS_INCORRECT);
			return "/teacher/changeinfo";
		}
		else if(!newPass.matches(Constant.Pattern.PATTERN_PASS)) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_FORMAT_PASS);
			return "/teacher/changeinfo";
		}
		else if(!reNewPass.equals(newPass)) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_RE_PASS_INCORRECT);
			return "/teacher/changeinfo";
		} else {
			teacher.setUsername(account);
			teacher.setPassword(passwordEncoder.encode(newPass));

			Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
			Set<Role> roles = teacher.getRoles();
			for (Role role : roles) {
				grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
			}

			org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(teacher.getUsername(), teacher.getPassword(), grantedAuthorities);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			userService.saveUser(teacher);

			model.addAttribute("success", true);
			return "login";
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createGroupGet(HttpServletRequest request, Model model) throws IOException {

		return "/teacher/create";
	}

	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createGroupPost(@RequestParam("file") MultipartFile file, @RequestParam("group") String group, Model model) {
		if(file.isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_FILE_IS_NOT_EXIST);
			return "/teacher/create";
		} else if(group.equals("")) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			return "/teacher/create";
		}
		boolean resultReadFileExel = false;
		Teacher teacher = teacherService.findTeacherByUsername(getUserName());
		if(group != null) {
			Group groupObj = new Group(group);
			groupObj.setTeacher(teacher);
			groupService.saveGroup(groupObj);
			resultReadFileExel = handleFileExelService.readFileExel(file, teacher, groupObj, true);
		}
		if(resultReadFileExel) {
			model.addAttribute("success", true);
			return "/teacher/create";

		} else {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_WHILE_READ_FILE);
			model.addAttribute("success", false);
			return "/teacher/create";
		}
	}

	// createAlGroup start
	@RequestMapping(value = "/create_all", method = RequestMethod.GET)
	public String createAllGroupGet(HttpServletRequest request, Model model) throws IOException {

		return "/teacher/create_all";
	}

	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	@RequestMapping(value = "/create_all", method = RequestMethod.POST)
	public String createAllGroupPost(@RequestParam("file") MultipartFile file, @RequestParam("group") String group, Model model) {
		if(file.isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_FILE_IS_NOT_EXIST);
			return "/teacher/create_all";
		}
		boolean resultReadFileExel = false;
		Teacher teacher = teacherService.findTeacherByUsername(getUserName());
		if(group == null || group.equals("")) {
			resultReadFileExel = handleFileExelService.readFileExel(file, teacher, null, false);
		}
		if(resultReadFileExel) {
			model.addAttribute("success", true);
			return "/teacher/create_all";
		} else {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_WHILE_READ_FILE);
			model.addAttribute("success", false);
			return "/teacher/create_all";
		}
	}
	// createAlGroup finish

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String createExaminationGet(Model model) throws IOException {
		if(getUserName() != null) {
			Teacher teacher = teacherService.findGroupIdByUsername(getUserName());
			if(teacher == null) {
				model.addAttribute("success", false);
				model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_GROUP);
				return "/teacher/test";
			}
			ArrayList<String> groups = new ArrayList<>();
			for(Group group : teacher.getGroups()) {
				groups.add(group.getName());
			}

			model.addAttribute("groups", groups);
		}

		return "/teacher/test";
	}

	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public String createExaminationPost(@RequestParam("file") MultipartFile file, @RequestParam("timer") String timer,
				@RequestParam(value = "group", required = false) String group, Model model) {
		if(group == null) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			return "/teacher/test";
		} else if(file.isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_FILE_IS_NOT_EXIST);
			return "/teacher/test";
		} else if(timer.equals("")) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			return "/teacher/test";
		}
		try {
			Long.parseLong(timer);
		} catch (Exception e) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NOT_NUMBER);
			return "/teacher/test";
		}

		boolean resultReadFileWord= false;
		if(group != null) {
			resultReadFileWord = handleFileWordService.readFileWord(file, teacherService.findTeacherByUsername(getUserName()), groupService.findGroupByGroupName(group), timer);
		}
		if(resultReadFileWord) {
			model.addAttribute("success", true);
			return "/teacher/test";
		} else {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_WHILE_READ_FILE);
			model.addAttribute("success", false);
			return "/teacher/test";
		}
	}

	@RequestMapping(value = "/test_all", method = RequestMethod.GET)
	public String createExaminationAllGet(Model model) throws IOException {

		return "/teacher/test_all";
	}

	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	@RequestMapping(value = "/test_all", method = RequestMethod.POST)
	public String createExaminationAllPost(@RequestParam("file") MultipartFile file, @RequestParam("timer") String timer,
										@RequestParam("group") String group, Model model) {
		if(file.isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_FILE_IS_NOT_EXIST);
			return "/teacher/test_all";
		} else if(timer.equals("")) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			return "/teacher/test_all";
		}
		try {
			Long.parseLong(timer);
		} catch (Exception e) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NOT_NUMBER);
			return "/teacher/test_all";
		}

		boolean resultReadFileWord= false;
		if(group == null || group.equals("")) {
			resultReadFileWord = handleFileWordService.readFileWord(file, teacherService.findTeacherByUsername(getUserName()), null, timer);
		}
		if(resultReadFileWord) {
			model.addAttribute("success", true);
			return "/teacher/test_all";
		} else {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_WHILE_READ_FILE);
			model.addAttribute("success", false);
			return "/teacher/test_all";
		}
	}

	@RequestMapping(value = "/preview", method = RequestMethod.GET)
	public String previewExaminationGet(Model model) throws IOException {
		if (getUserName() != null) {
			Teacher teacher = teacherService.findTeacherByUsername(getUserName());
			ArrayList<String> groups = new ArrayList<>();
			for(Group group : teacher.getGroups()) {
				groups.add(group.getName());
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/preview";
	}

	@RequestMapping(value = "/preview", method = RequestMethod.POST)
	public String previewExaminationPost(@RequestParam(value = "group", required = false) String group, Model model) throws IOException {
		if(group == null) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			model.addAttribute("success", false);
			return "/teacher/preview";
		}
		List<Question> list = questionService.getExaminationByGroupId(groupService.findGroupByGroupName(group).getId());
		if(list.isEmpty()) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NOT_EXAM);
			model.addAttribute("success", false);
			return "/teacher/preview";
		}
		ArrayList<Examination> examinations = new ArrayList<>();
		int count = 0;
		for (Question a : list) {
			Examination examination = new Examination();
			examination.setQuestion("Câu " + (++count) + ": " + a.getName());
			examination.setAnswers(a.getAnswers());
			examination.setRadio(a.isRadio());
			examinations.add(examination);
		}

		model.addAttribute("examinations", examinations);
		model.addAttribute("groups", group);

		model.addAttribute("success", true);
		return "/teacher/preview";
	}


	@RequestMapping(value = "/output", method = RequestMethod.GET)
	public String outputGet(Model model) throws IOException {
		if (getUserName() != null) {
			Teacher teacher = teacherService.findTeacherByUsername(getUserName());
			ArrayList<String> groups = new ArrayList<>();
			for(Group group : teacher.getGroups()) {
				groups.add(group.getName());
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/output";
	}

	@RequestMapping(value = "/output", method = RequestMethod.POST)
	public ModelAndView outputPost(@RequestParam("fileName") String fileName, @RequestParam(value = "group", required = false) String group, HttpServletResponse response, ModelMap model) throws IOException {
		if(group == null) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output", model);
		} else if(fileName.equals("")) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output", model);
		}
		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
		ArrayList<Object> params = new ArrayList<>();
		params.add(groupService.findGroupByGroupName(group).getId());
		params.add(fileName);

		return new ModelAndView("excelPOIView", "params", params);
	}

	@RequestMapping(value = "/output_all", method = RequestMethod.GET)
	public String outputAllGet(Model model) throws IOException {

		return "/teacher/output_all";
	}

	@RequestMapping(value = "/output_all", method = RequestMethod.POST)
	public ModelAndView outputAllPost(@RequestParam("fileName") String fileName, HttpServletResponse response, ModelMap model) throws IOException {
		if(fileName.equals("")) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output_all", model);
		}
		ArrayList<Group> groups = groupService.findAllGroup();
		if(groups.isEmpty()) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_GROUP_FOR_OUTPUT);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output_all", model);
		}
		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
		ArrayList<Object> params = new ArrayList<>();
		params.add(null);
		params.add(fileName);

		return new ModelAndView("excelPOIView", "params", params);
	}


	@RequestMapping(value = "/output_test", method = RequestMethod.GET)
	public String outputTestGet(Model model) throws IOException {
		if (getUserName() != null) {
			Teacher teacher = teacherService.findTeacherByUsername(getUserName());
			ArrayList<String> groups = new ArrayList<>();
			for(Group group : teacher.getGroups()) {
				groups.add(group.getName());
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/output_test";
	}

	@RequestMapping(value = "/output_test", method = RequestMethod.POST)
	public ModelAndView outputTestPost(@RequestParam("fileName") String fileName, @RequestParam(value = "group", required = false) String group, @RequestParam("original") String original,
					   HttpServletResponse response, ModelMap model) throws IOException {
		if(group == null) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output_test", model);
		} else if(fileName.equals("")) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output_test", model);
		}
		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
		ArrayList<Object> params = new ArrayList<>();
		params.add(groupService.findGroupByGroupName(group).getId());
		params.add(fileName);
		params.add(original);

		return new ModelAndView("excelPOIView", "params", params);
	}

	@RequestMapping(value = "/output_all_test", method = RequestMethod.GET)
	public String outputAllTestGet(Model model) throws IOException {

		return "/teacher/output_all_test";
	}

	@RequestMapping(value = "/output_all_test", method = RequestMethod.POST)
	public ModelAndView outputAllTestPost(@RequestParam("fileName") String fileName, @RequestParam("original")
		String original, HttpServletResponse response, ModelMap model) throws IOException {
		if(fileName.equals("")) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output_all_test", model);
		}
		ArrayList<Group> groups = groupService.findAllGroup();
		if(groups.isEmpty()) {
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_GROUP_FOR_OUTPUT);
			model.addAttribute("success", false);
			return new ModelAndView("/teacher/output_all_test", model);
		}
		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
		ArrayList<Object> params = new ArrayList<>();
		params.add(null);
		params.add(fileName);
		params.add(original);

		return new ModelAndView("excelPOIView", "params", params);
	}

	@RequestMapping(value = "/infostudent", method = RequestMethod.GET)
	public String infoStudentGet(Model model) throws IOException {
		if (getUserName() != null) {
			Object objects = groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(getUserName()).getId());
			ArrayList<String> groups = new ArrayList<>();
			if(objects != null) {
				for(Group group : (ArrayList<Group>)objects) {
					groups.add(group.getName());
				}
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/infostudent";
	}

	@RequestMapping(value = "/infostudent", method = RequestMethod.POST)
	public String infoStudentPost(@RequestParam(value = "group", required = false) String nameGroup, Model model) throws IOException {
		if(nameGroup == null) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			return "/teacher/infostudent";
		}
		Group group = groupService.findGroupByGroupName(nameGroup);
		model.addAttribute("nameGroup", group.getName());
		try {
			Object students = studentService.findAllByGroupId(group.getId());
			model.addAttribute("countStudent", ((ArrayList<Group>)(students)).size());
			int countAllStudentTested = 0;
			int countAllStudentNotTested = 0;
			for(Student student : ((ArrayList<Student>)(students))) {
				if(student.isTested()) {
					countAllStudentTested ++;
				} else {
					countAllStudentNotTested ++;
				}
			}
			model.addAttribute("countStudentTested", countAllStudentTested);
			model.addAttribute("countStudentNotTested", countAllStudentNotTested);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Object objects = groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(getUserName()).getId());
		ArrayList<String> groups = new ArrayList<>();
		if(objects != null) {
			for(Group groupNew : (ArrayList<Group>)objects) {
				groups.add(groupNew.getName());
			}
		}
		model.addAttribute("groups", groups);

		model.addAttribute("success", true);
		return "/teacher/infostudent";
	}

	@RequestMapping(value = "/change_group", method = RequestMethod.GET)
	public String changeInfoGroupGet(Model model) throws IOException {
		if (getUserName() != null) {
			Object objects = groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(getUserName()).getId());
			ArrayList<String> groups = new ArrayList<>();
			if(objects != null) {
				for(Group group : (ArrayList<Group>)objects) {
					groups.add(group.getName());
				}
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/change_group";
	}

	@RequestMapping(value = "/change_group", method = RequestMethod.POST)
	public String changeInfoGroupPost(@RequestParam(value = "group", required = false) String nameGroup,
				  @RequestParam(value = "name") String name, Model model) throws IOException {
		if(nameGroup == null) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			return "/teacher/change_group";
		} else if(name.equals("")) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_EMPTY_INPUT);
			return "/teacher/change_group";
		}
		Group group = groupService.findGroupByGroupName(nameGroup);
		groupService.updateGroupName(name, group.getId());

		Object objects = groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(getUserName()).getId());
		ArrayList<String> groups = new ArrayList<>();
		if(objects != null) {
			for(Group groupNew : (ArrayList<Group>)objects) {
				groups.add(groupNew.getName());
			}
		}
		model.addAttribute("groups", groups);

		model.addAttribute("success", true);
		return "/teacher/change_group";
	}

	@RequestMapping(value = "/delete_group", method = RequestMethod.GET)
	public String deleteGroupGet(Model model) throws IOException {
		if (getUserName() != null) {
			Object objects = groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(getUserName()).getId());
			ArrayList<String> groups = new ArrayList<>();
			if(objects != null) {
				for(Group group : (ArrayList<Group>)objects) {
					groups.add(group.getName());
				}
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/delete_group";
	}

	@RequestMapping(value = "/delete_group", method = RequestMethod.POST)
	public String deleteGroupPost(@RequestParam(value = "group", required = false) String nameGroup, Model model) throws IOException {
		if(nameGroup == null) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			return "/teacher/delete_group";
		}
		Group group = groupService.findGroupByGroupName(nameGroup);
		ArrayList<Question> questions = (ArrayList<Question>)questionService.findAllQuestionByGroupId(group.getId());
		questionService.deleteAllQuestionByGroupId(group.getId());
		for(Question question : questions) {
			answerService.deleteAllAnswerByQuestionId(question.getId());
			questionService.deleteQuestionById(question.getId());
		}

		ArrayList<Student> students = studentService.findAllByGroupId(group.getId());
		for(Student student : students) {
			userService.deleteUserById(userService.findByUserName(student.getIdB()).getId());
		}
		studentService.deleteAllStudentByGroupId(group.getId());
		groupService.deleteGroupById(group.getId());

		model.addAttribute("success", true);
		return "/teacher/delete_group";
	}

	@RequestMapping(value = "/delete_exam", method = RequestMethod.GET)
	public String deleteExamGet(Model model) throws IOException {
		if (getUserName() != null) {
			Object objects = groupService.findGroupsByTeacherId(teacherService.findTeacherByUsername(getUserName()).getId());
			ArrayList<String> groups = new ArrayList<>();
			if(objects != null) {
				for(Group group : (ArrayList<Group>)objects) {
					groups.add(group.getName());
				}
			}
			model.addAttribute("groups", groups);
		}

		return "/teacher/delete_exam";
	}

	@RequestMapping(value = "/delete_exam", method = RequestMethod.POST)
	public String deleteExamPost(@RequestParam(value = "group", required = false) String nameGroup, Model model) throws IOException {
		if(nameGroup == null) {
			model.addAttribute("success", false);
			model.addAttribute("error_message", Constant.ErrorMessage.ERROR_NO_DATA);
			return "/teacher/delete_exam";
		}
		Group group = groupService.findGroupByGroupName(nameGroup);
		ArrayList<Question> questions = (ArrayList<Question>)questionService.findAllQuestionByGroupId(group.getId());
		questionService.deleteAllQuestionByGroupId(group.getId());
		for(Question question : questions) {
			answerService.deleteAllAnswerByQuestionId(question.getId());
			questionService.deleteQuestionById(question.getId());
		}

		model.addAttribute("success", true);
		return "/teacher/delete_exam";
	}


	public String getUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = null;
		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUsername();
		}

		return userName;
	}
}