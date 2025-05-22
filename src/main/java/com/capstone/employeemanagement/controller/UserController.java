package com.capstone.employeemanagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.employeemanagement.model.User;
import com.capstone.employeemanagement.service.UserServiceImpl;

import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {
	
	@Autowired
	private final UserServiceImpl userService;
	
	public UserController(final UserServiceImpl userService) {
		this.userService = userService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(
		@RequestParam(name = "username", required = true) String username,
		@RequestParam(name = "password", required = true) String password,
		HttpSession session
	) {
		Map<String, Object> response = new HashMap<>();
		
		User user = userService.getUserByUsername(username);
		
		if (user == null) {
			response.put("success", false);
			response.put("message", "Invalid credentials: Username not found.");
//			response.put("redirect", "/login");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		if (!userService.passwordMatches(user, password)) {
			response.put("success", false);
			response.put("message", "Invalid credentials: Incorrect password.");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		session.setAttribute("user", user);
		response.put("success", true);
		response.put("message", "Successfully logged in.");
		response.put("redirect", "/home");
		
		return ResponseEntity.ok(response);
	}

}
