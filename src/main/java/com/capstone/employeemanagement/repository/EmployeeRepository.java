package com.capstone.employeemanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	// filter employee name by keyword
	List<Employee> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);
	
	// filter by birth date range
	// note: birth date is in Person class
	List<Employee> findByBirthDateBetweenOrderByBirthDateAsc(LocalDate minDate, LocalDate maxDate);
	
	// filter by salary range
	// List<Employee> findBySalaryBetweenOrderBySalaryAsc(BigDecimal minSalary, BigDecimal maxSalary);
	
	// filter by department
	List<Employee> findByDeparment(Department department);

}
