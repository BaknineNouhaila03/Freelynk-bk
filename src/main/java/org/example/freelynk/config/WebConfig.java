package org.example.freelynk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the uploads directory
        String uploadPath = new File("uploads").getAbsolutePath();

        System.out.println("Configuring static resources for path: " + uploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + File.separator)
                .setCachePeriod(3600)
                .resourceChain(false); // Set to false for direct access
    }
}