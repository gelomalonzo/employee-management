package com.capstone.employeemanagement.service;

import java.util.List;

import com.capstone.employeemanagement.model.Department;

public interface DepartmentService {
	
	Department getDepartmentById(Integer departmentId);
	List<Department> getAllDepartments();
	Integer getDepartmentIdByName(String departmentName);

}
