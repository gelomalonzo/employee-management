package com.capstone.employeemanagement.service;

import com.capstone.employeemanagement.model.User;

public interface UserService {
	
	User getUserByUsername(String username);
	Boolean passwordMatches(User user, String password);

}
