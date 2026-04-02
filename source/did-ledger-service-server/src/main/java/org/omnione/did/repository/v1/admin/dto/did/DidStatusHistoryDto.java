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

import org.omnione.did.base.db.domain.DidDocumentStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.omnione.did.data.model.enums.did.DidDocStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DidStatusHistoryDto {
    private final Long id;
    private final Short version;
    private final DidDocStatus fromStatus;
    private final DidDocStatus toStatus;
    private final String reason;
    private final String changedAt;
    private final String createdAt;
    private final String updatedAt;

    public static DidStatusHistoryDto fromDidDocumentStatusHistory(DidDocumentStatusHistory history) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return DidStatusHistoryDto.builder()
                .id(history.getId())
                .version(history.getVersion())
                .fromStatus(history.getFromStatus())
                .toStatus(history.getToStatus())
                .reason(history.getReason())
                .changedAt(formatInstant(history.getChangedAt(), formatter))
                .createdAt(formatInstant(history.getCreatedAt(), formatter))
                .updatedAt(formatInstant(history.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
