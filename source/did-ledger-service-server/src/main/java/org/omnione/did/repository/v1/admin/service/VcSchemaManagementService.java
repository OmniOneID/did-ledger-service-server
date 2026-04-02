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
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.repository.v1.admin.dto.vcschema.VcSchemaDetailDto;
import org.omnione.did.repository.v1.admin.dto.vcschema.VcSchemaDto;
import org.omnione.did.repository.v1.agent.service.query.VcSchemaQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VcSchemaManagementService {
    private final VcSchemaQueryService vcSchemaQueryService;
    private final VcSchemaRepository vcSchemaRepository;
    private final ObjectMapper objectMapper;

    public Page<VcSchemaDto> searchVcSchema(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchVcSchema ===");

        log.debug("\t--> Searching VC Schema list with key: {}, value: {}", searchKey, searchValue);
        Page<VcSchemaDto> result = vcSchemaQueryService.searchVcSchemaList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchVcSchema ===");
        return result;
    }

    public VcSchemaDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding VC Schema by id: {}", id);
        VcSchemaDto result = VcSchemaDto.fromVcSchemaInfo(vcSchemaQueryService.findById(id));

        log.debug("=== Finished findById ===");
        return result;
    }

    public VcSchemaDetailDto getVcSchemaDetail(Long id) {
        log.debug("=== Starting getVcSchemaDetail ===");

        log.debug("\t--> Finding VC Schema by id: {}", id);
        // 1. Get basic VC Schema information
        VcSchemaDto basicInfo = VcSchemaDto.fromVcSchemaInfo(vcSchemaQueryService.findById(id));

        log.debug("\t--> Formatting schema JSON");
        // 2. Parse and format schema JSON for better display
        String parsedSchema;
        try {
            Object json = objectMapper.readValue(basicInfo.getSchema(), Object.class);
            parsedSchema = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse schema JSON, using raw value", e);
            parsedSchema = basicInfo.getSchema();
        }

        // 3. Build detailed DTO with formatted schema
        VcSchemaDetailDto result = VcSchemaDetailDto.builder()
                .basicInfo(basicInfo)
                .parsedSchema(parsedSchema)
                .build();

        log.debug("=== Finished getVcSchemaDetail ===");
        return result;
    }
}
