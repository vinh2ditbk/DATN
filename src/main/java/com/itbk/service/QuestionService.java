package com.itbk.service;

import com.itbk.model.Question;

import java.util.List;

/**
 * Created by PC on 10/27/2017.
 */
public interface QuestionService {

	Question saveQuestion(Question question);

	Question findLastest();

	List<Question> getExaminationByGroupId(int groupId);

	List<Question> findAllQuestionByGroupId(int groupId);

	void deleteQuestionById(int id);

	void deleteAllQuestionByGroupId(int groupId);
}
