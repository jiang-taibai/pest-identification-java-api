package com.jsjds;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>创建时间：2021/4/26 22:36</p>
 * <p>主要功能：资源映射路径</p>
 *
 * @author 太白
 */
@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    @Value("${pest-image-path.prefix}")
    public String prefixPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocations = "file:" + prefixPath;
        registry.addResourceHandler("/**")
                .addResourceLocations(resourceLocations.intern());
    }

}
