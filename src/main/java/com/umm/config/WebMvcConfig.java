package com.umm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "https://project-umm.com",
                    "https://*.project-umm.com",
                    "http://localhost:3000",
                    "http://localhost:8000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders(
                    "Authorization",
                    "Content-Type",
                    "Accept"
                )
                .allowCredentials(true)
                .maxAge(3600L);
    }
}
