package com.capstone.employeemanagement.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capstone.employeemanagement.model.User;
import com.capstone.employeemanagement.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		
		return new org.springframework.security.core.userdetails.User(
			user.getUsername(),
			user.getPassword(),
			Collections.emptyList()
		);
	}

}
