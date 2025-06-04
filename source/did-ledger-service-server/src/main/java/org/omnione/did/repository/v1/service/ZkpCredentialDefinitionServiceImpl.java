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
package org.omnione.did.repository.v1.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.domain.ZkpCredentialSchema;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.repository.v1.dto.common.EmptyResDto;
import org.omnione.did.repository.v1.dto.zkp.InputZkpCredentialDefinitionReqDto;
import org.omnione.did.repository.v1.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.repository.v1.service.query.ZkpCredentialSchemaQueryService;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinitionValue;
import org.omnione.did.zkp.datamodel.enums.CredentialType;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
@Profile("!sample")
public class ZkpCredentialDefinitionServiceImpl implements ZkpCredentialDefinitionService {
    private final GsonWrapper gsonWrapper;
    private final ZkpCredentialSchemaQueryService zkpCredentialSchemaQueryService;
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService;

    @Override
    public void generateZkpCredentialDefinition(InputZkpCredentialDefinitionReqDto request) {
        log.debug("=== Starting generateZkpCredentialDefinition ===");

        // 1. decode and parse the credential schema
        CredentialDefinition credentialDefinition = decodeAndParseCredentialSchema(request.getCredentialDefinition());

        // 2. check if the credential schema exists
        ZkpCredentialSchema zkpCredentialSchema = validateSchemaExists(credentialDefinition.getSchemaId());

        // 3. check if the credential definition already exists
        validateDefinitionNotExists(credentialDefinition.getId());

        // 4. save the credential definition
        saveCredentialDefinition(credentialDefinition, zkpCredentialSchema);

        log.debug("*** Finished generateZkpCredentialDefinition ***");
    }

    private CredentialDefinition decodeAndParseCredentialSchema(String encodedCredentialDefinition) {
        log.debug("\t--> Decoding Credential Definition");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedCredentialDefinition);

        return gsonWrapper.fromJson(new String(decodedData), CredentialDefinition.class);
    }

    private ZkpCredentialSchema validateSchemaExists(String schemaId) {
        log.debug("\t--> Validating if Credential Schema exists for schemaId: {}", schemaId);

        return zkpCredentialSchemaQueryService.findBySchemaId(schemaId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.CREDENTIAL_SCHEMA_NOT_FOUND));
    }

    private void validateDefinitionNotExists(String definitionId) {
        log.debug("\t--> Checking if the Credential Definition is already registered ***");
        if (zkpCredentialDefinitionQueryService.findByDefinitionId(definitionId).isPresent()) {
            log.error("\t--> Credential Definition already exists for definitionId: {}", definitionId);
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_ALREADY_EXISTS);
        }
    }

    private void saveCredentialDefinition(CredentialDefinition credentialDefinition, ZkpCredentialSchema zkpCredentialSchema) {
        log.debug("\t--> Saving Credential Definition: {}", credentialDefinition);

        String definitionId = credentialDefinition.getId();
        String schemaId = credentialDefinition.getSchemaId();
        String version = credentialDefinition.getVer();
        CredentialType type = credentialDefinition.getType();
        String tag = credentialDefinition.getTag();
        CredentialDefinitionValue value = credentialDefinition.getValue();

        String valueJson = gsonWrapper.toJson(value);
        String credentialDefinitionJson = gsonWrapper.toJson(credentialDefinition);

        zkpCredentialDefinitionQueryService.save(ZkpCredentialDefinition.builder()
                .definitionId(definitionId)
                .schemaId(schemaId)
                .version(version)
                .type(type)
                .value(valueJson)
                .tag(tag)
                .definition(credentialDefinitionJson)
                .zkpCredentialSchemaId(zkpCredentialSchema.getId())
                .build());


        log.debug("\t--> Credential Definition saved successfully");
    }
}
