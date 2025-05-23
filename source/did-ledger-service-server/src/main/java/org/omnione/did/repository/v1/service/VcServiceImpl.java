package org.omnione.did.repository.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.VcMetadata;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import lombok.RequiredArgsConstructor;
import org.omnione.did.repository.v1.dto.vc.TssGetVcMetaResDto;
import org.omnione.did.repository.v1.dto.vc.UpdateVcStatusReqDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

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
                .status(VcStatus.ACTIVE)
                .metadata(vcMeta.toJson())
                .build());
        log.debug("\tVC Meta VC_ID: {}", vcMetadata.getVcId());
        log.debug("== Finish Input VC Meta");
    }

    @Override
    public TssGetVcMetaResDto getVcMetaByVcId(String vcId) {
        log.debug("== Start Get VC Meta");

        log.debug("\t Find by VC_ID: {}", vcId);
        VcMetadata vcMetadata = vcMetaQueryService.findByVcId(vcId);

        log.debug("\t Encoded VC Metadata");
        String encodedVcMeta = BaseMultibaseUtil.encode(vcMetadata.getMetadata().getBytes(StandardCharsets.UTF_8));

        log.debug("== Finish Get VC Meta");
        return TssGetVcMetaResDto.builder()
                .vcId(vcId)
                .vcMeta(encodedVcMeta)
                .build();
    }

    @Override
    public void updateVc(UpdateVcStatusReqDto request) {
        log.debug("== Start Update VC Status");

        log.debug("\t Find VC Meta");
        final String vcId = request.getVcId();
        VcMetadata vcMetadata = vcMetaQueryService.findByVcId(vcId);

        log.debug("\t Check VC Status");
        if (VcStatus.REVOKED.equals(vcMetadata.getStatus())) {
            throw new OpenDidException(ErrorCode.REVOKED_VC_CANNOT_UPDATE);
        }

        final VcStatus vcStatus = request.getVcStatus();

        VcMeta vcMeta = new VcMeta();
        vcMeta.fromJson(vcMetadata.getMetadata());
        vcMeta.setStatus(vcStatus.getRawValue());

        vcMetadata.setStatus(vcStatus);

        log.debug("\t Update VC Status");
        vcMetaQueryService.save(vcMetadata);
        log.debug("== Finish Update VC Status");
    }
}
