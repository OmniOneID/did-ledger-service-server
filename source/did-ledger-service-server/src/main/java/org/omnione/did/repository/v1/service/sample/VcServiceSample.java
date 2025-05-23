package org.omnione.did.repository.v1.service.sample;

import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.repository.v1.dto.vc.InputVcMetaReqDto;
import org.omnione.did.repository.v1.dto.vc.TssGetVcMetaResDto;
import org.omnione.did.repository.v1.dto.vc.UpdateVcStatusReqDto;
import org.omnione.did.repository.v1.service.VcService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Profile("sample")
@Service
public class VcServiceSample implements VcService {
    private final TestFileLoaderService fileLoaderService;

    @Override
    public void inputVcMeta(VcMeta request) {

    }


    @Override
    public TssGetVcMetaResDto getVcMetaByVcId(String vcId) {
        try {
            String vcMeta = fileLoaderService.getFileContent("vcmeta-mdl.json");

            String encodedVcMeta = MultiBaseUtils.encode(vcMeta.getBytes(StandardCharsets.UTF_8), MultiBaseType.base64url);
            return TssGetVcMetaResDto.builder()
                    .vcId(vcId)
                    .vcMeta(encodedVcMeta)
                    .build();
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void updateVc(UpdateVcStatusReqDto request) {

    }
}
