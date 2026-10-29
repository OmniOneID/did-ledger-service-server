---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

DID Ledger Service API
==

- Date: 2026-10-27
- Version: v3.0.0
  
Table of Contents
---
<!-- TOC tocDepth:2..3 chapterDepth:2..6 -->

- [1. Overview](#1-overview)
- [2. Terminology](#2-terminology)
- [3. API List](#3-api-list)
    - [3.1. Sequential API](#31-sequential-api)
    - [3.2. Single Call API](#32-single-call-api)
- [4. Common Specifications](#4-common-specifications)
    - [4.1. API Key Authentication](#41-api-key-authentication)
    - [4.2. Error Response](#42-error-response)
- [5. Single Call API](#5-single-call-api)
    - [5.1. Register DID Document](#51-register-did-document)
    - [5.2. Get DID Document](#52-get-did-document)
    - [5.3. Update DID Document Status](#53-update-did-document-status)
    - [5.4. Register VC Metadata](#54-register-vc-metadata)
    - [5.5. Get VC Metadata](#55-get-vc-metadata)
    - [5.6. Update VC Metadata Status](#56-update-vc-metadata-status)
    - [5.7. Register VC Schema](#57-register-vc-schema)
    - [5.8. Get VC Schema](#58-get-vc-schema)
    - [5.9. Register ZKP Credential Schema](#59-register-zkp-credential-schema)
    - [5.10. Get ZKP Credential Schema](#510-get-zkp-credential-schema)
    - [5.11. Register ZKP Credential Definition](#511-register-zkp-credential-definition)
    - [5.12. Get ZKP Credential Definition](#512-get-zkp-credential-definition)

<!-- /TOC -->


## 1. Overview

This document defines the Agent APIs provided by the DID Ledger Service Server (LSS). The LSS uses a PostgreSQL database to provide the same ledger functionality as a blockchain, and provides registration, retrieval, and status change of DID Documents, VC Metadata, VC Schemas, ZKP Credential Schemas, and ZKP Credential Definitions.

- The base path of the Agent APIs is `/lss/api/v1`, and the default server port is `8098`. (e.g. `http://${Host}:8098/lss/api/v1/did-doc`)
- The Admin APIs under `/lss/admin/v1/**` are used only by the LSS Admin Console and are not covered in this document.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 2. Terminology
- Protocol
  - A set of `Sequential APIs` that must be called in a predetermined order to perform a specific function. The API call sequence must be strictly followed, as incorrect ordering may lead to unexpected results.
- Sequential API
  - A series of APIs that are called in a predetermined order to perform a specific function (protocol). Each API must be called sequentially, and incorrect ordering may prevent proper operation.
  - However, some protocols may have APIs with the same calling sequence, in which case one API can be selected and called.
- Single Call API
  - An API that can be called independently regardless of order, like typical REST APIs.
- Standard API
  - APIs clearly defined in the API documentation that must be provided consistently across all implementations. Standard APIs ensure interoperability between systems and must operate according to predefined specifications.
- Non-Standard API
  - APIs that can be defined or customized differently according to the needs of each implementation. The non-standard APIs provided in this document are just one example and may be implemented differently for each implementation. In this case, separate documentation for each implementation is required.
- API Key
  - An authentication key passed in the `X-API-Key` header when calling an Agent API. It is issued in the LSS Admin Console, and the Role assigned at issuance determines which APIs can be called.
- Role
  - The permission level assigned to an API Key: `TAS`, `ISSUER`, or `READ`. For the Role required by each API, see [4.1.2. Access Permissions by Role](#412-access-permissions-by-role).
- M/O notation
  - In request/response body tables, `M` means Mandatory and `O` means Optional. In header and parameter tables, a leading `+` marks a mandatory item.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 3. API List

### 3.1. Sequential API
The DID Ledger Service currently has no protocols defined for performing specific functions, and therefore does not provide sequential APIs.

<div style="page-break-after: always; margin-top: 40px;"></div>

### 3.2. Single Call API

| API                              | Method | URL                                 | Description                           | Role     | Standard API |
| -------------------------------- | ------ | ----------------------------------- | ------------------------------------- | -------- | ------------ |
| `register-did-doc`               | POST   | /lss/api/v1/did-doc                 | Register DID Document (incl. update)  | `TAS`    | N            |
| `get-did-doc`                    | GET    | /lss/api/v1/did-doc                 | Get DID Document                      | `READ`   | N            |
| `update-did-doc-status`          | PUT    | /lss/api/v1/did-doc                 | Update DID Document status            | `TAS`    | N            |
| `register-vc-meta`               | POST   | /lss/api/v1/vc-meta                 | Register VC Metadata                  | `ISSUER` | N            |
| `get-vc-meta`                    | GET    | /lss/api/v1/vc-meta                 | Get VC Metadata                       | `READ`   | N            |
| `update-vc-meta-status`          | PUT    | /lss/api/v1/vc-meta                 | Update VC Metadata status             | `ISSUER` | N            |
| `register-vc-schema`             | POST   | /lss/api/v1/vc-schema               | Register VC Schema                    | `ISSUER` | N            |
| `get-vc-schema`                  | GET    | /lss/api/v1/vc-schema               | Get VC Schema                         | `READ`   | N            |
| `register-credential-schema`     | POST   | /lss/api/v1/credential-schema       | Register ZKP Credential Schema        | `ISSUER` | N            |
| `get-credential-schema`          | GET    | /lss/api/v1/credential-schema       | Get ZKP Credential Schema             | `READ`   | N            |
| `register-credential-definition` | POST   | /lss/api/v1/credential-definition   | Register ZKP Credential Definition    | `ISSUER` | N            |
| `get-credential-definition`      | GET    | /lss/api/v1/credential-definition   | Get ZKP Credential Definition         | `READ`   | N            |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 4. Common Specifications

### 4.1. API Key Authentication

Every LSS Agent API has a required Role (`@ApiKeyRequired`). When API Key validation is enabled, the API Key in the `X-API-Key` header and its Role are validated before the request is processed.

#### 4.1.1. Request Header

| Header        | Value       | Remarks                                                                        |
| ------------- | ----------- | ------------------------------------------------------------------------------ |
| + `X-API-Key` | `{API Key}` | Mandatory when API Key validation is enabled. If absent, the `API-Key` header value is used |

- The server hashes the received API Key with SHA-256 and compares it with the stored hash. An API Key that is not registered or has been deactivated is treated as invalid.
- The Agent APIs do not use the `Authorization` header. The `Authorization: Bearer` header is used for administrator authentication (JWT) of the LSS Admin Console; a request containing an invalid Bearer token receives HTTP 401 with no response body.

#### 4.1.2. Access Permissions by Role

Each API has a required Role, and an API Key with a higher Role can also call APIs that require a lower Role.

| Required Role | Allowed API Key Roles       | Target APIs                                                                                                   |
| ------------- | --------------------------- | ------------------------------------------------------------------------------------------------------------- |
| `TAS`         | `TAS`                       | Register DID Document, Update DID Document Status                                                             |
| `ISSUER`      | `TAS`, `ISSUER`             | Register/Update status of VC Metadata, Register VC Schema, Register ZKP Credential Schema, Register ZKP Credential Definition |
| `READ`        | `TAS`, `ISSUER`, `READ`     | All retrieval (GET) APIs                                                                                      |

#### 4.1.3. Validation Setting

- Whether API Keys are validated is determined by the `API_KEY_VALIDATION_ENABLED` value in the `server_config` table, which can be changed on the **Server Configuration** screen of the LSS Admin Console.
- The initial value is `false`. When the value is `false`, no API Key validation is performed, so all Agent APIs can be called without the `X-API-Key` header.
- Validation is enabled when the value is one of `true`, `1`, `Y`, or `yes`. The setting is read on every request, so changes take effect immediately.

#### 4.1.4. API Key Issuance

- API Keys are issued on the **API Key Management** screen of the LSS Admin Console by specifying a name, a Role, and a validity period (days).
- An issued API Key starts with `lss_`, and its plain text is shown only once at issuance. Only the SHA-256 hash is stored on the server, so the value shown at issuance must be kept securely.
- Issued API Keys can be deactivated and activated on the API Key Management screen.
- An API Key has an expiry date, and an API Key whose expiry date has passed is rejected with the `SSRVLSS14506` error. Renewing it on the API Key Management screen extends the expiry date without changing the key value.

#### 4.1.5. API Key Validation Errors

When API Key validation fails, the following errors are returned with the body described in [4.2.1. Error Response Format](#421-error-response-format). These errors apply to all Agent APIs and occur only when API Key validation is enabled.

| HTTP Status | Code         | Description                                                  |
| ----------- | ------------ | ------------------------------------------------------------ |
| 401         | SSRVLSS14502 | The `X-API-Key` (or `API-Key`) header is missing or empty    |
| 401         | SSRVLSS14503 | The API Key is not registered or has been deactivated        |
| 401         | SSRVLSS14506 | The expiry date of the API Key has passed                    |
| 403         | SSRVLSS14504 | The Role of the API Key does not satisfy the required Role   |
| 500         | SSRVLSS14505 | An error occurred while validating the API Key               |

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.2. Error Response

For the full list and detailed descriptions of error codes, see the [DID Ledger Service Error Code](../errorCode/LedgerService_ErrorCode.md) document. Each API in this document lists only the error codes that the API can return.

#### 4.2.1. Error Response Format

| Name          | Type   | M/O | Description    |
| ------------- | ------ | --- | -------------- |
| `code`        | string | M   | Error code     |
| `description` | string | M   | Error message  |

```json
{
  "code": "SSRVLSS11501",
  "description": "DID not found"
}
```

- The HTTP status code defined for each error code is used. Even when a target is not found, 400 or 500 is returned instead of 404, so clients should distinguish errors by `code`, not by HTTP status.

#### 4.2.2. Common Handling Rules

| Situation                                                                  | HTTP Status | code           | description                                            |
| -------------------------------------------------------------------------- | ----------- | -------------- | ------------------------------------------------------ |
| The request body cannot be read (malformed JSON, undefined enum value, etc.) | 500       | `SSRVLSS11001` | `""` (empty string)                                    |
| Request body validation fails (Register VC Metadata API)                   | 500         | `9999`         | Field validation messages joined with `"; "`           |

- Exceptions other than the cases above and those defined as LSS error codes (e.g. retrieving or updating the status of VC Metadata with a non-existent `vcId`) are not returned in the error response format above.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 5. Single Call API

Single Call APIs are independent APIs that perform a specific function.
Therefore, they are not Sequential APIs (aka protocols) which are groups of APIs that must be called in order, so no protocol number is assigned.
The list of Single Call APIs provided by the DID Ledger Service is shown in the table below.

| API                              | Method | URL                                 | Description                           | Role     | Standard API |
| -------------------------------- | ------ | ----------------------------------- | ------------------------------------- | -------- | ------------ |
| `register-did-doc`               | POST   | /lss/api/v1/did-doc                 | Register DID Document (incl. update)  | `TAS`    | N            |
| `get-did-doc`                    | GET    | /lss/api/v1/did-doc                 | Get DID Document                      | `READ`   | N            |
| `update-did-doc-status`          | PUT    | /lss/api/v1/did-doc                 | Update DID Document status            | `TAS`    | N            |
| `register-vc-meta`               | POST   | /lss/api/v1/vc-meta                 | Register VC Metadata                  | `ISSUER` | N            |
| `get-vc-meta`                    | GET    | /lss/api/v1/vc-meta                 | Get VC Metadata                       | `READ`   | N            |
| `update-vc-meta-status`          | PUT    | /lss/api/v1/vc-meta                 | Update VC Metadata status             | `ISSUER` | N            |
| `register-vc-schema`             | POST   | /lss/api/v1/vc-schema               | Register VC Schema                    | `ISSUER` | N            |
| `get-vc-schema`                  | GET    | /lss/api/v1/vc-schema               | Get VC Schema                         | `READ`   | N            |
| `register-credential-schema`     | POST   | /lss/api/v1/credential-schema       | Register ZKP Credential Schema        | `ISSUER` | N            |
| `get-credential-schema`          | GET    | /lss/api/v1/credential-schema       | Get ZKP Credential Schema             | `READ`   | N            |
| `register-credential-definition` | POST   | /lss/api/v1/credential-definition   | Register ZKP Credential Definition    | `ISSUER` | N            |
| `get-credential-definition`      | GET    | /lss/api/v1/credential-definition   | Get ZKP Credential Definition         | `READ`   | N            |

■ Authorization

All APIs pass the API Key in the `X-API-Key` header, and the Role required by each API is shown in the `Authorization` item of that API. For how API Keys are validated and how validation is enabled, see [4.1. API Key Authentication](#41-api-key-authentication). API Key validation errors (HTTP 401/403/500) are as described in [4.1.5. API Key Validation Errors](#415-api-key-validation-errors) and are omitted from each API's error list.

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.1. Register DID Document

Registers a DID Document signed by the TAS (controller) to the ledger. If the same DID is already registered, the requested DID Document is stored as a new version.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `POST`                 |              |
| Path          | `/lss/api/v1/did-doc`  |              |
| Authorization | `X-API-Key`            | Role: `TAS`  |

#### 5.1.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name                              | Type               | M/O | Description                                                                                       |
| --------------------------------- | ------------------ | --- | ------------------------------------------------------------------------------------------------- |
| `roleType`                        | string (enum)      | M   | Role of the DID owner. Stored for a new registration; not used when updating an already registered DID. Values: `TAS`, `WALLET`, `ISSUER`, `VERIFIER`, `WALLET_PROVIDER`, `APP_PROVIDER`, `LIST_PROVIDER`, `OP_PROVIDER`, `KYC_PROVIDER`, `NOTIFICATION_PROVIDER`, `LOG_PROVIDER`, `PORTAL_PROVIDER`, `DELEGATION_PROVIDER`, `STORAGE_PROVIDER`, `BACKUP_PROVIDER`, `ETC` |
| `didDoc`                          | object             | M   | DID Document signed by the controller (InvokedDidDoc)                                             |
| `didDoc.didDoc`                   | string (multibase) | M   | Multibase-encoded DID Document JSON. The `versionId` of the DID Document must be an integer string |
| `didDoc.controller`               | object             | M   | Controller information                                                                            |
| `didDoc.controller.did`           | string             | M   | Controller DID. Must be a DID registered in the LSS with Role `TAS`                               |
| `didDoc.controller.certVcRef`     | string             | O   | URL of the controller's certificate VC                                                            |
| `didDoc.nonce`                    | string (multibase) | O   | Controller nonce. Included in the signed data                                                     |
| `didDoc.proof`                    | object             | M   | Invocation proof of the controller                                                                |
| `didDoc.proof.type`               | string             | M   | Proof type. `Secp256k1Signature2018`, `Secp256r1Signature2018`                                    |
| `didDoc.proof.created`            | string             | O   | Signing date and time. Included in the signed data                                                |
| `didDoc.proof.verificationMethod` | string             | O   | Signing key URL. Included in the signed data                                                      |
| `didDoc.proof.proofPurpose`       | string             | O   | Proof purpose. Included in the signed data                                                        |
| `didDoc.proof.proofValue`         | string (multibase) | M   | Signature value                                                                                   |

- M/O reflects how the LSS server processes the request. `didDoc.controller` and `didDoc.proof` are not verified when no DID is registered in the LSS yet (first registration).

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.1.2. Response

**■ Process**
1. Multibase-decode `didDoc.didDoc` and parse the DID Document
1. Verify the controller signature (only when at least one DID is registered in the LSS)
    - Extract the DID from `didDoc.controller.did` and check that it is registered
    - Check that the Role of the controller DID is `TAS`
    - Verify the signature with the first `capabilityInvocation` key of the current version of the controller's DID Document
    - Signed data: SHA-256 hash of the `didDoc` object, with `proof.proofValue` removed, serialized as key-sorted JSON
1. Check whether the DID Document's `id` (DID) is already registered
    - Not registered: register a new DID with `roleType` as its Role (status `ACTIVATED`, version = `versionId` of the DID Document)
    - Registered: check that `versionId` equals the last stored version + 1, deactivate the previous DID Document, and store the new version (DID status `ACTIVATED`)
1. Save the DID Document status history

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

| Code         | Description                                                                 |
| ------------ | --------------------------------------------------------------------------- |
| SSRVLSS11000 | Controller signature verification failed                                    |
| SSRVLSS11500 | Update of a registered DID, but `versionId` is not the last version + 1     |
| SSRVLSS11501 | The controller DID is not registered                                        |
| SSRVLSS11504 | The DID Document of the controller DID cannot be found                      |
| SSRVLSS13002 | The Role of the controller DID is not `TAS`                                 |

**■ Status 500 - Server error**

| Code         | Description                                         |
| ------------ | --------------------------------------------------- |
| SSRVLSS11001 | The request body cannot be read                     |
| SSRVLSS99999 | Failed to generate the hash for signature verification |

#### 5.1.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/lss/api/v1/did-doc" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "roleType": "ISSUER",
    "didDoc": {
      "didDoc": "meyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvbnMvZGlkL3YxIl0s...",
      "controller": {
        "did": "did:omn:tas",
        "certVcRef": "http://127.0.0.1:8090/tas/api/v1/certificate-vc"
      },
      "nonce": "mT2Na/BYYqJy2nm6TRvqs6Q",
      "proof": {
        "type": "Secp256r1Signature2018",
        "created": "2026-10-27T10:00:00Z",
        "verificationMethod": "did:omn:tas?versionId=1#invoke",
        "proofPurpose": "capabilityInvocation",
        "proofValue": "mH1Xl2pIzwN8..."
      }
    }
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.2. Get DID Document

Retrieves a DID Document from the ledger. A specific version can be retrieved with the `versionId` of a DID URL.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `GET`                  |              |
| Path          | `/lss/api/v1/did-doc`  |              |
| Authorization | `X-API-Key`            | Role: `READ` |

#### 5.2.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                                      |
| ------------- | ----------- | -------------------------------------------- |
| + `X-API-Key` | `{API Key}` | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name  | Description                    | Remarks                   |
| ----- | ------------------------------ | ------------------------- |
| `did` | DID or DID URL to retrieve     | O, default: `did:omn:tas` |

**■ Interpretation of the `did` Parameter**

- The part before `?` or `#` is used as the DID.
- If a `versionId=` value is present, that value (up to `&`, `#`, or the end of the string) is used as the version to retrieve; otherwise the current version of the DID is used. `versionId` must be an integer.
    - e.g. `did:omn:issuer?versionId=1#assert` → DID `did:omn:issuer`, version `1`
    - e.g. `did:omn:issuer` → DID `did:omn:issuer`, current version
- Because this is a query parameter value, `:`, `?`, `=`, `#`, and `&` must be URL-encoded. (e.g. `did%3Aomn%3Aissuer%3FversionId%3D1`)

**■ HTTP Body**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.2.2. Response

**■ Process**
1. Extract the DID from the `did` parameter and check that it is registered
1. Determine the version: the `versionId` if present, otherwise the current version of the DID
1. Retrieve the DID Document according to the DID status
    - `ACTIVATED`, `DEACTIVATED`: from the registered DID Documents
    - `REVOKED`, `TERMINATED`: the most recently revoked document of that version from the revoked DID Documents
1. Return the stored DID Document JSON

**■ Status 200 - Success**

- The response body is the stored DID Document JSON string (not multibase-encoded, keys sorted).
- The server does not set the response `Content-Type` explicitly, so it is determined by the request's `Accept` header.

**■ Status 400 - Client error**

| Code         | Description                                                            |
| ------------ | ---------------------------------------------------------------------- |
| SSRVLSS11501 | The DID cannot be extracted from the `did` parameter or is not registered |
| SSRVLSS11504 | The DID Document of the requested version does not exist               |

**■ Status 500 - Server error**

N/A

#### 5.2.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/lss/api/v1/did-doc?did=did%3Aomn%3Aissuer%3FversionId%3D1" \
  -H "X-API-Key: ${API_KEY}"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: text/plain;charset=UTF-8

{
  "@context": ["https://www.w3.org/ns/did/v1"],
  "assertionMethod": ["assert"],
  "capabilityInvocation": ["invoke"],
  "controller": "did:omn:tas",
  "created": "2026-10-27T10:00:00Z",
  "deactivated": false,
  "id": "did:omn:issuer",
  "proofs": [
    {
      "created": "2026-10-27T10:00:00Z",
      "proofPurpose": "assertionMethod",
      "proofValue": "mIHzY5Cg2qkWb...",
      "type": "Secp256r1Signature2018",
      "verificationMethod": "did:omn:issuer?versionId=1#assert"
    },
    {
      "created": "2026-10-27T10:00:00Z",
      "proofPurpose": "capabilityInvocation",
      "proofValue": "mH9b2Qe0Lx7Tn...",
      "type": "Secp256r1Signature2018",
      "verificationMethod": "did:omn:issuer?versionId=1#invoke"
    }
  ],
  "updated": "2026-10-27T10:00:00Z",
  "verificationMethod": [
    {
      "authType": 1,
      "controller": "did:omn:tas",
      "id": "assert",
      "publicKeyMultibase": "z27t2aTWNdcQhZdbbA...",
      "type": "Secp256r1VerificationKey2018"
    },
    {
      "authType": 1,
      "controller": "did:omn:tas",
      "id": "invoke",
      "publicKeyMultibase": "z23EJQEZkKmrLUpSgp...",
      "type": "Secp256r1VerificationKey2018"
    }
  ],
  "versionId": "1"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.3. Update DID Document Status

Changes the status of a DID registered in the ledger.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `PUT`                  |              |
| Path          | `/lss/api/v1/did-doc`  |              |
| Authorization | `X-API-Key`            | Role: `TAS`  |

#### 5.3.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name     | Type          | M/O | Description                                                                                         |
| -------- | ------------- | --- | --------------------------------------------------------------------------------------------------- |
| `did`    | string        | M   | DID or DID URL whose status is changed. `versionId` is interpreted as in [5.2.1](#521-request)      |
| `status` | string (enum) | M   | New status. `ACTIVATED`, `DEACTIVATED`, `REVOKED`, `TERMINATED`                                     |

#### 5.3.2. Response

**■ Process**
1. Extract the DID from `did` and check that it is registered
1. Determine the target version: the `versionId` if present, otherwise the current version of the DID
1. Validate the status transition
    - If the current status is `TERMINATED`, the status cannot be changed
    - If the current status is `REVOKED`, it can only be changed to `TERMINATED`
1. Process according to the new status
    - `TERMINATED`: record the termination time
    - `REVOKED`: revoke the DID Documents of all versions of the DID
    - `ACTIVATED`, `DEACTIVATED`: change the deactivated flag of the target version's DID Document
1. Save the status history and update the DID status

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

| Code         | Description                                                                         |
| ------------ | ----------------------------------------------------------------------------------- |
| SSRVLSS11501 | The DID cannot be extracted from `did` or is not registered                         |
| SSRVLSS11504 | When changing to `ACTIVATED`/`DEACTIVATED`, the DID Document of the target version does not exist |
| SSRVLSS13000 | The current status of the DID is `TERMINATED`                                       |
| SSRVLSS13001 | A DID whose current status is `REVOKED` is requested to change to a status other than `TERMINATED` |

**■ Status 500 - Server error**

| Code         | Description                     |
| ------------ | ------------------------------- |
| SSRVLSS11001 | The request body cannot be read |

#### 5.3.3. Example

**■ Request**

```shell
curl -v -X PUT "http://${Host}:${Port}/lss/api/v1/did-doc" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "did": "did:omn:issuer",
    "status": "DEACTIVATED"
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.4. Register VC Metadata

Registers VC (Verifiable Credential) Metadata to the ledger. The request body is the VC Metadata object (VcMeta) itself.

| Item          | Description            | Remarks        |
| ------------- | ---------------------- | -------------- |
| Method        | `POST`                 |                |
| Path          | `/lss/api/v1/vc-meta`  |                |
| Authorization | `X-API-Key`            | Role: `ISSUER` |

#### 5.4.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name                              | Type          | M/O | Description                                                       |
| --------------------------------- | ------------- | --- | ----------------------------------------------------------------- |
| `id`                              | string        | M   | VC ID                                                             |
| `issuer`                          | object        | M   | Issuer information                                                |
| `issuer.did`                      | string        | M   | Issuer DID                                                        |
| `issuer.certVcRef`                | string        | O   | URL of the issuer's certificate VC                                |
| `subject`                         | string        | M   | Subject (holder) DID                                              |
| `credentialSchema`                | object        | M   | Credential schema information                                     |
| `credentialSchema.id`             | string        | M   | VC Schema URL                                                     |
| `credentialSchema.type`           | string        | M   | VC Schema format type (e.g. `OsdSchemaCredential`)                |
| `credentialSchema.value`          | string        | O   | Optional item of the common Credential Schema model. Stored as is in the VC Metadata if provided |
| `credentialSchema.presentAll`     | boolean       | O   | Same as above                                                     |
| `credentialSchema.displayClaims`  | array(string) | O   | Same as above                                                     |
| `credentialSchema.requiredClaims` | array(string) | O   | Same as above                                                     |
| `credentialSchema.allowedIssuers` | array(string) | O   | Same as above                                                     |
| `status`                          | string        | M   | VC status (`ACTIVE`, `INACTIVE`, `REVOKED`)                       |
| `issuanceDate`                    | string        | M   | Issuance date and time (see date-time format)                     |
| `validFrom`                       | string        | M   | Start of validity period (see date-time format)                   |
| `validUntil`                      | string        | M   | End of validity period (see date-time format)                     |
| `formatVersion`                   | string        | M   | VC format version (e.g. `1.0`)                                    |
| `language`                        | string        | M   | VC language code (e.g. `ko`)                                      |

- Date-time format: `yyyy-MM-ddTHH:mm:ssZ`, `yyyy-MM-ddTHH:mm:ss.SSSZ` to `yyyy-MM-ddTHH:mm:ss.SSSSSSZ` with 3 to 6 fractional-second digits, or `yyyy-MM-ddTHH:mm:ss±hh:mm` with an offset
- Mandatory string items do not allow empty strings.

#### 5.4.2. Response

**■ Process**
1. Validate the request body (mandatory items, date-time format)
1. Store the VC Metadata (VC ID, issuer DID, subject DID, VC Schema ID (`credentialSchema.id`), status, dates, format version, language, and the VC Metadata JSON)
1. Save the VC status history (registered as `ACTIVE`)

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                                                                         |
| ------------ | ----------------------------------------------------------------------------------- |
| 9999         | Request body validation failed. `description` contains the field validation messages |
| SSRVLSS11001 | The request body cannot be read                                                     |

#### 5.4.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/lss/api/v1/vc-meta" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "id": "c184fb29-e6e1-4144-bae0-ccc44a3770df",
    "issuer": {
      "did": "did:omn:issuer",
      "certVcRef": "http://127.0.0.1:8091/issuer/api/v1/certificate-vc"
    },
    "subject": "did:omn:holder",
    "credentialSchema": {
      "id": "http://127.0.0.1:8091/issuer/api/v1/vc/vcschema?name=mdl",
      "type": "OsdSchemaCredential"
    },
    "status": "ACTIVE",
    "issuanceDate": "2026-10-27T10:00:00Z",
    "validFrom": "2026-10-27T10:00:00Z",
    "validUntil": "2027-10-27T10:00:00Z",
    "formatVersion": "1.0",
    "language": "ko"
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.5. Get VC Metadata

Retrieves VC Metadata from the ledger.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `GET`                  |              |
| Path          | `/lss/api/v1/vc-meta`  |              |
| Authorization | `X-API-Key`            | Role: `READ` |

#### 5.5.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                                      |
| ------------- | ----------- | -------------------------------------------- |
| + `X-API-Key` | `{API Key}` | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name     | Description | Remarks |
| -------- | ----------- | ------- |
| + `vcId` | VC ID       | M       |

**■ HTTP Body**

N/A

#### 5.5.2. Response

**■ Process**
1. Retrieve the VC Metadata by `vcId`
1. Return the stored VC Metadata JSON (reflecting `status` updated by status changes)

**■ Status 200 - Success**

- The response body is the VC Metadata JSON string, with the same items as the HTTP Body in [5.4.1](#541-request) (not multibase-encoded, keys sorted).
- The server does not set the response `Content-Type` explicitly, so it is determined by the request's `Accept` header.

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

N/A

- If no VC Metadata exists for `vcId`, an `IllegalArgumentException` is thrown inside the server, and the server's default error response is returned instead of an LSS error code.

#### 5.5.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/lss/api/v1/vc-meta?vcId=c184fb29-e6e1-4144-bae0-ccc44a3770df" \
  -H "X-API-Key: ${API_KEY}"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: text/plain;charset=UTF-8

{
  "credentialSchema": {
    "id": "http://127.0.0.1:8091/issuer/api/v1/vc/vcschema?name=mdl",
    "type": "OsdSchemaCredential"
  },
  "formatVersion": "1.0",
  "id": "c184fb29-e6e1-4144-bae0-ccc44a3770df",
  "issuanceDate": "2026-10-27T10:00:00Z",
  "issuer": {
    "certVcRef": "http://127.0.0.1:8091/issuer/api/v1/certificate-vc",
    "did": "did:omn:issuer"
  },
  "language": "ko",
  "status": "ACTIVE",
  "subject": "did:omn:holder",
  "validFrom": "2026-10-27T10:00:00Z",
  "validUntil": "2027-10-27T10:00:00Z"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.6. Update VC Metadata Status

Changes the status of a VC registered in the ledger.

| Item          | Description            | Remarks        |
| ------------- | ---------------------- | -------------- |
| Method        | `PUT`                  |                |
| Path          | `/lss/api/v1/vc-meta`  |                |
| Authorization | `X-API-Key`            | Role: `ISSUER` |

#### 5.6.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name       | Type          | M/O | Description                                        |
| ---------- | ------------- | --- | -------------------------------------------------- |
| `vcId`     | string        | M   | ID of the VC whose status is changed               |
| `vcStatus` | string (enum) | M   | New VC status. `ACTIVE`, `INACTIVE`, `REVOKED`     |

#### 5.6.2. Response

**■ Process**
1. Retrieve the VC Metadata by `vcId`
1. If the current status is `REVOKED`, the status cannot be changed
1. Update the VC Metadata status and the `status` in the stored VC Metadata JSON to `vcStatus`
1. Save the VC status history (previous status → new status)

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

| Code         | Description                                              |
| ------------ | -------------------------------------------------------- |
| SSRVLSS12000 | Status change requested for a VC that is already `REVOKED` |

**■ Status 500 - Server error**

| Code         | Description                     |
| ------------ | ------------------------------- |
| SSRVLSS11001 | The request body cannot be read |

- If no VC Metadata exists for `vcId`, an `IllegalArgumentException` is thrown inside the server, and the server's default error response is returned instead of an LSS error code.

#### 5.6.3. Example

**■ Request**

```shell
curl -v -X PUT "http://${Host}:${Port}/lss/api/v1/vc-meta" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "vcId": "c184fb29-e6e1-4144-bae0-ccc44a3770df",
    "vcStatus": "REVOKED"
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.7. Register VC Schema

Registers a VC Schema to the ledger.

| Item          | Description              | Remarks        |
| ------------- | ------------------------ | -------------- |
| Method        | `POST`                   |                |
| Path          | `/lss/api/v1/vc-schema`  |                |
| Authorization | `X-API-Key`              | Role: `ISSUER` |

#### 5.7.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name       | Type               | M/O | Description                                                                                  |
| ---------- | ------------------ | --- | -------------------------------------------------------------------------------------------- |
| `did`      | string             | M   | Issuer DID registering the VC Schema. Must exactly match a registered DID (DID URL not allowed) |
| `vcSchema` | string (multibase) | M   | Multibase-encoded VC Schema JSON                                                             |

- The VC Schema JSON decoded from `vcSchema` requires `@id`, `@schema`, `title`, `description`, `metadata` (`language`, `formatVersion`), and `credentialSubject`. `@id` is used as the VC Schema ID.

#### 5.7.2. Response

**■ Process**
1. Multibase-decode `vcSchema`, parse the VC Schema, and validate mandatory items
1. Check whether a VC Schema with the same `@id` is already registered
1. Check the issuer DID
    - Check that it is a registered DID
    - Check that its Role is `ISSUER` or `TAS`
    - Check that its status is `ACTIVATED`
1. Store the VC Schema

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

| Code         | Description                                                                   |
| ------------ | ----------------------------------------------------------------------------- |
| SSRVLSS10003 | Failed to decode `vcSchema`, or VC Schema validation failed (e.g. missing mandatory items) |
| SSRVLSS11501 | The issuer DID is not registered                                              |
| SSRVLSS11502 | The Role of the issuer DID is neither `ISSUER` nor `TAS`                      |
| SSRVLSS11503 | The status of the issuer DID is not `ACTIVATED`                               |
| SSRVLSS12001 | JSON syntax error in the decoded VC Schema                                    |
| SSRVLSS12002 | A VC Schema with the same `@id` is already registered                         |

**■ Status 500 - Server error**

| Code         | Description                     |
| ------------ | ------------------------------- |
| SSRVLSS11001 | The request body cannot be read |
| SSRVLSS13502 | Failed to store the VC Schema   |

#### 5.7.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/lss/api/v1/vc-schema" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "did": "did:omn:issuer",
    "vcSchema": "meyJAaWQiOiJodHRwOi8vMTI3LjAuMC4xOjgwOTEvaXNzdWVyL2FwaS92MS92..."
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.8. Get VC Schema

Retrieves a VC Schema from the ledger.

| Item          | Description              | Remarks      |
| ------------- | ------------------------ | ------------ |
| Method        | `GET`                    |              |
| Path          | `/lss/api/v1/vc-schema`  |              |
| Authorization | `X-API-Key`              | Role: `READ` |

#### 5.8.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                                      |
| ------------- | ----------- | -------------------------------------------- |
| + `X-API-Key` | `{API Key}` | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name         | Description                          | Remarks |
| ------------ | ------------------------------------ | ------- |
| + `schemaId` | VC Schema ID (`@id` of the VC Schema) | M      |

**■ HTTP Body**

N/A

#### 5.8.2. Response

**■ Process**
1. Retrieve the VC Schema by `schemaId`
1. Serialize the stored VC Schema JSON as a JSON string value and return it

**■ Status 200 - Success**

- The response body is the VC Schema JSON serialized as a single JSON string value. That is, the whole body is wrapped in double quotes and the inner double quotes are escaped. Clients must parse the body as a JSON string and then parse the result again as the VC Schema JSON.
- The server does not set the response `Content-Type` explicitly, so it is determined by the request's `Accept` header.

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                              |
| ------------ | ---------------------------------------- |
| SSRVLSS12003 | No VC Schema exists for `schemaId`       |

#### 5.8.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/lss/api/v1/vc-schema?schemaId=http%3A%2F%2F127.0.0.1%3A8091%2Fissuer%2Fapi%2Fv1%2Fvc%2Fvcschema%3Fname%3Dmdl" \
  -H "X-API-Key: ${API_KEY}"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: text/plain;charset=UTF-8

"{\"@id\":\"http://127.0.0.1:8091/issuer/api/v1/vc/vcschema?name=mdl\",\"@schema\":\"https://opendid.org/schema/vc.osd\",\"title\":\"OpenDID Mobile Driver License\",\"description\":\"VC-formatted OpenDID Mobile License Driver.\",\"metadata\":{\"language\":\"ko\",\"formatVersion\":\"1.0\"},\"credentialSubject\":{\"claims\":[{\"namespace\":{\"id\":\"org.iso.18013.5\",\"name\":\"ISO/IEC 18013-5:2021 - Personal identification\",\"ref\":\"https://www.iso.org/standard/69084.html\"},\"items\":[{\"id\":\"family_name\",\"caption\":\"Family Name\",\"type\":\"text\",\"format\":\"plain\",\"hideValue\":false}]}]}}"
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.9. Register ZKP Credential Schema

Registers a ZKP Credential Schema to the ledger.

| Item          | Description                      | Remarks        |
| ------------- | -------------------------------- | -------------- |
| Method        | `POST`                           |                |
| Path          | `/lss/api/v1/credential-schema`  |                |
| Authorization | `X-API-Key`                      | Role: `ISSUER` |

#### 5.9.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name               | Type               | M/O | Description                                      |
| ------------------ | ------------------ | --- | ------------------------------------------------ |
| `credentialSchema` | string (multibase) | M   | Multibase-encoded ZKP Credential Schema JSON     |

**■ Credential Schema (decoded `credentialSchema`)**

| Name        | Type          | M/O | Description                                                                                                      |
| ----------- | ------------- | --- | ---------------------------------------------------------------------------------------------------------------- |
| `id`        | string        | M   | Credential Schema ID. The first three `:`-separated elements (`did:{method}:{identifier}`) are used as the issuer DID (e.g. `did:omn:issuer:2:mdl:1.0`) |
| `name`      | string        | M   | Schema name                                                                                                      |
| `version`   | string        | M   | Schema version                                                                                                   |
| `attrNames` | array(string) | M   | List of attribute names                                                                                          |
| `attrTypes` | array(object) | O   | List of attribute type definitions (`namespace`, `items`)                                                        |
| `tag`       | string        | M   | Tag                                                                                                              |

#### 5.9.2. Response

**■ Process**
1. Multibase-decode `credentialSchema` and parse the Credential Schema
1. Check mandatory items (`id`, `name`, `version`, `attrNames`, `tag`)
1. Extract the issuer DID from `id` (`id` must have at least four `:`-separated elements and start with `did`)
1. Check whether a Credential Schema with the same `id` is already registered
1. Check the issuer DID
    - Check that it is a registered DID
    - Check that its Role is `ISSUER`
    - Check that its status is `ACTIVATED`
1. Store the Credential Schema

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

| Code         | Description                                                                          |
| ------------ | ------------------------------------------------------------------------------------ |
| SSRVLSS10003 | Error while parsing, e.g. failed to decode `credentialSchema`                        |
| SSRVLSS11501 | The issuer DID extracted from `id` is not registered                                 |
| SSRVLSS11502 | The Role of the issuer DID is not `ISSUER`                                           |
| SSRVLSS11503 | The status of the issuer DID is not `ACTIVATED`                                      |
| SSRVLSS12500 | `id` is empty, has fewer than four `:`-separated elements, or does not start with `did` |
| SSRVLSS12501 | A Credential Schema with the same `id` is already registered                         |
| SSRVLSS12505 | JSON syntax error in the decoded Credential Schema                                   |

**■ Status 500 - Server error**

| Code         | Description                            |
| ------------ | -------------------------------------- |
| SSRVLSS11001 | The request body cannot be read        |
| SSRVLSS13502 | Failed to store the Credential Schema  |

#### 5.9.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/lss/api/v1/credential-schema" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "credentialSchema": "meyJpZCI6ImRpZDpvbW46aXNzdWVyOjI6emtwc2NoZW1hZXhhbXBsZToxLjAi..."
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.10. Get ZKP Credential Schema

Retrieves a ZKP Credential Schema from the ledger.

| Item          | Description                      | Remarks      |
| ------------- | -------------------------------- | ------------ |
| Method        | `GET`                            |              |
| Path          | `/lss/api/v1/credential-schema`  |              |
| Authorization | `X-API-Key`                      | Role: `READ` |

#### 5.10.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                                      |
| ------------- | ----------- | -------------------------------------------- |
| + `X-API-Key` | `{API Key}` | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name         | Description          | Remarks |
| ------------ | -------------------- | ------- |
| + `schemaId` | Credential Schema ID | M       |

**■ HTTP Body**

N/A

#### 5.10.2. Response

**■ Process**
1. Retrieve the Credential Schema by `schemaId`
1. Return the stored Credential Schema as JSON

**■ Status 200 - Success**

- `Content-Type: application/json`
- The response body is the Credential Schema JSON, with the same items as the Credential Schema in [5.9.1](#591-request).

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                                   |
| ------------ | --------------------------------------------- |
| SSRVLSS12502 | No Credential Schema exists for `schemaId`    |

#### 5.10.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/lss/api/v1/credential-schema?schemaId=did%3Aomn%3Aissuer%3A2%3Azkpschemaexample%3A1.0" \
  -H "X-API-Key: ${API_KEY}"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": "did:omn:issuer:2:zkpschemaexample:1.0",
  "name": "zkpschemaexample",
  "version": "1.0",
  "attrNames": [
    "zkpnamespace.birth",
    "zkpnamespace.city"
  ],
  "attrTypes": [
    {
      "namespace": {
        "id": "zkpnamespace",
        "name": "ZKP NameSpace"
      },
      "items": [
        {
          "label": "birth",
          "caption": "Birth(yyyymmdd)",
          "type": "Number"
        },
        {
          "label": "city",
          "caption": "City",
          "type": "String"
        }
      ]
    }
  ],
  "tag": "zkpschema"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.11. Register ZKP Credential Definition

Registers a ZKP Credential Definition to the ledger.

| Item          | Description                          | Remarks        |
| ------------- | ------------------------------------ | -------------- |
| Method        | `POST`                               |                |
| Path          | `/lss/api/v1/credential-definition`  |                |
| Authorization | `X-API-Key`                          | Role: `ISSUER` |

#### 5.11.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                                      |
| ---------------- | -------------------------------- | -------------------------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                                              |
| + `X-API-Key`    | `{API Key}`                      | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name                   | Type               | M/O | Description                                          |
| ---------------------- | ------------------ | --- | ---------------------------------------------------- |
| `credentialDefinition` | string (multibase) | M   | Multibase-encoded ZKP Credential Definition JSON     |

**■ Credential Definition (decoded `credentialDefinition`)**

| Name       | Type          | M/O | Description                                         |
| ---------- | ------------- | --- | --------------------------------------------------- |
| `id`       | string        | M   | Credential Definition ID                            |
| `schemaId` | string        | M   | ID of a ZKP Credential Schema registered in the LSS |
| `ver`      | string        | M   | Version                                             |
| `type`     | string (enum) | M   | Credential type. `CL`                               |
| `value`    | object        | M   | Public key value of the Credential Definition (`primary`) |
| `tag`      | string        | M   | Tag                                                 |

#### 5.11.2. Response

**■ Process**
1. Multibase-decode `credentialDefinition` and parse the Credential Definition
1. Check mandatory items (`id`, `schemaId`, `ver`, `type`, `value`, `tag`)
1. Check that the Credential Schema of `schemaId` is registered
1. Check whether a Credential Definition with the same `id` is already registered
1. Store the Credential Definition

**■ Status 200 - Success**

No response body

**■ Status 400 - Client error**

| Code         | Description                                                       |
| ------------ | ----------------------------------------------------------------- |
| SSRVLSS10003 | Error while parsing, e.g. failed to decode `credentialDefinition` |
| SSRVLSS12503 | A Credential Definition with the same `id` is already registered  |
| SSRVLSS12506 | JSON syntax error in the decoded Credential Definition            |

**■ Status 500 - Server error**

| Code         | Description                                                    |
| ------------ | -------------------------------------------------------------- |
| SSRVLSS11001 | The request body cannot be read                                |
| SSRVLSS12502 | The Credential Schema of `schemaId` is not registered          |
| SSRVLSS13502 | Failed to store the Credential Definition                      |

#### 5.11.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/lss/api/v1/credential-definition" \
  -H "Content-Type: application/json;charset=utf-8" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "credentialDefinition": "meyJpZCI6ImRpZDpvbW46aXNzdWVyOjI6emtwc2NoZW1hZXhhbXBsZToxLjA6..."
  }'
```

**■ Response**

```http
HTTP/1.1 200 OK
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.12. Get ZKP Credential Definition

Retrieves a ZKP Credential Definition from the ledger.

| Item          | Description                          | Remarks      |
| ------------- | ------------------------------------ | ------------ |
| Method        | `GET`                                |              |
| Path          | `/lss/api/v1/credential-definition`  |              |
| Authorization | `X-API-Key`                          | Role: `READ` |

#### 5.12.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                                      |
| ------------- | ----------- | -------------------------------------------- |
| + `X-API-Key` | `{API Key}` | Mandatory when API Key validation is enabled |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name             | Description              | Remarks |
| ---------------- | ------------------------ | ------- |
| + `definitionId` | Credential Definition ID | M       |

**■ HTTP Body**

N/A

#### 5.12.2. Response

**■ Process**
1. Retrieve the Credential Definition by `definitionId`
1. Return the stored Credential Definition as JSON

**■ Status 200 - Success**

- `Content-Type: application/json`
- The response body is the Credential Definition JSON, with the same items as the Credential Definition in [5.11.1](#5111-request).

**■ Status 400 - Client error**

| Code         | Description                                           |
| ------------ | ----------------------------------------------------- |
| SSRVLSS12504 | No Credential Definition exists for `definitionId`    |

**■ Status 500 - Server error**

N/A

#### 5.12.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/lss/api/v1/credential-definition?definitionId=did%3Aomn%3Aissuer%3A2%3Azkpschemaexample%3A1.0%3A3%3ACL%3Adid%3Aomn%3Aissuer%3A2%3Azkpschemaexample%3A1.0%3Atag" \
  -H "X-API-Key: ${API_KEY}"
```

**■ Response**

The values in `value.primary` are long and are partially omitted.

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": "did:omn:issuer:2:zkpschemaexample:1.0:3:CL:did:omn:issuer:2:zkpschemaexample:1.0:tag",
  "schemaId": "did:omn:issuer:2:zkpschemaexample:1.0",
  "ver": "1.0",
  "type": "CL",
  "value": {
    "primary": {
      "n": 9871533257...,
      "z": 2043958174...,
      "s": 6620148835...,
      "r": {...},
      "rctxt": 3358201746...
    }
  },
  "tag": "tag"
}
```
