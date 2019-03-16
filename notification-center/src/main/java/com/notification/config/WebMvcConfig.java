package com.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * web mvc config
 *SpringBoot 2.0 之后WebMvcConfigurerAdapter已过时,而继承 WebMvcConfigurationSupport 这个类来实现，这样会在某些情况下失效！
 * @author youb 2018-10-02 20:48:20
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
    /**
     *  这个方法是用来配置静态资源的，比如html，js，css等
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
    
    /**
     *  这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效(多个拦截器，顺序执行 )
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    		// addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
//    		registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/", "/register");
    		
    		//访问监控拦截器,针对所有的请求
//        registry.addInterceptor(accessMonitorInterceptor).addPathPatterns("/**");
        
    }
    
    
    
    /**
     * 跨域支持
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600 * 24);
    }

}