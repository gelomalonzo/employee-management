package com.capstone.employeemanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.service.DepartmentServiceImpl;
import com.capstone.employeemanagement.service.EmployeeServiceImpl;

@RestController
public class DepartmentController {
	
	@Autowired
	private final DepartmentServiceImpl departmentService;
	
	@Autowired
	private final EmployeeServiceImpl employeeService;
	
	public DepartmentController(final DepartmentServiceImpl departmentService, final EmployeeServiceImpl employeeService) {
		this.departmentService = departmentService;
		this.employeeService = employeeService;
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
	
	@PostMapping("/departments/add")
	public ResponseEntity<?> addDepartment(
		@RequestParam(name = "name", required = true) String name
	) {
		Map<String, Object> response = new HashMap<>();
		
		Department existingDepartment = departmentService.getDepartmentByName(name);
		if (existingDepartment != null) {
			response.put("success", false);
			response.put("message", "Failed to add department. Department name already exists.");
			
			ResponseEntity.badRequest().body(response);
		}
		
		Department newDepartment = new Department(name);
		departmentService.saveDepartment(newDepartment);
		
		response.put("success", true);
		response.put("message", "Successfully added department.");
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/departments/{id}/edit")
	public ResponseEntity<?> editDepartment(
		@PathVariable(name = "id") Integer id,
		@RequestParam(name = "name", required = true) String name
	) {
		Department department = departmentService.getDepartmentById(id);
		department.setName(name);
		departmentService.saveDepartment(department);
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Successfully edited department name.");
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/departments/{id}/delete")
	public ResponseEntity<?> deleteDepartment(
		@PathVariable(name = "id") Integer id,
		@RequestParam(name = "transferDepartment", required = true) String transferDepartmentName
	) {
		Map<String, Object> response = new HashMap<>();
		
		Department transferDepartment = departmentService.getDepartmentByName(transferDepartmentName);
		System.out.println(transferDepartment);
		if (transferDepartment.getName() == "NO DEPARTMENT") {
			response.put("success", false);
			response.put("message", "Invalid action. Cannot delete the default department.");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		for (Employee employee : employeeService.getEmployeesByDepartment(id)) {
			employee.setDepartment(transferDepartment);
			employeeService.saveEmployee(employee);
		}
		
		departmentService.deleteDepartment(id);
		
		response.put("success", true);
		response.put("message", "Successfully deleted department.");
		
		return ResponseEntity.ok(response);
	}

}
