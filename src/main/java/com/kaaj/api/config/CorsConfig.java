package com.kaaj.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        System.out.println("==================================================");
        System.out.println("CORS CONFIGURATION INITIALIZED");
        System.out.println("==================================================");

        CorsConfiguration config = new CorsConfiguration();

        // PERMITIR TODOS LOS ORIGENES (para desarrollo)
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");  // Esto permite cualquier IP

        System.out.println("CORS: allowCredentials = true");
        System.out.println("CORS: allowedOriginPattern = *");

        // HEADERS PERMITIDOS
        config.addAllowedHeader("*");

        // MÉTODOS HTTP PERMITIDOS
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        System.out.println("CORS: allowedMethods = GET,POST,PUT,DELETE,PATCH,OPTIONS,HEAD");
        System.out.println("CORS: allowedHeaders = *");

        // HEADERS EXPUESTOS
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));

        // TIEMPO DE CACHÉ
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        System.out.println("CORS: registered for path pattern = /**");
        System.out.println("==================================================");

        return new CorsFilter(source);
    }
}