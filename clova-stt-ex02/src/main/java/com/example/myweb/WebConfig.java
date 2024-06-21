package com.example.myweb;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**")
	        .allowedOrigins("http://localhost:3000")
	        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
	        .allowedHeaders("*")
	        .allowCredentials(true);
    }
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set maximum file size
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // 10MB
        // Set maximum request size
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); // 10MB
        return factory.createMultipartConfig();
    }
}