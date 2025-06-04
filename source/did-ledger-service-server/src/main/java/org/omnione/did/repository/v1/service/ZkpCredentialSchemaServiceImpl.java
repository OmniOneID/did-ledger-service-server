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
import org.omnione.did.base.datamodel.RoleType;
import org.omnione.did.base.db.domain.Did;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.repository.v1.dto.common.EmptyResDto;
import org.omnione.did.repository.v1.dto.zkp.InputZkpCredentialSchemaReqDto;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
@Profile("!sample")
public class ZkpCredentialSchemaServiceImpl implements ZkpCredentialSchemaService {

    private final DidQueryServiceImpl didQueryService;

    @Override
    public EmptyResDto generateZkpCredentialSchema(InputZkpCredentialSchemaReqDto request) {

        log.debug("=== Starting generateZkpCredentialSchema ===");
        // decode and parse the credential schema from the request
        CredentialSchema credentialSchema = decodeAndParseCredentialSchema(request.getCredentialSchema());

        // extract issuer DID from the credential schema ID
        log.debug("\t--> Extracting issuer DID from Credential Schema ID ***");
        String schemaId = credentialSchema.getId();
        String issuerDid = extractIssuerDidFromCredentialSchemaId(schemaId);

        // query issuer information by DID
        log.debug("\t--> Querying issuer information by DID ***");
        Did foundIssuerInfo = didQueryService.didFindByDid(issuerDid).orElseThrow(() -> new OpenDidException(ErrorCode.DID_NOT_FOUND));

        // check if the entity has the role of Issuer
        log.debug("\t--> Checking if the issuer has the role of Issuer ***");
        if (!foundIssuerInfo.getRole().equals(RoleType.Issuer.name())) {
            log.error("\t--> The DID {} does not have the role of Issuer", issuerDid);
            throw new OpenDidException(ErrorCode.ROLE_TYPE_MISMATCH);
        }

        // issuer 정보 중에서 상태가 status 확인 (=activated)

        // 적재

        log.debug("*** Finished generateZkpCredentialSchema ***");

        return new EmptyResDto();
    }

    private CredentialSchema decodeAndParseCredentialSchema(String encodedVcSchema) {
        log.debug("\t--> Decoding Credential Schema");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedVcSchema);
        CredentialSchema credentialSchema = GsonWrapper.getGson().fromJson(new String(decodedData), CredentialSchema.class);
        log.debug("\t--> Decoded Credential Schema: {}", credentialSchema);
        return credentialSchema;
    }

    /**
     * Extracts the issuer DID from a credential schema ID.
     *
     * The schema ID follows the format: issuer-did:2:schema-name:version
     * where issuer-did is typically in the format: did:method:identifier
     *
     * @param schemaId the credential schema ID to parse
     * @return the issuer DID extracted from the schema ID
     * @throws OpenDidException if schemaId is null, empty, or has invalid format
     *
     * @example
     * Input: "did:example:123:2:MySchema:1.0"
     * Output: "did:example:123"
     */
    private String extractIssuerDidFromCredentialSchemaId(String schemaId) {
        if (schemaId == null || schemaId.trim().isEmpty()) {
            log.error("\t--> Invalid schemaId: {}", schemaId);
            throw new OpenDidException(ErrorCode.INVALID_CREDENTIAL_SCHEMA_ID);
        }

        String[] parts = schemaId.split(":");
        if (parts.length < 4) {
            log.error("\t--> Invalid schemaId format: {}", schemaId);
            throw new OpenDidException(ErrorCode.INVALID_CREDENTIAL_SCHEMA_ID);
        }

        // issuer-did는 did:method:identifier 형태여야 함
        if (!"did".equals(parts[0])) {
            log.error("\t--> Invalid issuer DID format in schemaId: {}", schemaId);
            throw new OpenDidException(ErrorCode.INVALID_CREDENTIAL_SCHEMA_ID);
        }

        // did:method:identifier 추출
        String issuerDid = parts[0] + ":" + parts[1] + ":" + parts[2];
        log.debug("\t--> Extracted issuerDid: {}", issuerDid);
        return issuerDid;
    }
}
