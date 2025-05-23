package com.capstone.employeemanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	// filter by name
	List<Employee> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
	
	// filter by department
	List<Employee> findByDepartment(Department department);
	
	// filter by age (birth date)
	List<Employee> findByBirthDateBetweenOrderByIdAsc(LocalDate minDate, LocalDate maxDate);
	
	// filter by name and department
	List<Employee> findByNameContainingIgnoreCaseAndDepartmentOrderByIdAsc(String name, Department department);
	
	// filter by name and age (birth date)
	List<Employee> findByNameContainingIgnoreCaseAndBirthDateBetweenOrderByIdAsc(String name, LocalDate minDate, LocalDate maxDate);
	
	// filter by department and age (birth date)
	List<Employee> findByDepartmentAndBirthDateBetweenOrderByIdAsc(Department department, LocalDate minDate, LocalDate maxDate);
	
	// filter by name, department, and age (birth date)
	List<Employee> findByNameContainingIgnoreCaseAndDepartmentAndBirthDateBetweenOrderByIdAsc(String name, Department department, LocalDate minDate, LocalDate maxDate);
}
