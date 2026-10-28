package org.omnione.did.base.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omnione.did.base.constants.ConfigConstant;
import org.omnione.did.base.db.constant.ApiKeyRole;
import org.omnione.did.base.db.domain.ApiKey;
import org.omnione.did.base.db.repository.ApiKeyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiKeyValidationServiceTest {

    private ApiKeyRepository apiKeyRepository;
    private ApiKeyValidationService apiKeyValidationService;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        ServerConfigService serverConfigService = mock(ServerConfigService.class);
        when(serverConfigService.isConfigEnabled(ConfigConstant.API_KEY_VALIDATION_ENABLED)).thenReturn(true);
        apiKeyRepository = mock(ApiKeyRepository.class);
        apiKeyValidationService = new ApiKeyValidationService(serverConfigService, apiKeyRepository);

        request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "test-api-key");
    }

    @Test
    void rejectsExpiredApiKey() {
        givenActiveApiKey(Instant.now().minus(1, ChronoUnit.DAYS));

        ApiKeyValidationService.ValidationResult result = apiKeyValidationService.validateApiKey(ApiKeyRole.READ, request);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.API_KEY_EXPIRED);
        verify(apiKeyRepository, never()).updateLastUsedAtByApiKey(anyString());
    }

    @Test
    void acceptsApiKeyBeforeExpiry() {
        givenActiveApiKey(Instant.now().plus(1, ChronoUnit.DAYS));
        when(apiKeyRepository.updateLastUsedAtByApiKey(anyString())).thenReturn(1);

        ApiKeyValidationService.ValidationResult result = apiKeyValidationService.validateApiKey(ApiKeyRole.READ, request);

        assertThat(result.isValid()).isTrue();
    }

    private void givenActiveApiKey(Instant expiresAt) {
        ApiKey apiKey = ApiKey.builder()
                .apiKey("hashed-api-key")
                .role(ApiKeyRole.TAS)
                .isActive(true)
                .expiresAt(expiresAt)
                .build();
        when(apiKeyRepository.findByApiKeyAndIsActiveTrue(anyString())).thenReturn(Optional.of(apiKey));
    }
}
