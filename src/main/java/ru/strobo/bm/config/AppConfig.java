/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.strobo.bm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.strobo.bm.http.CheckSessionInterseptor;

/**
 *
 * @author k.baukov
 */
@Configuration  
public class AppConfig extends WebMvcConfigurerAdapter  {  
    
    @Autowired
    CheckSessionInterseptor csi;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(csi).addPathPatterns("/api/*").
               excludePathPatterns("/api/login").
               excludePathPatterns("/api/ctrlc");
    }
} 