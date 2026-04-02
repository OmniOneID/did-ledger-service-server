package org.omnione.did.base.service;

import org.omnione.did.base.constants.ConfigConstant;
import org.omnione.did.base.db.constant.ApiKeyRole;
import org.omnione.did.base.db.domain.ApiKey;
import org.omnione.did.base.db.repository.ApiKeyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseDigestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HexFormat;
import java.util.Optional;

/**
 * API Key validation service
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ApiKeyValidationService {
    
    private final ServerConfigService serverConfigService;
    private final ApiKeyRepository apiKeyRepository;
    
    /**
     * Validation result class containing validation status and error code
     */
    @Getter
    public static class ValidationResult {
        private final boolean valid;
        private final ErrorCode errorCode;
        
        private ValidationResult(boolean valid, ErrorCode errorCode) {
            this.valid = valid;
            this.errorCode = errorCode;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(ErrorCode errorCode) {
            return new ValidationResult(false, errorCode);
        }
    }
    
    /**
     * Validates API key and role permissions
     * 
     * @param requiredRole minimum required role for access
     * @param request HTTP request object
     * @return ValidationResult containing validation status and error code
     */
    @Transactional
    public ValidationResult validateApiKey(ApiKeyRole requiredRole, HttpServletRequest request) {
        // 1. Check if API key validation is enabled in server_config table
        boolean isValidationEnabled = serverConfigService.isConfigEnabled(ConfigConstant.API_KEY_VALIDATION_ENABLED);
        
        if (!isValidationEnabled) {
            log.debug("API key validation is disabled, skipping validation");
            return ValidationResult.success(); // Skip validation if disabled
        }
        
        // 2. Extract API key from request headers
        String apiKey = extractApiKeyFromHeader(request);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("API key is missing in request header");
            return ValidationResult.failure(ErrorCode.API_KEY_MISSING);
        }
        
        // 3. Validate API key using SHA-256 hash comparison and retrieve entity
        ApiKey apiKeyEntity = getValidApiKeyEntity(apiKey);
        if (apiKeyEntity == null) {
            log.warn("Invalid API key: {}", maskApiKey(apiKey));
            return ValidationResult.failure(ErrorCode.API_KEY_INVALID);
        }
        
        // 4. Check role-based permissions
        boolean hasPermission = checkRolePermission(apiKeyEntity.getRole(), requiredRole);
        if (!hasPermission) {
            log.warn("API key role '{}' does not have permission for required role '{}'", apiKeyEntity.getRole(), requiredRole);
            return ValidationResult.failure(ErrorCode.API_KEY_INSUFFICIENT_PERMISSION);
        }
        
        // 5. Update last_used_at timestamp using database's current timestamp
        try {
            updateLastUsedAt(apiKeyEntity.getApiKey());
            log.debug("API key validation successful and last_used_at updated using DB timestamp. Key role: {}, Required role: {}", 
                     apiKeyEntity.getRole(), requiredRole);
        } catch (OpenDidException e) {
            log.error("Failed to update last_used_at for API key", e);
            // Continue with success since validation was successful
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validates API key using SHA-256 hash comparison and returns the entity if valid
     * 
     * SHA-256은 같은 평문에 대해 항상 같은 해시값을 생성하므로,
     * 평문을 해시해서 DB에서 직접 조회할 수 있습니다.
     * 
     * @param plainApiKey plain text API key to validate
     * @return API key entity or null if invalid
     */
    private ApiKey getValidApiKeyEntity(String plainApiKey) {
        try {
            String hashedApiKey = hashApiKey(plainApiKey);
            
            log.debug("Validating API key using SHA-256 hash comparison");
            Optional<ApiKey> apiKeyEntity = apiKeyRepository.findByApiKeyAndIsActiveTrue(hashedApiKey);
            
            if (apiKeyEntity.isPresent()) {
                log.debug("API key found and validated using SHA-256 hash for key ID: {}", apiKeyEntity.get().getId());
                return apiKeyEntity.get();
            } else {
                log.debug("API key not found or inactive");
                return null;
            }
            
        } catch (OpenDidException e) {
            log.error("Error validating API key using SHA-256: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Hashes an API key using SHA-256 algorithm.
     * 
     * @param plainApiKey the plain text API key to hash
     * @return the SHA-256 hashed API key as hex string
     */
    private String hashApiKey(String plainApiKey) {
        try {
            byte[] hashBytes = BaseDigestUtil.generateHash(plainApiKey);
            return HexFormat.of().formatHex(hashBytes);
        } catch (OpenDidException e) {
            log.error("Failed to hash API key", e);
            throw new RuntimeException("Failed to hash API key", e);
        }
    }
    
    /**
     * Updates the last_used_at timestamp using database's CURRENT_TIMESTAMP
     * 
     * @param hashedApiKey the hashed API key string to update
     */
    private void updateLastUsedAt(String hashedApiKey) {
        try {
            int updatedRows = apiKeyRepository.updateLastUsedAtByApiKey(hashedApiKey);
            if (updatedRows > 0) {
                log.debug("Updated last_used_at using DB timestamp for API key (rows affected: {})", updatedRows);
            } else {
                log.warn("No rows updated for last_used_at - API key may have been deactivated");
            }
        } catch (OpenDidException e) {
            log.error("Failed to update last_used_at using DB timestamp for API key", e);
            throw e;
        }
    }
    
    /**
     * Checks if the API key role has permission for the required role
     * 
     * @param keyRole role of the API key
     * @param requiredRole minimum required role
     * @return true if permission is granted, false otherwise
     */
    private boolean checkRolePermission(ApiKeyRole keyRole, ApiKeyRole requiredRole) {
        switch (requiredRole) {
            case TAS:
                // TAS required: Only TAS allowed
                return keyRole == ApiKeyRole.TAS;
                
            case ISSUER:
                // ISSUER required: TAS and ISSUER allowed
                return keyRole == ApiKeyRole.TAS || keyRole == ApiKeyRole.ISSUER;
                
            case READ:
                // READ required: TAS, ISSUER, and READ allowed
                return keyRole == ApiKeyRole.TAS || keyRole == ApiKeyRole.ISSUER || keyRole == ApiKeyRole.READ;
                
            default:
                log.warn("Unknown required role: {}", requiredRole);
                return false;
        }
    }
    
    /**
     * Creates a masked version of the API key for logging purposes
     * Shows first 7 characters, middle asterisks, and last 3 characters
     * 
     * @param apiKey the plain text API key to mask
     * @return masked API key string
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "****";
        }
        return apiKey.substring(0, 7) + "***" + apiKey.substring(apiKey.length() - 3);
    }
    
    /**
     * Extracts API key from HTTP request headers
     * 
     * @param request HTTP request object
     * @return API key string or null if not found
     */
    private String extractApiKeyFromHeader(HttpServletRequest request) {
        // Try X-API-Key header first
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) {
            return apiKey.trim();
        }
        
        // Try alternative header name if needed
        apiKey = request.getHeader("API-Key");
        if (apiKey != null) {
            return apiKey.trim();
        }
        
        return null;
    }
}
