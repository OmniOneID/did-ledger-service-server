package org.omnione.did.repository.v1.service;

import org.omnione.did.repository.v1.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.dto.did.UpdateDidDocReqDto;

public interface DidService {
    void generateDid(InputDidDocReqDto request);

    String getDid(String didDoc);

    void updateStatus(UpdateDidDocReqDto request);
}
