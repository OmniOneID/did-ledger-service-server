package org.omnione.did.base.service;

import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for managing server_config table operations
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ServerConfigService {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Checks if a specific configuration is enabled in server_config table
     * 
     * @param configKey configuration key to check
     * @return true if enabled, false if disabled or not found
     */
    public boolean isConfigEnabled(String configKey) {
        try {
            String sql = "SELECT config_value FROM server_config WHERE config_key = ?";
            String configValue = jdbcTemplate.queryForObject(sql, String.class, configKey);
            
            if (configValue == null) {
                log.warn("Config key '{}' not found in server_config table", configKey);
                return false;
            }
            
            // Consider "true", "1", "Y", "yes" as enabled
            return "true".equalsIgnoreCase(configValue.trim()) 
                   || "1".equals(configValue.trim())
                   || "Y".equalsIgnoreCase(configValue.trim())
                   || "yes".equalsIgnoreCase(configValue.trim());
                   
        } catch (OpenDidException e) {
            log.error("Error checking config '{}' from server_config table", configKey, e);
            return false; // Return false on error by default
        }
    }
}
