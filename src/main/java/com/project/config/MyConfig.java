package com.project.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.Filter;

@Configuration
public class MyConfig{
	
	@Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	
	@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


		 http
         .csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(auth -> auth
                 .requestMatchers("/admin/**").hasRole("ADMIN")
                 .requestMatchers("/user/**").hasRole("USER")
                 .requestMatchers("/**").permitAll()
                 .anyRequest().authenticated()
         )
         .formLogin(form -> form
        		 .loginPage("/signin")
        		 .loginProcessingUrl("/dologin")
                .defaultSuccessUrl("/user/index", true)
                 .permitAll()
         )
         .logout(logout -> logout.permitAll());

        return http.build();
 
	}
	

}
