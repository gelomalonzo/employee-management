package com.capstone.employeemanagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstone.employeemanagement.model.User;
import com.capstone.employeemanagement.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private final UserRepository userRepo;
	
	@Autowired
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(final UserRepository userRepo, final PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User getUserByUsername(String username) {
		Optional<User> userOptional = userRepo.findByUsername(username);
		
		if (userOptional.isEmpty()) {
			return null;
		}
		
		return userOptional.get();
	}

	@Override
	public Boolean passwordMatches(User user, String password) {
		return passwordEncoder.matches(password, user.getPassword());
	}

}
