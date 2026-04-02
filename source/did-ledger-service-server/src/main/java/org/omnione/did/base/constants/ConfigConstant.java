package org.omnione.did.base.constants;

/**
 * Configuration constants for server_config table
 */
public class ConfigConstant {
    
    /**
     * Configuration key for enabling/disabling API key validation
     */
    public static final String API_KEY_VALIDATION_ENABLED = "API_KEY_VALIDATION_ENABLED";
    
    // Private constructor to prevent instantiation
    private ConfigConstant() {
        throw new IllegalStateException("Utility class");
    }
}
