package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.VcMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VcMetadataRepository extends JpaRepository<VcMetadata, Long> {
    Optional<VcMetadata> findByVcId(String vcId);
}
