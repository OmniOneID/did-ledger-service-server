package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.DidDocumentRevoked;
import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DidDocumentRevokedRepository extends JpaRepository<DidDocumentRevoked, Long> {
}
