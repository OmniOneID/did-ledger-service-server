package org.omnione.did.base.config;

import org.omnione.did.base.aop.ApiLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationPropertiesScan("org.omnione.did.base.property")
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final ApiKeyValidationInterceptor apiKeyValidationInterceptor;
    private final ApiLogInterceptor apiLogInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyValidationInterceptor)
                .addPathPatterns(
                    // Agent API 경로들
                    "/lss/api/v1/did-doc",           // DidController
                    "/lss/api/v1/did-doc/**",
                    "/lss/api/v1/vc-meta",           // VcController  
                    "/lss/api/v1/vc-meta/**",
                    "/lss/api/v1/vc-schema",         // VcSchemaController
                    "/lss/api/v1/vc-schema/**",
                    "/lss/api/v1/credential-definition",      // ZkpCredentialDefinitionController
                    "/lss/api/v1/credential-definition/**",
                    "/lss/api/v1/credential-schema",          // ZkpCredentialSchemaController
                    "/lss/api/v1/credential-schema/**"
                    // 필요한 다른 경로들도 여기에 추가
                );
        registry.addInterceptor(apiLogInterceptor);
    }
}
