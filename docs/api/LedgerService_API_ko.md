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

- 일자: 2026-10-27
- 버전: v2.1.0
  
목차
---
<!-- TOC tocDepth:2..3 chapterDepth:2..6 -->

- [1. 개요](#1-개요)
- [2. 용어 설명](#2-용어-설명)
- [3. API 목록](#3-api-목록)
    - [3.1. 순차 API](#31-순차-api)
    - [3.2. 단일호출 API](#32-단일호출-api)
- [4. 공통 사항](#4-공통-사항)
    - [4.1. API Key 인증](#41-api-key-인증)
    - [4.2. 에러 응답](#42-에러-응답)
- [5. 단일 호출 API](#5-단일-호출-api)
    - [5.1. DID Document 등록](#51-did-document-등록)
    - [5.2. DID Document 조회](#52-did-document-조회)
    - [5.3. DID Document 상태 변경](#53-did-document-상태-변경)
    - [5.4. VC Metadata 등록](#54-vc-metadata-등록)
    - [5.5. VC Metadata 조회](#55-vc-metadata-조회)
    - [5.6. VC Metadata 상태 변경](#56-vc-metadata-상태-변경)
    - [5.7. VC Schema 등록](#57-vc-schema-등록)
    - [5.8. VC Schema 조회](#58-vc-schema-조회)
    - [5.9. ZKP Credential Schema 등록](#59-zkp-credential-schema-등록)
    - [5.10. ZKP Credential Schema 조회](#510-zkp-credential-schema-조회)
    - [5.11. ZKP Credential Definition 등록](#511-zkp-credential-definition-등록)
    - [5.12. ZKP Credential Definition 조회](#512-zkp-credential-definition-조회)

<!-- /TOC -->


## 1. 개요

본 문서는 DID Ledger Service Server(이하 LSS)가 제공하는 Agent API를 정의한다. LSS는 PostgreSQL 데이터베이스를 활용하여 블록체인과 동일한 원장 기능을 제공하며, DID Document, VC Metadata, VC Schema, ZKP Credential Schema, ZKP Credential Definition의 등록·조회·상태 변경 기능을 제공한다.

- Agent API의 기본 경로는 `/lss/api/v1`이며, 서버 기본 포트는 `8098`이다. (예: `http://${Host}:8098/lss/api/v1/did-doc`)
- `/lss/admin/v1/**` 경로의 Admin API는 LSS Admin Console 전용으로 사용되므로 본 문서에서 다루지 않는다.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 2. 용어 설명
- 프로토콜 (Protocol)
  - 특정 기능을 수행하기 위해 정해진 순서에 따라 호출해야 하는 `순차 API`의 집합이다. API 호출 순서를 엄격히 따라야 하며, 순서가 잘못될 경우 예상하지 못한 결과가 발생할 수 있다.
- 순차 API (Sequential API)
  - 특정 기능(프로토콜)을 수행하기 위해 정해진 순서대로 호출하는 일련의 API를 말한다. 각 API는 순차적으로 호출되어야 하며, 순서가 잘못될 경우 제대로 동작하지 않을 수 있다.
  - 그러나 일부 프로토콜에서는 같은 호출 순서를 가진 API가 존재할 수 있으며, 이 경우 하나의 API를 선택하여 호출할 수 있다.
- 단일 호출 API (Single Call API)
  - 일반적인 REST API처럼 순서에 관계없이 독립적으로 호출 가능한 API를 의미한다.
- 표준 API (Standard API)
  - API 문서에서 명확하게 정의된 API로, 모든 구현체에서 일관된 방식으로 제공되어야 한다. 표준 API는 시스템 간 상호 운용성을 보장하며, 사전에 정의된 스펙에 따라 동작해야 한다.
- 비표준 API (Non-Standard API)
  - 구현체마다 필요에 따라 다르게 정의되거나 커스터마이징될 수 있는 API이다. 본 문서에서 제공하는 비표준 API는 한 가지 예시일 뿐이며, 각 구현체에 맞춰 다르게 구현될 수 있다. 이 경우, 구현체별 별도의 문서화가 필요하다.
- API Key
  - Agent API 호출 시 `X-API-Key` 헤더로 전달하는 인증 키이다. LSS Admin Console에서 발급하며, 발급 시 지정한 Role에 따라 호출 가능한 API가 결정된다.
- Role
  - API Key에 부여되는 권한 등급으로 `TAS`, `ISSUER`, `READ`가 있다. 각 API가 요구하는 Role은 [4.1.2. Role별 접근 권한](#412-role별-접근-권한)을 참고한다.
- M/O 표기
  - 요청/응답 본문 표의 `M`은 필수(Mandatory), `O`는 선택(Optional) 항목을 의미한다. 헤더 및 파라미터 표에서 이름 앞의 `+`는 필수 항목을 의미한다.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 3. API 목록

### 3.1. 순차 API
DID Ledger Service는 현재 특정 기능을 수행하기 위한 프로토콜이 정의되어 있지 않으며, 따라서 순차 API도 제공하지 않는다.

<div style="page-break-after: always; margin-top: 40px;"></div>

### 3.2. 단일호출 API

| API                              | Method | URL                                 | Description                         | Role     | 표준API |
| -------------------------------- | ------ | ----------------------------------- | ----------------------------------- | -------- | ------- |
| `register-did-doc`               | POST   | /lss/api/v1/did-doc                 | DID Document 등록 (갱신 포함)       | `TAS`    | N       |
| `get-did-doc`                    | GET    | /lss/api/v1/did-doc                 | DID Document 조회                   | `READ`   | N       |
| `update-did-doc-status`          | PUT    | /lss/api/v1/did-doc                 | DID Document 상태 변경              | `TAS`    | N       |
| `register-vc-meta`               | POST   | /lss/api/v1/vc-meta                 | VC Metadata 등록                    | `ISSUER` | N       |
| `get-vc-meta`                    | GET    | /lss/api/v1/vc-meta                 | VC Metadata 조회                    | `READ`   | N       |
| `update-vc-meta-status`          | PUT    | /lss/api/v1/vc-meta                 | VC Metadata 상태 변경               | `ISSUER` | N       |
| `register-vc-schema`             | POST   | /lss/api/v1/vc-schema               | VC Schema 등록                      | `ISSUER` | N       |
| `get-vc-schema`                  | GET    | /lss/api/v1/vc-schema               | VC Schema 조회                      | `READ`   | N       |
| `register-credential-schema`     | POST   | /lss/api/v1/credential-schema       | ZKP Credential Schema 등록          | `ISSUER` | N       |
| `get-credential-schema`          | GET    | /lss/api/v1/credential-schema       | ZKP Credential Schema 조회          | `READ`   | N       |
| `register-credential-definition` | POST   | /lss/api/v1/credential-definition   | ZKP Credential Definition 등록      | `ISSUER` | N       |
| `get-credential-definition`      | GET    | /lss/api/v1/credential-definition   | ZKP Credential Definition 조회      | `READ`   | N       |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 4. 공통 사항

### 4.1. API Key 인증

LSS의 모든 Agent API에는 요구 Role이 지정되어 있으며(`@ApiKeyRequired`), API Key 검증이 활성화된 경우 요청이 처리되기 전에 `X-API-Key` 헤더의 API Key와 Role을 검증한다.

#### 4.1.1. 요청 헤더

| Header        | Value       | Remarks                                                    |
| ------------- | ----------- | ---------------------------------------------------------- |
| + `X-API-Key` | `{API Key}` | API Key 검증 활성화 시 필수. 헤더가 없으면 `API-Key` 헤더 값을 사용 |

- 서버는 전달받은 API Key를 SHA-256으로 해시하여 저장된 해시값과 비교한다. 등록되지 않았거나 비활성화된 API Key는 유효하지 않은 키로 처리된다.
- Agent API는 `Authorization` 헤더를 사용하지 않는다. `Authorization: Bearer` 헤더는 LSS Admin Console의 관리자 인증(JWT)에 사용되며, 유효하지 않은 Bearer 토큰이 포함된 요청은 응답 본문 없이 HTTP 401이 반환된다.

#### 4.1.2. Role별 접근 권한

API마다 요구 Role이 지정되어 있으며, 상위 Role의 API Key는 하위 Role이 요구되는 API도 호출할 수 있다.

| API 요구 Role | 허용되는 API Key Role       | 대상 API                                                                                         |
| ------------- | --------------------------- | ------------------------------------------------------------------------------------------------ |
| `TAS`         | `TAS`                       | DID Document 등록, DID Document 상태 변경                                                        |
| `ISSUER`      | `TAS`, `ISSUER`             | VC Metadata 등록/상태 변경, VC Schema 등록, ZKP Credential Schema 등록, ZKP Credential Definition 등록 |
| `READ`        | `TAS`, `ISSUER`, `READ`     | 모든 조회(GET) API                                                                               |

#### 4.1.3. 검증 활성화 설정

- API Key 검증 여부는 `server_config` 테이블의 `API_KEY_VALIDATION_ENABLED` 값으로 결정되며, LSS Admin Console의 **Server Configuration** 화면에서 변경할 수 있다.
- 초기값은 `false`이다. 값이 `false`이면 API Key 검증을 수행하지 않으므로 `X-API-Key` 헤더 없이 모든 Agent API를 호출할 수 있다.
- 값이 `true`, `1`, `Y`, `yes` 중 하나이면 검증이 활성화된다. 설정값은 요청마다 조회되므로 변경 즉시 반영된다.

#### 4.1.4. API Key 발급

- API Key는 LSS Admin Console의 **API Key Management** 화면에서 이름, Role, 유효 기간(일)을 지정하여 발급한다.
- 발급된 API Key는 `lss_`로 시작하며, 평문은 발급 시 한 번만 표시된다. 서버에는 SHA-256 해시값만 저장되므로 발급 시 표시된 값을 안전하게 보관해야 한다.
- 발급된 API Key는 API Key Management 화면에서 비활성화/활성화할 수 있다.
- API Key에는 만료일이 있으며, 만료일이 지난 API Key는 `SSRVLSS14506` 오류로 거부된다. API Key Management 화면에서 갱신하면 Key 값은 그대로 두고 만료일을 연장한다.

#### 4.1.5. API Key 검증 에러

API Key 검증에 실패하면 [4.2.1. 에러 응답 형식](#421-에러-응답-형식)의 본문과 함께 다음 에러가 반환된다. 이 에러는 모든 Agent API에 공통으로 적용되며, API Key 검증이 활성화된 경우에만 발생한다.

| HTTP Status | Code         | Description                                                |
| ----------- | ------------ | ---------------------------------------------------------- |
| 401         | SSRVLSS14502 | `X-API-Key`(또는 `API-Key`) 헤더가 없거나 값이 비어 있음  |
| 401         | SSRVLSS14503 | 등록되지 않았거나 비활성화된 API Key                       |
| 401         | SSRVLSS14506 | 만료일이 지난 API Key                                      |
| 403         | SSRVLSS14504 | API Key의 Role이 API 요구 Role을 충족하지 않음             |
| 500         | SSRVLSS14505 | API Key 검증 처리 중 오류                                  |

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.2. 에러 응답

에러 코드의 전체 목록과 상세 설명은 [DID Ledger Service Error Code](../errorCode/LedgerService_ErrorCode_ko.md) 문서를 참고한다. 본 문서의 각 API에는 해당 API가 반환할 수 있는 에러 코드만 기재한다.

#### 4.2.1. 에러 응답 형식

| Name          | Type   | M/O | Description  |
| ------------- | ------ | --- | ------------ |
| `code`        | string | M   | 에러 코드    |
| `description` | string | M   | 에러 메시지  |

```json
{
  "code": "SSRVLSS11501",
  "description": "DID not found"
}
```

- HTTP 상태 코드는 에러 코드별로 정의된 값이 사용된다. 대상을 찾지 못한 경우에도 404가 아닌 400 또는 500이 반환되므로, 클라이언트는 HTTP 상태가 아닌 `code`로 에러를 구분해야 한다.

#### 4.2.2. 공통 처리 규칙

| 상황                                                                 | HTTP Status | code           | description                                     |
| -------------------------------------------------------------------- | ----------- | -------------- | ----------------------------------------------- |
| 요청 본문을 읽을 수 없음 (잘못된 JSON, 정의되지 않은 enum 값 등)     | 500         | `SSRVLSS11001` | `""` (빈 문자열)                                |
| 요청 본문 유효성 검증 실패 (VC Metadata 등록 API)                    | 500         | `9999`         | 필드별 검증 메시지를 `"; "`로 연결한 문자열     |

- 위 경우와 LSS 에러 코드로 정의된 경우 외의 예외(예: 존재하지 않는 `vcId`로 VC Metadata 조회/상태 변경)는 위 에러 응답 형식으로 반환되지 않는다.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 5. 단일 호출 API

단일 호출 API는 특정 기능을 수행하는 하나의 독립된 API이다.
따라서 순서대로 호출해야 하는 API의 집단인 순차 API(aka, 프로토콜)이 아니므로 프로토콜 번호가 부여되지 않는다.
DID Ledger Service가 제공하는 단일 호출 API 목록은 아래 표와 같다.

| API                              | Method | URL                                 | Description                         | Role     | 표준API |
| -------------------------------- | ------ | ----------------------------------- | ----------------------------------- | -------- | ------- |
| `register-did-doc`               | POST   | /lss/api/v1/did-doc                 | DID Document 등록 (갱신 포함)       | `TAS`    | N       |
| `get-did-doc`                    | GET    | /lss/api/v1/did-doc                 | DID Document 조회                   | `READ`   | N       |
| `update-did-doc-status`          | PUT    | /lss/api/v1/did-doc                 | DID Document 상태 변경              | `TAS`    | N       |
| `register-vc-meta`               | POST   | /lss/api/v1/vc-meta                 | VC Metadata 등록                    | `ISSUER` | N       |
| `get-vc-meta`                    | GET    | /lss/api/v1/vc-meta                 | VC Metadata 조회                    | `READ`   | N       |
| `update-vc-meta-status`          | PUT    | /lss/api/v1/vc-meta                 | VC Metadata 상태 변경               | `ISSUER` | N       |
| `register-vc-schema`             | POST   | /lss/api/v1/vc-schema               | VC Schema 등록                      | `ISSUER` | N       |
| `get-vc-schema`                  | GET    | /lss/api/v1/vc-schema               | VC Schema 조회                      | `READ`   | N       |
| `register-credential-schema`     | POST   | /lss/api/v1/credential-schema       | ZKP Credential Schema 등록          | `ISSUER` | N       |
| `get-credential-schema`          | GET    | /lss/api/v1/credential-schema       | ZKP Credential Schema 조회          | `READ`   | N       |
| `register-credential-definition` | POST   | /lss/api/v1/credential-definition   | ZKP Credential Definition 등록      | `ISSUER` | N       |
| `get-credential-definition`      | GET    | /lss/api/v1/credential-definition   | ZKP Credential Definition 조회      | `READ`   | N       |

■ Authorization

모든 API는 `X-API-Key` 헤더로 API Key를 전달하며, API별 요구 Role은 각 API의 `Authorization` 항목에 표기한다. API Key 검증 방식과 검증 활성화 설정은 [4.1. API Key 인증](#41-api-key-인증)을 참고한다. API Key 검증 에러(HTTP 401/403/500)는 [4.1.5. API Key 검증 에러](#415-api-key-검증-에러)와 같으며, 각 API의 에러 목록에서는 생략한다.

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.1. DID Document 등록

TAS(Controller)가 서명한 DID Document를 원장에 등록한다. 같은 DID가 이미 등록되어 있으면 요청한 DID Document를 새 버전으로 갱신한다.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `POST`                 |              |
| Path          | `/lss/api/v1/did-doc`  |              |
| Authorization | `X-API-Key`            | Role: `TAS`  |

#### 5.1.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name                              | Type               | M/O | Description                                                                                       |
| --------------------------------- | ------------------ | --- | ------------------------------------------------------------------------------------------------- |
| `roleType`                        | string (enum)      | M   | DID 소유자의 역할. 신규 등록 시 저장되며, 이미 등록된 DID를 갱신할 때는 사용하지 않음. 값: `TAS`, `WALLET`, `ISSUER`, `VERIFIER`, `WALLET_PROVIDER`, `APP_PROVIDER`, `LIST_PROVIDER`, `OP_PROVIDER`, `KYC_PROVIDER`, `NOTIFICATION_PROVIDER`, `LOG_PROVIDER`, `PORTAL_PROVIDER`, `DELEGATION_PROVIDER`, `STORAGE_PROVIDER`, `BACKUP_PROVIDER`, `ETC` |
| `didDoc`                          | object             | M   | Controller가 서명한 DID Document (InvokedDidDoc)                                                  |
| `didDoc.didDoc`                   | string (multibase) | M   | Multibase 인코딩된 DID Document JSON. DID Document의 `versionId`는 정수 문자열이어야 함            |
| `didDoc.controller`               | object             | M   | Controller 정보                                                                                   |
| `didDoc.controller.did`           | string             | M   | Controller DID. LSS에 Role `TAS`로 등록된 DID여야 함                                              |
| `didDoc.controller.certVcRef`     | string             | O   | Controller 가입증명서 VC URL                                                                      |
| `didDoc.nonce`                    | string (multibase) | O   | Controller nonce. 서명 대상 데이터에 포함됨                                                       |
| `didDoc.proof`                    | object             | M   | Controller의 Invocation 서명                                                                      |
| `didDoc.proof.type`               | string             | M   | 서명 유형. `Secp256k1Signature2018`, `Secp256r1Signature2018`                                     |
| `didDoc.proof.created`            | string             | O   | 서명 일시. 서명 대상 데이터에 포함됨                                                              |
| `didDoc.proof.verificationMethod` | string             | O   | 서명 키 URL. 서명 대상 데이터에 포함됨                                                            |
| `didDoc.proof.proofPurpose`       | string             | O   | 서명 목적. 서명 대상 데이터에 포함됨                                                              |
| `didDoc.proof.proofValue`         | string (multibase) | M   | 서명 값                                                                                           |

- M/O는 LSS 서버의 처리 기준이다. `didDoc.controller`와 `didDoc.proof`는 LSS에 등록된 DID가 하나도 없는 경우(최초 등록) 검증하지 않는다.

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.1.2. Response

**■ Process**
1. `didDoc.didDoc`을 Multibase 디코딩하여 DID Document 파싱
1. Controller 서명 검증 (LSS에 등록된 DID가 1개 이상인 경우에만 수행)
    - `didDoc.controller.did`에서 DID를 추출하여 등록 여부 확인
    - Controller DID의 Role이 `TAS`인지 확인
    - Controller DID의 현재 버전 DID Document에서 첫 번째 `capabilityInvocation` 키로 서명 검증
    - 서명 대상: `proof.proofValue`를 제거한 `didDoc` 객체를 키 정렬된 JSON으로 직렬화한 값의 SHA-256 해시
1. DID Document의 `id`(DID)로 등록 여부 확인
    - 미등록: `roleType`을 Role로 하여 DID 신규 등록 (상태 `ACTIVATED`, 버전 = DID Document의 `versionId`)
    - 등록됨: `versionId`가 마지막으로 저장된 버전 + 1인지 확인 후, 이전 버전 DID Document를 비활성화하고 새 버전을 저장 (DID 상태 `ACTIVATED`)
1. DID Document 상태 변경 이력 저장

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

| Code         | Description                                                      |
| ------------ | ---------------------------------------------------------------- |
| SSRVLSS11000 | Controller 서명 검증 실패                                        |
| SSRVLSS11500 | 등록된 DID의 갱신 요청이지만 `versionId`가 마지막 버전 + 1이 아님 |
| SSRVLSS11501 | Controller DID가 등록되어 있지 않음                              |
| SSRVLSS11504 | Controller DID의 DID Document를 찾을 수 없음                     |
| SSRVLSS13002 | Controller DID의 Role이 `TAS`가 아님                             |

**■ Status 500 - Server error**

| Code         | Description                         |
| ------------ | ----------------------------------- |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음            |
| SSRVLSS99999 | 서명 검증용 해시 생성 실패          |

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

### 5.2. DID Document 조회

원장에서 DID Document를 조회한다. DID URL의 `versionId`로 특정 버전을 조회할 수 있다.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `GET`                  |              |
| Path          | `/lss/api/v1/did-doc`  |              |
| Authorization | `X-API-Key`            | Role: `READ` |

#### 5.2.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                     |
| ------------- | ----------- | --------------------------- |
| + `X-API-Key` | `{API Key}` | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name  | Description          | Remarks                   |
| ----- | -------------------- | ------------------------- |
| `did` | 조회할 DID 또는 DID URL | O, 기본값: `did:omn:tas` |

**■ `did` 파라미터 해석**

- `?` 또는 `#` 앞부분을 DID로 사용한다.
- `versionId=` 값이 포함되어 있으면 그 값(`&`, `#` 또는 문자열 끝까지)을 조회할 버전으로 사용하고, 없으면 DID의 현재 버전을 사용한다. `versionId`는 정수여야 한다.
    - 예: `did:omn:issuer?versionId=1#assert` → DID `did:omn:issuer`, 버전 `1`
    - 예: `did:omn:issuer` → DID `did:omn:issuer`, 현재 버전
- 쿼리 파라미터 값이므로 `:`, `?`, `=`, `#`, `&`는 URL 인코딩하여 전달한다. (예: `did%3Aomn%3Aissuer%3FversionId%3D1`)

**■ HTTP Body**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.2.2. Response

**■ Process**
1. `did` 파라미터에서 DID를 추출하여 등록 여부 확인
1. `versionId`가 있으면 해당 버전, 없으면 DID의 현재 버전으로 조회 버전 결정
1. DID 상태에 따라 DID Document 조회
    - `ACTIVATED`, `DEACTIVATED`: 등록된 DID Document에서 조회
    - `REVOKED`, `TERMINATED`: 폐기 처리된 DID Document에서 해당 버전의 최근 폐기 문서 조회
1. 저장된 DID Document JSON 반환

**■ Status 200 - Success**

- 응답 본문은 저장된 DID Document JSON 문자열이다 (Multibase 인코딩 아님, 키 정렬됨).
- 서버가 응답 `Content-Type`을 명시하지 않으므로 요청의 `Accept` 헤더에 따라 결정된다.

**■ Status 400 - Client error**

| Code         | Description                                               |
| ------------ | --------------------------------------------------------- |
| SSRVLSS11501 | `did` 파라미터에서 DID를 추출할 수 없거나 등록되지 않은 DID |
| SSRVLSS11504 | 요청한 버전의 DID Document가 없음                          |

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

### 5.3. DID Document 상태 변경

원장에 등록된 DID의 상태를 변경한다.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `PUT`                  |              |
| Path          | `/lss/api/v1/did-doc`  |              |
| Authorization | `X-API-Key`            | Role: `TAS`  |

#### 5.3.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name     | Type          | M/O | Description                                                                                  |
| -------- | ------------- | --- | -------------------------------------------------------------------------------------------- |
| `did`    | string        | M   | 상태를 변경할 DID 또는 DID URL. `versionId` 해석 방식은 [5.2.1](#521-request)과 같음        |
| `status` | string (enum) | M   | 변경할 상태. `ACTIVATED`, `DEACTIVATED`, `REVOKED`, `TERMINATED`                             |

#### 5.3.2. Response

**■ Process**
1. `did`에서 DID를 추출하여 등록 여부 확인
1. `versionId`가 있으면 해당 버전, 없으면 DID의 현재 버전으로 대상 버전 결정
1. 상태 전이 검증
    - 현재 상태가 `TERMINATED`이면 변경 불가
    - 현재 상태가 `REVOKED`이면 `TERMINATED`로만 변경 가능
1. 변경할 상태에 따라 처리
    - `TERMINATED`: 종료 일시 기록
    - `REVOKED`: 해당 DID의 모든 버전 DID Document를 폐기 처리
    - `ACTIVATED`, `DEACTIVATED`: 대상 버전 DID Document의 비활성화 여부 변경
1. 상태 변경 이력 저장 후 DID 상태 갱신

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

| Code         | Description                                                         |
| ------------ | ------------------------------------------------------------------- |
| SSRVLSS11501 | `did`에서 DID를 추출할 수 없거나 등록되지 않은 DID                  |
| SSRVLSS11504 | `ACTIVATED`/`DEACTIVATED`로 변경 시 대상 버전 DID Document가 없음   |
| SSRVLSS13000 | 현재 상태가 `TERMINATED`인 DID                                      |
| SSRVLSS13001 | 현재 상태가 `REVOKED`인 DID를 `TERMINATED` 외의 상태로 변경 요청    |

**■ Status 500 - Server error**

| Code         | Description              |
| ------------ | ------------------------ |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음 |

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

### 5.4. VC Metadata 등록

VC(Verifiable Credential) Metadata를 원장에 등록한다. 요청 본문은 VC Metadata 객체(VcMeta) 자체이다.

| Item          | Description            | Remarks        |
| ------------- | ---------------------- | -------------- |
| Method        | `POST`                 |                |
| Path          | `/lss/api/v1/vc-meta`  |                |
| Authorization | `X-API-Key`            | Role: `ISSUER` |

#### 5.4.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name                              | Type          | M/O | Description                                                  |
| --------------------------------- | ------------- | --- | ------------------------------------------------------------ |
| `id`                              | string        | M   | VC ID                                                        |
| `issuer`                          | object        | M   | Issuer 정보                                                  |
| `issuer.did`                      | string        | M   | Issuer DID                                                   |
| `issuer.certVcRef`                | string        | O   | Issuer 가입증명서 VC URL                                     |
| `subject`                         | string        | M   | Subject(Holder) DID                                          |
| `credentialSchema`                | object        | M   | Credential Schema 정보                                       |
| `credentialSchema.id`             | string        | M   | VC Schema URL                                                |
| `credentialSchema.type`           | string        | M   | VC Schema 형식 유형 (예: `OsdSchemaCredential`)             |
| `credentialSchema.value`          | string        | O   | Credential Schema 공통 모델의 선택 항목. 전달 시 VC Metadata에 그대로 저장됨 |
| `credentialSchema.presentAll`     | boolean       | O   | 상동                                                         |
| `credentialSchema.displayClaims`  | array(string) | O   | 상동                                                         |
| `credentialSchema.requiredClaims` | array(string) | O   | 상동                                                         |
| `credentialSchema.allowedIssuers` | array(string) | O   | 상동                                                         |
| `status`                          | string        | M   | VC 상태 (`ACTIVE`, `INACTIVE`, `REVOKED`)                    |
| `issuanceDate`                    | string        | M   | 발급 일시 (일시 형식 참고)                                   |
| `validFrom`                       | string        | M   | 유효기간 시작 일시 (일시 형식 참고)                          |
| `validUntil`                      | string        | M   | 유효기간 종료 일시 (일시 형식 참고)                          |
| `formatVersion`                   | string        | M   | VC 형식 버전 (예: `1.0`)                                     |
| `language`                        | string        | M   | VC 언어 코드 (예: `ko`)                                      |

- 일시 형식: `yyyy-MM-ddTHH:mm:ssZ`, 소수점 이하 3~6자리 초를 포함한 `yyyy-MM-ddTHH:mm:ss.SSSZ` ~ `yyyy-MM-ddTHH:mm:ss.SSSSSSZ`, 또는 오프셋을 포함한 `yyyy-MM-ddTHH:mm:ss±hh:mm`
- 문자열 필수 항목은 빈 문자열을 허용하지 않는다.

#### 5.4.2. Response

**■ Process**
1. 요청 본문 유효성 검증 (필수 항목, 일시 형식)
1. VC Metadata 저장 (VC ID, Issuer DID, Subject DID, VC Schema ID(`credentialSchema.id`), 상태, 일시, 형식 버전, 언어 및 VC Metadata JSON)
1. VC 상태 이력 저장 (`ACTIVE`로 등록)

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                                                           |
| ------------ | --------------------------------------------------------------------- |
| 9999         | 요청 본문 유효성 검증 실패. `description`에 필드별 검증 메시지가 포함됨 |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음                                              |

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

### 5.5. VC Metadata 조회

원장에서 VC Metadata를 조회한다.

| Item          | Description            | Remarks      |
| ------------- | ---------------------- | ------------ |
| Method        | `GET`                  |              |
| Path          | `/lss/api/v1/vc-meta`  |              |
| Authorization | `X-API-Key`            | Role: `READ` |

#### 5.5.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                     |
| ------------- | ----------- | --------------------------- |
| + `X-API-Key` | `{API Key}` | API Key 검증 활성화 시 필수 |

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
1. `vcId`로 VC Metadata 조회
1. 저장된 VC Metadata JSON 반환 (상태 변경 시 갱신된 `status` 반영)

**■ Status 200 - Success**

- 응답 본문은 VC Metadata JSON 문자열이며, 항목은 [5.4.1](#541-request)의 HTTP Body와 같다 (Multibase 인코딩 아님, 키 정렬됨).
- 서버가 응답 `Content-Type`을 명시하지 않으므로 요청의 `Accept` 헤더에 따라 결정된다.

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

N/A

- `vcId`에 해당하는 VC Metadata가 없으면 서버 내부에서 `IllegalArgumentException`이 발생하며, LSS 에러 코드가 아닌 서버 기본 에러 응답이 반환된다.

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

### 5.6. VC Metadata 상태 변경

원장에 등록된 VC의 상태를 변경한다.

| Item          | Description            | Remarks        |
| ------------- | ---------------------- | -------------- |
| Method        | `PUT`                  |                |
| Path          | `/lss/api/v1/vc-meta`  |                |
| Authorization | `X-API-Key`            | Role: `ISSUER` |

#### 5.6.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name       | Type          | M/O | Description                                   |
| ---------- | ------------- | --- | --------------------------------------------- |
| `vcId`     | string        | M   | 상태를 변경할 VC ID                           |
| `vcStatus` | string (enum) | M   | 변경할 VC 상태. `ACTIVE`, `INACTIVE`, `REVOKED` |

#### 5.6.2. Response

**■ Process**
1. `vcId`로 VC Metadata 조회
1. 현재 상태가 `REVOKED`이면 변경 불가
1. VC Metadata의 상태와 저장된 VC Metadata JSON의 `status`를 `vcStatus`로 갱신
1. VC 상태 이력 저장 (이전 상태 → 변경 상태)

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

| Code         | Description                             |
| ------------ | --------------------------------------- |
| SSRVLSS12000 | 이미 `REVOKED` 상태인 VC의 상태 변경 요청 |

**■ Status 500 - Server error**

| Code         | Description              |
| ------------ | ------------------------ |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음 |

- `vcId`에 해당하는 VC Metadata가 없으면 서버 내부에서 `IllegalArgumentException`이 발생하며, LSS 에러 코드가 아닌 서버 기본 에러 응답이 반환된다.

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

### 5.7. VC Schema 등록

VC Schema를 원장에 등록한다.

| Item          | Description              | Remarks        |
| ------------- | ------------------------ | -------------- |
| Method        | `POST`                   |                |
| Path          | `/lss/api/v1/vc-schema`  |                |
| Authorization | `X-API-Key`              | Role: `ISSUER` |

#### 5.7.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name       | Type               | M/O | Description                                                                 |
| ---------- | ------------------ | --- | --------------------------------------------------------------------------- |
| `did`      | string             | M   | VC Schema를 등록하는 Issuer DID. 등록된 DID와 정확히 일치해야 함 (DID URL 불가) |
| `vcSchema` | string (multibase) | M   | Multibase 인코딩된 VC Schema JSON                                           |

- `vcSchema`를 디코딩한 VC Schema JSON에는 `@id`, `@schema`, `title`, `description`, `metadata`(`language`, `formatVersion`), `credentialSubject`가 필수이다. `@id`가 VC Schema ID로 사용된다.

#### 5.7.2. Response

**■ Process**
1. `vcSchema`를 Multibase 디코딩하여 VC Schema 파싱 및 필수 항목 검증
1. `@id`로 이미 등록된 VC Schema인지 확인
1. Issuer DID 확인
    - 등록된 DID인지 확인
    - Role이 `ISSUER` 또는 `TAS`인지 확인
    - 상태가 `ACTIVATED`인지 확인
1. VC Schema 저장

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

| Code         | Description                                                    |
| ------------ | -------------------------------------------------------------- |
| SSRVLSS10003 | `vcSchema` 디코딩 실패, 또는 필수 항목 누락 등 VC Schema 검증 실패 |
| SSRVLSS11501 | Issuer DID가 등록되어 있지 않음                                |
| SSRVLSS11502 | Issuer DID의 Role이 `ISSUER` 또는 `TAS`가 아님                 |
| SSRVLSS11503 | Issuer DID의 상태가 `ACTIVATED`가 아님                         |
| SSRVLSS12001 | 디코딩한 VC Schema의 JSON 구문 오류                            |
| SSRVLSS12002 | 같은 `@id`의 VC Schema가 이미 등록되어 있음                    |

**■ Status 500 - Server error**

| Code         | Description              |
| ------------ | ------------------------ |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음 |
| SSRVLSS13502 | VC Schema 저장 실패      |

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

### 5.8. VC Schema 조회

원장에서 VC Schema를 조회한다.

| Item          | Description              | Remarks      |
| ------------- | ------------------------ | ------------ |
| Method        | `GET`                    |              |
| Path          | `/lss/api/v1/vc-schema`  |              |
| Authorization | `X-API-Key`              | Role: `READ` |

#### 5.8.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                     |
| ------------- | ----------- | --------------------------- |
| + `X-API-Key` | `{API Key}` | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

| name         | Description                  | Remarks |
| ------------ | ---------------------------- | ------- |
| + `schemaId` | VC Schema ID (VC Schema의 `@id`) | M    |

**■ HTTP Body**

N/A

#### 5.8.2. Response

**■ Process**
1. `schemaId`로 VC Schema 조회
1. 저장된 VC Schema JSON을 JSON 문자열 값으로 직렬화하여 반환

**■ Status 200 - Success**

- 응답 본문은 VC Schema JSON을 하나의 JSON 문자열 값으로 직렬화한 값이다. 즉, 본문 전체가 큰따옴표로 감싸져 있고 내부의 큰따옴표는 이스케이프되어 있다. 클라이언트는 본문을 JSON 문자열로 파싱한 뒤, 그 결과를 다시 VC Schema JSON으로 파싱해야 한다.
- 서버가 응답 `Content-Type`을 명시하지 않으므로 요청의 `Accept` 헤더에 따라 결정된다.

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                             |
| ------------ | --------------------------------------- |
| SSRVLSS12003 | `schemaId`에 해당하는 VC Schema가 없음  |

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

### 5.9. ZKP Credential Schema 등록

ZKP Credential Schema를 원장에 등록한다.

| Item          | Description                      | Remarks        |
| ------------- | -------------------------------- | -------------- |
| Method        | `POST`                           |                |
| Path          | `/lss/api/v1/credential-schema`  |                |
| Authorization | `X-API-Key`                      | Role: `ISSUER` |

#### 5.9.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name               | Type               | M/O | Description                                    |
| ------------------ | ------------------ | --- | ---------------------------------------------- |
| `credentialSchema` | string (multibase) | M   | Multibase 인코딩된 ZKP Credential Schema JSON  |

**■ Credential Schema (`credentialSchema` 디코딩 결과)**

| Name        | Type          | M/O | Description                                                                                                  |
| ----------- | ------------- | --- | ------------------------------------------------------------------------------------------------------------ |
| `id`        | string        | M   | Credential Schema ID. `:`로 구분한 앞 세 요소(`did:{method}:{identifier}`)를 Issuer DID로 사용 (예: `did:omn:issuer:2:mdl:1.0`) |
| `name`      | string        | M   | Schema 이름                                                                                                  |
| `version`   | string        | M   | Schema 버전                                                                                                  |
| `attrNames` | array(string) | M   | 속성 이름 목록                                                                                               |
| `attrTypes` | array(object) | O   | 속성 타입 정의 목록 (`namespace`, `items`)                                                                   |
| `tag`       | string        | M   | 태그                                                                                                         |

#### 5.9.2. Response

**■ Process**
1. `credentialSchema`를 Multibase 디코딩하여 Credential Schema 파싱
1. 필수 항목 확인 (`id`, `name`, `version`, `attrNames`, `tag`)
1. `id`에서 Issuer DID 추출 (`id`가 `:`로 구분한 요소 4개 이상이고 `did`로 시작해야 함)
1. 같은 `id`의 Credential Schema가 이미 등록되어 있는지 확인
1. Issuer DID 확인
    - 등록된 DID인지 확인
    - Role이 `ISSUER`인지 확인
    - 상태가 `ACTIVATED`인지 확인
1. Credential Schema 저장

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

| Code         | Description                                                           |
| ------------ | --------------------------------------------------------------------- |
| SSRVLSS10003 | `credentialSchema` 디코딩 실패 등 파싱 중 오류                        |
| SSRVLSS11501 | `id`에서 추출한 Issuer DID가 등록되어 있지 않음                       |
| SSRVLSS11502 | Issuer DID의 Role이 `ISSUER`가 아님                                   |
| SSRVLSS11503 | Issuer DID의 상태가 `ACTIVATED`가 아님                                |
| SSRVLSS12500 | `id`가 비어 있거나, `:`로 구분한 요소가 4개 미만이거나, `did`로 시작하지 않음 |
| SSRVLSS12501 | 같은 `id`의 Credential Schema가 이미 등록되어 있음                    |
| SSRVLSS12505 | 디코딩한 Credential Schema의 JSON 구문 오류                           |

**■ Status 500 - Server error**

| Code         | Description                    |
| ------------ | ------------------------------ |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음       |
| SSRVLSS13502 | Credential Schema 저장 실패    |

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

### 5.10. ZKP Credential Schema 조회

원장에서 ZKP Credential Schema를 조회한다.

| Item          | Description                      | Remarks      |
| ------------- | -------------------------------- | ------------ |
| Method        | `GET`                            |              |
| Path          | `/lss/api/v1/credential-schema`  |              |
| Authorization | `X-API-Key`                      | Role: `READ` |

#### 5.10.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                     |
| ------------- | ----------- | --------------------------- |
| + `X-API-Key` | `{API Key}` | API Key 검증 활성화 시 필수 |

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
1. `schemaId`로 Credential Schema 조회
1. 저장된 Credential Schema를 JSON으로 반환

**■ Status 200 - Success**

- `Content-Type: application/json`
- 응답 본문은 Credential Schema JSON이며, 항목은 [5.9.1](#591-request)의 Credential Schema와 같다.

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                                     |
| ------------ | ----------------------------------------------- |
| SSRVLSS12502 | `schemaId`에 해당하는 Credential Schema가 없음  |

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

### 5.11. ZKP Credential Definition 등록

ZKP Credential Definition을 원장에 등록한다.

| Item          | Description                          | Remarks        |
| ------------- | ------------------------------------ | -------------- |
| Method        | `POST`                               |                |
| Path          | `/lss/api/v1/credential-definition`  |                |
| Authorization | `X-API-Key`                          | Role: `ISSUER` |

#### 5.11.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks                     |
| ---------------- | -------------------------------- | --------------------------- |
| + `Content-Type` | `application/json;charset=utf-8` |                             |
| + `X-API-Key`    | `{API Key}`                      | API Key 검증 활성화 시 필수 |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

| Name                   | Type               | M/O | Description                                        |
| ---------------------- | ------------------ | --- | -------------------------------------------------- |
| `credentialDefinition` | string (multibase) | M   | Multibase 인코딩된 ZKP Credential Definition JSON  |

**■ Credential Definition (`credentialDefinition` 디코딩 결과)**

| Name       | Type          | M/O | Description                                       |
| ---------- | ------------- | --- | ------------------------------------------------- |
| `id`       | string        | M   | Credential Definition ID                          |
| `schemaId` | string        | M   | LSS에 등록된 ZKP Credential Schema ID             |
| `ver`      | string        | M   | 버전                                              |
| `type`     | string (enum) | M   | Credential 유형. `CL`                             |
| `value`    | object        | M   | Credential Definition 공개키 값 (`primary`)       |
| `tag`      | string        | M   | 태그                                              |

#### 5.11.2. Response

**■ Process**
1. `credentialDefinition`을 Multibase 디코딩하여 Credential Definition 파싱
1. 필수 항목 확인 (`id`, `schemaId`, `ver`, `type`, `value`, `tag`)
1. `schemaId`의 Credential Schema가 등록되어 있는지 확인
1. 같은 `id`의 Credential Definition이 이미 등록되어 있는지 확인
1. Credential Definition 저장

**■ Status 200 - Success**

응답 본문 없음

**■ Status 400 - Client error**

| Code         | Description                                              |
| ------------ | -------------------------------------------------------- |
| SSRVLSS10003 | `credentialDefinition` 디코딩 실패 등 파싱 중 오류       |
| SSRVLSS12503 | 같은 `id`의 Credential Definition이 이미 등록되어 있음   |
| SSRVLSS12506 | 디코딩한 Credential Definition의 JSON 구문 오류          |

**■ Status 500 - Server error**

| Code         | Description                                               |
| ------------ | --------------------------------------------------------- |
| SSRVLSS11001 | 요청 본문을 읽을 수 없음                                  |
| SSRVLSS12502 | `schemaId`에 해당하는 Credential Schema가 등록되어 있지 않음 |
| SSRVLSS13502 | Credential Definition 저장 실패                           |

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

### 5.12. ZKP Credential Definition 조회

원장에서 ZKP Credential Definition을 조회한다.

| Item          | Description                          | Remarks      |
| ------------- | ------------------------------------ | ------------ |
| Method        | `GET`                                |              |
| Path          | `/lss/api/v1/credential-definition`  |              |
| Authorization | `X-API-Key`                          | Role: `READ` |

#### 5.12.1. Request

**■ HTTP Headers**

| Header        | Value       | Remarks                     |
| ------------- | ----------- | --------------------------- |
| + `X-API-Key` | `{API Key}` | API Key 검증 활성화 시 필수 |

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
1. `definitionId`로 Credential Definition 조회
1. 저장된 Credential Definition을 JSON으로 반환

**■ Status 200 - Success**

- `Content-Type: application/json`
- 응답 본문은 Credential Definition JSON이며, 항목은 [5.11.1](#5111-request)의 Credential Definition과 같다.

**■ Status 400 - Client error**

| Code         | Description                                            |
| ------------ | ------------------------------------------------------ |
| SSRVLSS12504 | `definitionId`에 해당하는 Credential Definition이 없음 |

**■ Status 500 - Server error**

N/A

#### 5.12.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/lss/api/v1/credential-definition?definitionId=did%3Aomn%3Aissuer%3A2%3Azkpschemaexample%3A1.0%3A3%3ACL%3Adid%3Aomn%3Aissuer%3A2%3Azkpschemaexample%3A1.0%3Atag" \
  -H "X-API-Key: ${API_KEY}"
```

**■ Response**

`value.primary`의 값은 길이가 길어 일부를 생략하였다.

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
