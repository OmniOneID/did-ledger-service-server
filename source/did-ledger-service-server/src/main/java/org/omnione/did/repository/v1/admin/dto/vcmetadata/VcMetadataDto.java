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

import org.omnione.did.base.db.domain.VcMetadata;
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
public class VcMetadataDto {
    private final Long id;
    private final String vcId;
    private final String issuerDid;
    private final String subjectDid;
    private final String vcSchema;
    private final String issuanceDate;
    private final String validFrom;
    private final String validUntil;
    private final String formatVersion;
    private final String language;
    private final String status;
    private final String metadata;
    private final String createdAt;
    private final String updatedAt;

    public static VcMetadataDto fromVcMetadata(VcMetadata vcMetadata) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return VcMetadataDto.builder()
                .id(vcMetadata.getId())
                .vcId(vcMetadata.getVcId())
                .issuerDid(vcMetadata.getIssuerDid())
                .subjectDid(vcMetadata.getSubjectDid())
                .vcSchema(vcMetadata.getVcSchema())
                .issuanceDate(vcMetadata.getIssuanceDate())
                .validFrom(vcMetadata.getValidFrom())
                .validUntil(vcMetadata.getValidUntil())
                .formatVersion(vcMetadata.getFormatVersion())
                .language(vcMetadata.getLanguage())
                .status(vcMetadata.getStatus())
                .metadata(vcMetadata.getMetadata())
                .createdAt(formatInstant(vcMetadata.getCreatedAt(), formatter))
                .updatedAt(formatInstant(vcMetadata.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
