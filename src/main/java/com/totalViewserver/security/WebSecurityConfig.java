package com.totalViewserver.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Slf4j
@Configuration
@EnableWebSecurity
@CrossOrigin(origins = "http://localhost:5000, " +
                        "http://xraysite.kr:20400," +
                        "http://172.16.150.21, " +
                       "http://xraydata.site:20400," +
                       "http://localhost:3000", allowCredentials = "true")
public class WebSecurityConfig extends WebMvcConfigurationSupport {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        return http.csrf().disable().build();
    }
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://xraydata.site:20400",
                        "http://xraysite.kr:20400",
                        "http://172.16.150.21",
                        "http://localhost:5000",
                        "http://localhost:3000")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600);
    }
}
