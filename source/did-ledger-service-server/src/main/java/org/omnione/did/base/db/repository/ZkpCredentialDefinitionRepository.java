package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.domain.ZkpCredentialSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZkpCredentialDefinitionRepository extends JpaRepository<ZkpCredentialDefinition, Long> {
    Optional<ZkpCredentialDefinition> findByDefinitionId(String definitionId);
}
