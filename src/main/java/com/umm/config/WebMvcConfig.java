package com.umm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://project-umm.com",
                        "https://www.project-umm.com",
                        "https://server.project-umm.com",
                        "https://www.server.project-umm.com",
                        "http://localhost:8000"
                )
                .allowedMethods("*");
    }
}
