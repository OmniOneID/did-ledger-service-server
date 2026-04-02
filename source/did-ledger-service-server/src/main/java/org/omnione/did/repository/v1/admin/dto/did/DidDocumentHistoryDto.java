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
package org.omnione.did.repository.v1.admin.dto.did;

import org.omnione.did.base.db.domain.DidDocument;
import org.omnione.did.base.db.domain.DidDocumentRevoked;
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
public class DidDocumentHistoryDto {
    private final Long id;
    private final Short version;
    private final String document;
    private final String controller;
    private final Boolean deactivated;
    private final Boolean isRevoked; // Whether this is revoked (from did_document_revoked table)
    private final String createdAt;
    private final String updatedAt;
    private final String revokedAt; // Revocation timestamp (only for revoked documents)

    public static DidDocumentHistoryDto fromDidDocument(DidDocument didDocument) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return DidDocumentHistoryDto.builder()
                .id(didDocument.getId())
                .version(didDocument.getVersion())
                .document(didDocument.getDocument())
                .controller(didDocument.getController())
                .deactivated(didDocument.getDeactivated())
                .isRevoked(false) // Currently active document
                .createdAt(formatInstant(didDocument.getCreatedAt(), formatter))
                .updatedAt(formatInstant(didDocument.getUpdatedAt(), formatter))
                .revokedAt(null) // Not revoked
                .build();
    }

    public static DidDocumentHistoryDto fromDidDocumentRevoked(DidDocumentRevoked didDocumentRevoked) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return DidDocumentHistoryDto.builder()
                .id(didDocumentRevoked.getId())
                .version(didDocumentRevoked.getVersion())
                .document(didDocumentRevoked.getDocument())
                .controller(didDocumentRevoked.getController())
                .deactivated(didDocumentRevoked.getDeactivated())
                .isRevoked(true) // Revoked document
                .createdAt(formatInstant(didDocumentRevoked.getCreatedAt(), formatter))
                .updatedAt(formatInstant(didDocumentRevoked.getUpdatedAt(), formatter))
                .revokedAt(formatInstant(didDocumentRevoked.getRevokedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
