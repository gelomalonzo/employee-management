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
	
	@Transient
	public int getAge() {
		LocalDate today = LocalDate.now();
		int age = today.getYear() - this.getBirthDate().getYear();
		
		if (today.getMonthValue() < this.getBirthDate().getMonthValue()) {
			age--; // subtract 1 from age if current month is earlier than the birth month
		} else if (today.getMonthValue() == this.getBirthDate().getMonthValue() && today.getDayOfMonth() < this.getBirthDate().getDayOfMonth()) {
			age--; // subtract 1 from age if current month is the birth month, but current day is earlier than birth day
		}
		
		return age;
	}

}
