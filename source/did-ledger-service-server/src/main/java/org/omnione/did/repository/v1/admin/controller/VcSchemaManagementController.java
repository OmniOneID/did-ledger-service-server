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
import org.omnione.did.repository.v1.admin.dto.vcschema.VcSchemaDetailDto;
import org.omnione.did.repository.v1.admin.dto.vcschema.VcSchemaDto;
import org.omnione.did.repository.v1.admin.service.VcSchemaManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing VC Schema in the Admin Console.
 * <p>
 * Provides endpoints for VC Schema retrieval and search functionality.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class VcSchemaManagementController {

    private final VcSchemaManagementService vcSchemaManagementService;

    /**
     * Retrieves a paginated list of VC Schema based on search criteria.
     *
     * @param searchKey   the key to filter by (schemaId, title, version, description, did)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of VC Schema DTOs
     */
    @GetMapping(value = "/vc-schema/list")
    public Page<VcSchemaDto> searchVcSchema(String searchKey, String searchValue, Pageable pageable) {
        return vcSchemaManagementService.searchVcSchema(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves VC Schema details by ID.
     *
     * @param id the VC Schema ID
     * @return VC Schema DTO
     */
    @GetMapping(value = "/vc-schema")
    public VcSchemaDto getVcSchema(@RequestParam Long id) {
        return vcSchemaManagementService.findById(id);
    }

    /**
     * Retrieves comprehensive VC Schema details including formatted schema.
     *
     * @param id the VC Schema ID
     * @return comprehensive VC Schema detail DTO with formatted schema
     */
    @GetMapping(value = "/vc-schema/detail")
    public VcSchemaDetailDto getVcSchemaDetail(@RequestParam Long id) {
        return vcSchemaManagementService.getVcSchemaDetail(id);
    }
}
