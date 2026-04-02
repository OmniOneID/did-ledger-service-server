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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new API key request.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CreateApiKeyReqDto {
    
    /**
     * Name of the API key for identification purposes.
     */
    @NotBlank(message = "API key name is required")
    @Size(max = 100, message = "API key name must not exceed 100 characters")
    private final String name;
    
    /**
     * Optional description of the API key's purpose.
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private final String description;
    
    /**
     * Role of the API key.
     */
    @NotNull(message = "API key role is required")
    private final ApiKeyRole role;
    
    /**
     * Number of days from creation until the API key expires.
     * Must be at least 1 day.
     */
    @NotNull(message = "Expiration days is required")
    @Min(value = 1, message = "Expiration days must be at least 1")
    private final Integer expirationDays;
}
