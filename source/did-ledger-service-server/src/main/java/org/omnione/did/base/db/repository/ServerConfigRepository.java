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

import org.omnione.did.base.db.domain.ServerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerConfigRepository extends JpaRepository<ServerConfig, Long> {
    
    /**
     * Finds a server configuration by its config key.
     *
     * @param configKey the configuration key
     * @return Optional containing the ServerConfig if found
     */
    Optional<ServerConfig> findByConfigKey(String configKey);
    
    /**
     * Checks if a configuration key exists.
     *
     * @param configKey the configuration key to check
     * @return true if the key exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);
}
