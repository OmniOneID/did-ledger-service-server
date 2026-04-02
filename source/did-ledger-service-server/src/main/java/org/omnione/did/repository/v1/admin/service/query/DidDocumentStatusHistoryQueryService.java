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

import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.omnione.did.base.db.repository.DidDocumentStatusHistoryRepository;
import org.omnione.did.repository.v1.admin.dto.did.DidDocumentStatusHistoryWithDidDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DidDocumentStatusHistoryQueryService {
    private final DidDocumentStatusHistoryRepository didDocumentStatusHistoryRepository;

    public Page<DidDocumentStatusHistoryWithDidDto> searchDidDocumentStatusHistoryList(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchDidDocumentStatusHistoryList ===");
        log.debug("\t--> searchKey: {}, searchValue: {}", searchKey, searchValue);

        Page<DidDocumentStatusHistoryWithDidDto> historyPage = didDocumentStatusHistoryRepository.searchDidDocumentStatusHistoriesWithDid(searchKey, searchValue, pageable);

        log.debug("=== Finished searchDidDocumentStatusHistoryList ===");
        return historyPage;
    }

    public DidDocumentStatusHistory findById(Long id) {
        log.debug("=== Starting findById ===");
        log.debug("\t--> id: {}", id);

        DidDocumentStatusHistory result = didDocumentStatusHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DidDocumentStatusHistory not found with id: " + id));

        log.debug("=== Finished findById ===");
        return result;
    }
}
