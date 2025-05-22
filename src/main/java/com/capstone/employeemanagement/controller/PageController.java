package com.capstone.employeemanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {
	
	@GetMapping("/")
	public String indexPage(HttpSession session) {
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
	public String homePage(HttpSession session) {
		return "forward:/html/home.html";
	}

}
