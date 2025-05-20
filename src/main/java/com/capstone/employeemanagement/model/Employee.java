package com.capstone.employeemanagement.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMPLOYEE_ID")
	private Integer id;
	
	@Column(name = "SALARY", scale = 2)
	private BigDecimal salary;
	
	@ManyToOne // many employees to one department
	@JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "DEPARTMENT_ID")
	private Department department;
	
	public Employee() {
		
	}
	
	public Employee(BigDecimal salary, Department department) {
		this.salary = salary;
		this.department = department;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public BigDecimal getSalary() {
		return this.salary;
	}
	
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	
	public Department getDepartment() {
		return this.department;
	}
	
	public void setDepartment(Department department) {
		this.department = department;
	}

}
