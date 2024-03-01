package com.baijiacms.qsp.common;


import com.qsp.player.libqsp.common.QspConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * http反向代理 显示网页用
 * @author：ChenXingYu
 * @date 2023/5/4 11:58
 */
@Configuration
public class HttpResourceConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/game/**")
                //是否发送Cookie
                .allowCredentials(false)
                //放行哪些原始域
                .allowedOrigins("*")
                .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE"});
//                .allowedHeaders("*")
//                .exposedHeaders("*");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/game/**").addResourceLocations("file:"+QspConstants.getGameBaseFolder());


    }
}
