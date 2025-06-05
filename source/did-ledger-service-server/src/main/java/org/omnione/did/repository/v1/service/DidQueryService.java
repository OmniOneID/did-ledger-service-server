package org.omnione.did.repository.v1.service;

import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.omnione.did.data.model.enums.vc.RoleType;

import java.util.List;
import java.util.Optional;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : DidQueryService
 * @since : 6/14/24
 */
public interface DidQueryService {
    Did save(Did did);
    DidDocument save(DidDocument didDocument);
    Optional<Did> didFindByDid(String did);
    Optional<DidDocument> didDocFindByDidIdAndVersion(Long did, Short version);
    Optional<DidDocument> didDocRevokedFindByDidIdAndVersion(Long did, Short version);
    void updateDeactivateByDidEntity(Long didId, Boolean deactivated);
    Optional<DidDocument> findFirstByDidIdOrderByIdDesc(Long didId);

    DidDocumentStatusHistory save(DidDocumentStatusHistory didDocumentStatusHistory);

    List<DidDocument> findAllByDidId(Long didId);

    void revokeDidDocument(Long didId);

    Optional<DidDocument> findDidDocFirstByDidAndVersion(Long id, Short version);

    Did findDidFirstByRole(RoleType roleType);
}
