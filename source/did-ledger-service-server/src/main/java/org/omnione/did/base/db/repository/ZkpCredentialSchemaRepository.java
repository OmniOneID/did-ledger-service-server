package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.VcStatusHistory;
import org.omnione.did.base.db.domain.ZkpCredentialSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZkpCredentialSchemaRepository extends JpaRepository<ZkpCredentialSchema, Long> {
}
