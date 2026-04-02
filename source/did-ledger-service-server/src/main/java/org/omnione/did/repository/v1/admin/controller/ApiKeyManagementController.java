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
package org.omnione.did.repository.v1.admin.controller;

import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.admin.dto.apikey.*;
import org.omnione.did.repository.v1.admin.service.ApiKeyManagementService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing API keys in the Admin Console.
 * <p>
 * Provides endpoints for API key retrieval, creation, updates, and management.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class ApiKeyManagementController {

    private final ApiKeyManagementService apiKeyManagementService;

    /**
     * Retrieves a paginated list of API keys based on search criteria.
     *
     * @param searchKey   the key to filter by (name, role, isActive)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of API key DTOs
     */
    @GetMapping(value = "/api-keys/list")
    public Page<ApiKeyDto> searchApiKeys(String searchKey, String searchValue, Pageable pageable) {
        return apiKeyManagementService.searchApiKeys(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves API key details by ID.
     *
     * @param id the API key ID
     * @return API key DTO
     */
    @GetMapping(value = "/api-keys")
    public ApiKeyDto getApiKey(@RequestParam Long id) {
        return apiKeyManagementService.findById(id);
    }

    /**
     * Creates a new API key with the specified name and description.
     * The generated API key is returned in plain text only once upon creation.
     *
     * @param createApiKeyReqDto the request data for API key creation
     * @return response containing the plain text API key and metadata
     */

    @ArticleLog(name = "[API Manage] Register API Key", description = "Register API Key",
            targetType = "API_KEY", actionType = ActionType.CREATE)
    @PostMapping(value = "/api-keys")
    public CreateApiKeyResDto createApiKey(@Valid @RequestBody CreateApiKeyReqDto createApiKeyReqDto) {
        return apiKeyManagementService.createApiKey(createApiKeyReqDto);
    }

    /**
     * Deactivates an existing API key by setting its active status to false.
     *
     * @param id the API key ID to deactivate
     * @return empty response indicating successful deactivation
     */

    @ArticleLog(name = "[API Manage] Update API Key", description = "Deactivates an existing API key by setting its active status to false.",
            targetType = "API_KEY", actionType = ActionType.UPDATE)
    @PutMapping(value = "/api-keys/{id}/deactivate")
    public EmptyResDto deactivateApiKey(@PathVariable Long id) {
        return apiKeyManagementService.deactivateApiKey(id);
    }

    /**
     * Activates an existing API key by setting its active status to true.
     *
     * @param id the API key ID to activate
     * @return empty response indicating successful activation
     */
    @ArticleLog(name = "[API Manage] Update API Key", description = "Activates an existing API key by setting its active status to true.",
            targetType = "API_KEY", actionType = ActionType.UPDATE)
    @PutMapping(value = "/api-keys/{id}/activate")
    public EmptyResDto activateApiKey(@PathVariable Long id) {
        return apiKeyManagementService.activateApiKey(id);
    }

    /**
     * Renews an existing API key by extending its expiration date.
     * Only active API keys can be renewed.
     *
     * @param id the API key ID to renew
     * @param renewApiKeyReqDto the request data containing extension days (optional, defaults to 30 days)
     * @return response containing renewal details including previous and new expiration dates
     */
    @ArticleLog(name = "[API Manage] Renew API Key", description = "Renews an existing API key by extending its expiration date.",
            targetType = "API_KEY", actionType = ActionType.UPDATE)
    @PutMapping(value = "/api-keys/{id}/renew")
    public RenewApiKeyResDto renewApiKey(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) RenewApiKeyReqDto renewApiKeyReqDto) {

        return apiKeyManagementService.renewApiKey(id, renewApiKeyReqDto);
    }
}
