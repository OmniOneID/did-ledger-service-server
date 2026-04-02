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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for API key renewal requests.
 * <p>
 * Contains validation for extension days to ensure reasonable renewal periods.
 */
@Getter
@Setter
public class RenewApiKeyReqDto {

    /**
     * Number of days to extend the API key expiration.
     * If not provided, defaults to 30 days.
     * Valid range: 1-36525 days (100 years) or 3652425 days (No Limit - 9999 years).
     */
    @Min(value = 1, message = "Extension days must be at least 1")
    @Max(value = 3652425, message = "Extension days must not exceed 3652425")
    private Integer extensionDays = 30;
}