/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.repository.v1.agent.service;

import org.omnione.did.base.datamodel.InvokedDidDoc;
import org.omnione.did.base.datamodel.Proof;
import org.omnione.did.base.datamodel.ProofType;
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.*;
import org.omnione.did.repository.v1.agent.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.agent.dto.did.UpdateDidDocReqDto;
import org.omnione.did.repository.v1.agent.service.query.DidQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Profile("!sample")
@Service
public class DidServiceImpl implements DidService {
    private static final String REASON_REGISTER = "Register DID Document";
    private static final String REASON_UPDATE = "update DID Document";
    private static final String REASON_STATUS_CHANGE = "Update DID Status";

    private final DidQueryService didQueryService;

    @Override
    public void generateDid(InputDidDocReqDto request) {
        log.debug("=== Starting generateDid ===");

        log.debug("\t--> Parsing and verifying DID Document");
        var didDocument = parseDidDoc(request);
        verifyDidDoc(request.getDidDoc());

        log.debug("\t--> Processing DID Document (update or register)");
        didQueryService.didFindByDid(didDocument.getId())
                .ifPresentOrElse(
                        did -> updateDidDoc(did, didDocument),
                        () -> registerDidDoc(didDocument, request.getRoleType())
                );

        log.debug("=== Finished generateDid ===");
    }

    private org.omnione.did.data.model.did.DidDocument parseDidDoc(InputDidDocReqDto request) {
        log.debug("\t--> Parsing DID Document");

        String didDocJson = new String(BaseMultibaseUtil.decode(request.getDidDoc().getDidDoc()), StandardCharsets.UTF_8);
        return BaseCoreDidUtil.parseDidDoc(didDocJson).getDocument();
    }

    private void updateDidDoc(Did previousDid, org.omnione.did.data.model.did.DidDocument newDoc) {
        log.debug("\t--> Update DID Document");

        log.debug("\t--> Check last version of did document");
        Short newVersion = Short.parseShort(newDoc.getVersionId());

        didQueryService.findFirstByDidIdOrderByIdDesc(previousDid.getId())
                .ifPresent(prevDoc -> {
                    if (prevDoc.getVersion() != newVersion - 1) {
                        throw new OpenDidException(ErrorCode.DID_DOC_VERSION_MISMATCH);
                    }
                });
        log.debug("\t--> Update did");
        Did updatedDid = didQueryService.save(Did.builder()
                .id(previousDid.getId())
                .did(previousDid.getDid())
                .role(previousDid.getRole())
                .status(DidDocStatus.ACTIVATED)
                .version(newVersion)
                .build());
        log.debug("\t--> Deactivate previous did document");
        didQueryService.updateDeactivateByDidEntity(updatedDid.getId(), true);
        log.debug("\t--> Insert did document");
        didQueryService.save(buildDidDocument(updatedDid.getId(), newVersion, newDoc));
        // TODO: Check From Status
        saveStatusHistory(previousDid.getId(), DidDocStatus.ACTIVATED, previousDid.getStatus(), newVersion, REASON_UPDATE);
    }

    private void registerDidDoc(org.omnione.did.data.model.did.DidDocument doc, RoleType roleType) {
        log.debug("\t--> Register DID Document");

        Short version = Short.parseShort(doc.getVersionId());
        Did newDid = didQueryService.save(Did.builder()
                .did(doc.getId())
                .role(roleType)
                .status(DidDocStatus.ACTIVATED)
                .version(version)
                .build());

        didQueryService.save(buildDidDocument(newDid.getId(), version, doc));
        saveStatusHistory(newDid.getId(), null, DidDocStatus.ACTIVATED, version, REASON_REGISTER);
    }

    private DidDocument buildDidDocument(Long didId, Short version, org.omnione.did.data.model.did.DidDocument doc) {

        return DidDocument.builder()
                .didId(didId)
                .version(version)
                .document(doc.toJson())
                .controller(doc.getController())
                .deactivated(false)
                .build();
    }

    private void saveStatusHistory(Long didId, DidDocStatus from, DidDocStatus to, Short version, String reason) {
        log.debug("\t--> Save did document Status History");

        didQueryService.save(DidDocumentStatusHistory.builder()
                .didId(didId)
                .version(version)
                .fromStatus(from)
                .toStatus(to)
                .changedAt(Instant.now())
                .reason(reason)
                .build());
    }

    @Override
    public String getDid(String didKeyUrl) {
        log.debug("=== Starting getDid ===");

        log.debug("\t--> Extracting DID from URL");
        String did = DidUtil.extractDid(didKeyUrl);
        if (did == null || did.isEmpty()) {
            log.error("DID is null or empty");
            throw new OpenDidException(ErrorCode.DID_NOT_FOUND);
        }

        log.debug("\t--> Finding DID entity");
        Did didEntity = didQueryService.didFindByDid(did)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_NOT_FOUND));

        log.debug("\t--> Extracting version or using current version");
        Short version = Optional.ofNullable(DidUtil.extractVersion(didKeyUrl))
                .orElse(didEntity.getVersion());

        log.debug("\t--> Finding DID document based on status");
        Optional<DidDocument> didDocument = switch (didEntity.getStatus()) {
            case ACTIVATED, DEACTIVATED -> didQueryService.didDocFindByDidIdAndVersion(didEntity.getId(), version);
            case REVOKED, TERMINATED ->
                    didQueryService.didDocRevokedFindLatestByDidIdAndVersion(didEntity.getId(), version);
        };

        DidDocument doc = didDocument.orElseThrow(() -> new OpenDidException(ErrorCode.DID_DOC_NOT_FOUND));

        log.debug("=== Finished getDid ===");
        return doc.getDocument();
    }


    @Override
    public void updateStatus(UpdateDidDocReqDto request) {
        log.debug("=== Starting updateStatus ===");

        log.debug("\t--> Extracting DID from request");
        String did = DidUtil.extractDid(request.getDid());
        if (did == null || did.isEmpty()) {
            log.error("DID is null or empty");
            throw new OpenDidException(ErrorCode.DID_NOT_FOUND);
        }

        log.debug("\t--> Finding DID entity");
        Did didEntity = didQueryService.didFindByDid(did)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_NOT_FOUND));

        log.debug("\t--> Extracting version or using current version");
        Short version = Optional.ofNullable(DidUtil.extractVersion(request.getDid()))
                .orElse(didEntity.getVersion());

        log.debug("\t--> Validating status transition");
        DidDocStatus currentStatus = didEntity.getStatus();
        DidDocStatus targetStatus = request.getStatus();

        if (DidDocStatus.TERMINATED.equals(currentStatus)) {
            log.error("Terminated DIDs can't change their status - DID: {}", didEntity.getDid());
            throw new OpenDidException(ErrorCode.TERMINATED_STATUS_CAN_NOT_CHANGE);
        }

        if (DidDocStatus.REVOKED.equals(currentStatus) && !DidDocStatus.TERMINATED.equals(targetStatus)) {
            log.error("Revoked DIDs can't change their status to (De)Activate - DID: {}", didEntity.getDid());
            throw new OpenDidException(ErrorCode.REVOKED_STATUS_CAN_NOT_CHANGE);
        }

        log.debug("\t--> Updating DID status based on target status");
        if (DidDocStatus.TERMINATED.equals(targetStatus)) {
            didEntity.setTerminatedTime(Instant.now());
        } else if (DidDocStatus.REVOKED.equals(targetStatus)) {
            didQueryService.revokeDidDocument(didEntity.getId());
        } else {
            DidDocument doc = didQueryService.findDidDocFirstByDidAndVersion(didEntity.getId(), version)
                    .orElseThrow(() -> new OpenDidException(ErrorCode.DID_DOC_NOT_FOUND));
            doc.setDeactivated(DidDocStatus.DEACTIVATED.equals(targetStatus));
        }

        log.debug("\t--> Saving status history and updating DID entity");
        saveStatusHistory(didEntity.getId(), currentStatus, targetStatus, version, REASON_STATUS_CHANGE);
        didEntity.setStatus(targetStatus);

        log.debug("=== Finished updateStatus ===");
    }


    private void verifyDidDoc(InvokedDidDoc invokedDidDoc) {
        log.debug("\t--> Signature verification for TA");
        if (!didQueryService.existDid()) {
            return;
        }
        String controllerDid = DidUtil.extractDid(invokedDidDoc.getController().getDid());

        Did controllerDidEntity = didQueryService.didFindByDid(controllerDid)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_NOT_FOUND));

        Short extractedVersion = DidUtil.extractVersion(controllerDid);
        Short version = (extractedVersion != null) ? extractedVersion : controllerDidEntity.getVersion();

        if (!RoleType.TAS.equals(controllerDidEntity.getRole())) {
            log.error("The DID's Role is not TA - DID: {}, Role: {}", controllerDidEntity.getDid(),
                    controllerDidEntity.getRole().name());
            throw new OpenDidException(ErrorCode.DID_ROLE_MISMATCH_TA);
        }

        DidDocument controllerDidDoc = didQueryService.didDocFindByDidIdAndVersion(controllerDidEntity.getId(), version)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_DOC_NOT_FOUND));

        DidManager controllerDidManager = new DidManager();
        controllerDidManager.parse(controllerDidDoc.getDocument());

        InvokedDidDoc toVerify = removeProof(invokedDidDoc);
        String json = JsonUtil.serializeAndSort(toVerify);
        byte[] hash = BaseDigestUtil.generateHash(json);

        String invocationKeyId = controllerDidManager.getDocument().getCapabilityInvocation().getFirst();
        VerificationMethod method = controllerDidManager.getVerificationMethodByKeyId(invocationKeyId);
        BaseCryptoUtil.verifySignature(
                method.getPublicKeyMultibase(),
                invokedDidDoc.getProof().getProofValue(),
                hash,
                ProofType.fromDisplayName(invokedDidDoc.getProof().getType()).toEccCurveType()
        );
    }

    private InvokedDidDoc removeProof(InvokedDidDoc invokedDidDoc) {
        Proof newProof = new Proof();
        newProof.setType(invokedDidDoc.getProof().getType());
        newProof.setCreated(invokedDidDoc.getProof().getCreated());
        newProof.setVerificationMethod(invokedDidDoc.getProof().getVerificationMethod());
        newProof.setProofPurpose(invokedDidDoc.getProof().getProofPurpose());
        newProof.setProofValue(null);

        return new InvokedDidDoc(invokedDidDoc.getDidDoc(), invokedDidDoc.getController(), invokedDidDoc.getNonce(), newProof);
    }
}
