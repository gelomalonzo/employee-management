package com.capstone.employeemanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.capstone.employeemanagement.model.Employee;

public interface EmployeeService {
	
	List<Employee> getAllEmployees();
	Page<Employee> getAllEmployees(Pageable pageable);
	
	Employee getEmployeeById(Integer employeeId);
	
	List<Employee> getEmployeesByNameAndAge(String name, Integer minAge, Integer maxAge);
	Page<Employee> getEmployeesByNameAndAge(String name, Integer minAge, Integer maxAge, Pageable pageable);
	
	List<Employee> getEmployeesByNameAndDepartmentAndAge(String name, Integer departmentId, Integer minAge, Integer maxAge);
	Page<Employee> getEmployeesByNameAndDepartmentAndAge(String name, Integer departmentId, Integer minAge, Integer maxAge, Pageable pageable);
	
	List<Employee> getEmployeesByDepartment(Integer departmentId);
	
	Employee saveEmployee(Employee employee); // add new employee or update employee details
	void deleteEmployee(Integer employeeId);

}
