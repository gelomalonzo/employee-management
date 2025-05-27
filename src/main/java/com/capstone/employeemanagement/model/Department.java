package com.capstone.employeemanagement.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Department {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DEPARTMENT_ID")
	private Integer id;
	
	@Column(name = "NAME")
	private String name;
	
	@OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
	private List<Employee> employees;
	
	public Department() {
		
	}
	
	public Department(String name) {
		this.name = name;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getNumberOfEmployees() {
		if (employees != null) {
			return employees.size();
		}
		
		return 0;
	}

}
