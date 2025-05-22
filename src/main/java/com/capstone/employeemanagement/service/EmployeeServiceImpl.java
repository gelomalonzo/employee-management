package com.capstone.employeemanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.repository.DepartmentRepository;
import com.capstone.employeemanagement.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@Autowired
	private DepartmentRepository departmentRepo;
	
	private Department getDepartment(Integer departmentId) {
		Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
		
		if (!departmentOptional.isPresent()) {
			return null;
		}
		
		return departmentOptional.get();
	}
	
	private ArrayList<LocalDate> getBirthDateRange(Integer minAge, Integer maxAge) {
		LocalDate today = LocalDate.now();
		
		ArrayList<LocalDate> birthDateRange = new ArrayList<>();
		birthDateRange.add(today.minusYears(maxAge).plusDays(1)); // earliest birth date
		birthDateRange.add(today.minusYears(minAge)); // latest birth date
		
		return birthDateRange;
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		return employeeRepo.findAll(Sort.by("id"));
	}
	
	@Override
	public Employee getEmployeeById(Integer employeeId) {
		Optional<Employee> employeeOptional = employeeRepo.findById(employeeId);
		
		if (employeeOptional.isEmpty()) {
			return null; // EMPTY: no employee found with the specified employee ID
		}
		
		return employeeOptional.get();
	}
	
	@Override
	public List<Employee> getEmployeesByName(String name) {
		return employeeRepo.findByNameContainingIgnoreCaseOrderByIdAsc(name);
	}
	
	@Override
	public List<Employee> getEmployeesByDepartment(Integer departmentId) {
		Department department = getDepartment(departmentId);
		if (department == null) {
			return null;
		}
		
		return employeeRepo.findByDepartment(department);
	}
	
	@Override
	public List<Employee> getEmployeesByAge(Integer minAge, Integer maxAge) {
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByBirthDateBetweenOrderByIdAsc(birthDateRange.get(0), birthDateRange.get(1));
	}
	
	@Override
	public List<Employee> getEmployeesByNameAndDepartment(String name, Integer departmentId) {
		Department department = getDepartment(departmentId);
		if (department == null) {
			return null;
		}
		
		return employeeRepo.findByNameContainingIgnoreCaseAndDepartmentOrderByIdAsc(name, department);
	}
	
	@Override
	public List<Employee> getEmployeesByNameAndAge(String name, Integer minAge, Integer maxAge) {
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByNameContainingIgnoreCaseAndBirthDateBetweenOrderByIdAsc(name, birthDateRange.get(0), birthDateRange.get(1));
	}
	
	@Override
	public List<Employee> getEmployeesByDepartmentAndAge(Integer departmentId, Integer minAge, Integer maxAge) {
		Department department = getDepartment(departmentId);
		if (department == null) {
			return null;
		}
		
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByDepartmentAndBirthDateBetweenOrderByIdAsc(department, birthDateRange.get(0), birthDateRange.get(1));
	}
	
	@Override
	public List<Employee> getEmployeesByNameAndDepartmentAndAge(String name, Integer departmentId, Integer minAge, Integer maxAge) {
		Department department = getDepartment(departmentId);
		if (department == null) {
			return null;
		}
		
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByNameContainingIgnoreCaseAndDepartmentAndBirthDateBetweenOrderByIdAsc(name, department, birthDateRange.get(0), birthDateRange.get(1));
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

	@Override
	public Employee saveEmployee(Employee employee) {
		return employeeRepo.save(employee);
	}

	@Override
	public void deleteEmployee(Integer employeeId) {
		employeeRepo.deleteById(employeeId);
	}

}
