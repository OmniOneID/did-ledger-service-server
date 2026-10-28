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

DID Ledger Service Error Code
==

- Date: 2026-10-27
- Version: v2.1.0

Table of Contents
---
<!-- TOC tocDepth:2..3 chapterDepth:2..6 -->

- [1. Overview](#1-overview)
- [2. Error Code Format](#2-error-code-format)
- [3. Error Code Categories](#3-error-code-categories)
- [4. Error Response Format](#4-error-response-format)
  - [4.1. Response Body](#41-response-body)
  - [4.2. Responses by Handling Path](#42-responses-by-handling-path)
  - [4.3. Notes on Error Handling](#43-notes-on-error-handling)
- [5. Error Code List](#5-error-code-list)
  - [5.1. General Errors (10000 ~ 10999)](#51-general-errors-10000--10999)
  - [5.2. API Processing Errors (11000 ~ 11499)](#52-api-processing-errors-11000--11499)
  - [5.3. DID Errors (11500 ~ 11999)](#53-did-errors-11500--11999)
  - [5.4. VC Errors (12000 ~ 12499)](#54-vc-errors-12000--12499)
  - [5.5. ZKP Errors (12500 ~ 12999)](#55-zkp-errors-12500--12999)
  - [5.6. Policy Errors (13000 ~ 13499)](#56-policy-errors-13000--13499)
  - [5.7. DB Errors (13500 ~ 13999)](#57-db-errors-13500--13999)
  - [5.8. Admin Errors (14000 ~ 14499)](#58-admin-errors-14000--14499)
  - [5.9. API Key Errors (14500 ~ 14999)](#59-api-key-errors-14500--14999)
  - [5.10. Server Config Errors (15000 ~ 15499)](#510-server-config-errors-15000--15499)
  - [5.11. Miscellaneous Errors (90000 ~ 99999)](#511-miscellaneous-errors-90000--99999)
- [6. Error Codes by Agent API](#6-error-codes-by-agent-api)
  - [6.1. API Key Authentication (Common)](#61-api-key-authentication-common)
  - [6.2. DID Document API](#62-did-document-api)
  - [6.3. VC Metadata API](#63-vc-metadata-api)
  - [6.4. VC Schema API](#64-vc-schema-api)
  - [6.5. Credential Schema API](#65-credential-schema-api)
  - [6.6. Credential Definition API](#66-credential-definition-api)

<!-- /TOC -->

## 1. Overview

This document defines the error codes returned by the DID Ledger Service Server (LSS). The error codes are defined in the `org.omnione.did.base.exception.ErrorCode` enum and represent failures that occur while processing the Agent API (`/lss/api/v1`), API key authentication, and the admin console API (`/lss/admin/v1`).

The error codes, HTTP statuses, and messages in this document are copied as-is from the values defined in the source code. Because the message is returned in the response body exactly as written in English, the tables show the original text, and the explanation is given in a separate column.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 2. Error Code Format

DID Ledger Service error codes consist of a fixed prefix and a 5-digit number.

**Format**: `SSRVLSS{Number}`

- **SSRVLSS**: 7-character fixed prefix. `SSRV` is the common error code prefix of Open DID servers (e.g., `SSRVTRA` for TAS, `SSRVISS` for Issuer), and `LSS` stands for Ledger Service Server.
- **Number**: 5-digit number. Each category is assigned a number range (see [3. Error Code Categories](#3-error-code-categories)), and within a category the numbers increase by 1 starting from the beginning of the range.

**Example**: `SSRVLSS11501` - DID not found (the second code in the DID error range 11500 ~ 11999)

> **Note**
> - The code value of `DECRYPTION_FAILED` is `SSRVLSS100015`. Its number has 6 digits, so it does not follow the format above. Number `10005` is unused in the sequence, but the server returns the defined value (`SSRVLSS100015`) as-is.
> - When request body validation (`@Valid`) fails, the code `9999` is returned without going through `ErrorCode`. This value does not follow the `SSRVLSS` format (see [4.2. Responses by Handling Path](#42-responses-by-handling-path)).

<div style="page-break-after: always; margin-top: 50px;"></div>

## 3. Error Code Categories

Categories and number ranges follow the sections defined in the comments of `ErrorCode.java`. The range 15500 ~ 89999 is not assigned.

| Category | Range         | Description                                          | Codes  |
| -------- | ------------- | ---------------------------------------------------- | ------ |
| 1        | 10000 ~ 10999 | General errors (encoding/decoding, encryption/decryption) | 6 |
| 2        | 11000 ~ 11499 | API processing errors (signature verification, request body parsing) | 2 |
| 3        | 11500 ~ 11999 | DID-related errors                                   | 7      |
| 4        | 12000 ~ 12499 | VC and VC Schema related errors                      | 5      |
| 5        | 12500 ~ 12999 | ZKP (Credential Schema/Definition) related errors    | 7      |
| 6        | 13000 ~ 13499 | Policy errors (DID status transitions, role policy)  | 3      |
| 7        | 13500 ~ 13999 | DB-related errors                                    | 5      |
| 8        | 14000 ~ 14499 | Admin-related errors (login, password, OTP)          | 10     |
| 9        | 14500 ~ 14999 | API key related errors (authentication, management)  | 7      |
| 10       | 15000 ~ 15499 | Server configuration related errors                  | 3      |
| 99       | 90000 ~ 99999 | Miscellaneous errors                                 | 1      |
|          |               | **Total**                                            | **56** |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 4. Error Response Format

### 4.1. Response Body

The error response body is serialized from `org.omnione.did.base.response.ErrorResponse`.

| Field       | Type   | Description                                                  |
| ----------- | ------ | ------------------------------------------------------------ |
| code        | String | Error code (e.g., `SSRVLSS11501`)                            |
| description | String | Error message. The original English message defined in `ErrorCode` |

```json
{
  "code": "SSRVLSS11501",
  "description": "DID not found"
}
```

- The HTTP status code is the HTTP status defined in `ErrorCode`.
- `description` always contains only the `ErrorCode` message. Additional text that the server appends with the `OpenDidException(ErrorCode, String)` constructor stays in the exception message (logs) and is not included in the response.

### 4.2. Responses by Handling Path

Errors are converted into responses by `GlobalControllerAdvice` (`@RestControllerAdvice`, target package `org.omnione.did`) and `ApiKeyValidationInterceptor`. The response depends on the handling path as follows.

| Situation                                                       | Handled by                    | HTTP Status                    | code                                   | description                                       |
| --------------------------------------------------------------- | ----------------------------- | ------------------------------ | -------------------------------------- | ------------------------------------------------- |
| `OpenDidException` thrown by service logic                      | `GlobalControllerAdvice`      | HTTP status of the `ErrorCode` | Code of the `ErrorCode`                | Message of the `ErrorCode`                        |
| Request body cannot be read (`HttpMessageNotReadableException`) | `GlobalControllerAdvice`      | 500                            | `SSRVLSS11001`                         | `""` (empty string)                               |
| Request body validation failed (`MethodArgumentNotValidException`) | `GlobalControllerAdvice`   | 500                            | `9999`                                 | Field validation messages joined with `"; "`      |
| API key validation failed                                       | `ApiKeyValidationInterceptor` | HTTP status of the `ErrorCode` (401/403/500) | `SSRVLSS14502` ~ `SSRVLSS14506` | Message of the `ErrorCode`                  |
| Invalid admin JWT (`Authorization: Bearer`)                     | `JwtAuthenticationFilter`     | 401                            | None                                   | None (no response body)                           |
| Any other exception                                             | Spring Boot default error handling | Mostly 500                | None                                   | None (Spring Boot default error body)             |

**■ Example Response for Request Body Validation Failure**

```json
{
  "code": "9999",
  "description": "<field validation message 1>; <field validation message 2>"
}
```

**■ Exceptions Not Converted to Error Codes**

`GlobalControllerAdvice` handles only `OpenDidException`, `HttpMessageNotReadableException`, and `MethodArgumentNotValidException`. Any other exception is returned as a Spring Boot default error response, not in the LSS format (`code`/`description`). Typical cases in the current code are:

- No VC Metadata exists for the `vcId` when retrieving or updating the status of VC Metadata through the Agent API (`IllegalArgumentException`)
- Multibase decoding or parsing of the requested DID Document fails during DID Document registration
- The `versionId` value of a DID URL is not a number
- Unhandled DB exceptions such as DB constraint violations

Other authorization failures of the admin API (`/lss/admin/**`), such as a missing token, also follow the default Spring Security behavior and do not return an LSS error code.

### 4.3. Notes on Error Handling

1. **Branch on `code`, not on the HTTP status**: Some errors caused by the request content return HTTP 500 (`SSRVLSS11001`, `9999`, `SSRVLSS12003`, `SSRVLSS12502`). Also, when a target cannot be found, 400 or 500 is returned instead of 404.
2. **Retry only on transient failures**: 5xx errors caused by request data, such as those in item 1, return the same result when the same request is sent again.
3. **API key validation depends on server configuration**: Validation runs only when the `API_KEY_VALIDATION_ENABLED` value in the `server_config` table is one of `true`, `1`, `Y`, or `yes`. When it is disabled, `SSRVLSS14502` ~ `SSRVLSS14506` are not returned. The API key is sent in the `X-API-Key` header (or the `API-Key` header if `X-API-Key` is absent).
4. **Error codes in the API log**: For APIs annotated with `@ArticleLog`, the error code is recorded in the API log, and this value is set by `GlobalControllerAdvice`. A request body validation failure is recorded as `REQUEST_VALIDATION_ERROR`, unlike the response code (`9999`).

<div style="page-break-after: always; margin-top: 50px;"></div>

## 5. Error Code List

The "Message" column of each table is the original text returned in the `description` of the response. Codes marked "Not currently thrown" are defined but not returned by the current code.

### 5.1. General Errors (10000 ~ 10999)

| Error Code    | Constant          | HTTP Status | Message                  | Description                                                                                            |
| ------------- | ----------------- | ----------- | ------------------------ | ------------------------------------------------------------------------------------------------------ |
| SSRVLSS10000  | CLIENT_ERROR      | 400         | Client Error             | Generic client error. Not currently thrown                                                            |
| SSRVLSS10001  | SERVER_ERROR      | 500         | Server Error             | Generic server error. Not currently thrown                                                            |
| SSRVLSS10002  | ENCODING_FAILED   | 500         | Failed to encoding data. | Data encoding failed. Not currently thrown                                                            |
| SSRVLSS10003  | DECODING_FAILED   | 400         | Failed to decoding data. | An unexpected error occurred while decoding or parsing request data when registering a VC Schema, Credential Schema, or Credential Definition |
| SSRVLSS10004  | ENCRYPTION_FAILED | 500         | Failed to encrypt data.  | Data encryption failed. Not currently thrown                                                          |
| SSRVLSS100015 | DECRYPTION_FAILED | 400         | Failed to decrypt data.  | Data decryption failed. The code number is defined with 6 digits (see [2. Error Code Format](#2-error-code-format)). Not currently thrown |

### 5.2. API Processing Errors (11000 ~ 11499)

| Error Code   | Constant                | HTTP Status | Message               | Description                                                                                           |
| ------------ | ----------------------- | ----------- | --------------------- | ----------------------------------------------------------------------------------------------------- |
| SSRVLSS11000 | VERIFY_SIGN_FAIL        | 400         | Verify Signature Fail | Signature (proof) verification of a DID Document registration request failed                         |
| SSRVLSS11001 | REQUEST_BODY_UNREADABLE | 500         | `""` (empty string)   | The request body cannot be read or deserialized (malformed JSON, invalid enum value, etc.). Because the message is an empty string, `description` is also returned as an empty string |

### 5.3. DID Errors (11500 ~ 11999)

| Error Code   | Constant                 | HTTP Status | Message                                                 | Description                                                                                                   |
| ------------ | ------------------------ | ----------- | ------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| SSRVLSS11500 | DID_DOC_VERSION_MISMATCH | 400         | DidDoc version mismatch                                 | When registering a DID Document for an already registered DID, `versionId` is not the last version + 1      |
| SSRVLSS11501 | DID_NOT_FOUND            | 400         | DID not found                                           | The requested DID cannot be extracted or is not registered (DID Document retrieval and status update, controller DID, issuer DID of a VC Schema or Credential Schema) |
| SSRVLSS11502 | ROLE_TYPE_MISMATCH       | 400         | Role type mismatch                                      | The role of the issuer DID differs from the required role (VC Schema: ISSUER or TAS, Credential Schema: ISSUER) |
| SSRVLSS11503 | DID_NOT_ACTIVATED        | 400         | DID not activated                                       | The status of the issuer DID is not ACTIVATED                                                                 |
| SSRVLSS11504 | DID_DOC_NOT_FOUND        | 400         | Failed to find DID Document: DID Document not found     | The DID Document for the DID and version cannot be found. Also returned when the admin console looks up a DID by ID or by DID and the DID does not exist |
| SSRVLSS11505 | ROLE_DID_NOT_FOUND       | 400         | Failed to find DID: DID not found by Role               | DID lookup by role failed. Not currently thrown because the method that throws it (`DidQueryServiceImpl.findDidFirstByRole`) is never called |
| SSRVLSS11506 | TA_DID_DOC_NOT_FOUND     | 400         | Failed to find DID Document: TA DID Document not found  | TA DID Document lookup failed. Not currently thrown                                                           |

### 5.4. VC Errors (12000 ~ 12499)

| Error Code   | Constant                     | HTTP Status | Message                                                   | Description                                                                                             |
| ------------ | ---------------------------- | ----------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| SSRVLSS12000 | REVOKED_VC_CANNOT_UPDATE     | 400         | A revoked VC cannot be updated.                           | Request to change the status of a revoked (REVOKED) VC                                                  |
| SSRVLSS12001 | INVALID_VC_SCHEMA            | 400         | Invalid VC Schema.                                        | JSON syntax error in the decoded VC Schema |
| SSRVLSS12002 | VC_SCHEMA_ALREADY_REGISTERED | 400         | Failed to register VC Schema: VC Schema already exists.   | A VC Schema with the same Schema ID is already registered                                               |
| SSRVLSS12003 | VC_SCHEMA_NOT_FOUND          | 500         | VC Schema not found.                                      | The VC Schema cannot be found (Agent retrieval, admin console retrieval). This code is also returned when the admin console looks up VC Metadata by ID or VC ID and the VC Metadata does not exist |
| SSRVLSS12004 | VC_STATUS_HISTORY_NOT_FOUND  | 400         | VC Status History not found.                              | No history exists when the admin console looks up a VC status history entry by ID                      |

### 5.5. ZKP Errors (12500 ~ 12999)

| Error Code   | Constant                             | HTTP Status | Message                                                                          | Description                                                                                   |
| ------------ | ------------------------------------ | ----------- | -------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| SSRVLSS12500 | INVALID_CREDENTIAL_SCHEMA_ID         | 400         | Invalid Credential Schema ID.                                                    | Invalid Credential Schema ID format (empty, fewer than 4 parts when split by `:`, or does not start with `did`) |
| SSRVLSS12501 | CREDENTIAL_SCHEMA_ALREADY_REGISTERED | 400         | Failed to register Credential Schema: Credential Schema already exists.          | A Credential Schema with the same ID is already registered                                    |
| SSRVLSS12502 | CREDENTIAL_SCHEMA_NOT_FOUND          | 500         | Credential Schema not found.                                                     | The Credential Schema cannot be found. Also returned when the Credential Schema referenced during Credential Definition registration does not exist |
| SSRVLSS12503 | CREDENTIAL_DEFINITION_ALREADY_EXISTS | 400         | Failed to register Credential Definition: Credential Definition already exists.  | A Credential Definition with the same ID is already registered                                |
| SSRVLSS12504 | CREDENTIAL_DEFINITION_NOT_FOUND      | 400         | Credential Definition not found.                                                 | The Credential Definition cannot be found                                                     |
| SSRVLSS12505 | INVALID_CREDENTIAL_SCHEMA            | 400         | Invalid Credential Schema.                                                       | JSON syntax error in the decoded Credential Schema |
| SSRVLSS12506 | INVALID_CREDENTIAL_DEFINITION        | 400         | Invalid Credential Definition.                                                   | Credential Definition JSON syntax error                                                       |

### 5.6. Policy Errors (13000 ~ 13499)

| Error Code   | Constant                         | HTTP Status | Message                                                  | Description                                                              |
| ------------ | -------------------------------- | ----------- | -------------------------------------------------------- | ------------------------------------------------------------------------ |
| SSRVLSS13000 | TERMINATED_STATUS_CAN_NOT_CHANGE | 400         | Terminated DIDs can't change their status.               | Request to change the status of a DID in TERMINATED status               |
| SSRVLSS13001 | REVOKED_STATUS_CAN_NOT_CHANGE    | 400         | Revoked DIDs can't change their status to (De)Activate.  | Request to change a DID in REVOKED status to a status other than TERMINATED |
| SSRVLSS13002 | DID_ROLE_MISMATCH_TA             | 400         | The DID's Role is not TA.                                | The role of the controller DID in a DID Document registration request is not TAS |

### 5.7. DB Errors (13500 ~ 13999)

| Error Code   | Constant            | HTTP Status | Message                    | Description                                                                     |
| ------------ | ------------------- | ----------- | -------------------------- | ------------------------------------------------------------------------------- |
| SSRVLSS13500 | DB_CONNECTION_ERROR | 500         | Database connection error. | DB connection error. Not currently thrown                                       |
| SSRVLSS13501 | DB_QUERY_ERROR      | 500         | Database query error.      | DB query error. Not currently thrown                                            |
| SSRVLSS13502 | DB_INSERT_ERROR     | 500         | Database insert error.     | An error occurred while saving a VC Schema, Credential Schema, or Credential Definition |
| SSRVLSS13503 | DB_UPDATE_ERROR     | 500         | Database update error.     | DB update error. Not currently thrown                                           |
| SSRVLSS13504 | DB_DELETE_ERROR     | 500         | Database delete error.     | DB delete error. Not currently thrown                                           |

### 5.8. Admin Errors (14000 ~ 14499)

| Error Code   | Constant                           | HTTP Status | Message                                                         | Description                                                                                                 |
| ------------ | ---------------------------------- | ----------- | --------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| SSRVLSS14000 | ADMIN_INFO_NOT_FOUND               | 400         | Admin not found.                                                | The admin account does not exist or the password does not match (login, password change, login ID and password change, admin lookup) |
| SSRVLSS14001 | ADMIN_ALREADY_EXISTS               | 400         | Failed to register admin: admin is already registered.          | The same login ID already exists when registering an admin or changing a login ID                           |
| SSRVLSS14002 | ADMIN_PASSWORD_POLICY_NOT_FOUND    | 500         | Failed to find admin password policy: policy is not registered. | No admin password policy is registered                                                                      |
| SSRVLSS14003 | DAILY_OTP_SEND_LIMIT_EXCEEDED      | 400         | Daily OTP limit exceeded.                                       | The daily send limit of the password reset OTP has been exceeded, or sending is blocked                     |
| SSRVLSS14004 | OTP_COOLDOWN_NOT_EXPIRED           | 400         | Please wait before requesting another OTP.                      | The waiting period before requesting another OTP has not yet passed                                         |
| SSRVLSS14005 | INVALID_OTP                        | 400         | Invalid OTP code.                                               | The OTP format is invalid, no valid (unused and unexpired) OTP exists, or the OTP does not match            |
| SSRVLSS14006 | OTP_EXPIRED                        | 400         | OTP has expired. Please request a new one.                      | OTP expired. Not currently thrown because an expired OTP is handled as `INVALID_OTP`                        |
| SSRVLSS14007 | OTP_VERIFY_LIMIT_EXCEEDED          | 400         | Too many verification attempts.                                 | The number of OTP verification attempts has been exceeded                                                   |
| SSRVLSS14008 | ADMIN_NOT_FOUND_FOR_PASSWORD_RESET | 400         | Admin not found.                                                | No admin exists for the login ID when sending or verifying an OTP, or when checking, unblocking, or resetting the OTP send limit. The message is the same as `ADMIN_INFO_NOT_FOUND`, so distinguish them by `code` |
| SSRVLSS14009 | EMAIL_SEND_FAILED                  | 500         | Failed to send email.                                           | An `OpenDidException` occurred while sending the password reset OTP email                                   |

### 5.9. API Key Errors (14500 ~ 14999)

| Error Code   | Constant                        | HTTP Status | Message                                                   | Description                                                                                  |
| ------------ | ------------------------------- | ----------- | --------------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| SSRVLSS14500 | API_KEY_NOT_FOUND               | 400         | API Key not found.                                        | The API key cannot be found in the admin console. Also returned when renewal of a deactivated API key is requested |
| SSRVLSS14501 | API_KEY_GENERATION_FAILED       | 500         | Failed to generate unique API key after maximum attempts. | A unique API key could not be generated within the maximum number of attempts, or generating the API key hash failed |
| SSRVLSS14502 | API_KEY_MISSING                 | 401         | API Key is missing in request header.                     | The Agent API request has no `X-API-Key`/`API-Key` header, or its value is empty             |
| SSRVLSS14503 | API_KEY_INVALID                 | 401         | Invalid API key.                                          | The API key is not registered or is deactivated                                              |
| SSRVLSS14504 | API_KEY_INSUFFICIENT_PERMISSION | 403         | Insufficient permissions for this API.                    | The role of the API key does not meet the permission required by the API (see [6.1. API Key Authentication (Common)](#61-api-key-authentication-common)) |
| SSRVLSS14505 | API_KEY_VALIDATION_ERROR        | 500         | API key validation failed.                                | An `OpenDidException` occurred during API key validation                                     |
| SSRVLSS14506 | API_KEY_EXPIRED                 | 401         | API key has expired.                                      | The expiry date (`expires_at`) of the API key has passed. It can be used again after renewal in the Admin Console |

### 5.10. Server Config Errors (15000 ~ 15499)

| Error Code   | Constant                    | HTTP Status | Message                                 | Description                                                              |
| ------------ | --------------------------- | ----------- | --------------------------------------- | ------------------------------------------------------------------------ |
| SSRVLSS15000 | SERVER_CONFIG_NOT_FOUND     | 400         | Server configuration not found.         | Server configuration lookup failed. Not currently thrown                 |
| SSRVLSS15001 | SERVER_CONFIG_KEY_NOT_FOUND | 400         | Server configuration key not found.     | The admin console retrieved or updated a configuration key that does not exist |
| SSRVLSS15002 | SERVER_CONFIG_UPDATE_FAILED | 500         | Failed to update server configuration.  | Server configuration update failed. Not currently thrown                 |

### 5.11. Miscellaneous Errors (90000 ~ 99999)

| Error Code   | Constant | HTTP Status | Message | Description                                                                                                                |
| ------------ | -------- | ----------- | ------- | -------------------------------------------------------------------------------------------------------------------------- |
| SSRVLSS99999 | TODO     | 500         | TODO.   | Temporary code for errors without a dedicated code. Thrown when hash generation (`BaseDigestUtil.generateHash`) fails, and may be returned during the signature verification step of DID Document registration |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 6. Error Codes by Agent API

This section lists the error codes that each Agent API (base path `/lss/api/v1`) can return. It is based on the service implementations that run when the `sample` profile is not active (the `*Impl` classes in `repository/v1/agent/service`) and on the common handling in the `base` package. The service implementations of the `sample` profile do not return error codes.

The API key authentication errors in [6.1](#61-api-key-authentication-common) apply to all Agent APIs, and POST/PUT APIs that take a request body return `SSRVLSS11001` (REQUEST_BODY_UNREADABLE) when the body cannot be read. Each table below lists only the API-specific errors, excluding these common errors. For exceptions that are not converted to LSS error codes, see [4.2. Responses by Handling Path](#42-responses-by-handling-path).

### 6.1. API Key Authentication (Common)

Every Agent API is annotated with `@ApiKeyRequired`, so when API key validation is enabled, `ApiKeyValidationInterceptor` validates the API key before the request reaches the controller. API key roles are ranked TAS > ISSUER > READ, and a higher role can call APIs that require a lower role.

| API                                                                                                                     | Required Permission | Allowed API Key Roles |
| ----------------------------------------------------------------------------------------------------------------------- | ------------------- | --------------------- |
| POST `/did-doc`, PUT `/did-doc`                                                                                         | TAS                 | TAS                   |
| POST `/vc-meta`, PUT `/vc-meta`, POST `/vc-schema`, POST `/credential-schema`, POST `/credential-definition`            | ISSUER              | TAS, ISSUER           |
| GET `/did-doc`, GET `/vc-meta`, GET `/vc-schema`, GET `/credential-schema`, GET `/credential-definition`                | READ                | TAS, ISSUER, READ     |

| Error Code   | Constant                        | HTTP Status | Condition                                                         |
| ------------ | ------------------------------- | ----------- | ----------------------------------------------------------------- |
| SSRVLSS14502 | API_KEY_MISSING                 | 401         | No `X-API-Key`/`API-Key` header, or its value is empty            |
| SSRVLSS14503 | API_KEY_INVALID                 | 401         | The API key is not registered or is deactivated                   |
| SSRVLSS14504 | API_KEY_INSUFFICIENT_PERMISSION | 403         | The API key role is not among the allowed roles in the table above |
| SSRVLSS14505 | API_KEY_VALIDATION_ERROR        | 500         | An `OpenDidException` occurred during validation                  |
| SSRVLSS14506 | API_KEY_EXPIRED                 | 401         | The expiry date (`expires_at`) of the API key has passed          |

### 6.2. DID Document API

Path: `/lss/api/v1/did-doc`

| Method | Error Code   | Constant                         | HTTP Status | Condition                                                                                     |
| ------ | ------------ | -------------------------------- | ----------- | --------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS11501 | DID_NOT_FOUND                    | 400         | The controller DID used for signature verification is not registered (verified only when at least one DID exists in the ledger) |
| POST   | SSRVLSS13002 | DID_ROLE_MISMATCH_TA             | 400         | The role of the controller DID is not TAS                                                     |
| POST   | SSRVLSS11504 | DID_DOC_NOT_FOUND                | 400         | The DID Document of the controller DID cannot be found                                        |
| POST   | SSRVLSS99999 | TODO                             | 500         | Hash generation for signature verification failed                                             |
| POST   | SSRVLSS11000 | VERIFY_SIGN_FAIL                 | 400         | Signature verification of the request proof failed                                            |
| POST   | SSRVLSS11500 | DID_DOC_VERSION_MISMATCH         | 400         | The DID is already registered and the `versionId` of the new DID Document is not the last version + 1 |
| GET    | SSRVLSS11501 | DID_NOT_FOUND                    | 400         | The DID cannot be extracted from the `did` parameter, or the DID is not registered            |
| GET    | SSRVLSS11504 | DID_DOC_NOT_FOUND                | 400         | No DID Document exists for the requested version (the current version if none is given). For REVOKED/TERMINATED status, the lookup is done on revoked documents |
| PUT    | SSRVLSS11501 | DID_NOT_FOUND                    | 400         | The DID cannot be extracted from the request `did`, or the DID is not registered              |
| PUT    | SSRVLSS13000 | TERMINATED_STATUS_CAN_NOT_CHANGE | 400         | The current status is TERMINATED                                                              |
| PUT    | SSRVLSS13001 | REVOKED_STATUS_CAN_NOT_CHANGE    | 400         | The current status is REVOKED and the target status is not TERMINATED                         |
| PUT    | SSRVLSS11504 | DID_DOC_NOT_FOUND                | 400         | No DID Document exists for the version when changing to ACTIVATED or DEACTIVATED              |

### 6.3. VC Metadata API

Path: `/lss/api/v1/vc-meta`

| Method | Error Code   | Constant                 | HTTP Status | Condition                                                                      |
| ------ | ------------ | ------------------------ | ----------- | ------------------------------------------------------------------------------ |
| POST   | 9999         | -                        | 500         | The request body (`VcMeta`) violates the Bean Validation constraints declared on the model (`@Valid`) |
| PUT    | SSRVLSS12000 | REVOKED_VC_CANNOT_UPDATE | 400         | Request to change the status of a VC that is already REVOKED                   |

For GET and PUT, if no VC Metadata exists for the `vcId`, an `IllegalArgumentException` is thrown and a Spring Boot default error response is returned instead of an LSS error code. Apart from this case, GET has no API-specific error codes.

### 6.4. VC Schema API

Path: `/lss/api/v1/vc-schema`

| Method | Error Code   | Constant                     | HTTP Status | Condition                                                                                                       |
| ------ | ------------ | ---------------------------- | ----------- | --------------------------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS12001 | INVALID_VC_SCHEMA            | 400         | JSON syntax error in the decoded VC Schema |
| POST   | SSRVLSS10003 | DECODING_FAILED              | 400         | A Multibase decoding failure of the VC Schema, or a data model constraint validation failure (such as a missing required field: id, schema, title, description, metadata (language, formatVersion), credentialSubject) |
| POST   | SSRVLSS12002 | VC_SCHEMA_ALREADY_REGISTERED | 400         | A VC Schema with the same Schema ID is already registered                                                       |
| POST   | SSRVLSS11501 | DID_NOT_FOUND                | 400         | The request `did` (issuer DID) is not registered                                                                |
| POST   | SSRVLSS11502 | ROLE_TYPE_MISMATCH           | 400         | The role of the issuer DID is not ISSUER or TAS                                                                 |
| POST   | SSRVLSS11503 | DID_NOT_ACTIVATED            | 400         | The status of the issuer DID is not ACTIVATED                                                                   |
| POST   | SSRVLSS13502 | DB_INSERT_ERROR              | 500         | An error occurred while saving the VC Schema                                                                    |
| GET    | SSRVLSS12003 | VC_SCHEMA_NOT_FOUND          | 500         | No VC Schema exists for the `schemaId`                                                                          |

### 6.5. Credential Schema API

Path: `/lss/api/v1/credential-schema`

| Method | Error Code   | Constant                             | HTTP Status | Condition                                                                                     |
| ------ | ------------ | ------------------------------------ | ----------- | --------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS12505 | INVALID_CREDENTIAL_SCHEMA            | 400         | JSON syntax error in the decoded Credential Schema |
| POST   | SSRVLSS10003 | DECODING_FAILED                      | 400         | An unexpected error during parsing, such as a Multibase decoding failure of the Credential Schema |
| POST   | SSRVLSS12500 | INVALID_CREDENTIAL_SCHEMA_ID         | 400         | The Credential Schema ID is empty, has fewer than 4 parts when split by `:`, or does not start with `did` |
| POST   | SSRVLSS12501 | CREDENTIAL_SCHEMA_ALREADY_REGISTERED | 400         | A Credential Schema with the same ID is already registered                                    |
| POST   | SSRVLSS11501 | DID_NOT_FOUND                        | 400         | The issuer DID (`did:<method>:<identifier>`) extracted from the Credential Schema ID is not registered |
| POST   | SSRVLSS11502 | ROLE_TYPE_MISMATCH                   | 400         | The role of the issuer DID is not ISSUER (unlike VC Schema, TAS is not allowed either)        |
| POST   | SSRVLSS11503 | DID_NOT_ACTIVATED                    | 400         | The status of the issuer DID is not ACTIVATED                                                 |
| POST   | SSRVLSS13502 | DB_INSERT_ERROR                      | 500         | An error occurred while saving the Credential Schema                                          |
| GET    | SSRVLSS12502 | CREDENTIAL_SCHEMA_NOT_FOUND          | 500         | No Credential Schema exists for the `schemaId`                                                |

### 6.6. Credential Definition API

Path: `/lss/api/v1/credential-definition`

| Method | Error Code   | Constant                             | HTTP Status | Condition                                                                                   |
| ------ | ------------ | ------------------------------------ | ----------- | ------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS12506 | INVALID_CREDENTIAL_DEFINITION        | 400         | JSON syntax error in the decoded Credential Definition                                      |
| POST   | SSRVLSS10003 | DECODING_FAILED                      | 400         | An unexpected error during parsing, such as a Multibase decoding failure of the Credential Definition |
| POST   | SSRVLSS12502 | CREDENTIAL_SCHEMA_NOT_FOUND          | 500         | The Credential Schema referenced by `schemaId` is not registered                            |

> When a required field is missing: the service's required-field check throws a `NullPointerException` while putting a null value into `Map.of`, so a response that is not an LSS error code (HTTP 500) is returned instead of `SSRVLSS12505`. Refer to [4.2. Responses by Handling Path](#42-responses-by-handling-path).
| POST   | SSRVLSS12503 | CREDENTIAL_DEFINITION_ALREADY_EXISTS | 400         | A Credential Definition with the same ID is already registered                              |
| POST   | SSRVLSS13502 | DB_INSERT_ERROR                      | 500         | An error occurred while saving the Credential Definition                                    |
| GET    | SSRVLSS12504 | CREDENTIAL_DEFINITION_NOT_FOUND      | 400         | No Credential Definition exists for the `definitionId`                                      |

> When a required field is missing: the service's required-field check throws a `NullPointerException` while putting a null value into `Map.of`, so a response that is not an LSS error code (HTTP 500) is returned instead of `SSRVLSS12505`. Refer to [4.2. Responses by Handling Path](#42-responses-by-handling-path).
