package org.omnione.did.base.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class OpenApiConfig {
    private final BuildProperties buildProperties;
    
    @Value("${springdoc.server.url:}")
    private String serverUrl;

    @Bean
    public OpenAPI openApi() {
        OpenAPI openAPI = new OpenAPI()
                .info(getInfo())
                .components(getComponents())
                .addSecurityItem(new SecurityRequirement().addList("X-API-Key"));
        
        // Only add server configuration if springdoc.server.url is explicitly set
        if (StringUtils.hasText(serverUrl)) {
            String resolvedServerUrl = resolveServerUrl();
            log.info("OpenAPI server URL configured: {}", resolvedServerUrl);
            
            openAPI.servers(List.of(
                    new io.swagger.v3.oas.models.servers.Server()
                            .url(resolvedServerUrl)
            ));
        } else {
            log.info("No custom server URL configured. Using default SpringDoc behavior.");
        }
        
        return openAPI;
    }
    
    private Components getComponents() {
        return new Components()
                .addSecuritySchemes("X-API-Key", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description("API Key for authentication. Required roles: TAS, ISSUER, READ")
                );
    }
    
    /**
     * Safely processes and validates the server URL.
     * Adds protocol prefix if missing and trims whitespace.
     */
    private String resolveServerUrl() {
        // URL format validation (basic check)
        if (!serverUrl.startsWith("http://") && !serverUrl.startsWith("https://")) {
            String correctedUrl = "https://" + serverUrl;
            log.warn("Server URL missing protocol. Corrected from '{}' to '{}'", serverUrl, correctedUrl);
            return correctedUrl;
        }
        
        return serverUrl.trim();
    }

    private Info getInfo() {
        return new Info()
                .title(buildProperties.getName() + " API")
                .description(buildProperties.getName())
                .version(buildProperties.getVersion())
                .license(getLicense());
    }

    private License getLicense() {
        return new License().name("Apache 2.0")
                .url("https://github.com/OmniOneID");
    }

}
