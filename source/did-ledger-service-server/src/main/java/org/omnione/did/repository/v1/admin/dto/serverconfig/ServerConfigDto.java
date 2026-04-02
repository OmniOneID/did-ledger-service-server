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
package org.omnione.did.repository.v1.admin.dto.serverconfig;

import org.omnione.did.base.db.domain.ServerConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for server configuration data.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ServerConfigDto {
    
    /**
     * The configuration ID.
     */
    private final Long id;
    
    /**
     * The configuration key.
     */
    private final String configKey;
    
    /**
     * The configuration value.
     */
    private final String configValue;
    
    /**
     * The configuration description.
     */
    private final String description;
    
    /**
     * The creation timestamp.
     */
    private final Instant createdAt;
    
    /**
     * The last update timestamp.
     */
    private final Instant updatedAt;
    
    /**
     * Converts a ServerConfig entity to ServerConfigDto.
     *
     * @param serverConfig the ServerConfig entity
     * @return ServerConfigDto
     */
    public static ServerConfigDto fromServerConfig(ServerConfig serverConfig) {
        return ServerConfigDto.builder()
                .id(serverConfig.getId())
                .configKey(serverConfig.getConfigKey())
                .configValue(serverConfig.getConfigValue())
                .description(serverConfig.getDescription())
                .createdAt(serverConfig.getCreatedAt())
                .updatedAt(serverConfig.getUpdatedAt())
                .build();
    }
}
