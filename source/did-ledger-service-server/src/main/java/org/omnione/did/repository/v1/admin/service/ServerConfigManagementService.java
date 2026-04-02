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
package org.omnione.did.repository.v1.admin.service;

import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.serverconfig.ServerConfigDto;
import org.omnione.did.repository.v1.admin.dto.serverconfig.UpdateServerConfigReqDto;
import org.omnione.did.repository.v1.admin.dto.serverconfig.UpdateServerConfigResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServerConfigManagementService {
    
    private final ServerConfigRepository serverConfigRepository;
    
    /**
     * Retrieves all server configurations.
     *
     * @return list of all server configuration DTOs
     */
    public List<ServerConfigDto> getAllServerConfigs() {
        log.debug("=== Starting getAllServerConfigs ===");
        
        log.debug("\t--> Retrieving all server configurations");
        List<ServerConfig> configs = serverConfigRepository.findAll();
        
        List<ServerConfigDto> result = configs.stream()
                .map(ServerConfigDto::fromServerConfig)
                .toList();
        
        log.debug("\t--> Found {} server configurations", result.size());
        log.debug("=== Finished getAllServerConfigs ===");
        return result;
    }
    
    /**
     * Updates multiple server configurations.
     *
     * @param reqDto the request DTO containing configurations to update
     * @return response DTO with update results
     * @throws OpenDidException if any configuration key is not found
     */
    public UpdateServerConfigResDto updateServerConfigs(UpdateServerConfigReqDto reqDto) {
        log.debug("=== Starting updateServerConfigs ===");
        
        log.debug("\t--> Updating {} server configurations", reqDto.getConfigs().size());
        
        List<ServerConfigDto> updatedConfigs = new ArrayList<>();
        
        for (UpdateServerConfigReqDto.ConfigItem configItem : reqDto.getConfigs()) {
            log.debug("\t--> Processing config key: {}", configItem.getConfigKey());
            
            Optional<ServerConfig> configOpt = serverConfigRepository.findByConfigKey(configItem.getConfigKey());
            
            if (configOpt.isEmpty()) {
                log.error("Server config not found for key: {}", configItem.getConfigKey());
                throw new OpenDidException(ErrorCode.SERVER_CONFIG_KEY_NOT_FOUND);
            }
            
            ServerConfig config = configOpt.get();
            
            // Update configuration values
            config.setConfigValue(configItem.getConfigValue());
            if (configItem.getDescription() != null) {
                config.setDescription(configItem.getDescription());
            }
            
            ServerConfig savedConfig = serverConfigRepository.save(config);
            updatedConfigs.add(ServerConfigDto.fromServerConfig(savedConfig));
            
            log.info("Server config updated: key={}, value={}", 
                    savedConfig.getConfigKey(), savedConfig.getConfigValue());
        }
        
        UpdateServerConfigResDto result = UpdateServerConfigResDto.builder()
                .updatedCount(updatedConfigs.size())
                .updatedConfigs(updatedConfigs)
                .message("Server configurations updated successfully")
                .build();
        
        log.debug("=== Finished updateServerConfigs ===");
        return result;
    }
    
    /**
     * Retrieves a server configuration by its key.
     *
     * @param configKey the configuration key
     * @return server configuration DTO
     * @throws OpenDidException if configuration key is not found
     */
    public ServerConfigDto getServerConfigByKey(String configKey) {
        log.debug("=== Starting getServerConfigByKey ===");
        
        log.debug("\t--> Finding server config by key: {}", configKey);
        Optional<ServerConfig> configOpt = serverConfigRepository.findByConfigKey(configKey);
        
        if (configOpt.isEmpty()) {
            log.error("Server config not found for key: {}", configKey);
            throw new OpenDidException(ErrorCode.SERVER_CONFIG_KEY_NOT_FOUND);
        }
        
        ServerConfigDto result = ServerConfigDto.fromServerConfig(configOpt.get());
        
        log.debug("=== Finished getServerConfigByKey ===");
        return result;
    }
}
