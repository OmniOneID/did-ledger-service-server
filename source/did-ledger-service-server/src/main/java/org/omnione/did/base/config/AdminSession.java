package org.omnione.did.base.config;

import org.omnione.did.base.db.constant.AdminRole;

public record AdminSession(Long id, String loginId, AdminRole role) implements java.io.Serializable {}

