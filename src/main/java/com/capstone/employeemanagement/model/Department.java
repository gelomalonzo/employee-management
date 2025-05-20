package com.capstone.employeemanagement.model;

import jakarta.persistence.*;

@Entity
public class Department {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DEPARTMENT_ID")
	private Integer id;
	
	@Column(name = "NAME")
	private String name;
	
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

}
