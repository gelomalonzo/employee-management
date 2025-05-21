package com.capstone.employeemanagement.controller;

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
		return "forward:/html/login.html";
	}
	
	@GetMapping("/home")
	public String homePage() {
		return "forward:/html/home.html";
	}

}
