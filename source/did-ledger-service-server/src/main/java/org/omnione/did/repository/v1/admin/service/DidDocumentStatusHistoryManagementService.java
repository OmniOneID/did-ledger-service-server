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

import org.omnione.did.repository.v1.admin.dto.did.DidDocumentStatusHistoryDto;
import org.omnione.did.repository.v1.admin.dto.did.DidDocumentStatusHistoryWithDidDto;
import org.omnione.did.repository.v1.admin.service.query.DidDocumentStatusHistoryQueryService;
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
public class DidDocumentStatusHistoryManagementService {
    private final DidDocumentStatusHistoryQueryService didDocumentStatusHistoryQueryService;

    public Page<DidDocumentStatusHistoryWithDidDto> searchDidDocumentStatusHistories(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchDidDocumentStatusHistories ===");

        log.debug("\t--> Searching DidDocumentStatusHistory list with key: {}, value: {}", searchKey, searchValue);
        Page<DidDocumentStatusHistoryWithDidDto> result = didDocumentStatusHistoryQueryService.searchDidDocumentStatusHistoryList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchDidDocumentStatusHistories ===");
        return result;
    }

    public DidDocumentStatusHistoryDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding DidDocumentStatusHistory by id: {}", id);
        DidDocumentStatusHistoryDto result = DidDocumentStatusHistoryDto.fromDidDocumentStatusHistory(
                didDocumentStatusHistoryQueryService.findById(id)
        );

        log.debug("=== Finished findById ===");
        return result;
    }
}
