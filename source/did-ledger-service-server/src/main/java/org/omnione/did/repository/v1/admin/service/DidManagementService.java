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

import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.domain.DidDocumentRevoked;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.omnione.did.base.db.repository.DidDocumentRepository;
import org.omnione.did.base.db.repository.DidDocumentRevokedRepository;
import org.omnione.did.base.db.repository.DidDocumentStatusHistoryRepository;
import org.omnione.did.base.db.repository.DidRepository;
import org.omnione.did.repository.v1.admin.dto.did.*;
import org.omnione.did.repository.v1.admin.service.query.DidQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DidManagementService {
    private final DidQueryService didQueryService;
    private final DidRepository didRepository;
    private final DidDocumentRepository didDocumentRepository;
    private final DidDocumentRevokedRepository didDocumentRevokedRepository;
    private final DidDocumentStatusHistoryRepository didDocumentStatusHistoryRepository;

    public Page<DidDto> searchDids(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchDids ===");

        log.debug("\t--> Searching DID list with key: {}, value: {}", searchKey, searchValue);
        Page<DidDto> result = didQueryService.searchDidList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchDids ===");
        return result;
    }

    public DidDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding DID by id: {}", id);
        DidDto result = DidDto.fromDid(didQueryService.findById(id));

        log.debug("=== Finished findById ===");
        return result;
    }

    public DidDetailDto getDidDetail(Long id) {
        log.debug("=== Starting getDidDetail ===");

        log.debug("\t--> Finding DID by id: {}", id);
        // 1. Get basic DID information
        DidDto basicInfo = DidDto.fromDid(didQueryService.findById(id));

        log.debug("\t--> Finding DID Documents by didId: {}", id);
        // 2. Get current active DID Document list
        List<DidDocument> documents = didDocumentRepository.findAllByDidIdOrderByVersionDesc(id);
        List<DidDocumentHistoryDto> activeDocumentHistory = documents.stream()
                .map(DidDocumentHistoryDto::fromDidDocument)
                .collect(Collectors.toList());

        log.debug("\t--> Finding Revoked DID Documents by didId: {}", id);
        // 3. Get revoked DID Document list
        List<DidDocumentRevoked> revokedDocuments = didDocumentRevokedRepository.findAllByDidIdOrderByVersionDesc(id);
        List<DidDocumentHistoryDto> revokedDocumentHistory = revokedDocuments.stream()
                .map(DidDocumentHistoryDto::fromDidDocumentRevoked)
                .collect(Collectors.toList());

        log.debug("\t--> Merging and sorting document history");
        // 4. Merge both lists and sort by version (descending)
        List<DidDocumentHistoryDto> combinedDocumentHistory = new ArrayList<>();
        combinedDocumentHistory.addAll(activeDocumentHistory);
        combinedDocumentHistory.addAll(revokedDocumentHistory);
        
        // Sort by version (highest version first)
        combinedDocumentHistory.sort((a, b) -> b.getVersion().compareTo(a.getVersion()));

        log.debug("\t--> Finding Status History by didId: {}", id);
        // 5. Get status change history (ordered by change date descending)
        List<DidDocumentStatusHistory> statusHistory = didDocumentStatusHistoryRepository.findAllByDidIdOrderByChangedAtDesc(id);
        List<DidStatusHistoryDto> statusHistoryDtos = statusHistory.stream()
                .map(DidStatusHistoryDto::fromDidDocumentStatusHistory)
                .collect(Collectors.toList());

        // 6. Build detailed DTO with all information
        DidDetailDto result = DidDetailDto.builder()
                .basicInfo(basicInfo)
                .documentHistory(combinedDocumentHistory) // Combined document history
                .statusHistory(statusHistoryDtos)
                .build();

        log.debug("=== Finished getDidDetail ===");
        return result;
    }
}
