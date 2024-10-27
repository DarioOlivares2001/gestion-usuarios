package com.microusers.gesusers.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Considerar habilitar CSRF si no es una API REST
                .authorizeHttpRequests(auth -> auth
                        // Acceso a la gestión de usuarios
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        // Otras rutas pueden necesitar autenticación
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .httpBasic(withDefaults()) // Autenticación básica
               
                .build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Acceso denegado: No tienes permisos para realizar esta operación.");
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}123")  // La anotación {noop} indica que la contraseña no está cifrada.
                .roles("USER")
                .build();
    
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}123")
                .roles("ADMIN")
                .build();
    
        return new InMemoryUserDetailsManager(user, admin);
    }
}
