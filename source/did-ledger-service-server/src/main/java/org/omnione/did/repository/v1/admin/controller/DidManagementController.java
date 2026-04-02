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
import org.omnione.did.repository.v1.admin.dto.did.DidDetailDto;
import org.omnione.did.repository.v1.admin.dto.did.DidDto;
import org.omnione.did.repository.v1.admin.service.DidManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing DID documents in the Admin Console.
 * <p>
 * Provides endpoints for DID document retrieval and search functionality.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class DidManagementController {

    private final DidManagementService didManagementService;

    /**
     * Retrieves a paginated list of DIDs based on search criteria.
     *
     * @param searchKey   the key to filter by (did, role, status, version)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of DID DTOs
     */
    @GetMapping(value = "/dids/list")
    public Page<DidDto> searchDids(String searchKey, String searchValue, Pageable pageable) {
        return didManagementService.searchDids(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves DID details by ID.
     *
     * @param id the DID ID
     * @return DID DTO
     */
    @GetMapping(value = "/dids")
    public DidDto getDid(@RequestParam Long id) {
        return didManagementService.findById(id);
    }

    /**
     * Retrieves comprehensive DID details including documents, revoked documents, and status history.
     *
     * @param id the DID ID
     * @return comprehensive DID detail DTO with all related information
     */
    @GetMapping(value = "/dids/detail")
    public DidDetailDto getDidDetail(@RequestParam Long id) {
        return didManagementService.getDidDetail(id);
    }
}
