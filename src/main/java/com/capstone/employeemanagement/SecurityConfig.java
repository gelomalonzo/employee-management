package com.capstone.employeemanagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/login", "/login.html", "/login.js").permitAll() // login routes
				.requestMatchers("/component/nav.html", "/js/nav.js").permitAll() // navbar routes
				.requestMatchers("/component/alert.html", "/js/alert.js").permitAll() // alert routes
				.requestMatchers("/home", "/home.html", "/home.js").authenticated() // home routes
				.requestMatchers("/employees/*", "/employees/*/edit", "/employees/*/delete").authenticated() // employee routes
				.requestMatchers("/component/employee-modal.html", "/js/employee-modal.js").authenticated() // employee modal routes
				.requestMatchers("/departments/*").authenticated() // department routes
				.requestMatchers("/html/*", "/js/*").permitAll() // static routes
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/login?success", true)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
			);
		
		return http.build();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
