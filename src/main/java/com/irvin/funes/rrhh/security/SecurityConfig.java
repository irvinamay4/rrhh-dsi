package com.irvin.funes.rrhh.security;

import com.irvin.funes.rrhh.repositories.UsuarioRepository;
import com.irvin.funes.rrhh.security.filters.JwtAuthenticationFilter;
import com.irvin.funes.rrhh.security.filters.JwtAuthorizationFilter;
import com.irvin.funes.rrhh.security.jwt.JwtUtils;
import com.irvin.funes.rrhh.services.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    JwtAuthorizationFilter authorizationFilter;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, usuarioRepository);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS aquí
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF si estás trabajando con JWT
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();  // Permitir preflight CORS para todas las rutas
                    auth.requestMatchers("/planilla/crear/{id}").hasAnyRole("ADMIN", "RRHH");
                    auth.requestMatchers("/planillas").hasAnyRole("ADMIN", "RRHH");
                    auth.requestMatchers("/solicitudes").hasAnyRole("ADMIN", "RRHH");
                    auth.requestMatchers("/asuetos-trabajados").hasAnyRole("ADMIN", "RRHH");
                    auth.requestMatchers("/carga-laboral-diurna").hasAnyRole("ADMIN", "RRHH");
                    // Otras rutas con restricciones
                    auth.requestMatchers("/usuarios").hasAnyRole("ADMIN", "RRHH");
                    auth.requestMatchers("/usuarios{id}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/crear").hasAnyRole("ADMIN");
                    auth.requestMatchers("/modificar/{id}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/{id}").hasAnyRole("ADMIN");
                    auth.requestMatchers("/sdiaslibres/consultar/usuario/{usuarioId}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/sdiaslibres/crear/{id}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/asuetos-trabajados/consultar/usuario/{usuarioId}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/asuetos-trabajados/crear/{id}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/carga-laboral-diurna/consultar/usuario/{usuarioId}").hasAnyRole("ADMIN", "RRHH", "USER");
                    auth.requestMatchers("/carga-laboral-diurna/crear/{id}").hasAnyRole("ADMIN", "RRHH", "USER");


                    // Cualquier otra petición debe estar autenticada
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


/*
    @Bean
    UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("pedro")
                .password("1234")
                .roles()
                .build());

        return manager;
    }*/

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
       return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder)
                .and().build();
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("1234"));
    }
}
