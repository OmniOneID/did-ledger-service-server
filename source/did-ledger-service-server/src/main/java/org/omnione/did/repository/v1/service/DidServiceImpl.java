package org.omnione.did.repository.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.DidDocStatus;
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.*;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.crypto.enums.EccCurveType;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.repository.v1.dto.did.InputDidDocReqDto;
import lombok.RequiredArgsConstructor;
import org.omnione.did.repository.v1.dto.did.UpdateDidDocReqDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
        return didManager.getDocument();
    }

    private void updateDidDoc(Did previousDid, org.omnione.did.data.model.did.DidDocument didDoc) {
        log.debug("\t--> Check last version of did document");
        Optional<DidDocument> firstByOrderByIdDesc = didQueryService.findFirstByOrderByIdDesc();

        firstByOrderByIdDesc.ifPresent(didDocument -> {
            if (didDocument.getVersion() != Short.parseShort(didDoc.getVersionId()) - 1) {
                throw new OpenDidException(ErrorCode.DID_DOC_VERSION_MISMATCH);
            }
        });

        log.debug("\t--> Update did");
        Did savedDid = didQueryService.save(Did.builder()
                .id(previousDid.getId())
                .did(previousDid.getDid())
                .role(previousDid.getRole())
                .status(DidDocStatus.ACTIVATE)
                .version(Short.parseShort(didDoc.getVersionId()))
                .build());

        log.debug("\t--> Deactivate previous did document");
        didQueryService.updateDeactivateByDidEntity(savedDid.getId(), true);

        log.debug("\t--> Insert did document");
        didQueryService.save(DidDocument.builder()
                .didId(savedDid.getId())
                .version(Short.parseShort(didDoc.getVersionId()))
                .document(didDoc.toJson())
                .controller("did:omn:tas") // TODO: TAS
                .deactivated(false)
                .build());

        log.debug("\t--> Save did document Status History");
        DidDocument didDocument = firstByOrderByIdDesc.get();
        DidDocumentStatusHistory updateDidDocument = DidDocumentStatusHistory.builder()
                .version(didDocument.getVersion())
                .fromStatus(previousDid.getStatus())
                .toStatus(previousDid.getStatus())
                .changedAt(Instant.now())
                .reason("update DID Document")
                .didId(previousDid.getId())
                .build();

        didQueryService.save(updateDidDocument);
    }

    private void registerDidDoc(org.omnione.did.data.model.did.DidDocument didDocument, RoleType roleType) {
        Did didEntity = didQueryService.save(Did.builder()
                .did(didDocument.getId())
                .role(roleType)
                .status(DidDocStatus.ACTIVATE)
                .version(Short.parseShort(didDocument.getVersionId()))
                .build());
        didQueryService.save(DidDocument.builder()
                .didId(didEntity.getId())
                .version(Short.parseShort(didDocument.getVersionId()))
                .document(didDocument.toJson())
                .controller("did:omn:tas") // TODO: TAS DID
                .deactivated(false)
                .build());

        didQueryService.save(DidDocumentStatusHistory.builder()
                .version(Short.parseShort(didDocument.getVersionId()))
                .toStatus(DidDocStatus.ACTIVATE)
                .changedAt(Instant.now())
                .reason("Register DID Document")
                .didId(didEntity.getId())
                .build());
    }

    @Override
    public String getDid(String didKeyUrl) {

        String did = DidUtil.extractDid(didKeyUrl);
        Short version = DidUtil.extractVersion(didKeyUrl);

        Did didDoc = didQueryService.didFindByDid(did)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TODO));

        if (version == null) {
            version = didDoc.getVersion();
        }

        DidDocument didDocument = didQueryService.didDocFindByDid(didDoc.getId(), version)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TODO));


        return didDocument.getDocument();
    }

    @Override
    public void updateStatus(UpdateDidDocReqDto request) {
        // TODO
        String didKeyUrl = request.getDid();

        String did = DidUtil.extractDid(didKeyUrl);
        Short version = DidUtil.extractVersion(didKeyUrl);

        Did didEntity = didQueryService.didFindByDid(did).orElseThrow(() -> new OpenDidException(ErrorCode.TODO));
        DidDocStatus didEntityStatus = didEntity.getStatus();

        if (DidDocStatus.REVOKED.equals(didEntityStatus)) {
            throw new OpenDidException(ErrorCode.TODO);
        }

        DidDocStatus status = DidDocStatus.valueOf(request.getStatus());
        didEntity.setStatus(status);


        if (DidDocStatus.REVOKED.equals(DidDocStatus.valueOf(request.getStatus()))) {
            // TODO: Revoked 테이블 이관

        }

    }

    private void verifyDidDoc(InvokedDidDoc invokedDidDoc) {
        String controller = DidUtil.extractDid(invokedDidDoc.getController().getDid());

        Did did = didQueryService.didFindByDid(controller).orElseThrow(() -> new OpenDidException(ErrorCode.TODO));
        if (!RoleType.TAS.equals(did.getRole())) {
            throw new OpenDidException(ErrorCode.TODO);
        }
        DidDocument didDocument = didQueryService.didDocFindByDid(did.getId(), did.getVersion()).orElseThrow(() -> new OpenDidException(ErrorCode.TODO));
        DidManager didManager = new DidManager();
        didManager.parse(didDocument.getDocument());

        InvokedDidDoc signatureMessageObject = removeProof(invokedDidDoc);
        String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

        byte[] bytes = BaseDigestUtil.generateHash(jsonString);
        VerificationMethod verificationMethod = didManager.getVerificationMethodByKeyId("invoke"); // TODO: Invoke key ID
        String encodedPublicKey = verificationMethod.getPublicKeyMultibase();

        BaseCryptoUtil.verifySignature(encodedPublicKey, invokedDidDoc.getProof().getProofValue(), bytes,
                EccCurveType.fromString(invokedDidDoc.getProof().getType()));
    }

    /**
     * Removes the proof from a DID document.
     *
     * @param invokedDidDoc The DID document to remove the proof from
     * @return DidDocument The DID document without proof
     */
    private InvokedDidDoc removeProof(InvokedDidDoc invokedDidDoc) {
        Proof tmpProof = new Proof();
        tmpProof.setType(invokedDidDoc.getProof().getType());
        tmpProof.setCreated(invokedDidDoc.getProof().getCreated());
        tmpProof.setVerificationMethod(invokedDidDoc.getProof().getVerificationMethod());
        tmpProof.setProofPurpose(invokedDidDoc.getProof().getProofPurpose());
        tmpProof.setProofValue(null);

        return new InvokedDidDoc(invokedDidDoc.getDidDoc(), tmpProof,
                invokedDidDoc.getController(), invokedDidDoc.getNonce());
    }

}
