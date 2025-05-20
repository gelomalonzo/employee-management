package com.capstone.employeemanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.repository.DepartmentRepository;
import com.capstone.employeemanagement.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepo;
	private DepartmentRepository departmentRepo;
	
	@Override
	public List<Employee> getEmployeeById(Integer employeeId) {
		Optional<Employee> employeeOptional = employeeRepo.findById(employeeId);
		
		if (!employeeOptional.isPresent()) {
			return new ArrayList<>(); // EMPTY: no employee found with the specified employee ID
		}
		
		List<Employee> employees = new ArrayList<>();
		employees.add(employeeOptional.get());
		
		return employees;
	}
	
	@Override
	public List<Employee> getEmployeeByName(String keyword) {
		return employeeRepo.findByNameContainingIgnoreCaseOrderByNameAsc(keyword);
	}
	
	@Override
	public List<Employee> getEmployeesByDepartment(Integer departmentId) {
		Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
		
		if (!departmentOptional.isPresent()) {
			return null; // ERROR: no department found with the specified department ID
		}
		
		Department department = departmentOptional.get();
		
		return employeeRepo.findByDeparment(department);
	}
	
	@Override
	public List<Employee> getEmployeesByAgeRange(int minAge, int maxAge) {
		LocalDate today = LocalDate.now();
		LocalDate earliestBirthDate = today.minusYears(minAge);
		LocalDate latestBirthDate = today.minusYears(maxAge);
		
		return employeeRepo.findByBirthDateBetweenOrderByBirthDateAsc(earliestBirthDate, latestBirthDate);
	}
	
	@Override
	public BigDecimal getAverageSalary(List<Employee> employees) {
		BigDecimal sumSalary = new BigDecimal(0);
		
		for (Employee employee : employees) {
			sumSalary = sumSalary.add(employee.getSalary());
		}
		
		return sumSalary.divide(new BigDecimal(employees.size()));
	}
	
	@Override
	public Integer getAverageAge(List<Employee> employees) {
		int sumAge = 0;
		
		for (Employee employee : employees) {
			sumAge += employee.getAge();
		}
		
		return sumAge / employees.size();
	}

}
