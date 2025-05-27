package com.capstone.employeemanagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
				.requestMatchers("/", "/login", "/login.html", "/login.js", "/component/login-success.html", "/component/logout-success.html", "/component/login-error.html").permitAll() // login routes
				.requestMatchers("/component/nav.html", "/js/nav.js").permitAll() // navbar routes
				.requestMatchers("/component/alert.html", "/js/alert.js").permitAll() // alert routes
				.requestMatchers("/home", "/home.html", "/home.js").hasAnyRole("ADMIN", "USER") // home routes
				.requestMatchers("/departments", "/departments.html", "/departments.js", "/departments/**").hasRole("ADMIN")
//				.requestMatchers(HttpMethod.DELETE, "/departments/**").hasRole("ADMIN")
				.requestMatchers("/employees/*", "/employees/*/edit", "/employees/*/delete").authenticated() // employee routes
				.requestMatchers("/component/employee-modal.html", "/js/employee-modal.js").authenticated() // employee modal routes
				.requestMatchers("/departments/*").authenticated() // department routes
				.requestMatchers("/html/*", "/js/*", "person-lines-fill.svg").permitAll() // static routes
				.requestMatchers("/user/info").permitAll()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)
				.failureUrl("/component/login-error.html")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
			);
		
		return http.build();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
