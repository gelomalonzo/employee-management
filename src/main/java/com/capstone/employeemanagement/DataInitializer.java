package com.capstone.employeemanagement;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.capstone.employeemanagement.model.Department;
import com.capstone.employeemanagement.model.Role;
import com.capstone.employeemanagement.model.User;
import com.capstone.employeemanagement.repository.DepartmentRepository;
import com.capstone.employeemanagement.repository.UserRepository;

@Configuration
public class DataInitializer {
	
	@Bean
	public CommandLineRunner seedUsers(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepo.findByUsername("admin").isEmpty()) {
				User admin = new User("admin", passwordEncoder.encode("password"), Role.ROLE_ADMIN);
				userRepo.save(admin);
			}
			
			if (userRepo.findByUsername("user").isEmpty()) {
				User user = new User("user", passwordEncoder.encode("password"), Role.ROLE_USER);
				userRepo.save(user);
			}
		};
	}
	
	@Bean
	public CommandLineRunner seedDepartments(DepartmentRepository departmentRepo) {
		return args -> {
			ArrayList<String> departmentNames = new ArrayList<>();
			departmentNames.addAll(Arrays.asList("NO DEPARTMENT", "Finance", "HR", "IT", "Marketing", "Operations", "Facilities Management"));
			
			for (String departmentName : departmentNames) {
				if (departmentRepo.findByNameIgnoreCase(departmentName).isEmpty()) {
					Department department = new Department(departmentName);
					departmentRepo.save(department);
				}
			}
		};
	}

}
