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
package org.omnione.did.repository.v1.agent.service;

import org.omnione.did.base.db.domain.VcMetadata;
import org.omnione.did.base.db.domain.VcStatusHistory;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.agent.dto.vc.UpdateVcStatusReqDto;
import org.omnione.did.repository.v1.agent.service.query.VcMetaQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Profile("!sample")
@Slf4j
@RequiredArgsConstructor
@Service
public class VcServiceImpl implements VcService {

    private final VcMetaQueryService vcMetaQueryService;

    @Override
    public void inputVcMeta(VcMeta vcMeta) {
        log.debug("=== Starting inputVcMeta ===");

        log.debug("\t--> Saving VC Meta to DB");
        VcMetadata vcMetadata = vcMetaQueryService.save(VcMetadata.builder()
                .vcId(vcMeta.getId())
                .issuerDid(vcMeta.getIssuer().getDid())
                .subjectDid(vcMeta.getSubject())
                .vcSchema(vcMeta.getCredentialSchema().getId())
                .issuanceDate(vcMeta.getIssuanceDate())
                .validFrom(vcMeta.getValidFrom())
                .validUntil(vcMeta.getValidUntil())
                .formatVersion(vcMeta.getFormatVersion())
                .language(vcMeta.getLanguage())
                .status(vcMeta.getStatus())
                .metadata(vcMeta.toJson())
                .build());
        log.debug("\tVC Meta VC_ID: {}", vcMetadata.getVcId());

        log.debug("\t--> Saving VC Status History for new VC registration");
        VcStatusHistory vcStatusHistory = VcStatusHistory.builder()
                .vcId(vcMeta.getId())
                .fromStatus(null)  // from_status is null for initial registration
                .toStatus(VcStatus.ACTIVE)  // to_status is ACTIVE for new registration
                .changedAt(Instant.now())
                .build();
        
        vcMetaQueryService.save(vcStatusHistory);
        log.debug("\tVC Status History saved for VC_ID: {}", vcMeta.getId());

        log.debug("=== Finished inputVcMeta ===");
    }

    @Override
    public String getVcMetaByVcId(String vcId) {
        log.debug("=== Starting getVcMetaByVcId ===");

        log.debug("\t--> Finding VC Meta by VC_ID: {}", vcId);
        VcMetadata vcMetadata = vcMetaQueryService.findByVcId(vcId);

        log.debug("\t--> Returning encoded VC Metadata");
        String result = vcMetadata.getMetadata();

        log.debug("=== Finished getVcMetaByVcId ===");
        return result;
    }

    @Override
    public void updateVc(UpdateVcStatusReqDto request) {
        log.debug("=== Starting updateVc ===");

        log.debug("\t--> Finding VC Meta by VC ID: {}", request.getVcId());
        final String vcId = request.getVcId();
        VcMetadata vcMetadata = vcMetaQueryService.findByVcId(vcId);

        log.debug("\t--> Checking VC Status: {}", vcMetadata.getStatus());
        if (VcStatus.REVOKED.name().equals(vcMetadata.getStatus())) {
            throw new OpenDidException(ErrorCode.REVOKED_VC_CANNOT_UPDATE);
        }

        // Store current status for fromStatus before updating
        final String currentStatus = vcMetadata.getStatus();
        final VcStatus vcStatus = request.getVcStatus();

        log.debug("\t--> Updating VC metadata with new status from {} to {}", currentStatus, vcStatus.name());
        VcMeta vcMeta = new VcMeta();
        vcMeta.fromJson(vcMetadata.getMetadata());
        vcMeta.setStatus(vcStatus.getRawValue());

        vcMetadata.setStatus(vcStatus.name());
        vcMetadata.setMetadata(vcMeta.toJson());

        log.debug("\t--> Saving updated VC Status");
        vcMetaQueryService.save(vcMetadata);

        log.debug("\t--> Saving VC Status History");
        VcStatusHistory vcStatusHistory = VcStatusHistory.builder()
                .vcId(vcMeta.getId())
                .fromStatus(VcStatus.fromString(currentStatus))  // Use the original status before change
                .toStatus(vcStatus)
                .changedAt(Instant.now())
                .build();

        vcMetaQueryService.save(vcStatusHistory);

        log.debug("=== Finished updateVc ===");
    }
}
