package com.capstone.employeemanagement.controller;

import org.springframework.web.bind.annotation.RestController;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.service.EmployeeServiceImpl;

import jakarta.servlet.http.HttpSession;

import com.capstone.employeemanagement.service.DepartmentServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
	
	@Autowired
	private final EmployeeServiceImpl employeeService;
	
	@Autowired
	private final DepartmentServiceImpl departmentService;
	
	public EmployeeController(final EmployeeServiceImpl employeeService, final DepartmentServiceImpl departmentService) {
		this.employeeService = employeeService;
		this.departmentService = departmentService;
	}
	
	private List<Employee> getFilteredEmployees(String name, Integer departmentId, Integer minAge, Integer maxAge) {
		if (minAge == null) {
			minAge = 18;
		}
		
		if (maxAge == null) {
			maxAge = 100;
		} else {
			maxAge++;
		}
		
		List<Employee> employees = new ArrayList<>();
		
		if (name != null && departmentId != null && minAge != null) { // all are given
			employees = employeeService.getEmployeesByNameAndDepartmentAndAge(name, departmentId, minAge, maxAge);
		} else if (name != null && departmentId != null && minAge == null) { // name and department are given
			employees = employeeService.getEmployeesByNameAndDepartment(name, departmentId);
		} else if (name != null && departmentId == null && minAge != null) { // name and age are given
			employees = employeeService.getEmployeesByNameAndAge(name, minAge, maxAge);
		} else if (name == null && departmentId != null && minAge != null) { // department and age are given
			employees = employeeService.getEmployeesByDepartmentAndAge(departmentId, minAge, maxAge);
		} else if (name != null && departmentId == null && minAge == null) { // name is given
			employees = employeeService.getEmployeesByName(name);
		} else if (name == null && departmentId != null && minAge == null) { // department is given
			employees = employeeService.getEmployeesByDepartment(departmentId);
		} else if (name == null && departmentId == null && minAge != null) { // age is given
			employees = employeeService.getEmployeesByAge(minAge, maxAge);
		}
		
		return employees;
	}
	
	private Map<String, Object> getAverages(List<Employee> employees) {
		Map<String, Object> averages = new HashMap<>();
		
		BigDecimal averageSalary = new BigDecimal(0);
		Integer averageAge = 0;
		Integer minAgeRange = null;
		Integer maxAgeRange = null;
		
		if (!employees.isEmpty()) {
			for (Employee employee : employees) {
				averageSalary = averageSalary.add(employee.getSalary());
				averageAge += employee.getAge();
				
				if (minAgeRange == null || employee.getAge() < minAgeRange) {
					minAgeRange = employee.getAge();
				}
				
				if (maxAgeRange == null || employee.getAge() > maxAgeRange) {
					maxAgeRange = employee.getAge();
				}
			}
			
			averageSalary = averageSalary.divide(new BigDecimal(employees.size()), 2, RoundingMode.HALF_UP);
			averageAge = averageAge / employees.size();
		}
		
		averages.put("averageSalary", averageSalary);
		averages.put("averageAge", averageAge);
		averages.put("minAgeRange", minAgeRange);
		averages.put("maxAgeRange", maxAgeRange);
		
		return averages;
	}
	
	private Map<String, Object> generateResponseForEmployeeList(List<Employee> employees) {
		Map<String, Object> averages = getAverages(employees);
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("employees", employees);
		response.put("isEmpty", employees.isEmpty());
		response.put("averageSalary", averages.get("averageSalary"));
		response.put("averageAge", averages.get("averageAge"));
		response.put("minAgeRange", averages.get("minAgeRange"));
		response.put("maxAgeRange", averages.get("maxAgeRange"));
		
		if (employees.isEmpty()) {
			response.put("message", "No employees found.");
		} else {
			response.put("message", "Successfully retrieved employee list.");
		}
		
		return response;
	}
	
	private Map<String, Object> generateResponseForSavedEmployee(Employee savedEmployee) {
		Map<String, Object> response = new HashMap<>();
		
		response.put("savedEmployee", savedEmployee);
		response.put("success", true);
		response.put("message", "Successfully saved employee details.");
		
		return response;
	}
	
	@GetMapping("/employees/all")
	public ResponseEntity<?> getAllEmployees() {
		List<Employee> employees = employeeService.getAllEmployees();
		
		Map<String, Object> response = generateResponseForEmployeeList(employees);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/employees/{id}")
	public ResponseEntity<?> getEmployeeById(
		@PathVariable("id") Integer id
	) {
		List<Employee> employees = new ArrayList<>();
		
		Employee employee = employeeService.getEmployeeById(id);
		if (employee != null) {
			employees.add(employee);
		}
		
		Map<String, Object> response = generateResponseForEmployeeList(employees);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/employees/filter")
	public ResponseEntity<?> filterEmployees(
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "departmentId", required = false) Integer departmentId,
		@RequestParam(name = "minAge", required = false) Integer minAge,
		@RequestParam(name = "maxAge", required = false) Integer maxAge
	) {
		List<Employee> employees = getFilteredEmployees(name, departmentId, minAge, maxAge);
		
		Map<String, Object> response = generateResponseForEmployeeList(employees);
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/employees/add")
	public ResponseEntity<?> addEmployee(
		@RequestParam(name = "name", required = true) String name,
		@RequestParam(name = "birthDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
		@RequestParam(name = "salary", required = true) BigDecimal salary,
		@RequestParam(name = "departmentId", required = true) Integer departmentId
	) {
		Map<String, Object> response = new HashMap<>();
		
		Department department = departmentService.getDepartmentById(departmentId);
		if (department == null) {
			response.put("success", false);
			response.put("message", "Error adding new employee. Couldn't find department.");
			response.put("redirect", "/home");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		if (name == null || name.isBlank() || name.isEmpty() || birthDate == null || salary == null) {
			response.put("success", false);
			response.put("message", "Error adding new employee. All fields must be filled out.");
			response.put("redirect", "/home");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		Employee newEmployee = new Employee(name, birthDate, salary, department);
		employeeService.saveEmployee(newEmployee);
		
		response = generateResponseForSavedEmployee(newEmployee);
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/employees/{id}/edit")
	public ResponseEntity<?> editEmployee(
		@PathVariable(name = "id") Integer id,
		@RequestParam(name = "name", required = true) String name,
		@RequestParam(name = "birthDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
		@RequestParam(name = "salary", required = true) BigDecimal salary,
		@RequestParam(name = "departmentId", required = true) Integer departmentId
	) {
		Map<String, Object> response = new HashMap<>();
		
		Department department = departmentService.getDepartmentById(departmentId);
		if (department == null) {
			response.put("success", false);
			response.put("message", "Error editing employee's department. Couldn't find department.");
			response.put("redirect", "/home");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		Employee employee = employeeService.getEmployeeById(id);
		if (employee == null) {
			response.put("success", false);
			response.put("message", "Error editing employee. Couldn't find employee's ID on the database.");
			response.put("redirect", "/home");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		if (name == null || name.isBlank() || name.isEmpty() || birthDate == null || salary == null) {
			response.put("success", false);
			response.put("message", "Error editing employee. All fields must be filled out.");
			response.put("redirect", "/home");
			
			return ResponseEntity.badRequest().body(response);
		}
		
		employee.setName(name);
		employee.setBirthDate(birthDate);
		employee.setSalary(salary);
		employee.setDepartment(department);
		
		employeeService.saveEmployee(employee);
		
		response = generateResponseForSavedEmployee(employee);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/employees/{id}/delete")
	public ResponseEntity<?> deleteEmployee(
		@PathVariable(name = "id") Integer id
	) {
		employeeService.deleteEmployee(id);
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Successfully deleted employee from the database.");
		response.put("redirect", "/home");
		
		return ResponseEntity.ok(response);
	}
	
}
