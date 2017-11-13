package com.itbk.service;

import com.itbk.model.Group;

import java.util.ArrayList;

/**
 * Created by PC on 11/9/2017.
 */
public interface GroupService {

	Group saveGroup(Group group);

	Group findGroupByGroupName(String name);

	Group findGroupById(int id);

	ArrayList<Group> findAllGroup();
}
