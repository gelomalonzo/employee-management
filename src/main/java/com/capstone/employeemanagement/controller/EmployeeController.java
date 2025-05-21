package com.capstone.employeemanagement.controller;

import org.springframework.web.bind.annotation.RestController;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.service.EmployeeServiceImpl;
import com.capstone.employeemanagement.service.DepartmentServiceImpl;

import java.math.BigDecimal;
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
		if (minAge != null && maxAge == null) { // if only minAge is given
			maxAge = 120;
		} else if (minAge == null && maxAge != null) { // if only maxAge is given
			minAge = 18;
		} // now, age variables are either both null or both not null only
		
		if (minAge != null && minAge == maxAge) { // if both minAge and maxAge are given, but they have the same value
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
	
	private Map<String, Object> generateResponseForEmployeeList(List<Employee> employees) {
		Map<String, Object> response = new HashMap<>();
		
		response.put("employees", employees);
		response.put("success", true);
		response.put("isEmpty", employees.isEmpty());
		
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
	
	@GetMapping("/employees/averageSalary")
	public ResponseEntity<?> getAverageSalary(
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "departmentId", required = false) Integer departmentId,
		@RequestParam(name = "minAge", required = false, defaultValue = "0") Integer minAge,
		@RequestParam(name = "maxAge", required = false, defaultValue = "100") Integer maxAge
	) {
		List<Employee> employees = getFilteredEmployees(name, departmentId, minAge, maxAge);
		BigDecimal averageSalary = new BigDecimal(0);
		
		for (Employee employee : employees) {
			averageSalary = averageSalary.add(employee.getSalary());
		}
		
		averageSalary = averageSalary.divide(new BigDecimal(employees.size()));
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("averageSalary", averageSalary);
		response.put("message", "Successfully calculated average salary of employees.");
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/employees/averageAge")
	public ResponseEntity<?> getAverageAge(
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "departmentId", required = false) Integer departmentId,
		@RequestParam(name = "minAge", required = false, defaultValue = "0") Integer minAge,
		@RequestParam(name = "maxAge", required = false, defaultValue = "100") Integer maxAge
	) {
		List<Employee> employees = getFilteredEmployees(name, departmentId, minAge, maxAge);
		int averageAge = 0;
		
		for (Employee employee : employees) {
			averageAge += employee.getAge();
		}
		
		averageAge = averageAge / employees.size();
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("averageAge", averageAge);
		response.put("message", "Successfully calculated average age of employees.");
			
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
		response.put("redirect", "/employees");
		
		return ResponseEntity.ok(response);
	}
	
}
