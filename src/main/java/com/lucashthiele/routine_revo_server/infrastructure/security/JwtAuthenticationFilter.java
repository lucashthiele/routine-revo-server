package com.lucashthiele.routine_revo_server.infrastructure.security;

import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  
  private final AuthTokenProvider authTokenProvider;
  private final UserDetailsService userDetailsService;
  
  public JwtAuthenticationFilter(AuthTokenProvider authTokenProvider, 
                                  UserDetailsService userDetailsService) {
    this.authTokenProvider = authTokenProvider;
    this.userDetailsService = userDetailsService;
  }
  
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain) 
      throws ServletException, IOException {
    
    String requestPath = request.getRequestURI();
    LOGGER.info("=== JWT Filter - Processing request: {} {}", request.getMethod(), requestPath);
    
    try {
      Optional<String> jwt = extractJwtFromRequest(request);
      
      if (jwt.isPresent()) {
        LOGGER.info("JWT token found in request");
        LOGGER.debug("JWT token: {}", jwt.get());
        
        Optional<String> userEmail = authTokenProvider.validateToken(jwt.get());
        
        if (userEmail.isPresent()) {
          LOGGER.info("JWT token validated successfully for user: {}", userEmail.get());
          
          try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail.get());
            LOGGER.info("User details loaded for: {}", userEmail.get());
            LOGGER.info("User authorities: {}", userDetails.getAuthorities());
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            LOGGER.info("User authenticated successfully: {}", userEmail.get());
          } catch (Exception e) {
            LOGGER.error("Error loading user details for: {}", userEmail.get(), e);
          }
        } else {
          LOGGER.warn("JWT token validation failed - token is invalid or expired");
        }
      } else {
        LOGGER.info("No JWT token found in Authorization header for path: {}", requestPath);
      }
    } catch (Exception e) {
      LOGGER.error("Cannot set user authentication for request: {}", requestPath, e);
    }
    
    LOGGER.info("=== JWT Filter - Proceeding with filter chain. Authentication: {}", 
        SecurityContextHolder.getContext().getAuthentication() != null ? "SET" : "NOT SET");
    
    filterChain.doFilter(request, response);
  }
  
  private Optional<String> extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
    }
    
    return Optional.empty();
  }
}

