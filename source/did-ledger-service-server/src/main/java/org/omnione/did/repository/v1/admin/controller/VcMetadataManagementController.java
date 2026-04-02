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
import org.omnione.did.repository.v1.admin.dto.vcmetadata.VcMetadataDetailDto;
import org.omnione.did.repository.v1.admin.dto.vcmetadata.VcMetadataDto;
import org.omnione.did.repository.v1.admin.service.VcMetadataManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing VC Metadata in the Admin Console.
 * <p>
 * Provides endpoints for VC Metadata retrieval and search functionality.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class VcMetadataManagementController {

    private final VcMetadataManagementService vcMetadataManagementService;

    /**
     * Retrieves a paginated list of VC Metadata based on search criteria.
     *
     * @param searchKey   the key to filter by (vcId, issuerDid, subjectDid, vcSchema, status)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of VC Metadata DTOs
     */
    @GetMapping(value = "/vc-metadata/list")
    public Page<VcMetadataDto> searchVcMetadata(String searchKey, String searchValue, Pageable pageable) {
        return vcMetadataManagementService.searchVcMetadata(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves VC Metadata details by ID.
     *
     * @param id the VC Metadata ID
     * @return VC Metadata DTO
     */
    @GetMapping(value = "/vc-metadata")
    public VcMetadataDto getVcMetadata(@RequestParam Long id) {
        return vcMetadataManagementService.findById(id);
    }

    /**
     * Retrieves comprehensive VC Metadata details including formatted metadata and status history.
     *
     * @param id the VC Metadata ID
     * @return comprehensive VC Metadata detail DTO with formatted metadata and status change history
     */
    @GetMapping(value = "/vc-metadata/detail")
    public VcMetadataDetailDto getVcMetadataDetail(@RequestParam Long id) {
        return vcMetadataManagementService.getVcMetadataDetail(id);
    }
}
