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

- 일자: 2026-10-27
- 버전: v2.1.0

목차
---
<!-- TOC tocDepth:2..3 chapterDepth:2..6 -->

- [1. 개요](#1-개요)
- [2. 에러 코드 형식](#2-에러-코드-형식)
- [3. 에러 코드 분류](#3-에러-코드-분류)
- [4. 에러 응답 형식](#4-에러-응답-형식)
  - [4.1. 응답 본문](#41-응답-본문)
  - [4.2. 처리 경로별 응답](#42-처리-경로별-응답)
  - [4.3. 에러 처리 시 유의 사항](#43-에러-처리-시-유의-사항)
- [5. 에러 코드 목록](#5-에러-코드-목록)
  - [5.1. 공통 에러 (10000 ~ 10999)](#51-공통-에러-10000--10999)
  - [5.2. API 처리 에러 (11000 ~ 11499)](#52-api-처리-에러-11000--11499)
  - [5.3. DID 에러 (11500 ~ 11999)](#53-did-에러-11500--11999)
  - [5.4. VC 에러 (12000 ~ 12499)](#54-vc-에러-12000--12499)
  - [5.5. ZKP 에러 (12500 ~ 12999)](#55-zkp-에러-12500--12999)
  - [5.6. 정책 에러 (13000 ~ 13499)](#56-정책-에러-13000--13499)
  - [5.7. DB 에러 (13500 ~ 13999)](#57-db-에러-13500--13999)
  - [5.8. 관리자 에러 (14000 ~ 14499)](#58-관리자-에러-14000--14499)
  - [5.9. API Key 에러 (14500 ~ 14999)](#59-api-key-에러-14500--14999)
  - [5.10. 서버 설정 에러 (15000 ~ 15499)](#510-서버-설정-에러-15000--15499)
  - [5.11. 기타 에러 (90000 ~ 99999)](#511-기타-에러-90000--99999)
- [6. Agent API별 에러 코드](#6-agent-api별-에러-코드)
  - [6.1. API Key 인증 (공통)](#61-api-key-인증-공통)
  - [6.2. DID Document API](#62-did-document-api)
  - [6.3. VC Metadata API](#63-vc-metadata-api)
  - [6.4. VC Schema API](#64-vc-schema-api)
  - [6.5. Credential Schema API](#65-credential-schema-api)
  - [6.6. Credential Definition API](#66-credential-definition-api)

<!-- /TOC -->

## 1. 개요

본 문서는 DID Ledger Service Server(LSS)가 반환하는 에러 코드를 정의한다. 에러 코드는 `org.omnione.did.base.exception.ErrorCode` 열거형에 정의되어 있으며, Agent API(`/lss/api/v1`), API Key 인증, 관리자 콘솔 API(`/lss/admin/v1`)를 처리하는 중에 발생하는 실패를 나타낸다.

본 문서의 에러 코드, HTTP 상태, 메시지는 소스 코드에 정의된 값을 그대로 옮긴 것이다. 메시지는 응답 본문에 영문 그대로 담기므로 표에도 원문을 그대로 적고, 한국어 설명은 별도 열에 둔다.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 2. 에러 코드 형식

DID Ledger Service의 에러 코드는 고정 접두어와 5자리 번호로 구성된다.

**형식**: `SSRVLSS{번호}`

- **SSRVLSS**: 7자리 고정 접두어. `SSRV`는 Open DID 서버 에러 코드의 공통 접두어(예: TAS는 `SSRVTRA`, Issuer는 `SSRVISS`)이고, `LSS`는 Ledger Service Server를 나타낸다.
- **번호**: 5자리 숫자. 분류마다 번호 범위가 할당되어 있으며([3. 에러 코드 분류](#3-에러-코드-분류) 참고), 각 분류 안에서 범위의 시작 번호부터 1씩 증가한다.

**예시**: `SSRVLSS11501` - DID not found (DID 에러 범위 11500 ~ 11999의 두 번째 코드)

> **참고**
> - `DECRYPTION_FAILED`의 코드 값은 `SSRVLSS100015`로, 번호가 6자리여서 위 형식에서 벗어난다. 순번상 `10005`가 비어 있으나, 서버는 정의된 값(`SSRVLSS100015`)을 그대로 반환한다.
> - 요청 본문 검증(`@Valid`) 실패 시에는 `ErrorCode`를 거치지 않고 `9999`라는 코드가 반환된다. 이 값은 `SSRVLSS` 형식이 아니다([4.2. 처리 경로별 응답](#42-처리-경로별-응답) 참고).

<div style="page-break-after: always; margin-top: 50px;"></div>

## 3. 에러 코드 분류

분류와 번호 범위는 `ErrorCode.java`의 주석에 정의된 구분을 따른다. 15500 ~ 89999 범위는 할당되어 있지 않다.

| 분류 | 범위          | 설명                                       | 코드 수 |
| ---- | ------------- | ------------------------------------------ | ------- |
| 1    | 10000 ~ 10999 | 공통 에러 (인코딩·디코딩, 암호화·복호화)   | 6       |
| 2    | 11000 ~ 11499 | API 처리 에러 (서명 검증, 요청 본문 파싱)  | 2       |
| 3    | 11500 ~ 11999 | DID 관련 에러                              | 7       |
| 4    | 12000 ~ 12499 | VC 및 VC Schema 관련 에러                  | 5       |
| 5    | 12500 ~ 12999 | ZKP(Credential Schema/Definition) 관련 에러 | 7       |
| 6    | 13000 ~ 13499 | 정책 에러 (DID 상태 전이, 역할 정책)       | 3       |
| 7    | 13500 ~ 13999 | DB 관련 에러                               | 5       |
| 8    | 14000 ~ 14499 | 관리자 관련 에러 (로그인, 비밀번호, OTP)   | 10      |
| 9    | 14500 ~ 14999 | API Key 관련 에러 (인증, 관리)             | 7       |
| 10   | 15000 ~ 15499 | 서버 설정 관련 에러                        | 3       |
| 99   | 90000 ~ 99999 | 기타 에러                                  | 1       |
|      |               | **합계**                                   | **56**  |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 4. 에러 응답 형식

### 4.1. 응답 본문

에러 응답 본문은 `org.omnione.did.base.response.ErrorResponse`로 직렬화된다.

| 필드        | 타입   | 설명                                              |
| ----------- | ------ | ------------------------------------------------- |
| code        | String | 에러 코드 (예: `SSRVLSS11501`)                    |
| description | String | 에러 메시지. `ErrorCode`에 정의된 영문 메시지 원문 |

```json
{
  "code": "SSRVLSS11501",
  "description": "DID not found"
}
```

- HTTP 상태 코드는 `ErrorCode`에 정의된 HTTP 상태가 그대로 사용된다.
- `description`에는 항상 `ErrorCode`의 메시지만 담긴다. 서버 내부에서 `OpenDidException(ErrorCode, String)` 생성자로 덧붙인 추가 메시지는 예외 메시지(로그)에만 남고 응답에는 포함되지 않는다.

### 4.2. 처리 경로별 응답

에러는 `GlobalControllerAdvice`(`@RestControllerAdvice`, 대상 패키지 `org.omnione.did`)와 `ApiKeyValidationInterceptor`에서 응답으로 변환된다. 처리 경로에 따라 응답은 다음과 같다.

| 발생 상황                                                     | 처리 위치                     | HTTP 상태                      | code                                   | description                                     |
| ------------------------------------------------------------- | ----------------------------- | ------------------------------ | -------------------------------------- | ----------------------------------------------- |
| 서비스 로직에서 `OpenDidException` 발생                      | `GlobalControllerAdvice`      | `ErrorCode`의 HTTP 상태        | `ErrorCode`의 코드                     | `ErrorCode`의 메시지                            |
| 요청 본문을 읽을 수 없음 (`HttpMessageNotReadableException`) | `GlobalControllerAdvice`      | 500                            | `SSRVLSS11001`                         | `""` (빈 문자열)                                |
| 요청 본문 검증 실패 (`MethodArgumentNotValidException`)      | `GlobalControllerAdvice`      | 500                            | `9999`                                 | 필드별 검증 메시지를 `"; "`로 이어 붙인 문자열 |
| API Key 검증 실패                                             | `ApiKeyValidationInterceptor` | `ErrorCode`의 HTTP 상태 (401/403/500) | `SSRVLSS14502` ~ `SSRVLSS14506` | `ErrorCode`의 메시지                            |
| 유효하지 않은 관리자 JWT (`Authorization: Bearer`)            | `JwtAuthenticationFilter`     | 401                            | 없음                                   | 없음 (응답 본문 없음)                           |
| 그 밖의 예외                                                  | Spring Boot 기본 에러 처리    | 대부분 500                     | 없음                                   | 없음 (Spring Boot 기본 에러 본문)               |

**■ 요청 본문 검증 실패 응답 예시**

```json
{
  "code": "9999",
  "description": "<필드 검증 메시지 1>; <필드 검증 메시지 2>"
}
```

**■ 에러 코드로 변환되지 않는 예외**

`GlobalControllerAdvice`는 `OpenDidException`, `HttpMessageNotReadableException`, `MethodArgumentNotValidException`만 처리한다. 그 밖의 예외는 LSS 형식(`code`/`description`)이 아닌 Spring Boot 기본 에러 응답으로 반환된다. 현재 코드에서 이에 해당하는 대표적인 경우는 다음과 같다.

- Agent VC Metadata 조회·상태 변경 시 `vcId`에 해당하는 VC Metadata가 없음 (`IllegalArgumentException`)
- DID Document 등록 시 요청 DID Document의 Multibase 디코딩 또는 파싱 실패
- DID URL의 `versionId` 값이 숫자가 아님
- DB 제약 조건 위반 등 처리되지 않은 DB 예외

토큰이 없는 경우 등 관리자 API(`/lss/admin/**`)의 그 밖의 인가 실패도 Spring Security 기본 동작을 따르며 LSS 에러 코드를 반환하지 않는다.

### 4.3. 에러 처리 시 유의 사항

1. **HTTP 상태가 아니라 `code`로 분기한다**: 요청 내용이 원인인 에러 중에도 HTTP 500을 반환하는 코드가 있다(`SSRVLSS11001`, `9999`, `SSRVLSS12003`, `SSRVLSS12502`). 또한 대상을 찾지 못한 경우에도 404가 아닌 400 또는 500이 반환된다.
2. **재시도는 일시적 장애에만 적용한다**: 위 1번처럼 요청 데이터 때문에 발생한 5xx 에러는 같은 요청을 다시 보내도 결과가 같다.
3. **API Key 검증은 서버 설정에 따라 동작한다**: `server_config` 테이블의 `API_KEY_VALIDATION_ENABLED` 값이 `true`, `1`, `Y`, `yes` 중 하나일 때만 검증하며, 비활성화되어 있으면 `SSRVLSS14502` ~ `SSRVLSS14506`가 반환되지 않는다. API Key는 `X-API-Key` 헤더(없으면 `API-Key` 헤더)로 전달한다.
4. **API 로그의 에러 코드**: `@ArticleLog`가 지정된 API는 API 로그에 에러 코드가 기록되며, 이 값은 `GlobalControllerAdvice`가 설정한다. 요청 본문 검증 실패는 응답 코드(`9999`)와 달리 `REQUEST_VALIDATION_ERROR`로 기록된다.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 5. 에러 코드 목록

각 표의 "메시지" 열은 응답의 `description`에 담기는 원문이다. "현재 발생 지점 없음"으로 표시한 코드는 정의만 되어 있고 현재 코드에서 반환되지 않는다.

### 5.1. 공통 에러 (10000 ~ 10999)

| 에러 코드     | 상수명            | HTTP 상태 | 메시지                   | 설명                                                                                                   |
| ------------- | ----------------- | --------- | ------------------------ | ------------------------------------------------------------------------------------------------------ |
| SSRVLSS10000  | CLIENT_ERROR      | 400       | Client Error             | 일반 클라이언트 오류. 현재 발생 지점 없음                                                             |
| SSRVLSS10001  | SERVER_ERROR      | 500       | Server Error             | 일반 서버 오류. 현재 발생 지점 없음                                                                   |
| SSRVLSS10002  | ENCODING_FAILED   | 500       | Failed to encoding data. | 데이터 인코딩 실패. 현재 발생 지점 없음                                                               |
| SSRVLSS10003  | DECODING_FAILED   | 400       | Failed to decoding data. | VC Schema, Credential Schema, Credential Definition 등록 시 요청 데이터의 디코딩 또는 파싱 중 예기치 않은 오류 발생 |
| SSRVLSS10004  | ENCRYPTION_FAILED | 500       | Failed to encrypt data.  | 데이터 암호화 실패. 현재 발생 지점 없음                                                               |
| SSRVLSS100015 | DECRYPTION_FAILED | 400       | Failed to decrypt data.  | 데이터 복호화 실패. 코드 번호가 6자리로 정의되어 있음([2. 에러 코드 형식](#2-에러-코드-형식) 참고). 현재 발생 지점 없음 |

### 5.2. API 처리 에러 (11000 ~ 11499)

| 에러 코드    | 상수명                  | HTTP 상태 | 메시지                | 설명                                                                                                  |
| ------------ | ----------------------- | --------- | --------------------- | ----------------------------------------------------------------------------------------------------- |
| SSRVLSS11000 | VERIFY_SIGN_FAIL        | 400       | Verify Signature Fail | DID Document 등록 요청의 서명(proof) 검증 실패                                                        |
| SSRVLSS11001 | REQUEST_BODY_UNREADABLE | 500       | `""` (빈 문자열)      | 요청 본문을 읽거나 역직렬화할 수 없음(잘못된 JSON, 잘못된 enum 값 등). 메시지가 빈 문자열이므로 `description`도 빈 문자열로 반환됨 |

### 5.3. DID 에러 (11500 ~ 11999)

| 에러 코드    | 상수명                   | HTTP 상태 | 메시지                                                  | 설명                                                                                                          |
| ------------ | ------------------------ | --------- | ------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| SSRVLSS11500 | DID_DOC_VERSION_MISMATCH | 400       | DidDoc version mismatch                                 | 이미 등록된 DID의 DID Document를 등록할 때 `versionId`가 마지막 버전 + 1이 아님                              |
| SSRVLSS11501 | DID_NOT_FOUND            | 400       | DID not found                                           | 요청한 DID를 추출할 수 없거나 등록되어 있지 않음(DID Document 조회·상태 변경, controller DID, VC Schema·Credential Schema 발급자 DID) |
| SSRVLSS11502 | ROLE_TYPE_MISMATCH       | 400       | Role type mismatch                                      | 발급자 DID의 역할이 요구 역할과 다름(VC Schema: ISSUER 또는 TAS, Credential Schema: ISSUER)                   |
| SSRVLSS11503 | DID_NOT_ACTIVATED        | 400       | DID not activated                                       | 발급자 DID의 상태가 ACTIVATED가 아님                                                                          |
| SSRVLSS11504 | DID_DOC_NOT_FOUND        | 400       | Failed to find DID Document: DID Document not found     | 해당 DID·버전의 DID Document를 찾을 수 없음. 관리자 콘솔에서 DID를 ID 또는 DID로 조회할 때 DID가 없는 경우에도 반환됨 |
| SSRVLSS11505 | ROLE_DID_NOT_FOUND       | 400       | Failed to find DID: DID not found by Role               | 역할 기준 DID 조회 실패. 이 코드를 던지는 메서드(`DidQueryServiceImpl.findDidFirstByRole`)가 호출되지 않아 현재 발생 지점 없음 |
| SSRVLSS11506 | TA_DID_DOC_NOT_FOUND     | 400       | Failed to find DID Document: TA DID Document not found  | TA DID Document 조회 실패. 현재 발생 지점 없음                                                                |

### 5.4. VC 에러 (12000 ~ 12499)

| 에러 코드    | 상수명                       | HTTP 상태 | 메시지                                                    | 설명                                                                                                    |
| ------------ | ---------------------------- | --------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| SSRVLSS12000 | REVOKED_VC_CANNOT_UPDATE     | 400       | A revoked VC cannot be updated.                           | 폐기(REVOKED)된 VC의 상태 변경 요청                                                                     |
| SSRVLSS12001 | INVALID_VC_SCHEMA            | 400       | Invalid VC Schema.                                        | 디코딩한 VC Schema의 JSON 구문 오류 |
| SSRVLSS12002 | VC_SCHEMA_ALREADY_REGISTERED | 400       | Failed to register VC Schema: VC Schema already exists.   | 같은 Schema ID의 VC Schema가 이미 등록되어 있음                                                         |
| SSRVLSS12003 | VC_SCHEMA_NOT_FOUND          | 500       | VC Schema not found.                                      | VC Schema를 찾을 수 없음(Agent 조회, 관리자 콘솔 조회). 관리자 콘솔에서 VC Metadata를 ID 또는 VC ID로 조회할 때 VC Metadata가 없는 경우에도 이 코드가 반환됨 |
| SSRVLSS12004 | VC_STATUS_HISTORY_NOT_FOUND  | 400       | VC Status History not found.                              | 관리자 콘솔에서 VC 상태 이력을 ID로 조회할 때 이력이 없음                                               |

### 5.5. ZKP 에러 (12500 ~ 12999)

| 에러 코드    | 상수명                               | HTTP 상태 | 메시지                                                                           | 설명                                                                                          |
| ------------ | ------------------------------------ | --------- | -------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| SSRVLSS12500 | INVALID_CREDENTIAL_SCHEMA_ID         | 400       | Invalid Credential Schema ID.                                                    | Credential Schema ID 형식 오류(비어 있음, `:`로 나눈 항목이 4개 미만, `did`로 시작하지 않음) |
| SSRVLSS12501 | CREDENTIAL_SCHEMA_ALREADY_REGISTERED | 400       | Failed to register Credential Schema: Credential Schema already exists.          | 같은 ID의 Credential Schema가 이미 등록되어 있음                                              |
| SSRVLSS12502 | CREDENTIAL_SCHEMA_NOT_FOUND          | 500       | Credential Schema not found.                                                     | Credential Schema를 찾을 수 없음. Credential Definition 등록 시 참조하는 Credential Schema가 없는 경우에도 반환됨 |
| SSRVLSS12503 | CREDENTIAL_DEFINITION_ALREADY_EXISTS | 400       | Failed to register Credential Definition: Credential Definition already exists.  | 같은 ID의 Credential Definition이 이미 등록되어 있음                                          |
| SSRVLSS12504 | CREDENTIAL_DEFINITION_NOT_FOUND      | 400       | Credential Definition not found.                                                 | Credential Definition을 찾을 수 없음                                                          |
| SSRVLSS12505 | INVALID_CREDENTIAL_SCHEMA            | 400       | Invalid Credential Schema.                                                       | 디코딩한 Credential Schema의 JSON 구문 오류 |
| SSRVLSS12506 | INVALID_CREDENTIAL_DEFINITION        | 400       | Invalid Credential Definition.                                                   | Credential Definition JSON 구문 오류                                                          |

### 5.6. 정책 에러 (13000 ~ 13499)

| 에러 코드    | 상수명                           | HTTP 상태 | 메시지                                                   | 설명                                                          |
| ------------ | -------------------------------- | --------- | -------------------------------------------------------- | ------------------------------------------------------------- |
| SSRVLSS13000 | TERMINATED_STATUS_CAN_NOT_CHANGE | 400       | Terminated DIDs can't change their status.               | TERMINATED 상태인 DID의 상태 변경 요청                        |
| SSRVLSS13001 | REVOKED_STATUS_CAN_NOT_CHANGE    | 400       | Revoked DIDs can't change their status to (De)Activate.  | REVOKED 상태인 DID를 TERMINATED 이외의 상태로 변경 요청       |
| SSRVLSS13002 | DID_ROLE_MISMATCH_TA             | 400       | The DID's Role is not TA.                                | DID Document 등록 요청의 controller DID 역할이 TAS가 아님     |

### 5.7. DB 에러 (13500 ~ 13999)

| 에러 코드    | 상수명              | HTTP 상태 | 메시지                     | 설명                                                                  |
| ------------ | ------------------- | --------- | -------------------------- | --------------------------------------------------------------------- |
| SSRVLSS13500 | DB_CONNECTION_ERROR | 500       | Database connection error. | DB 연결 오류. 현재 발생 지점 없음                                     |
| SSRVLSS13501 | DB_QUERY_ERROR      | 500       | Database query error.      | DB 조회 오류. 현재 발생 지점 없음                                     |
| SSRVLSS13502 | DB_INSERT_ERROR     | 500       | Database insert error.     | VC Schema, Credential Schema, Credential Definition 저장 중 오류 발생 |
| SSRVLSS13503 | DB_UPDATE_ERROR     | 500       | Database update error.     | DB 수정 오류. 현재 발생 지점 없음                                     |
| SSRVLSS13504 | DB_DELETE_ERROR     | 500       | Database delete error.     | DB 삭제 오류. 현재 발생 지점 없음                                     |

### 5.8. 관리자 에러 (14000 ~ 14499)

| 에러 코드    | 상수명                             | HTTP 상태 | 메시지                                                          | 설명                                                                                                        |
| ------------ | ---------------------------------- | --------- | --------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| SSRVLSS14000 | ADMIN_INFO_NOT_FOUND               | 400       | Admin not found.                                                | 관리자 계정이 없거나 비밀번호가 일치하지 않음(로그인, 비밀번호 변경, 로그인 ID·비밀번호 변경, 관리자 조회) |
| SSRVLSS14001 | ADMIN_ALREADY_EXISTS               | 400       | Failed to register admin: admin is already registered.          | 관리자 등록 또는 로그인 ID 변경 시 같은 로그인 ID가 이미 존재                                               |
| SSRVLSS14002 | ADMIN_PASSWORD_POLICY_NOT_FOUND    | 500       | Failed to find admin password policy: policy is not registered. | 관리자 비밀번호 정책이 등록되어 있지 않음                                                                   |
| SSRVLSS14003 | DAILY_OTP_SEND_LIMIT_EXCEEDED      | 400       | Daily OTP limit exceeded.                                       | 비밀번호 재설정 OTP의 일일 발송 한도를 초과했거나 발송이 차단된 상태                                        |
| SSRVLSS14004 | OTP_COOLDOWN_NOT_EXPIRED           | 400       | Please wait before requesting another OTP.                      | OTP 재요청 대기 시간이 아직 지나지 않음                                                                     |
| SSRVLSS14005 | INVALID_OTP                        | 400       | Invalid OTP code.                                               | OTP 형식이 잘못되었거나, 유효한(미사용·미만료) OTP가 없거나, OTP가 일치하지 않음                            |
| SSRVLSS14006 | OTP_EXPIRED                        | 400       | OTP has expired. Please request a new one.                      | OTP 만료. 만료된 OTP는 `INVALID_OTP`로 처리되므로 현재 발생 지점 없음                                       |
| SSRVLSS14007 | OTP_VERIFY_LIMIT_EXCEEDED          | 400       | Too many verification attempts.                                 | OTP 검증 시도 횟수 초과                                                                                     |
| SSRVLSS14008 | ADMIN_NOT_FOUND_FOR_PASSWORD_RESET | 400       | Admin not found.                                                | OTP 발송·검증, OTP 발송 한도 조회·차단 해제·초기화 시 로그인 ID에 해당하는 관리자가 없음. 메시지가 `ADMIN_INFO_NOT_FOUND`와 같으므로 `code`로 구분해야 함 |
| SSRVLSS14009 | EMAIL_SEND_FAILED                  | 500       | Failed to send email.                                           | 비밀번호 재설정 OTP 이메일 발송 처리 중 `OpenDidException` 발생                                             |

### 5.9. API Key 에러 (14500 ~ 14999)

| 에러 코드    | 상수명                          | HTTP 상태 | 메시지                                                    | 설명                                                                                         |
| ------------ | ------------------------------- | --------- | --------------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| SSRVLSS14500 | API_KEY_NOT_FOUND               | 400       | API Key not found.                                        | 관리자 콘솔에서 API Key를 찾을 수 없음. 비활성화된 API Key의 갱신을 요청한 경우에도 반환됨  |
| SSRVLSS14501 | API_KEY_GENERATION_FAILED       | 500       | Failed to generate unique API key after maximum attempts. | 최대 시도 횟수 안에 중복되지 않는 API Key를 생성하지 못했거나 API Key 해시 생성에 실패함     |
| SSRVLSS14502 | API_KEY_MISSING                 | 401       | API Key is missing in request header.                     | Agent API 요청에 `X-API-Key`/`API-Key` 헤더가 없거나 값이 비어 있음                          |
| SSRVLSS14503 | API_KEY_INVALID                 | 401       | Invalid API key.                                          | 등록되지 않았거나 비활성화된 API Key                                                         |
| SSRVLSS14504 | API_KEY_INSUFFICIENT_PERMISSION | 403       | Insufficient permissions for this API.                    | API Key의 역할이 해당 API에 필요한 권한에 미치지 못함([6.1. API Key 인증 (공통)](#61-api-key-인증-공통) 참고) |
| SSRVLSS14505 | API_KEY_VALIDATION_ERROR        | 500       | API key validation failed.                                | API Key 검증 처리 중 `OpenDidException` 발생                                                 |
| SSRVLSS14506 | API_KEY_EXPIRED                 | 401       | API key has expired.                                      | 만료일(`expires_at`)이 지난 API Key. Admin Console에서 갱신하면 다시 사용할 수 있음        |

### 5.10. 서버 설정 에러 (15000 ~ 15499)

| 에러 코드    | 상수명                      | HTTP 상태 | 메시지                                  | 설명                                                          |
| ------------ | --------------------------- | --------- | --------------------------------------- | ------------------------------------------------------------- |
| SSRVLSS15000 | SERVER_CONFIG_NOT_FOUND     | 400       | Server configuration not found.         | 서버 설정 조회 실패. 현재 발생 지점 없음                      |
| SSRVLSS15001 | SERVER_CONFIG_KEY_NOT_FOUND | 400       | Server configuration key not found.     | 관리자 콘솔에서 존재하지 않는 설정 키를 조회하거나 수정함     |
| SSRVLSS15002 | SERVER_CONFIG_UPDATE_FAILED | 500       | Failed to update server configuration.  | 서버 설정 수정 실패. 현재 발생 지점 없음                      |

### 5.11. 기타 에러 (90000 ~ 99999)

| 에러 코드    | 상수명 | HTTP 상태 | 메시지 | 설명                                                                                                                       |
| ------------ | ------ | --------- | ------ | -------------------------------------------------------------------------------------------------------------------------- |
| SSRVLSS99999 | TODO   | 500       | TODO.  | 전용 코드가 정해지지 않은 오류에 쓰는 임시 코드. 해시 생성(`BaseDigestUtil.generateHash`) 실패 시 발생하며, DID Document 등록의 서명 검증 단계에서 반환될 수 있음 |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 6. Agent API별 에러 코드

Agent API(기본 경로 `/lss/api/v1`)별로 반환될 수 있는 에러 코드를 정리한다. 이 내용은 `sample` 프로필이 아닐 때 동작하는 서비스 구현(`repository/v1/agent/service`의 `*Impl`)과 `base` 패키지의 공통 처리를 기준으로 한다. `sample` 프로필의 서비스 구현은 에러 코드를 반환하지 않는다.

모든 Agent API에는 [6.1](#61-api-key-인증-공통)의 API Key 인증 에러가 공통으로 적용되며, 요청 본문이 있는 POST/PUT API는 본문을 읽을 수 없을 때 `SSRVLSS11001`(REQUEST_BODY_UNREADABLE)을 반환한다. 아래 각 표에는 이 공통 에러를 제외한 API별 에러만 적는다. LSS 에러 코드로 변환되지 않는 예외는 [4.2. 처리 경로별 응답](#42-처리-경로별-응답)을 참고한다.

### 6.1. API Key 인증 (공통)

모든 Agent API에는 `@ApiKeyRequired`가 지정되어 있어, API Key 검증이 활성화된 경우 요청이 컨트롤러에 도달하기 전에 `ApiKeyValidationInterceptor`가 API Key를 검증한다. API Key 역할의 권한 순서는 TAS > ISSUER > READ이며, 상위 역할은 하위 역할이 필요한 API를 호출할 수 있다.

| API                                                                                                                     | 필요 권한 | 허용되는 API Key 역할 |
| ----------------------------------------------------------------------------------------------------------------------- | --------- | --------------------- |
| POST `/did-doc`, PUT `/did-doc`                                                                                         | TAS       | TAS                   |
| POST `/vc-meta`, PUT `/vc-meta`, POST `/vc-schema`, POST `/credential-schema`, POST `/credential-definition`            | ISSUER    | TAS, ISSUER           |
| GET `/did-doc`, GET `/vc-meta`, GET `/vc-schema`, GET `/credential-schema`, GET `/credential-definition`                | READ      | TAS, ISSUER, READ     |

| 에러 코드    | 상수명                          | HTTP 상태 | 발생 조건                                                        |
| ------------ | ------------------------------- | --------- | ---------------------------------------------------------------- |
| SSRVLSS14502 | API_KEY_MISSING                 | 401       | `X-API-Key`/`API-Key` 헤더가 없거나 값이 비어 있음               |
| SSRVLSS14503 | API_KEY_INVALID                 | 401       | 등록되지 않았거나 비활성화된 API Key                             |
| SSRVLSS14504 | API_KEY_INSUFFICIENT_PERMISSION | 403       | API Key 역할이 위 표의 허용 역할에 포함되지 않음                 |
| SSRVLSS14505 | API_KEY_VALIDATION_ERROR        | 500       | 검증 처리 중 `OpenDidException` 발생                             |
| SSRVLSS14506 | API_KEY_EXPIRED                 | 401       | 만료일(`expires_at`)이 지난 API Key                              |

### 6.2. DID Document API

경로: `/lss/api/v1/did-doc`

| 메서드 | 에러 코드    | 상수명                           | HTTP 상태 | 발생 조건                                                                                     |
| ------ | ------------ | -------------------------------- | --------- | --------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS11501 | DID_NOT_FOUND                    | 400       | 서명 검증에 쓰는 controller DID가 등록되어 있지 않음(원장에 DID가 하나 이상 있을 때만 검증)  |
| POST   | SSRVLSS13002 | DID_ROLE_MISMATCH_TA             | 400       | controller DID의 역할이 TAS가 아님                                                            |
| POST   | SSRVLSS11504 | DID_DOC_NOT_FOUND                | 400       | controller DID의 DID Document를 찾을 수 없음                                                  |
| POST   | SSRVLSS99999 | TODO                             | 500       | 서명 검증용 해시 생성 실패                                                                    |
| POST   | SSRVLSS11000 | VERIFY_SIGN_FAIL                 | 400       | 요청 proof의 서명 검증 실패                                                                   |
| POST   | SSRVLSS11500 | DID_DOC_VERSION_MISMATCH         | 400       | 이미 등록된 DID인데 새 DID Document의 `versionId`가 마지막 버전 + 1이 아님                    |
| GET    | SSRVLSS11501 | DID_NOT_FOUND                    | 400       | `did` 파라미터에서 DID를 추출할 수 없거나 등록되지 않은 DID                                   |
| GET    | SSRVLSS11504 | DID_DOC_NOT_FOUND                | 400       | 요청한 버전(없으면 현재 버전)의 DID Document가 없음. REVOKED/TERMINATED 상태이면 폐기된 문서에서 조회 |
| PUT    | SSRVLSS11501 | DID_NOT_FOUND                    | 400       | 요청의 `did`에서 DID를 추출할 수 없거나 등록되지 않은 DID                                     |
| PUT    | SSRVLSS13000 | TERMINATED_STATUS_CAN_NOT_CHANGE | 400       | 현재 상태가 TERMINATED                                                                        |
| PUT    | SSRVLSS13001 | REVOKED_STATUS_CAN_NOT_CHANGE    | 400       | 현재 상태가 REVOKED인데 변경할 상태가 TERMINATED가 아님                                       |
| PUT    | SSRVLSS11504 | DID_DOC_NOT_FOUND                | 400       | ACTIVATED 또는 DEACTIVATED로 변경할 때 해당 버전의 DID Document가 없음                        |

### 6.3. VC Metadata API

경로: `/lss/api/v1/vc-meta`

| 메서드 | 에러 코드    | 상수명                   | HTTP 상태 | 발생 조건                                                                      |
| ------ | ------------ | ------------------------ | --------- | ------------------------------------------------------------------------------ |
| POST   | 9999         | -                        | 500       | 요청 본문(`VcMeta`)이 모델에 선언된 Bean Validation 제약을 위반함(`@Valid`)    |
| PUT    | SSRVLSS12000 | REVOKED_VC_CANNOT_UPDATE | 400       | 이미 REVOKED 상태인 VC의 상태 변경 요청                                        |

GET과 PUT에서 `vcId`에 해당하는 VC Metadata가 없으면 `IllegalArgumentException`이 발생하며, LSS 에러 코드가 아닌 Spring Boot 기본 에러 응답이 반환된다. GET은 이 경우 외에 API별 에러 코드가 없다.

### 6.4. VC Schema API

경로: `/lss/api/v1/vc-schema`

| 메서드 | 에러 코드    | 상수명                       | HTTP 상태 | 발생 조건                                                                                                       |
| ------ | ------------ | ---------------------------- | --------- | --------------------------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS12001 | INVALID_VC_SCHEMA            | 400       | 디코딩한 VC Schema의 JSON 구문 오류 |
| POST   | SSRVLSS10003 | DECODING_FAILED              | 400       | VC Schema의 Multibase 디코딩 실패, 또는 데이터 모델 제약 검증 실패(필수 항목 id, schema, title, description, metadata(language, formatVersion), credentialSubject 누락 등) |
| POST   | SSRVLSS12002 | VC_SCHEMA_ALREADY_REGISTERED | 400       | 같은 Schema ID의 VC Schema가 이미 등록되어 있음                                                                 |
| POST   | SSRVLSS11501 | DID_NOT_FOUND                | 400       | 요청의 `did`(발급자 DID)가 등록되어 있지 않음                                                                   |
| POST   | SSRVLSS11502 | ROLE_TYPE_MISMATCH           | 400       | 발급자 DID의 역할이 ISSUER 또는 TAS가 아님                                                                      |
| POST   | SSRVLSS11503 | DID_NOT_ACTIVATED            | 400       | 발급자 DID의 상태가 ACTIVATED가 아님                                                                            |
| POST   | SSRVLSS13502 | DB_INSERT_ERROR              | 500       | VC Schema 저장 중 오류                                                                                          |
| GET    | SSRVLSS12003 | VC_SCHEMA_NOT_FOUND          | 500       | `schemaId`에 해당하는 VC Schema가 없음                                                                          |

### 6.5. Credential Schema API

경로: `/lss/api/v1/credential-schema`

| 메서드 | 에러 코드    | 상수명                               | HTTP 상태 | 발생 조건                                                                                     |
| ------ | ------------ | ------------------------------------ | --------- | --------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS12505 | INVALID_CREDENTIAL_SCHEMA            | 400       | 디코딩한 Credential Schema의 JSON 구문 오류 |
| POST   | SSRVLSS10003 | DECODING_FAILED                      | 400       | Credential Schema의 Multibase 디코딩 실패 등 파싱 중 예기치 않은 오류                         |
| POST   | SSRVLSS12500 | INVALID_CREDENTIAL_SCHEMA_ID         | 400       | Credential Schema ID가 비어 있거나, `:`로 나눈 항목이 4개 미만이거나, `did`로 시작하지 않음   |
| POST   | SSRVLSS12501 | CREDENTIAL_SCHEMA_ALREADY_REGISTERED | 400       | 같은 ID의 Credential Schema가 이미 등록되어 있음                                              |
| POST   | SSRVLSS11501 | DID_NOT_FOUND                        | 400       | Credential Schema ID에서 추출한 발급자 DID(`did:<method>:<identifier>`)가 등록되어 있지 않음  |
| POST   | SSRVLSS11502 | ROLE_TYPE_MISMATCH                   | 400       | 발급자 DID의 역할이 ISSUER가 아님(VC Schema와 달리 TAS도 허용하지 않음)                       |
| POST   | SSRVLSS11503 | DID_NOT_ACTIVATED                    | 400       | 발급자 DID의 상태가 ACTIVATED가 아님                                                          |
| POST   | SSRVLSS13502 | DB_INSERT_ERROR                      | 500       | Credential Schema 저장 중 오류                                                                |
| GET    | SSRVLSS12502 | CREDENTIAL_SCHEMA_NOT_FOUND          | 500       | `schemaId`에 해당하는 Credential Schema가 없음                                                |

> 필수 항목이 누락된 경우: 서비스의 필수 항목 검사가 `Map.of`에 null 값을 넣는 과정에서 `NullPointerException`이 발생하므로, `SSRVLSS12505` 대신 LSS 에러 코드 형식이 아닌 응답(HTTP 500)이 반환된다. [4.2. 처리 경로별 응답](#42-처리-경로별-응답)을 참고한다.

### 6.6. Credential Definition API

경로: `/lss/api/v1/credential-definition`

| 메서드 | 에러 코드    | 상수명                               | HTTP 상태 | 발생 조건                                                                                   |
| ------ | ------------ | ------------------------------------ | --------- | ------------------------------------------------------------------------------------------- |
| POST   | SSRVLSS12506 | INVALID_CREDENTIAL_DEFINITION        | 400       | 디코딩한 Credential Definition의 JSON 구문 오류                                             |
| POST   | SSRVLSS10003 | DECODING_FAILED                      | 400       | Credential Definition의 Multibase 디코딩 실패 등 파싱 중 예기치 않은 오류 |
| POST   | SSRVLSS12502 | CREDENTIAL_SCHEMA_NOT_FOUND          | 500       | `schemaId`가 가리키는 Credential Schema가 등록되어 있지 않음                                |
| POST   | SSRVLSS12503 | CREDENTIAL_DEFINITION_ALREADY_EXISTS | 400       | 같은 ID의 Credential Definition이 이미 등록되어 있음                                        |
| POST   | SSRVLSS13502 | DB_INSERT_ERROR                      | 500       | Credential Definition 저장 중 오류                                                          |
| GET    | SSRVLSS12504 | CREDENTIAL_DEFINITION_NOT_FOUND      | 400       | `definitionId`에 해당하는 Credential Definition이 없음                                      |

> 필수 항목이 누락된 경우: 서비스의 필수 항목 검사가 `Map.of`에 null 값을 넣는 과정에서 `NullPointerException`이 발생하므로, `SSRVLSS12505` 대신 LSS 에러 코드 형식이 아닌 응답(HTTP 500)이 반환된다. [4.2. 처리 경로별 응답](#42-처리-경로별-응답)을 참고한다.
