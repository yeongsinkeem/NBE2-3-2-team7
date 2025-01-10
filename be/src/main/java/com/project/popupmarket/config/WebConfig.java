package com.project.popupmarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.resource-path}")
    private String resourcePath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/popup_thumbnail/**")
                .addResourceLocations(resourcePath + "popup_thumbnail/");
        registry.addResourceHandler("/images/popup_detail/**")
                .addResourceLocations(resourcePath + "popup_detail/");
        registry.addResourceHandler("/images/place_thumbnail/**")
                .addResourceLocations(resourcePath + "place_thumbnail/");
        registry.addResourceHandler("/images/place_detail/**")
                .addResourceLocations(resourcePath + "place_detail/");
        registry.addResourceHandler("/images/user_brand/**")
                .addResourceLocations(resourcePath + "user_brand/");
    }
}
