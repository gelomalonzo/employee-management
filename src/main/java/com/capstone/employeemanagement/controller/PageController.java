package com.capstone.employeemanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	
	@GetMapping("/")
	public String indexPage() {
		return "redirect:/home";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
			return "redirect:/home";
		}

		return "forward:/html/login.html";
	}
	
	@GetMapping("/home")
	public String homePage() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null && !auth.isAuthenticated() && auth.getPrincipal().equals("anonymousUser")) {
			return "redirect:/login";
		}
		
		return "forward:/html/home.html";
	}
	
	@GetMapping("/departments")
	public String departmentsPage() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null && !auth.isAuthenticated() && auth.getPrincipal().equals("anonymousUser")) {
			return "redirect:/login";
		}
		
		return "forward:/html/department.html";
	}
	
	@GetMapping("/users")
	public String usersPage() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null && !auth.isAuthenticated() && auth.getPrincipal().equals("anonymousUser")) {
			return "redirect:/login";
		}
		
		return "forward:/html/user.html";
	}

}
