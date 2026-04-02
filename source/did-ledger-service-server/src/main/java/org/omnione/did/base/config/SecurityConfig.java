/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.base.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;


/**
 * The SecurityConfig class provides methods for configuring security settings.
 * This class configures the security settings for the application, such as CSRF, form login, HTTP basic authentication,
 * and custom authorization for specific endpoints.
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    static {
    }
    private final JwtAuthenticationFilter jwtFilter;

    /**
     * Configures the security filter chain that applies to all HTTP requests.
     * This method disables CSRF protection, basic authentication, form login, and logout functionalities,
     * and it customizes authorization for specific endpoints.
     *
     * @return the configured SecurityFilterChain instance
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(this::authorizeHttpRequestsCustomizer)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    /**
     * Customizes authorization for HTTP requests.
     * This method configures which requests are permitted and which require authorization.
     *
     * @param configurer the AuthorizationManagerRequestMatcherRegistry used to configure authorization rules
     */
    private void authorizeHttpRequestsCustomizer(AuthorizeHttpRequestsConfigurer<HttpSecurity>
                                                         .AuthorizationManagerRequestMatcherRegistry configurer) {
        allowedUrlsConfigurer(configurer);
        configurer
                .requestMatchers("/lss/admin/**").hasAnyRole("NORMAL", "ROOT")
                .requestMatchers("/lss/admin/v1/**").hasAnyRole("NORMAL", "ROOT");
        configurer.anyRequest().permitAll();
    }

    /**
     * Configures allowed URLs for unauthenticated access.
     * Specific API endpoints can be configured here to be accessible without authentication.
     * <p>
     * The following configuration is commented out because it is an example and may not be required in all environments.
     * Uncomment and modify as needed based on the specific security requirements of your application.
     *
     * @param configurer the AuthorizationManagerRequestMatcherRegistry used to configure URL-specific authorization rules
     */
    private void allowedUrlsConfigurer(AuthorizeHttpRequestsConfigurer<HttpSecurity>
                                               .AuthorizationManagerRequestMatcherRegistry configurer) {
        String[] POST_PERMIT_ALL = {
                "/lss/admin/v1/login",
                "/lss/admin/v1/refresh-token",
                "/lss/admin/v1/password-reset/send-otp",
                "/lss/admin/v1/password-reset/verify-otp",
                "/lss/admin/v1/refresh-token",

        };

        String[] GET_PERMIT_ALL = {
                "/", "/static/**",
                "/assets/**", "/error",
        };

        String[] PERMIT_ALL = {
                "/health",
                "/actuator/health",
                "/lss/admin/v1/admin-password-policy",
                "/lss/admin/v1/dids/detail",
        };
        // Allowed API
        configurer.requestMatchers(HttpMethod.POST, POST_PERMIT_ALL).permitAll()
                .requestMatchers(PERMIT_ALL).permitAll()
                .requestMatchers(HttpMethod.GET, GET_PERMIT_ALL).permitAll();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        encoders.put("bcrypt", new BCryptPasswordEncoder(12));

        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}