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
import org.omnione.did.base.db.domain.VcStatusHistory;
import org.omnione.did.base.db.repository.VcMetadataRepository;
import org.omnione.did.base.db.repository.VcStatusHistoryRepository;
import org.omnione.did.repository.v1.admin.dto.vcmetadata.VcMetadataDetailDto;
import org.omnione.did.repository.v1.admin.dto.vcmetadata.VcMetadataDto;
import org.omnione.did.repository.v1.admin.dto.vcmetadata.VcStatusHistoryDto;
import org.omnione.did.repository.v1.admin.service.query.VcMetadataQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VcMetadataManagementService {
    private final VcMetadataQueryService vcMetadataQueryService;
    private final VcMetadataRepository vcMetadataRepository;
    private final VcStatusHistoryRepository vcStatusHistoryRepository;
    private final ObjectMapper objectMapper;

    public Page<VcMetadataDto> searchVcMetadata(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchVcMetadata ===");

        log.debug("\t--> Searching VC Metadata list with key: {}, value: {}", searchKey, searchValue);
        Page<VcMetadataDto> result = vcMetadataQueryService.searchVcMetadataList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchVcMetadata ===");
        return result;
    }

    public VcMetadataDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding VC Metadata by id: {}", id);
        VcMetadataDto result = VcMetadataDto.fromVcMetadata(vcMetadataQueryService.findById(id));

        log.debug("=== Finished findById ===");
        return result;
    }

    public VcMetadataDetailDto getVcMetadataDetail(Long id) {
        log.debug("=== Starting getVcMetadataDetail ===");

        log.debug("\t--> Finding VC Metadata by id: {}", id);
        // 1. Get basic VC Metadata information
        VcMetadataDto basicInfo = VcMetadataDto.fromVcMetadata(vcMetadataQueryService.findById(id));

        log.debug("\t--> Formatting metadata JSON");
        // 2. Parse and format metadata JSON for better display
        String parsedMetadata;
        try {
            Object json = objectMapper.readValue(basicInfo.getMetadata(), Object.class);
            parsedMetadata = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse metadata JSON, using raw value", e);
            parsedMetadata = basicInfo.getMetadata();
        }

        log.debug("\t--> Finding Status History by vcId: {}", basicInfo.getVcId());
        // 3. Get status change history (ordered by change date descending)
        List<VcStatusHistory> statusHistory = vcStatusHistoryRepository.findAllByVcIdOrderByChangedAtDesc(basicInfo.getVcId());
        List<VcStatusHistoryDto> statusHistoryDtos = statusHistory.stream()
                .map(VcStatusHistoryDto::fromVcStatusHistory)
                .collect(Collectors.toList());

        // 4. Build detailed DTO with formatted metadata and status history
        VcMetadataDetailDto result = VcMetadataDetailDto.builder()
                .basicInfo(basicInfo)
                .parsedMetadata(parsedMetadata)
                .statusHistory(statusHistoryDtos)
                .build();

        log.debug("=== Finished getVcMetadataDetail ===");
        return result;
    }
}
