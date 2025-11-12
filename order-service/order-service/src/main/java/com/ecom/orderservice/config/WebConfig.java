package com.ecom.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ecom.orderservice.interceptor.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;

	public WebConfig(AuthInterceptor authInterceptor) {
		this.authInterceptor = authInterceptor;
	}

	@Override
	public void addInterceptors(@NonNull InterceptorRegistry registry) {
		WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(authInterceptor).addPathPatterns("/orders/**").excludePathPatterns("");
	}
}
