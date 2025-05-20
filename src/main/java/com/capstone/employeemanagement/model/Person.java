package com.capstone.employeemanagement.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Person {
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "BIRTH_DATE")
	private LocalDate birthDate;
	
	public Person() {
		
	}
	
	public Person(String name, LocalDate birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LocalDate getBirthDate() {
		return this.birthDate;
	}
	
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

}
