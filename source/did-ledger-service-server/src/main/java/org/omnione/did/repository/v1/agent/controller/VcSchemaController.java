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
 * See the License for the specific language governing permissions and1
 * limitations under the License.
 */
package org.omnione.did.repository.v1.agent.controller;

import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.config.ApiKeyRequired;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.constants.ApiType;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.db.constant.ApiKeyRole;
import org.omnione.did.repository.v1.agent.dto.vc.InputVcSchemaReqDto;
import org.omnione.did.repository.v1.agent.service.VcSchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.AGENT_V1  + UrlConstant.VC_SCHEMA)
public class VcSchemaController {

    private final VcSchemaService vcSchemaService;

    @ArticleLog(name = "[VC Schema] Generate VC Schema", apiType = ApiType.AGENT,
            targetType = "VC_SCHEMA", actionType = ActionType.CREATE)
    @PostMapping
    @ResponseBody
    @ApiKeyRequired(role = ApiKeyRole.ISSUER)
    public void generateVcSchema(@RequestBody InputVcSchemaReqDto request) {
        vcSchemaService.generateVcSchema(request);
    }

    @GetMapping
    @ResponseBody
    @ApiKeyRequired(role = ApiKeyRole.READ)
    public String getVcSchema(@RequestParam(value="schemaId") String schemaId) {
        return vcSchemaService.getVcSchema(schemaId);
    }
}
