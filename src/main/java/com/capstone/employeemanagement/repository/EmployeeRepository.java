package com.capstone.employeemanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	// filter by name and age (birth date)
	List<Employee> findByNameContainingIgnoreCaseAndBirthDateBetweenOrderByIdAsc(String name, LocalDate minDate, LocalDate maxDate);
	Page<Employee> findByNameContainingIgnoreCaseAndBirthDateBetweenOrderByIdAsc(String name, LocalDate minDate, LocalDate maxDate, Pageable pageable);
	
	// filter by name, department, and age (birth date)
	List<Employee> findByNameContainingIgnoreCaseAndDepartmentAndBirthDateBetweenOrderByIdAsc(String name, Department department, LocalDate minDate, LocalDate maxDate);
	Page<Employee> findByNameContainingIgnoreCaseAndDepartmentAndBirthDateBetweenOrderByIdAsc(String name, Department department, LocalDate minDate, LocalDate maxDate, Pageable pageable);
	
	// for obtaining number of employees in the department
	List<Employee> findByDepartment(Department department);
	
}
