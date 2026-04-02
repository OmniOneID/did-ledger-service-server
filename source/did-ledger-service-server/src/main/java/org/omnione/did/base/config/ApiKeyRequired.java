package org.omnione.did.base.config;

import org.omnione.did.base.db.constant.ApiKeyRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that require API key validation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiKeyRequired {
    /**
     * Minimum required role level for access
     * TAS: Only TAS role allowed
     * ISSUER: TAS and ISSUER roles allowed
     * READ: TAS, ISSUER, and READ roles allowed
     */
    ApiKeyRole role();
}
