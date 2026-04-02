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

import org.omnione.did.base.db.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long>, QuerydslPredicateExecutor<ApiKey>, ApiKeyRepositoryApiKey {
    Optional<ApiKey> findByApiKey(String apiKey);
    Optional<ApiKey> findByApiKeyAndIsActiveTrue(String apiKey);
    Optional<ApiKey> findByMaskedApiKey(String maskedApiKey);
    long countByName(String name);
    
    /**
     * Finds all active API keys for validation purposes
     * 
     * @return list of active API keys
     */
    List<ApiKey> findByIsActiveTrue();
    
    /**
     * Updates the last_used_at timestamp using database's current timestamp
     * Note: This method expects the hashed API key as stored in the database
     * 
     * @param hashedApiKey the hashed API key string to update
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE ApiKey a SET a.lastUsedAt = CURRENT_TIMESTAMP WHERE a.apiKey = :hashedApiKey AND a.isActive = true")
    int updateLastUsedAtByApiKey(@Param("hashedApiKey") String hashedApiKey);
}
