package org.omnione.did.repository.v1.service;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.DidDocStatus;
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.repository.DidDocumentRepository;
import org.omnione.did.base.db.repository.DidRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : DidQueryServiceImpl
 * @since : 6/14/24
 */
@RequiredArgsConstructor
@Service
public class DidQueryServiceImpl implements DidQueryService {
    private final DidRepository didRepository;
    private final DidDocumentRepository didDocumentRepository;

    @Override
    public Did save(Did did) {
        return didRepository.save(did);
    }

    @Override
    public DidDocument save(DidDocument didDocument) {
        return didDocumentRepository.save(didDocument);
    }

    @Override
    public Optional<Did> didFindByDid(String did) {
        return didRepository.findByDid(did);
    }

    @Override
    public Optional<DidDocument> didDocFindByDid(String did, Short version) {
        return didDocumentRepository.findByDidAndVersion(did, version);
    }

    @Override
    public void updateStatusByDidEntity(Long didId, DidDocStatus status) {
        didDocumentRepository.updateStatusByDidEntity(didId, status);
    }

    @Override
    public Optional<DidDocument> findFirstByOrderByIdDesc() {
        return didDocumentRepository.findFirstByOrderByIdDesc();
    }
}
