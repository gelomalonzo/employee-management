package com.capstone.employeemanagement.service;

import java.util.List;
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

	@Override
	public List<Department> getAllDepartments() {
		return departmentRepo.findAll();
	}

	@Override
	public Integer getDepartmentIdByName(String departmentName) {
		Optional<Department> department = departmentRepo.findByNameIgnoreCase(departmentName);
		
		if (department.isPresent()) {
			return department.get().getId();
		}
		
		return null;
	}

	@Override
	public Department saveDepartment(Department department) {
		return departmentRepo.save(department);
	}

	@Override
	public void deleteDepartment(Integer departmentId) {
		departmentRepo.deleteById(departmentId);
	}

	@Override
	public Department getDepartmentByName(String departmentName) {
		Optional<Department> department = departmentRepo.findByNameIgnoreCase(departmentName);
		
		if (department.isPresent()) {
			return department.get();
		}
		
		return null;
	}

}
