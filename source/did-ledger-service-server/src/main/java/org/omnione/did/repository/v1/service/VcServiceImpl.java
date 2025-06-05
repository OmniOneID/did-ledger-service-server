package org.omnione.did.repository.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.VcMetadata;
import org.omnione.did.base.db.domain.VcStatusHistory;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import lombok.RequiredArgsConstructor;
import org.omnione.did.repository.v1.dto.vc.UpdateVcStatusReqDto;
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
        log.debug("== start Input VC Meta");

        log.debug("\t VC Meta save to DB");
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
        log.debug("== Finish Input VC Meta");
    }

    @Override
    public String getVcMetaByVcId(String vcId) {
        log.debug("== Start Get VC Meta");

        log.debug("\t Find by VC_ID: {}", vcId);
        VcMetadata vcMetadata = vcMetaQueryService.findByVcId(vcId);

        log.debug("\t Encoded VC Metadata");

        log.debug("== Finish Get VC Meta");
        return vcMetadata.getMetadata();
    }

    @Override
    public void updateVc(UpdateVcStatusReqDto request) {
        log.debug("== Start Update VC Status");

        log.debug("\t Find VC Meta by VC ID: {}", request.getVcId());
        final String vcId = request.getVcId();
        VcMetadata vcMetadata = vcMetaQueryService.findByVcId(vcId);

        log.debug("\t Check VC Status: {}", vcMetadata.getStatus());
        if (VcStatus.REVOKED.name().equals(vcMetadata.getStatus())) {
            throw new OpenDidException(ErrorCode.REVOKED_VC_CANNOT_UPDATE);
        }

        final VcStatus vcStatus = request.getVcStatus();

        VcMeta vcMeta = new VcMeta();
        vcMeta.fromJson(vcMetadata.getMetadata());
        vcMeta.setStatus(vcStatus.getRawValue());

        vcMetadata.setStatus(vcStatus.name());
        vcMetadata.setMetadata(vcMeta.toJson());

        log.debug("\t Update VC Status");
        vcMetaQueryService.save(vcMetadata);

        log.debug("\t Save VC Status History");
        VcStatusHistory vcStatusHistory = VcStatusHistory.builder()
                .vcId(vcMeta.getId())
                .fromStatus(VcStatus.fromString(vcMetadata.getStatus()))
                .toStatus(vcStatus)
                .changedAt(Instant.now())
                .build();

        vcMetaQueryService.save(vcStatusHistory);

        log.debug("== Finish Update VC Status");
    }
}
