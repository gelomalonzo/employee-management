package com.capstone.employeemanagement.service;

import java.util.List;

import com.capstone.employeemanagement.model.Department;

public interface DepartmentService {
	
	Department getDepartmentById(Integer departmentId);
	Department getDepartmentByName(String departmentName);
	List<Department> getAllDepartments();
	Integer getDepartmentIdByName(String departmentName);
	
	Department saveDepartment(Department department);
	void deleteDepartment(Integer departmentId);

}
