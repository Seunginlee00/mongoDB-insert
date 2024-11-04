package com.example.demo.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
  @Bean
  public CorsConfigurationSource corsConfiguration() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedHeaders(Collections.singletonList("*"));
    config.setAllowedMethods(Collections.singletonList("*"));
    config.setAllowedOrigins(
        List.of("http://localhost:3000", "http://localhost:8080", "https://api.vrware.us" ));

    config.setAllowCredentials(true);
    config.setExposedHeaders(Arrays.asList("Authorization","Set-Cookie"));
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
