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
package org.omnione.did.repository.v1.admin.dto.vcschema;

import org.omnione.did.base.db.domain.VcSchemaInfo;
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
public class VcSchemaDto {
    private final Long id;
    private final String schemaId;
    private final String title;
    private final String version;
    private final String description;
    private final String schema;
    private final String did;
    private final String createdAt;
    private final String updatedAt;

    public static VcSchemaDto fromVcSchemaInfo(VcSchemaInfo vcSchemaInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return VcSchemaDto.builder()
                .id(vcSchemaInfo.getId())
                .schemaId(vcSchemaInfo.getSchemaId())
                .title(vcSchemaInfo.getTitle())
                .version(vcSchemaInfo.getVersion())
                .description(vcSchemaInfo.getDescription())
                .schema(vcSchemaInfo.getSchema())
                .did(vcSchemaInfo.getDid())
                .createdAt(formatInstant(vcSchemaInfo.getCreatedAt(), formatter))
                .updatedAt(formatInstant(vcSchemaInfo.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}
