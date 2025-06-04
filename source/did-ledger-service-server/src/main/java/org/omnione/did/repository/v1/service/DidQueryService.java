package org.omnione.did.repository.v1.service;

import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;

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
    Optional<DidDocument> didDocFindByDid(Long did, Short version);
    void updateDeactivateByDidEntity(Long didId, Boolean deactivated);
    Optional<DidDocument> findFirstByOrderByIdDesc();

    DidDocumentStatusHistory save(DidDocumentStatusHistory didDocumentStatusHistory);
}
