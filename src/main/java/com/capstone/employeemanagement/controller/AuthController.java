package com.capstone.employeemanagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	
	@GetMapping("/user/info")
	public ResponseEntity<?> getUserInfo(Authentication auth) {
		Map<String, Object> response = new HashMap<>();
		
		String username = new String();
		String role = new String();
		
		if (auth == null) {
			username = null;
			role = "ROLE_ANONYMOUS";
		} else {
			username = auth.getName();
			role = auth.getAuthorities().iterator().next().getAuthority();
		}
		
		response.put("success", true);
		response.put("username", username);
		response.put("role", role);
		response.put("message", "Successfully retrieved user info.");
		
		return ResponseEntity.ok(response);
	}

}
