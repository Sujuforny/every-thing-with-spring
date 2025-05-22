package com.suju.every_thing_with_spring.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder encoder;
    private RSAPublicKey key;

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
                    request.requestMatchers("/api/v1/info").hasAnyAuthority("SCOPE_ROLE_User","SCOPE_ROLE_Admin","SCOPE_ROLE_Moderator");//ROLE_Admin, ROLE_User, ROLE_Moderator
                    request.anyRequest().permitAll();
                })
//                .httpBasic(Customizer.withDefaults());
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }


    // ╔══════════════════════════════════════════════╗
    // ║              JWT Configuration               ║
    // ╚══════════════════════════════════════════════╝

    /**
     * Generates an RSA KeyPair at application startup.
     * This key pair will be used to sign (private key) and verify (public key) JWT tokens.
     * NOTE: 512 bits is insecure for production. Use at least 2048 bits in real-world scenarios.
     */
    @Bean
    public KeyPair keyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // ⚠️ Use 2048+ bits for production
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Converts the generated RSA KeyPair into an RSAKey object.
     * This is a JOSE (Java Object Signing and Encryption) representation of the key,
     * including metadata like a Key ID (kid).
     */
    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())        // Attach the private key for signing
                .keyID(UUID.randomUUID().toString())     // Unique key ID for this RSA key
                .build();
    }

    /**
     * Exposes the RSAKey as a JWKSource (JSON Web Key Source).
     * This allows the JWT encoder to retrieve the signing key and expose it via a JWK endpoint if needed.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        // Create a JWKSet containing the single RSA key
        JWKSet jwkSet = new JWKSet(rsaKey);

        // Return a JWKSource that selects keys from the JWKSet
        return new JWKSource<SecurityContext>() {
            @Override
            public List<JWK> get(JWKSelector jwkSelector, SecurityContext securityContext) throws KeySourceException {
                return jwkSelector.select(jwkSet); // Select keys matching the selector
            }
        };
    }

    /**
     * Configures a JwtDecoder bean using the public part of the RSA key.
     * This will be used to verify the JWT signatures in incoming requests.
     */
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    /**
     * Configures a JwtEncoder bean using the JWKSource.
     * This will be used to sign JWTs using the private RSA key when issuing tokens.
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

}
