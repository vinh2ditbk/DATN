package com.itbk.service;

import com.itbk.model.Teacher;
import com.itbk.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by PC on 10/26/2017.
 */

@Component
@Service("teacherService")
public class TeacherServiceImpl implements TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;

	@Override
	public Teacher saveTeacher(Teacher teacher) {
		return teacherRepository.save(teacher);
	}

	@Override
	public int updatePassword(String password, String username) {
		return teacherRepository.updatePassword(password, username);
	}

	@Override
	public Teacher findTeacherByUsername(String userName) {
		return teacherRepository.findTeacherByUsername(userName);
	}

	@Override
	public Teacher findTeacherByName(String name) {
		return teacherRepository.findTeacherByName(name);
	}

	@Override
	public Teacher findGroupIdByUsername(String userName) {
		return teacherRepository.findGroupIdByUsername(userName);
	}

	@Override
	public Object findAllTeacher() {
		return teacherRepository.findAll();
	}

	@Override
	public Object countAllTeacher() {
		return teacherRepository.count();
	}

	@Override
	public void deleteTeacherById(int id) {
		teacherRepository.delete(id);
	}


}
