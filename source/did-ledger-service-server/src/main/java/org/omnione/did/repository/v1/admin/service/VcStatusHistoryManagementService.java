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

import org.omnione.did.repository.v1.admin.dto.vcstatushistory.VcStatusHistoryDto;
import org.omnione.did.repository.v1.admin.service.query.VcStatusHistoryQueryService;
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
public class VcStatusHistoryManagementService {
    private final VcStatusHistoryQueryService vcStatusHistoryQueryService;

    public Page<VcStatusHistoryDto> searchVcStatusHistory(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchVcStatusHistory ===");

        log.debug("\t--> Searching VC Status History list with key: {}, value: {}", searchKey, searchValue);
        Page<VcStatusHistoryDto> result = vcStatusHistoryQueryService.searchVcStatusHistoryList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchVcStatusHistory ===");
        return result;
    }

    public VcStatusHistoryDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding VC Status History by id: {}", id);
        VcStatusHistoryDto result = VcStatusHistoryDto.fromVcStatusHistory(vcStatusHistoryQueryService.findById(id));

        log.debug("=== Finished findById ===");
        return result;
    }
}
