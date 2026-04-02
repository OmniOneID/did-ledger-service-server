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
package org.omnione.did.repository.v1.agent.service.query;

import org.omnione.did.base.db.domain.VcSchemaInfo;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.db.repository.VcSchemaRepositoryAdmin;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.vcschema.VcSchemaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class VcSchemaQueryService {
    private final VcSchemaRepository vcSchemaRepository;
    private final VcSchemaRepositoryAdmin vcSchemaRepositoryAdmin;

    // Agent용 메서드들
    public VcSchemaInfo save(VcSchemaInfo vcSchemaInfo) {
        return vcSchemaRepository.save(vcSchemaInfo);
    }

    public Optional<VcSchemaInfo> findBySchemaId(String schemaId) {
        return vcSchemaRepository.findBySchemaId(schemaId);
    }

    // Admin용 메서드들
    public Page<VcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        Page<VcSchemaInfo> vcSchemaInfoPage = vcSchemaRepositoryAdmin.searchVcSchema(searchKey, searchValue, pageable);

        List<VcSchemaDto> vcSchemaDtos = vcSchemaInfoPage.getContent().stream()
                .map(VcSchemaDto::fromVcSchemaInfo)
                .collect(Collectors.toList());

        return new PageImpl<>(vcSchemaDtos, pageable, vcSchemaInfoPage.getTotalElements());
    }

    public VcSchemaInfo findById(Long id) {
        return vcSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
    }

    public VcSchemaInfo findBySchemaIdWithException(String schemaId) {
        return vcSchemaRepository.findBySchemaId(schemaId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
    }
}
