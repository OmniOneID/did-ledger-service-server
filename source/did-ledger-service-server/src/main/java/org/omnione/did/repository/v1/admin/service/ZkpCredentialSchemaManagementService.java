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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ZkpCredentialSchema;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialSchemaDetailDto;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialSchemaDto;
import org.omnione.did.repository.v1.agent.service.query.ZkpCredentialSchemaQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ZkpCredentialSchemaManagementService {
    private final ZkpCredentialSchemaQueryService zkpCredentialSchemaQueryService;
    private final ObjectMapper objectMapper;

    public Page<ZkpCredentialSchemaDto> searchZkpCredentialSchema(String searchKey, String searchValue, Pageable pageable) {
        log.debug("\t--> Searching ZKP Credential Schema list with key: {}, value: {}", searchKey, searchValue);
        return zkpCredentialSchemaQueryService.searchZkpCredentialSchemaList(searchKey, searchValue, pageable);
    }

    public ZkpCredentialSchemaDetailDto getZkpCredentialSchemaDetail(Long id) {
        log.debug("\t--> Finding ZKP Credential Schema by id: {}", id);
        ZkpCredentialSchema entity = zkpCredentialSchemaQueryService.findById(id);

        return ZkpCredentialSchemaDetailDto.builder()
                .basicInfo(ZkpCredentialSchemaDto.fromEntity(entity))
                .parsedSchema(toPrettyJson(entity.getSchema()))
                .build();
    }

    private String toPrettyJson(String rawJson) {
        try {
            Object json = objectMapper.readValue(rawJson, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse ZKP Credential Schema JSON, using raw value", e);
            return rawJson;
        }
    }
}
