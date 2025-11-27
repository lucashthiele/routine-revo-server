package com.lucashthiele.routine_revo_server.config;

import com.lucashthiele.routine_revo_server.infrastructure.security.JwtAccessDeniedHandler;
import com.lucashthiele.routine_revo_server.infrastructure.security.JwtAuthenticationEntryPoint;
import com.lucashthiele.routine_revo_server.infrastructure.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
  
  @Value("${app.frontend.url}")
  private String frontendUrl;
  
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  
  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                        JwtAccessDeniedHandler jwtAccessDeniedHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    LOGGER.info("SecurityConfig initialized with JwtAuthenticationFilter");
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
  
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    configuration.setAllowedOrigins(List.of(
        frontendUrl
    ));
    
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    LOGGER.info("Configuring SecurityFilterChain with JWT authentication");
    
    http
        .cors(Customizer.withDefaults())
        // Disable CSRF
        .csrf(AbstractHttpConfigurer::disable)
        // Set session management to stateless
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // Configure exception handling
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
        )
        // Add JWT authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        
        .authorizeHttpRequests(authorize -> 
            authorize
            .requestMatchers("/api/v1/auth/login").permitAll()
            .requestMatchers("/api/v1/password-reset/**").permitAll()
            .requestMatchers("/api/v1/onboarding/**").permitAll()
            .anyRequest().authenticated()
        );
    
    LOGGER.info("SecurityFilterChain configured successfully");
    LOGGER.info("Public endpoints: /api/v1/auth/login, /api/v1/password-reset/**, /api/v1/onboarding/**");
    LOGGER.info("All other endpoints require authentication");
    
    return http.build();
  }
}
