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

import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.db.repository.DidRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.did.DidDto;
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
public class DidQueryService {
    private final DidRepository didRepository;

    public Page<DidDto> searchDidList(String searchKey, String searchValue, Pageable pageable) {
        Page<Did> didPage = didRepository.searchDids(searchKey, searchValue, pageable);

        List<DidDto> didDtos = didPage.getContent().stream()
                .map(DidDto::fromDid)
                .collect(Collectors.toList());

        return new PageImpl<>(didDtos, pageable, didPage.getTotalElements());
    }

    public Did findById(Long id) {
        return didRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_DOC_NOT_FOUND));
    }

    public Did findByDid(String did) {
        return didRepository.findByDid(did)
                .orElseThrow(() -> new OpenDidException(ErrorCode.DID_DOC_NOT_FOUND));
    }
}
