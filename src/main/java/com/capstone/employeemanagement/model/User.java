package com.capstone.employeemanagement.model;

import jakarta.persistence.*;

@Entity
public class User {
	
	/**
	 * The unique identifier for the user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private Integer id;
	
	/**
	 * The username of the user.
	 */
	@Column(name = "USERNAME")
	private String username;
	
	/**
	 * The password of the user.
	 */
	@Column(name = "PASSWORD")
	private String password;
	
	/**
	 * Default no-args constructor.
	 */
	public User() {
		
	}
	
	/**
	 * Constructs a User with the specified username and password.
	 * 
	 * @param username the username
	 * @param password the password
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}
