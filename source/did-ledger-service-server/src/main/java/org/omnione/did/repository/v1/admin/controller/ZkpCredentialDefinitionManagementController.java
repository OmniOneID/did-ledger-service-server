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
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialDefinitionDetailDto;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialDefinitionDto;
import org.omnione.did.repository.v1.admin.service.ZkpCredentialDefinitionManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for viewing ZKP Credential Definitions in the Admin Console.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class ZkpCredentialDefinitionManagementController {

    private final ZkpCredentialDefinitionManagementService zkpCredentialDefinitionManagementService;

    /**
     * Retrieves a paginated list of ZKP Credential Definitions based on search criteria.
     *
     * @param searchKey   the key to filter by (definitionId, schemaId, version, tag)
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of ZKP Credential Definition DTOs
     */
    @GetMapping(value = "/zkp-credential-definition/list")
    public Page<ZkpCredentialDefinitionDto> searchZkpCredentialDefinition(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialDefinitionManagementService.searchZkpCredentialDefinition(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves ZKP Credential Definition details including the formatted JSON.
     *
     * @param id the ZKP Credential Definition ID
     * @return ZKP Credential Definition detail DTO
     */
    @GetMapping(value = "/zkp-credential-definition/detail")
    public ZkpCredentialDefinitionDetailDto getZkpCredentialDefinitionDetail(@RequestParam Long id) {
        return zkpCredentialDefinitionManagementService.getZkpCredentialDefinitionDetail(id);
    }
}
