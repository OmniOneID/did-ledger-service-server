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
package org.omnione.did.repository.v1.admin.service.query;

import org.omnione.did.base.db.domain.VcMetadata;
import org.omnione.did.base.db.repository.VcMetadataRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.vcmetadata.VcMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VcMetadataQueryService {
    private final VcMetadataRepository vcMetadataRepository;

    public Page<VcMetadataDto> searchVcMetadataList(String searchKey, String searchValue, Pageable pageable) {
        Page<VcMetadata> vcMetadataPage = vcMetadataRepository.searchVcMetadata(searchKey, searchValue, pageable);

        List<VcMetadataDto> vcMetadataDtos = vcMetadataPage.getContent().stream()
                .map(VcMetadataDto::fromVcMetadata)
                .collect(Collectors.toList());

        return new PageImpl<>(vcMetadataDtos, pageable, vcMetadataPage.getTotalElements());
    }

    public VcMetadata findById(Long id) {
        return vcMetadataRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
    }

    public VcMetadata findByVcId(String vcId) {
        return vcMetadataRepository.findByVcId(vcId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
    }
}
