package org.omnione.did.base.property;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for the JWT.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperty {
    String secret;
    long accessTtlSeconds;
    long refreshTtlSeconds;
}
