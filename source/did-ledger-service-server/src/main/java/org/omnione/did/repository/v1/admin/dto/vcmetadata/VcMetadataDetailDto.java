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
package org.omnione.did.repository.v1.admin.dto.vcmetadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for comprehensive VC Metadata details displayed in the Admin Console.
 * <p>
 * Contains the basic VC metadata information, formatted metadata for display,
 * and status change history.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VcMetadataDetailDto {
    private final VcMetadataDto basicInfo;
    private final String parsedMetadata; // JSON formatted metadata for better display
    private final List<VcStatusHistoryDto> statusHistory; // VC status change history
}
