package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.DidDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DidDocumentRepository extends JpaRepository<DidDocument, Long> {
    Optional<DidDocument> findByDidIdAndVersion(Long didId, Short version);
    @Modifying
    @Query("UPDATE DidDocument d SET d.deactivated = :deactivated WHERE d.didId = :didId")
    void updateStatusByDidEntity(Long didId, Boolean deactivated);
    Optional<DidDocument> findFirstByDidIdOrderByIdDesc(Long didId);

    List<DidDocument> findAllByDidId(Long didId);

    void deleteAllByDidId(Long didId);
}
