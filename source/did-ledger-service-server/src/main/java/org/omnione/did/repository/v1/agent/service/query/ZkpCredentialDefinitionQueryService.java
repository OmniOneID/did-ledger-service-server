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

import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.repository.ZkpCredentialDefinitionRepository;
import org.omnione.did.base.db.repository.ZkpCredentialDefinitionRepositoryAdmin;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ZkpCredentialDefinitionQueryService {
    private final ZkpCredentialDefinitionRepository zkpCredentialDefinitionRepository;
    private final ZkpCredentialDefinitionRepositoryAdmin zkpCredentialDefinitionRepositoryAdmin;

    public Optional<ZkpCredentialDefinition> findByDefinitionId(String credentialDefinitionId) {
        return zkpCredentialDefinitionRepository.findByDefinitionId(credentialDefinitionId);
    }

    public ZkpCredentialDefinition save(ZkpCredentialDefinition zkpCredentialDefinition) {
        return zkpCredentialDefinitionRepository.save(zkpCredentialDefinition);
    }

    public Page<ZkpCredentialDefinitionDto> searchZkpCredentialDefinitionList(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialDefinitionRepositoryAdmin.searchZkpCredentialDefinition(searchKey, searchValue, pageable)
                .map(ZkpCredentialDefinitionDto::fromEntity);
    }

    public ZkpCredentialDefinition findById(Long id) {
        return zkpCredentialDefinitionRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_NOT_FOUND));
    }
}
