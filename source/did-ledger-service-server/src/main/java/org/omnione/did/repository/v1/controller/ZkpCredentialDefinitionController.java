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
package org.omnione.did.repository.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.dto.zkp.InputZkpCredentialDefinitionReqDto;
import org.omnione.did.repository.v1.service.ZkpCredentialDefinitionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS  + UrlConstant.CredentialDefinition.V1)
public class ZkpCredentialDefinitionController {

    private final ZkpCredentialDefinitionService zkpCredentialDefinitionService;

    @PostMapping
    @ResponseBody
    public void generateZkpCredentialDefinition(@RequestBody InputZkpCredentialDefinitionReqDto request) {
         zkpCredentialDefinitionService.generateZkpCredentialDefinition(request);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<String> getZkpCredentialDefinition(@RequestParam(value="definitionId") String definitionId) {
        String zkpCredentialDefinition = zkpCredentialDefinitionService.getZkpCredentialDefinition(definitionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(zkpCredentialDefinition);
    }
}
