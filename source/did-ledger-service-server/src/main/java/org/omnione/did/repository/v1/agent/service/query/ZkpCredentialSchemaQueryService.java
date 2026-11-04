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

import org.omnione.did.base.db.domain.ZkpCredentialSchema;
import org.omnione.did.base.db.repository.ZkpCredentialSchemaRepository;
import org.omnione.did.base.db.repository.ZkpCredentialSchemaRepositoryAdmin;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.zkp.ZkpCredentialSchemaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ZkpCredentialSchemaQueryService {
    private final ZkpCredentialSchemaRepository zkpCredentialSchemaRepository;
    private final ZkpCredentialSchemaRepositoryAdmin zkpCredentialSchemaRepositoryAdmin;

    public ZkpCredentialSchema save(ZkpCredentialSchema zkpCredentialSchema) {
        return zkpCredentialSchemaRepository.save(zkpCredentialSchema);
    }

    public Optional<ZkpCredentialSchema> findBySchemaId(String schemaId) {
        return zkpCredentialSchemaRepository.findBySchemaId(schemaId);
    }

    public Page<ZkpCredentialSchemaDto> searchZkpCredentialSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialSchemaRepositoryAdmin.searchZkpCredentialSchema(searchKey, searchValue, pageable)
                .map(ZkpCredentialSchemaDto::fromEntity);
    }

    public ZkpCredentialSchema findById(Long id) {
        return zkpCredentialSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_SCHEMA_NOT_FOUND));
    }
}
