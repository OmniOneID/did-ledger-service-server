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

import org.omnione.did.base.db.domain.VcStatusHistory;
import org.omnione.did.base.db.repository.VcStatusHistoryRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.vcstatushistory.VcStatusHistoryDto;
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
public class VcStatusHistoryQueryService {
    private final VcStatusHistoryRepository vcStatusHistoryRepository;

    public Page<VcStatusHistoryDto> searchVcStatusHistoryList(String searchKey, String searchValue, Pageable pageable) {
        Page<VcStatusHistory> vcStatusHistoryPage = vcStatusHistoryRepository.searchVcStatusHistory(searchKey, searchValue, pageable);

        List<VcStatusHistoryDto> vcStatusHistoryDtos = vcStatusHistoryPage.getContent().stream()
                .map(VcStatusHistoryDto::fromVcStatusHistory)
                .collect(Collectors.toList());

        return new PageImpl<>(vcStatusHistoryDtos, pageable, vcStatusHistoryPage.getTotalElements());
    }

    public VcStatusHistory findById(Long id) {
        return vcStatusHistoryRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_STATUS_HISTORY_NOT_FOUND));
    }
}
