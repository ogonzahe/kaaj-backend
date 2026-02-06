package com.kaaj.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desactivar CSRF (Esto es lo que causa el Error 405 en el Login)
                .csrf(csrf -> csrf.disable())

                // 2. Activar CORS para que use tu configuración de CorsConfig.java
                .cors(Customizer.withDefaults())

                // 3. Configurar permisos de URLs
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público al Login y a crear usuarios
                        .requestMatchers("/api/login", "/api/usuarios/**", "/api/seguridad/**").permitAll()

                        // Permitir OPTIONS (necesario para que el celular pregunte "puedo entrar?")
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Cualquier otra cosa, déjala pasar también (por ahora, para evitar problemas)
                        .anyRequest().permitAll());

        return http.build();
    }
}