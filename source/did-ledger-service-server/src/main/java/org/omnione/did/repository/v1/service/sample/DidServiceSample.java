package org.omnione.did.repository.v1.service.sample;

import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.repository.v1.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.dto.did.TssGetDidDocResDto;
import org.omnione.did.repository.v1.service.DidService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Profile("sample")
@Service
public class DidServiceSample implements DidService {
    private final TestFileLoaderService fileLoaderService;
    @Override
    public void generateDid(InputDidDocReqDto request) {

    }

    @Override
    public TssGetDidDocResDto getDid(String did) {

        try {
            did = did.replace("did:omn:", "");
            String didDoc = fileLoaderService.getFileContent("diddoc-" + did + ".json");

            String encodedDidDOc = MultiBaseUtils.encode(didDoc.getBytes(StandardCharsets.UTF_8), MultiBaseType.base64url);

            return TssGetDidDocResDto.builder()
                    .didDoc(encodedDidDOc)
                    .build();
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
    }
}
