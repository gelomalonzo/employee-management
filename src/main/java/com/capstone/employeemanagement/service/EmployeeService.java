package com.capstone.employeemanagement.service;

import java.math.BigDecimal;
import java.util.List;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;

public interface EmployeeService {
	
	List<Employee> getAllEmployees();
	Employee getEmployeeById(Integer employeeId);
	List<Employee> getEmployeesByName(String name);
	List<Employee> getEmployeesByDepartment(Integer departmentId);
	List<Employee> getEmployeesByAge(Integer minAge, Integer maxAge);
	List<Employee> getEmployeesByNameAndDepartment(String name, Integer departmentId);
	List<Employee> getEmployeesByNameAndAge(String name, Integer minAge, Integer maxAge);
	List<Employee> getEmployeesByDepartmentAndAge(Integer departmentId, Integer minAge, Integer maxAge);
	List<Employee> getEmployeesByNameAndDepartmentAndAge(String name, Integer departmentId, Integer minAge, Integer maxAge);
	BigDecimal getAverageSalary(List<Employee> employees);
	Integer getAverageAge(List<Employee> employees);
	
	Employee saveEmployee(Employee employee); // add new employee or update employee details
	void deleteEmployee(Integer employeeId);

}
