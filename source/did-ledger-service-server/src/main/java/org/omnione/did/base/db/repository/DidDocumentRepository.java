/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
