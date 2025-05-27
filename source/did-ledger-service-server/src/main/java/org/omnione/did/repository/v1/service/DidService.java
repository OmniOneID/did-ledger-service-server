package org.omnione.did.repository.v1.service;

import org.omnione.did.repository.v1.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.dto.did.TssGetDidDocResDto;
import org.omnione.did.repository.v1.dto.vc.InputVcMetaResDto;

public interface DidService {
    void generateDid(InputDidDocReqDto request);

    TssGetDidDocResDto getDid(String didDoc);
}
