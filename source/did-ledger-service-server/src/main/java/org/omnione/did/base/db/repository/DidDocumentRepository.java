package org.omnione.did.base.db.repository;

import org.omnione.did.base.constants.DidDocStatus;
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.domain.DidDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DidDocumentRepository extends JpaRepository<DidDocument, Long> {
    Optional<DidDocument> findByDidAndVersion(String did, Short version);
    @Modifying
    @Query("UPDATE DidDocument d SET d.status = :status WHERE d.didId = :didId")
    int updateStatusByDidEntity(Long didId, DidDocStatus status);
    Optional<DidDocument> findFirstByOrderByIdDesc();
}
