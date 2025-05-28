package com.capstone.employeemanagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public Page<Employee> getAllEmployees(Pageable pageable) {
		return employeeRepo.findAll(pageable);
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
	public List<Employee> getEmployeesByNameAndAge(String name, Integer minAge, Integer maxAge) {
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByNameContainingIgnoreCaseAndBirthDateBetweenOrderByIdAsc(name, birthDateRange.get(0), birthDateRange.get(1));
	}
	
	@Override
	public Page<Employee> getEmployeesByNameAndAge(String name, Integer minAge, Integer maxAge, Pageable pageable) {
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByNameContainingIgnoreCaseAndBirthDateBetween(name, birthDateRange.get(0), birthDateRange.get(1), pageable);
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
	public Page<Employee> getEmployeesByNameAndDepartmentAndAge(String name, Integer departmentId, Integer minAge, Integer maxAge, Pageable pageable) {
		Department department = getDepartment(departmentId);
		if (department == null) {
			return null;
		}
		
		ArrayList<LocalDate> birthDateRange = getBirthDateRange(minAge, maxAge);
		
		return employeeRepo.findByNameContainingIgnoreCaseAndDepartmentAndBirthDateBetween(name, department, birthDateRange.get(0), birthDateRange.get(1), pageable);
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
	public Employee saveEmployee(Employee employee) {
		return employeeRepo.save(employee);
	}

	@Override
	public void deleteEmployee(Integer employeeId) {
		employeeRepo.deleteById(employeeId);
	}

}
