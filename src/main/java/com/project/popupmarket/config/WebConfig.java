package com.project.popupmarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.resource-path}")
    private String resourcePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/popup_thumbnail/**")
                .addResourceLocations("file:C:/popupmarket/popup_thumbnail/");
        registry.addResourceHandler("/images/popup_detail/**")
                .addResourceLocations("file:C:/popupmarket/popup_detail/");
        registry.addResourceHandler("/images/place_thumbnail/**")
                .addResourceLocations("file:C:/popupmarket/place_thumbnail/");
        registry.addResourceHandler("/images/place_detail/**")
                .addResourceLocations("file:C:/popupmarket/place_detail/");
        registry.addResourceHandler("/images/user_brand/**")
                .addResourceLocations("file:C:/popupmarket/user_brand/");
    }
}
