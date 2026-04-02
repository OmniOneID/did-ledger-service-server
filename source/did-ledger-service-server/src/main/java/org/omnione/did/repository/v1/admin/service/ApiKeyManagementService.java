/*
 * Copyright 2025 OmniOne.
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
package org.omnione.did.repository.v1.admin.service;

import org.omnione.did.base.db.domain.ApiKey;
import org.omnione.did.base.db.repository.ApiKeyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.repository.v1.admin.dto.apikey.*;
import org.omnione.did.repository.v1.admin.service.query.ApiKeyQueryService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApiKeyManagementService {
    
    // Constants for API key generation
    private static final String API_KEY_PREFIX = "lss_";
    private static final int EXPIRY_DAYS = 30;
    private static final int MAX_GENERATION_ATTEMPTS = 10;
    
    private final ApiKeyQueryService apiKeyQueryService;
    private final ApiKeyRepository apiKeyRepository;

    /**
     * Retrieves a paginated list of API keys based on search criteria.
     *
     * @param searchKey   the key to filter by
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of API key DTOs
     */
    public Page<ApiKeyDto> searchApiKeys(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchApiKeys ===");

        log.debug("\t--> Searching API keys with key: {}, value: {}", searchKey, searchValue);
        Page<ApiKeyDto> result = apiKeyQueryService.searchApiKeyList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchApiKeys ===");
        return result;
    }

    /**
     * Retrieves API key details by ID.
     *
     * @param id the API key ID
     * @return API key DTO
     */
    public ApiKeyDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding API key by id: {}", id);
        ApiKeyDto result = ApiKeyDto.fromApiKey(apiKeyQueryService.findById(id));

        log.debug("=== Finished findById ===");
        return result;
    }
    
    /**
     * Creates a new API key with the specified details.
     * The generated API key is hashed using SHA-256 before storage and returned in plain text only once.
     *
     * @param reqDto the request DTO containing API key details
     * @return response DTO containing the plain text API key and metadata
     * @throws OpenDidException if API key generation fails after maximum attempts
     */
    public CreateApiKeyResDto createApiKey(CreateApiKeyReqDto reqDto) {
        log.debug("=== Starting createApiKey ===");

        log.debug("\t--> Generating unique plain text API key");
        // Generate unique plain text API key
        String plainApiKey = generateUniqueApiKey();
        
        log.debug("\t--> Hashing API key using SHA-256");
        // Hash the API key using SHA-256
        String hashedApiKey = hashApiKey(plainApiKey);
        
        log.debug("\t--> Creating masked version and setting expiration");
        // Create masked version for display purposes
        String maskedApiKey = maskApiKey(plainApiKey);
        
        // Set expiration date using the provided days from request
        // For 9999 years (3652425 days), set it directly to avoid overflow issues
        Instant expiresAt;
        if (reqDto.getExpirationDays() == 3652425) {
            // Set directly to year 9999
            expiresAt = Instant.parse("9999-12-31T23:59:59.999Z");
        } else {
            expiresAt = Instant.now().plusSeconds((long) reqDto.getExpirationDays() * 24 * 60 * 60);
        }
        
        log.debug("\t--> Building and saving API key entity");
        // Build and save the API key entity
        ApiKey entity = ApiKey.builder()
                .apiKey(hashedApiKey)
                .maskedApiKey(maskedApiKey)
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .role(reqDto.getRole())
                .isActive(true)
                .expiresAt(expiresAt)
                .build();
        
        apiKeyRepository.save(entity);
        
        log.info("API Key created: id={}, name={}, expirationDays={}, expiresAt={}", 
                entity.getId(), entity.getName(), reqDto.getExpirationDays(), expiresAt);
        
        // Return response with plain text API key (only returned once)
        CreateApiKeyResDto result = CreateApiKeyResDto.builder()
                .apiKey(plainApiKey)
                .name(entity.getName())
                .expiresAt(expiresAt)
                .build();

        log.debug("=== Finished createApiKey ===");
        return result;
    }
    
    /**
     * Deactivates an API key by setting its active status to false.
     *
     * @param id the API key ID to deactivate
     * @return empty response DTO
     * @throws OpenDidException if API key is not found
     */
    public EmptyResDto deactivateApiKey(Long id) {
        log.debug("=== Starting deactivateApiKey ===");

        log.debug("\t--> Finding API key to deactivate with id: {}", id);
        ApiKey apiKey = apiKeyQueryService.findById(id);
        
        log.debug("\t--> Setting API key as inactive");
        apiKey.setIsActive(false);
        apiKeyRepository.save(apiKey);
        
        log.info("API key deactivated: id={}, name={}", id, apiKey.getName());

        log.debug("=== Finished deactivateApiKey ===");
        return new EmptyResDto();
    }

    /**
     * Activates an API key by setting its active status to true.
     *
     * @param id the API key ID to activate
     * @return empty response DTO
     * @throws OpenDidException if API key is not found
     */
    public EmptyResDto activateApiKey(Long id) {
        log.debug("=== Starting activateApiKey ===");

        log.debug("\t--> Finding API key to activate with id: {}", id);
        ApiKey apiKey = apiKeyQueryService.findById(id);
        
        log.debug("\t--> Setting API key as active");
        apiKey.setIsActive(true);
        apiKeyRepository.save(apiKey);
        
        log.info("API key activated: id={}, name={}", id, apiKey.getName());

        log.debug("=== Finished activateApiKey ===");
        return new EmptyResDto();
    }

    /**
     * Renews an API key by extending its expiration date by the specified number of days.
     * Only active API keys can be renewed.
     *
     * @param id the API key ID to renew
     * @param renewApiKeyReqDto the request DTO containing renewal details
     * @return response DTO containing renewal details
     * @throws OpenDidException if API key is not found or not active
     */
    public RenewApiKeyResDto renewApiKey(Long id, RenewApiKeyReqDto renewApiKeyReqDto) {
        log.debug("=== Starting renewApiKey ===");

        int daysToExtend = calculateDaysToExtend(renewApiKeyReqDto);
        log.debug("daysToExtend={}", daysToExtend);

        log.debug("\t--> Finding API key to renew with id: {}, extensionDays: {}", id, daysToExtend);
        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findById(id);
        
        if (apiKeyOpt.isEmpty()) {
            throw new OpenDidException(ErrorCode.API_KEY_NOT_FOUND);
        }
        
        ApiKey apiKey = apiKeyOpt.get();
        
        log.debug("\t--> Checking if API key is active");
        if (!apiKey.getIsActive()) {
            throw new OpenDidException(ErrorCode.API_KEY_NOT_FOUND);
        }
        
        log.debug("\t--> Extending expiration date by {} days", daysToExtend);
        Instant previousExpiresAt = apiKey.getExpiresAt();
        
        // For 9999 years (3652425 days), set it directly to avoid overflow issues
        Instant newExpiresAt;
        if (daysToExtend == 3652425) {
            // Set directly to year 9999
            newExpiresAt = Instant.parse("9999-12-31T23:59:59.999Z");
        } else {
            // If the API key has already expired, extend from current time
            // Otherwise, extend from the current expiration date
            Instant baseTime = previousExpiresAt.isBefore(Instant.now()) ? Instant.now() : previousExpiresAt;
            newExpiresAt = baseTime.plusSeconds((long) daysToExtend * 24 * 60 * 60);
        }
        
        apiKey.setExpiresAt(newExpiresAt);
        apiKeyRepository.save(apiKey);
        
        log.info("API Key renewed: id={}, name={}, extensionDays={}, previousExpiresAt={}, newExpiresAt={}", 
                apiKey.getId(), apiKey.getName(), daysToExtend, previousExpiresAt, newExpiresAt);
        
        RenewApiKeyResDto result = RenewApiKeyResDto.builder()
                .id(apiKey.getId())
                .name(apiKey.getName())
                .previousExpiresAt(previousExpiresAt)
                .newExpiresAt(newExpiresAt)
                .message("API key has been renewed successfully for " + daysToExtend + " days")
                .build();

        log.debug("=== Finished renewApiKey ===");
        return result;
    }

    /**
     * Renews an API key by extending its expiration date by 30 days (legacy method).
     * Only active API keys can be renewed.
     *
     * @param id the API key ID to renew
     * @return response DTO containing renewal details
     * @throws OpenDidException if API key is not found or not active
     * @deprecated Use {@link #renewApiKey(Long, RenewApiKeyReqDto)} instead
     */
    @Deprecated
    public RenewApiKeyResDto renewApiKey(Long id) {
        RenewApiKeyReqDto reqDto = new RenewApiKeyReqDto();
        reqDto.setExtensionDays(EXPIRY_DAYS);
        return renewApiKey(id, reqDto);
    }

    /**
     * Calculates the number of days to extend based on the request DTO.
     * Returns the default expiry days if DTO is null or extensionDays is null.
     *
     * @param renewApiKeyReqDto the request DTO containing renewal details
     * @return the number of days to extend
     */
    private int calculateDaysToExtend(RenewApiKeyReqDto renewApiKeyReqDto) {
        return renewApiKeyReqDto != null && renewApiKeyReqDto.getExtensionDays() != null 
                ? renewApiKeyReqDto.getExtensionDays() 
                : EXPIRY_DAYS;
    }

    /**
     * Generates a unique API key by combining prefix and UUID.
     * Ensures uniqueness by checking against existing keys.
     *
     * @return unique plain text API key
     * @throws OpenDidException if unable to generate unique key after maximum attempts
     */
    private String generateUniqueApiKey() {
        String plainApiKey;
        int attempts = 0;
        
        do {
            plainApiKey = API_KEY_PREFIX + RandomUtil.generateUUID();
            attempts++;
            
            if (attempts > MAX_GENERATION_ATTEMPTS) {
                throw new OpenDidException(ErrorCode.API_KEY_GENERATION_FAILED);
            }
        } while (isApiKeyExists(plainApiKey));
        
        return plainApiKey;
    }
    
    /**
     * Checks if an API key already exists by comparing SHA-256 hash with stored hashed keys.
     *
     * @param plainApiKey the plain text API key to check
     * @return true if the API key already exists, false otherwise
     */
    private boolean isApiKeyExists(String plainApiKey) {
        String hashedApiKey = hashApiKey(plainApiKey);
        return apiKeyRepository.findByApiKey(hashedApiKey).isPresent();
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
            throw new OpenDidException(ErrorCode.API_KEY_GENERATION_FAILED);
        }
    }
    
    /**
     * Creates a masked version of the API key for display purposes.
     * Shows first 7 characters, middle asterisks, and last 3 characters.
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
}
