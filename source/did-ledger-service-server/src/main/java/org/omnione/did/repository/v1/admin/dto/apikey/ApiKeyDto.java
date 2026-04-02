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
package org.omnione.did.repository.v1.admin.dto.apikey;

import org.omnione.did.base.db.constant.ApiKeyRole;
import org.omnione.did.base.db.domain.ApiKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApiKeyDto {
    private final Long id;
    private final String apiKey;
    private final String maskedApiKey;
    private final String name;
    private final String description;
    private final ApiKeyRole role;
    private final Boolean isActive;
    private final String lastUsedAt;
    private final String expiresAt;
    private final String createdAt;
    private final String updatedAt;

    public static ApiKeyDto fromApiKey(ApiKey apiKey) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        return ApiKeyDto.builder()
                .id(apiKey.getId())
                .apiKey(apiKey.getApiKey())
                .maskedApiKey(apiKey.getMaskedApiKey())
                .name(apiKey.getName())
                .description(apiKey.getDescription())
                .role(apiKey.getRole())
                .isActive(apiKey.getIsActive())
                .lastUsedAt(formatInstant(apiKey.getLastUsedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .expiresAt(formatExpiresAt(apiKey.getExpiresAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createdAt(formatInstant(apiKey.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(formatInstant(apiKey.getUpdatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }

    private static String formatExpiresAt(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        
        // Handle year 9999 which exceeds LocalDateTime range
        if (instant.equals(Instant.parse("9999-12-31T23:59:59.999Z"))) {
            return "9999-12-31 23:59:59";
        }
        
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
