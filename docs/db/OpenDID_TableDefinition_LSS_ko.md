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

Open DID Ledger Service 데이터베이스 테이블 정의
==

- Date: 2026-11-06
- Version: v3.0.0

| 버전 | 일자 | 변경 내용 |
| ---- | ---- | ---- |
| v3.0.0 | 2026-11-06 | 최초 작성 |

목차
--
- [1. 개요](#1-개요)
- [2. 테이블 정의](#2-테이블-정의)
    - [2.1. DID](#21-did)
    - [2.2. DID\_DOCUMENT](#22-did_document)
    - [2.3. DID\_DOCUMENT\_REVOKED](#23-did_document_revoked)
    - [2.4. DID\_DOCUMENT\_STATUS\_HISTORY](#24-did_document_status_history)
    - [2.5. VC\_METADATA](#25-vc_metadata)
    - [2.6. VC\_STATUS\_HISTORY](#26-vc_status_history)
    - [2.7. VC\_SCHEMA](#27-vc_schema)
    - [2.8. ZKP\_CREDENTIAL\_SCHEMA](#28-zkp_credential_schema)
    - [2.9. ZKP\_CREDENTIAL\_DEFINITION](#29-zkp_credential_definition)
    - [2.10. API\_KEY](#210-api_key)
    - [2.11. SERVER\_CONFIG](#211-server_config)
    - [2.12. ADMIN](#212-admin)
    - [2.13. ADMIN\_PASSWORD\_POLICY](#213-admin_password_policy)
    - [2.14. ADMIN\_PASSWORD\_RESET\_TOKEN](#214-admin_password_reset_token)
    - [2.15. ADMIN\_OTP\_SEND\_LIMIT](#215-admin_otp_send_limit)
    - [2.16. API\_LOG](#216-api_log)

## 1. 개요

이 문서는 Ledger Service 서버에서 사용하는 데이터베이스 테이블의 구조를 정의합니다.

## 2. 테이블 정의

### 2.1. DID

DID 정보를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | did | VARCHAR | 100 | NO | N/A | DID |
|  | version | SMALLINT |  | NO | N/A | current DID Document version |
|  | role | VARCHAR | 30 | YES | N/A | role of the DID owner |
|  | status | VARCHAR | 20 | NO | N/A | DID status (ACTIVATED, DEACTIVATED, REVOKED, TERMINATED) |
|  | terminated_time | TIMESTAMP |  | YES | N/A | terminated date |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.2. DID_DOCUMENT

DID Document를 버전별로 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | did_id | BIGINT |  | NO | N/A | did id |
|  | document | TEXT |  | NO | N/A | DID Document (JSON format) |
|  | controller | VARCHAR | 100 | NO | N/A | controller DID |
|  | version | SMALLINT |  | NO | N/A | DID Document version |
|  | deactivated | BOOLEAN |  | NO | N/A | whether the document is deactivated |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.3. DID_DOCUMENT_REVOKED

폐기된 DID Document를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | did_id | BIGINT |  | NO | N/A | did id |
|  | document | TEXT |  | NO | N/A | DID Document (JSON format) |
|  | controller | VARCHAR | 100 | NO | N/A | controller DID |
|  | version | SMALLINT |  | NO | N/A | DID Document version |
|  | deactivated | BOOLEAN |  | NO | N/A | whether the document is deactivated |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |
|  | revoked_at | TIMESTAMP |  | NO | N/A | revoked date |

### 2.4. DID_DOCUMENT_STATUS_HISTORY

DID 상태 변경 이력을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | did_id | BIGINT |  | NO | N/A | did id |
|  | from_status | VARCHAR | 20 | YES | N/A | status before the change |
|  | to_status | VARCHAR | 20 | NO | N/A | status after the change |
|  | version | SMALLINT |  | NO | N/A | DID Document version |
|  | reason | TEXT |  | YES | N/A | reason for the change |
|  | changed_at | TIMESTAMP |  | NO | now() | changed date |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.5. VC_METADATA

VC Metadata를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | vc_id | VARCHAR | 100 | NO | N/A | VC identifier |
|  | issuer_did | VARCHAR | 100 | NO | N/A | issuer DID |
|  | subject_did | VARCHAR | 100 | NO | N/A | holder DID |
|  | vc_schema | VARCHAR | 200 | NO | N/A | VC Schema identifier |
|  | metadata | TEXT |  | NO | N/A | VC Metadata (JSON format) |
|  | format_version | VARCHAR | 10 | YES | N/A | format version |
|  | language | VARCHAR | 10 | YES | N/A | language |
|  | status | VARCHAR | 20 | NO | N/A | VC status (ACTIVE, INACTIVE, REVOKED, EXPIRED) |
|  | issuance_date | VARCHAR | 20 | NO | N/A | issuance date |
|  | valid_from | VARCHAR | 20 | NO | N/A | start of validity |
|  | valid_until | VARCHAR | 20 | NO | N/A | end of validity |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.6. VC_STATUS_HISTORY

VC 상태 변경 이력을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | vc_id | VARCHAR | 100 | NO | N/A | VC identifier |
|  | from_status | VARCHAR | 20 | YES | N/A | status before the change |
|  | to_status | VARCHAR | 20 | NO | N/A | status after the change |
|  | changed_at | TIMESTAMP |  | NO | now() | changed date |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.7. VC_SCHEMA

VC Schema를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | schema_id | VARCHAR | 100 | NO | N/A | VC Schema identifier |
|  | title | VARCHAR | 100 | NO | N/A | schema title |
|  | version | VARCHAR | 20 | NO | N/A | schema version |
|  | description | VARCHAR | 200 | NO | N/A | schema description |
|  | schema | TEXT |  | NO | N/A | VC Schema (JSON format) |
|  | did | VARCHAR | 100 | NO | N/A | publisher DID |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.8. ZKP_CREDENTIAL_SCHEMA

ZKP Credential Schema를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | schema_id | VARCHAR | 100 | NO | N/A | Credential Schema identifier |
|  | name | VARCHAR | 100 | NO | N/A | schema name |
|  | version | VARCHAR | 20 | NO | N/A | schema version |
|  | attr_names | TEXT |  | NO | N/A | attribute names |
|  | attr_types | TEXT |  | NO | N/A | attribute types |
|  | tag | VARCHAR | 100 | NO | N/A | tag |
|  | schema | TEXT |  | NO | N/A | Credential Schema (JSON format) |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.9. ZKP_CREDENTIAL_DEFINITION

ZKP Credential Definition을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | definition_id | VARCHAR | 100 | NO | N/A | Credential Definition identifier |
|  | schema_id | VARCHAR | 100 | NO | N/A | Credential Schema identifier |
|  | version | VARCHAR | 20 | NO | N/A | version |
|  | type | VARCHAR | 50 | NO | N/A | credential type |
|  | value | TEXT |  | NO | N/A | definition value |
|  | tag | VARCHAR | 100 | NO | N/A | tag |
|  | definition | TEXT |  | NO | N/A | Credential Definition (JSON format) |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |
|  | zkp_credential_schema_id | BIGINT |  | NO | N/A | zkp_credential_schema id |

### 2.10. API_KEY

Agent API 호출에 쓰는 API Key를 저장합니다. 평문은 저장하지 않습니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | api_key | TEXT |  | NO | N/A | SHA-256 hash of the API key |
|  | masked_api_key | VARCHAR | 50 | YES | N/A | masked API key for display |
|  | name | VARCHAR | 50 | NO | N/A | API key name |
|  | description | VARCHAR | 200 | YES | N/A | description |
|  | role | VARCHAR | 30 | NO | READ | role (TAS, ISSUER, READ) |
|  | is_active | BOOLEAN |  | NO | true | whether the key is active |
|  | last_used_at | TIMESTAMP |  | YES | N/A | last used date |
|  | expires_at | TIMESTAMP |  | NO | N/A | expiration date |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.11. SERVER_CONFIG

서버 설정을 키-값으로 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | config_key | VARCHAR | 100 | NO | N/A | configuration key |
|  | config_value | TEXT |  | NO | N/A | configuration value |
|  | description | VARCHAR | 200 | YES | N/A | description of the configuration |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.12. ADMIN

관리자 계정을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | login_id | VARCHAR | 50 | NO | N/A | administrator login ID |
|  | login_password | VARCHAR | 120 | NO | N/A | hashed login password |
|  | name | VARCHAR | 200 | YES | N/A | administrator name |
|  | email_verified | BOOLEAN |  | NO | false | whether the email is verified |
|  | require_password_reset | BOOLEAN |  | NO | true | whether a password change is required |
|  | role | VARCHAR | 50 | NO | N/A | role (ROOT, NORMAL) |
|  | last_password_changed_at | TIMESTAMP |  | NO | now() | last password change date |
|  | password_reset_reason | VARCHAR | 50 | YES | N/A | reason a password change is required |
|  | created_by | VARCHAR | 50 | NO | N/A | who created the account |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |
|  | password_algo | VARCHAR | 20 | NO | sha256 | password hash algorithm |

### 2.13. ADMIN_PASSWORD_POLICY

관리자 비밀번호 정책을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | min_length | SMALLINT |  | NO | 8 | minimum password length |
|  | require_uppercase | BOOLEAN |  | NO | false | whether an uppercase letter is required |
|  | require_number | BOOLEAN |  | NO | false | whether a number is required |
|  | require_special | BOOLEAN |  | NO | false | whether a special character is required |
|  | password_expiry_days | SMALLINT |  | NO | 365 | password expiration period in days |
|  | created_at | TIMESTAMP |  | NO | now() | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.14. ADMIN_PASSWORD_RESET_TOKEN

비밀번호 재설정 OTP를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | login_id | VARCHAR | 50 | NO | N/A | administrator login ID |
|  | token | VARCHAR | 200 | NO | N/A | hashed token |
|  | token_type | VARCHAR | 20 | NO | PASSWORD_RESET | token type |
|  | delivery_method | VARCHAR | 20 | NO | EMAIL | delivery method |
|  | expired_at | TIMESTAMP |  | NO | now() | expiration date |
|  | used | BOOLEAN |  | NO | false | whether the token was used |
|  | attempt_count | SMALLINT |  | NO | 0 | number of verification attempts |
|  | created_at | TIMESTAMP |  | NO | N/A | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |
|  | admin_id | BIGINT |  | NO | N/A | admin id |

### 2.15. ADMIN_OTP_SEND_LIMIT

비밀번호 재설정 OTP 발송 제한을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | login_id | VARCHAR | 50 | NO | N/A | administrator login ID |
|  | send_date | DATE |  | NO | N/A | send date |
|  | daily_send_count | SMALLINT |  | NO | 0 | number of sends on the date |
|  | last_sent_at | TIMESTAMP |  | YES | N/A | last sent date |
|  | cooldown_until | TIMESTAMP |  | YES | N/A | resend wait end date |
|  | blocked | BOOLEAN |  | NO | false | whether sending is blocked |
|  | created_at | TIMESTAMP |  | NO | N/A | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |

### 2.16. API_LOG

API 호출 로그를 저장합니다. 관리자 콘솔의 API Log와 Audit API Log에서 조회합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description |
| --- | ----------- | --------- | ------ | -------- | ------- | ----------- |
| PK | id | BIGINT |  | NO | N/A | id |
|  | method | VARCHAR | 10 | NO | N/A | HTTP method |
|  | uri | VARCHAR | 1048 | NO | N/A | request URI |
|  | status | INTEGER |  | NO | N/A | HTTP status code |
|  | result | VARCHAR | 20 | NO | N/A | processing result |
|  | api_name | VARCHAR | 255 | YES | N/A | API name |
|  | api_description | VARCHAR | 1000 | YES | N/A | API description |
|  | requester_id | VARCHAR | 128 | YES | N/A | requester identifier |
|  | duration_ms | BIGINT |  | NO | N/A | processing time in milliseconds |
|  | client_ip | VARCHAR | 64 | YES | N/A | client IP address |
|  | error_code | VARCHAR | 64 | YES | N/A | error code |
|  | message | TEXT |  | YES | N/A | error or result message |
|  | api_type | VARCHAR | 20 | YES | N/A | API type |
|  | created_at | TIMESTAMP |  | NO | N/A | created date |
|  | updated_at | TIMESTAMP |  | YES | N/A | updated date |
|  | target_type | VARCHAR | 50 | YES | N/A | type of the target |
|  | target_id | VARCHAR | 128 | YES | N/A | identifier of the target |
|  | user_agent | TEXT |  | YES | N/A | User-Agent header value |
|  | action_type | VARCHAR | 20 | NO | N/A | action type |

