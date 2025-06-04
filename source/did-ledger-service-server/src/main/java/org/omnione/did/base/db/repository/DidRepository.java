package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.Did;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DidRepository extends JpaRepository<Did, Long> {
    Optional<Did> findByDid(String did);

    Optional<Did> findByRole(RoleType roleType);
}
