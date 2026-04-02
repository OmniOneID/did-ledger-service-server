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
package org.omnione.did.repository.v1.admin.service.query;

import org.omnione.did.base.db.domain.ApiKey;
import org.omnione.did.base.db.repository.ApiKeyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.apikey.ApiKeyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyQueryService {
    private final ApiKeyRepository apiKeyRepository;

    public Page<ApiKeyDto> searchApiKeyList(String searchKey, String searchValue, Pageable pageable) {
        Page<ApiKey> apiKeyPage = apiKeyRepository.searchApiKeys(searchKey, searchValue, pageable);

        List<ApiKeyDto> apiKeyDtos = apiKeyPage.getContent().stream()
                .map(ApiKeyDto::fromApiKey)
                .collect(Collectors.toList());

        return new PageImpl<>(apiKeyDtos, pageable, apiKeyPage.getTotalElements());
    }

    public ApiKey findById(Long id) {
        return apiKeyRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.API_KEY_NOT_FOUND));
    }

    public ApiKey findByApiKey(String apiKey) {
        return apiKeyRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new OpenDidException(ErrorCode.API_KEY_NOT_FOUND));
    }

    public ApiKey findByApiKeyAndIsActiveTrue(String apiKey) {
        return apiKeyRepository.findByApiKeyAndIsActiveTrue(apiKey)
                .orElseThrow(() -> new OpenDidException(ErrorCode.API_KEY_NOT_FOUND));
    }

    public ApiKey findByMaskedApiKey(String maskedApiKey) {
        return apiKeyRepository.findByMaskedApiKey(maskedApiKey)
                .orElseThrow(() -> new OpenDidException(ErrorCode.API_KEY_NOT_FOUND));
    }

    public long countByName(String name) {
        return apiKeyRepository.countByName(name);
    }
}
