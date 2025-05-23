package org.omnione.did.repository.v1.service;

import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.repository.v1.dto.vc.InputVcMetaReqDto;
import org.omnione.did.repository.v1.dto.vc.TssGetVcMetaResDto;
import org.omnione.did.repository.v1.dto.vc.UpdateVcStatusReqDto;

public interface VcService {
    void inputVcMeta(VcMeta request);
    TssGetVcMetaResDto getVcMetaByVcId(String vcId);
    void updateVc(UpdateVcStatusReqDto request);
}
