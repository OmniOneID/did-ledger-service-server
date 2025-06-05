package org.omnione.did.repository.v1.service;

import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.repository.v1.dto.vc.UpdateVcStatusReqDto;

public interface VcService {
    void inputVcMeta(VcMeta request);
    String getVcMetaByVcId(String vcId);
    void updateVc(UpdateVcStatusReqDto request);
}
