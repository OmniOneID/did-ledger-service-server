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
package org.omnione.did.repository.v1.admin.dto.vcstatushistory;

import org.omnione.did.base.db.domain.VcStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * DTO for VC Status History information displayed in the Admin Console.
 * <p>
 * Contains the status change history for a specific VC.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VcStatusHistoryDto {
    private final Long id;
    private final String vcId;
    private final String fromStatus;
    private final String toStatus;
    private final String changedAt;
    private final String createdAt;
    private final String updatedAt;

    /**
     * Creates a VcStatusHistoryDto from a VcStatusHistory entity.
     *
     * @param history the VcStatusHistory entity
     * @return VcStatusHistoryDto instance
     */
    public static VcStatusHistoryDto fromVcStatusHistory(VcStatusHistory history) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return VcStatusHistoryDto.builder()
                .id(history.getId())
                .vcId(history.getVcId())
                .fromStatus(history.getFromStatus() != null ? history.getFromStatus().name() : null)
                .toStatus(history.getToStatus() != null ? history.getToStatus().name() : null)
                .changedAt(formatInstant(history.getChangedAt(), formatter))
                .createdAt(formatInstant(history.getCreatedAt(), formatter))
                .updatedAt(formatInstant(history.getUpdatedAt(), formatter))
                .build();
    }

    /**
     * Formats an Instant to a human-readable string.
     *
     * @param instant   the Instant to format
     * @param formatter the DateTimeFormatter to use
     * @return formatted string or null if instant is null
     */
    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
