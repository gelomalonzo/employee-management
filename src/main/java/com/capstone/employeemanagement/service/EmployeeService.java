package com.capstone.employeemanagement.service;

import java.math.BigDecimal;
import java.util.List;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;

public interface EmployeeService {
	
	List<Employee> getEmployeeById(Integer employeeId);
	List<Employee> getEmployeeByName(String keyword);
	List<Employee> getEmployeesByDepartment(Integer departmentId);
	List<Employee> getEmployeesByAgeRange(int minAge, int maxAge);
	BigDecimal getAverageSalary(List<Employee> employees);
	Integer getAverageAge(List<Employee> employees);

}
