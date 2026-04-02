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
package org.omnione.did.repository.v1.agent.controller;

import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.config.ApiKeyRequired;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.constants.ApiType;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.db.constant.ApiKeyRole;
import org.omnione.did.repository.v1.agent.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.agent.dto.did.UpdateDidDocReqDto;
import org.omnione.did.repository.v1.agent.service.DidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.AGENT_V1 + UrlConstant.DID)
public class DidController {

    private final DidService didService;

    @ArticleLog(name = "[DID] Generate DID Document", apiType = ApiType.AGENT,
            targetType = "DID", actionType = ActionType.CREATE)
    @PostMapping
    @ApiKeyRequired(role = ApiKeyRole.TAS)
    public void generateDid(@RequestBody InputDidDocReqDto request) {
        didService.generateDid(request);
    }

    @GetMapping
    @ApiKeyRequired(role = ApiKeyRole.READ)
    public String getDid(@RequestParam(value = "did", defaultValue = "did:omn:tas") String didDoc) {
        return didService.getDid(didDoc);
    }

    @ArticleLog(name = "[DID] Update DID Document Status", apiType = ApiType.AGENT,
            targetType = "DID", actionType = ActionType.UPDATE_STATUS)
    @PutMapping
    @ApiKeyRequired(role = ApiKeyRole.TAS)
    public void updateDidStatus(@RequestBody UpdateDidDocReqDto request) {
        didService.updateStatus(request);
    }
}
