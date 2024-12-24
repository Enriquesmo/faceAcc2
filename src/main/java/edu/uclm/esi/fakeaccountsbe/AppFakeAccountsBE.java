package edu.uclm.esi.fakeaccountsbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class AppFakeAccountsBE extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AppFakeAccountsBE.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AppFakeAccountsBE.class);
    }

    /**
     * Configuración de CORS para permitir solicitudes desde el frontend.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica CORS a todas las rutas
                        .allowedOrigins("https://localhost:4200") // Permitir solicitudes desde el frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowCredentials(true) // Permitir envío de cookies
                        .allowedHeaders("*"); // Permitir todos los headers
            }
        };
    }
}
