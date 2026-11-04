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

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialSchemaDetailDto;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialSchemaDto;
import org.omnione.did.repository.v1.admin.service.ZkpCredentialSchemaManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for viewing ZKP Credential Schemas in the Admin Console.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class ZkpCredentialSchemaManagementController {

    private final ZkpCredentialSchemaManagementService zkpCredentialSchemaManagementService;

    /**
     * Retrieves a paginated list of ZKP Credential Schemas based on search criteria.
     *
     * @param searchKey   the key to filter by (schemaId, name, version, tag)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of ZKP Credential Schema DTOs
     */
    @GetMapping(value = "/zkp-credential-schema/list")
    public Page<ZkpCredentialSchemaDto> searchZkpCredentialSchema(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialSchemaManagementService.searchZkpCredentialSchema(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves ZKP Credential Schema details including the formatted JSON.
     *
     * @param id the ZKP Credential Schema ID
     * @return ZKP Credential Schema detail DTO
     */
    @GetMapping(value = "/zkp-credential-schema/detail")
    public ZkpCredentialSchemaDetailDto getZkpCredentialSchemaDetail(@RequestParam Long id) {
        return zkpCredentialSchemaManagementService.getZkpCredentialSchemaDetail(id);
    }
}
