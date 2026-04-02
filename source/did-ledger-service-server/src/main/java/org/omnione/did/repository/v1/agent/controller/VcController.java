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
import org.omnione.did.repository.v1.agent.dto.vc.UpdateVcStatusReqDto;
import org.omnione.did.repository.v1.agent.service.VcService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.vc.VcMeta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.AGENT_V1 + UrlConstant.VC)
public class VcController {

    private final VcService vcService;

    @ArticleLog(name = "[VC] Input VC Meta", apiType = ApiType.AGENT,
            targetType = "VC", actionType = ActionType.CREATE)
    @PostMapping
    @ApiKeyRequired(role = ApiKeyRole.ISSUER)
    public void inputVcMeta(@RequestBody @Valid VcMeta request) {
        log.debug("generate VC");

        vcService.inputVcMeta(request);
    }

    @GetMapping
    @ApiKeyRequired(role = ApiKeyRole.READ)
    public ResponseEntity<String> getVcMeta(@RequestParam("vcId") String vcId) {
        log.debug("Get VC Meta");

        return new ResponseEntity<>(vcService.getVcMetaByVcId(vcId), HttpStatus.OK);
    }

    @ArticleLog(name = "[VC] Update VC Meta", apiType = ApiType.AGENT,
            targetType = "VC", actionType = ActionType.UPDATE_STATUS)
    @PutMapping
    @ApiKeyRequired(role = ApiKeyRole.ISSUER)
    public void updateVcMeta(@RequestBody @Valid UpdateVcStatusReqDto request) {
        log.debug("Update VC Status");
        vcService.updateVc(request);
    }
}
