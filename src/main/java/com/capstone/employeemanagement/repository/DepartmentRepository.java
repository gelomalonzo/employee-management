package com.capstone.employeemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstone.employeemanagement.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
	
	Optional<Department> findByNameIgnoreCase(String name);

}
