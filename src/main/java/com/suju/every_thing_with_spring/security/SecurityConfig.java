package com.suju.every_thing_with_spring.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder encoder;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider () {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(encoder);
        return auth;
    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        //disable form login
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        http.cors(AbstractHttpConfigurer::disable);
//        //Security mechanism
////        http.oauth2ResourceServer(oauth2 -> oauth2
////                .jwt(jwt -> jwt
////                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
////                )
////        );
//
//        //make security http STATELESS
//        http.sessionManagement( session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//
//        //authorize RUL mapping
//        http.authorizeHttpRequests( request ->{
//
//
////            request.requestMatchers("api/v1/users/**").hasAnyAuthority("SCOPE_ROLE_USER","SCOPE_ROLE_ADMIN");
//            request.requestMatchers("api/v1/info").authenticated();
////            request.requestMatchers("api/v1/auth/verify").authenticated();
//
////            request.requestMatchers("api/v1/account-types/**","api/v1/files/**","files/**").hasAnyAuthority("SCOPE_ROLE_ADMIN","SCOPE_ROLE_USER");
//            request.anyRequest().permitAll();
//
//        });
//
//
//        //exception
////        http.exceptionHandling( exception ->
////                exception.authenticationEntryPoint(customAuthenticationEntrypoint));
//        return http.build();
//    }


//@Bean
//public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder encoder) {
//    DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//    auth.setUserDetailsService(userDetailsService);
//    auth.setPasswordEncoder(encoder);
//    return auth;
//}

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder encoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthenticationProvider())
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/api/v1/register").permitAll();
                    request.requestMatchers("/api/v1/info").hasAnyRole("User");//ROLE_Admin, ROLE_User, ROLE_Moderator
                    request.anyRequest().permitAll();
                })
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
