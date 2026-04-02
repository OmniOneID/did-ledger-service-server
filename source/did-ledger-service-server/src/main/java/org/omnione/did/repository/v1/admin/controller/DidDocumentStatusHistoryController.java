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

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.admin.dto.did.DidDocumentStatusHistoryDto;
import org.omnione.did.repository.v1.admin.dto.did.DidDocumentStatusHistoryWithDidDto;
import org.omnione.did.repository.v1.admin.service.DidDocumentStatusHistoryManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing DID Document Status History in the Admin Console.
 * <p>
 * Provides endpoints for DID document status history retrieval and search functionality.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class DidDocumentStatusHistoryController {

    private final DidDocumentStatusHistoryManagementService didDocumentStatusHistoryManagementService;

    /**
     * Retrieves a paginated list of DID document status histories based on search criteria.
     *
     * @param searchKey   the key to filter by (did, version, fromStatus, toStatus, reason)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of DidDocumentStatusHistoryWithDid DTOs
     */
    @GetMapping(value = "/did-document-status-histories/list")
    public Page<DidDocumentStatusHistoryWithDidDto> searchDidDocumentStatusHistories(String searchKey, String searchValue, Pageable pageable) {
        return didDocumentStatusHistoryManagementService.searchDidDocumentStatusHistories(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves DID document status history details by ID.
     *
     * @param id the DidDocumentStatusHistory ID
     * @return DidDocumentStatusHistory DTO
     */
    @GetMapping(value = "/did-document-status-histories")
    public DidDocumentStatusHistoryDto getDidDocumentStatusHistory(@RequestParam Long id) {
        return didDocumentStatusHistoryManagementService.findById(id);
    }
}
