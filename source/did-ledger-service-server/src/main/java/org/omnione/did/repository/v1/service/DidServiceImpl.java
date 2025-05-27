package org.omnione.did.repository.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.DidDocStatus;
import org.omnione.did.base.datamodel.InvokedDidDoc;
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.DidUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.repository.v1.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.dto.did.TssGetDidDocResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Profile("!sample")
@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class DidServiceImpl implements DidService {

    private final DidQueryService didQueryService;

    @Override
    public void generateDid(InputDidDocReqDto request) {
        // TODO 소스 정리
        try {
            log.debug("=== Starting generateDid ===");
            // 1. DidDoc 파싱
            log.debug("\t--> Parsing DID Document");
            org.omnione.did.data.model.did.DidDocument didDocument = parseDidDoc(request);

            // 2. DID Doc TAS 서명 검증(권한 체크)
            log.debug("\t--> Verify DID Document");
            verifyDidDoc(request.getDidDoc());

            // 3. Did 정보 조회
            log.debug("\t--> Find DID");
            Optional<Did> didResult = didQueryService.didFindByDid(didDocument.getId());
            if (didResult.isPresent()) {
                Did did = didResult.get();
                // 3.2. DID Doc 업데이트 (등록된 DID가 있을 경우)
                log.debug("\t--> Update DID Document");
                updateDidDoc(did, didDocument);
            } else {
                // 3.1. DID Doc 등록 (등록된 DID가 없을 경우)
                log.debug("\t--> Register DID Document");
                registerDidDoc(didDocument, request.getRoleType());
            }

            log.debug("*** Finished generateDid ***");
        } catch (Exception e) {
            log.error("Failed to generate DID", e);
            throw new RuntimeException(e);
        }
    }
    private org.omnione.did.data.model.did.DidDocument parseDidDoc(InputDidDocReqDto request) {
        byte[] decodedDidDocBytes = BaseMultibaseUtil.decode(request.getDidDoc().getDidDoc());
        String didDocJson = new String(decodedDidDocBytes, StandardCharsets.UTF_8);

        DidManager didManager = BaseCoreDidUtil.parseDidDoc(didDocJson);
        return didManager.getDidDocument();
    }

    private void updateDidDoc(Did previousDid, org.omnione.did.data.model.did.DidDocument didDoc) {

        log.debug("\t--> Check last version of did document");
        Optional<DidDocument> firstByOrderByIdDesc = didQueryService.findFirstByOrderByIdDesc();
        if (firstByOrderByIdDesc.isPresent()) {
            DidDocument didDocument = firstByOrderByIdDesc.get();
            if (didDocument.getVersion() != Short.parseShort(didDoc.getVersionId()) - 1) {
                throw new OpenDidException(ErrorCode.DID_DOC_VERSION_MISMATCH);
            }
        }

        log.debug("\t--> Update did");
        Did savedDid = didQueryService.save(Did.builder()
                .id(previousDid.getId())
                .did(previousDid.getDid())
                .role(previousDid.getRole())
                .status(DidDocStatus.ACTIVATE)
                .version(Short.parseShort(didDoc.getVersionId()))
                .build());

        log.debug("\t--> Deactivate previous did document");
        didQueryService.updateStatusByDidEntity(savedDid.getId(), DidDocStatus.DEACTIVATE);

        log.debug("\t--> Insert did document");
        didQueryService.save(DidDocument.builder()
                .didId(savedDid.getId())
                .didId(savedDid.getId())
                .version(Short.parseShort(didDoc.getVersionId()))
                .did(savedDid.getDid())
                .document(didDoc.toJson())
                .status(DidDocStatus.ACTIVATE)
                .build());
    }

    private void registerDidDoc(org.omnione.did.data.model.did.DidDocument didDocument, String roleType) {
        Did didEntity = didQueryService.save(Did.builder()
                .did(didDocument.getId())
                .role(roleType)
                .status(DidDocStatus.ACTIVATE)
                .version(Short.parseShort(didDocument.getVersionId()))
                .build());
        didQueryService.save(DidDocument.builder()
                .didId(didEntity.getId())
                .didId(didEntity.getId())
                .version(Short.parseShort(didDocument.getVersionId()))
                .did(didEntity.getDid())
                .document(didDocument.toJson())
                .status(DidDocStatus.ACTIVATE)
                .build());
    }

    @Override
    public TssGetDidDocResDto getDid(String didKeyUrl) {

        String did = DidUtil.extractDid(didKeyUrl);
        Short version = DidUtil.extractVersion(didKeyUrl);

        if (version == null) {
            Did didDoc = didQueryService.didFindByDid(did)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.TODO));
            version = didDoc.getVersion();
        }

        DidDocument didDocument = didQueryService.didDocFindByDid(did, version)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TODO));


        try {
            String encodedDidDoc = MultiBaseUtils.encode(didDocument.getDocument().getBytes(StandardCharsets.UTF_8),
                    MultiBaseType.base64url);

            return TssGetDidDocResDto.builder()
                    .didDoc(encodedDidDoc)
                    .build();
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }

    }

    private void verifyDidDoc(InvokedDidDoc didDoc) {
        // todo: DID Doc 서명 검증
        // todo:  TAS Role 체크
    }
}
