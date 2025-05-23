package com.capstone.employeemanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.service.DepartmentServiceImpl;

@RestController
public class DepartmentController {
	
	@Autowired
	private final DepartmentServiceImpl departmentService;
	
	public DepartmentController(final DepartmentServiceImpl departmentService) {
		this.departmentService = departmentService;
	}
	
	@GetMapping("/departments/all")
	public ResponseEntity<?> getAllDepartments() {
		List<Department> departments = departmentService.getAllDepartments();
		
		Map<String, Object> response = new HashMap<>();
		
		response.put("success", true);
		response.put("departments", departments);
		
		if (departments.isEmpty()) {
			response.put("message", "Department list is empty.");
		} else {
			response.put("message", "Successfully retrieved departments list.");
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/departments/getIdByName")
	public ResponseEntity<?> getDepartmentIdByName(
		@RequestParam("departmentName") String departmentName
	) {
		Map<String, Object> response = new HashMap<>();
		
		Integer departmentId = departmentService.getDepartmentIdByName(departmentName);
		
		response.put("success", true);
		response.put("departmentId", departmentId);
		response.put("message", "Successfully retrieved department ID.");
		
		return ResponseEntity.ok(response);
	}

}
