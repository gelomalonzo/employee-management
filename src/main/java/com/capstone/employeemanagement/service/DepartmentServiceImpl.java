package com.capstone.employeemanagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {
	
	@Autowired
	private final DepartmentRepository departmentRepo;
	
	public DepartmentServiceImpl(final DepartmentRepository departmentRepo) {
		this.departmentRepo = departmentRepo;
	}

	@Override
	public Department getDepartmentById(Integer departmentId) {
		Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
		
		if (departmentOptional.isEmpty()) {
			return null;
		}
		
		return departmentOptional.get();
	}

}
