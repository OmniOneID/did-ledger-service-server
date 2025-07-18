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

- 일자: 2025-06-12
- 버전: v2.0.0

목차
---
<!-- TOC tocDepth:2..3 chapterDepth:2..6 -->

- [1. 개요](#1-개요)
- [2. 에러 코드 형식](#2-에러-코드-형식)
- [3. 에러 코드 분류](#3-에러-코드-분류)
- [4. API 에러 코드](#4-api-에러-코드)
  - [4.1. DID Document 등록 에러](#41-did-document-등록-에러)
  - [4.2. DID Document 조회 에러](#42-did-document-조회-에러)
  - [4.3. DID Document 수정 에러](#43-did-document-수정-에러)
  - [4.4. VC Metadata 등록 에러](#44-vc-metadata-등록-에러)
  - [4.5. VC Metadata 조회 에러](#45-vc-metadata-조회-에러)
  - [4.6. 인증/권한 에러](#46-인증권한-에러)
  - [4.7. 블록체인 트랜잭션 에러](#47-블록체인-트랜잭션-에러)
  - [4.8. 시스템 에러](#48-시스템-에러)

<!-- /TOC -->

## 1. 개요

본 문서는 DID Ledger Service Server에서 사용하는 에러 코드를 정의한다. 각 에러 코드는 블록체인 트랜잭션, DID Document 관리, VC Metadata 운영 중 발생할 수 있는 실패에 대한 구체적인 정보를 제공한다.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 2. 에러 코드 형식

DID Ledger Service는 표준화된 에러 코드 형식을 사용한다:

**형식**: `SRVLDG{분류}{번호}`

- **SRVLDG**: DID Ledger Service 서비스 식별자
- **분류**: 2자리 분류 식별자
- **번호**: 분류 내 3자리 순차 번호

**예시**: `SRVLDG00100` - 유효하지 않은 DID Document 형식

<div style="page-break-after: always; margin-top: 50px;"></div>

## 3. 에러 코드 분류

| 분류     | 범위         | 설명                                  |
| -------- | ------------ | ------------------------------------- |
| 00       | 00000-00999  | DID Document 관련 에러                |
| 01       | 01000-01999  | VC Metadata 관련 에러                 |
| 02       | 02000-02999  | 인증/권한 에러                        |
| 03       | 03000-03999  | 블록체인 트랜잭션 에러                |
| 04       | 04000-04999  | 시스템 및 인프라 에러                 |
| 05       | 05000-05999  | 검증 및 형식 에러                     |
| 99       | 99000-99999  | 알 수 없는 및 예상치 못한 에러        |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 4. API 에러 코드

### 4.1. DID Document 등록 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG00100  | 400       | 유효하지 않은 DID Document 형식      | DID Document JSON 구조 확인                |
| SRVLDG00101  | 400       | 유효하지 않은 디지털 서명            | 올바른 개인키로 서명 검증                   |
| SRVLDG00102  | 400       | 이미 존재하는 DID                    | 기존 DID는 업데이트 API 사용               |
| SRVLDG00103  | 400       | 유효하지 않은 invoker nonce          | 트랜잭션용 고유 nonce 제공                 |
| SRVLDG00104  | 400       | DID Document 검증 실패               | W3C 표준에 따른 DID Document 확인          |
| SRVLDG00200  | 500       | DID Document 등록 실패               | 블록체인 연결 상태 확인                    |
| SRVLDG00201  | 500       | 데이터베이스 트랜잭션 실패           | 재시도 또는 시스템 관리자 문의             |

### 4.2. DID Document 조회 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG00300  | 400       | 존재하지 않는 DID                    | DID 식별자가 올바른지 확인                 |
| SRVLDG00301  | 400       | 유효하지 않은 DID 형식               | did:omn:ledger 구문 확인                   |
| SRVLDG00302  | 400       | 비활성화된 DID                       | 비활성화된 DID Document는 조회 불가        |
| SRVLDG00400  | 500       | DID Document 조회 실패               | 시스템 상태 확인 후 재시도                 |
| SRVLDG00401  | 500       | 블록체인 쿼리 실패                   | 블록체인 노드 연결 상태 확인               |

### 4.3. DID Document 수정 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG00500  | 400       | 존재하지 않는 DID                    | 수정 전 DID 등록 필요                      |
| SRVLDG00501  | 400       | 유효하지 않은 서명                   | DID 소유자 키로 서명 검증                  |
| SRVLDG00502  | 400       | 권한 없는 수정 시도                  | DID 소유자만 수정 가능                     |
| SRVLDG00503  | 400       | 유효하지 않은 버전 업데이트          | 버전 순서 확인                             |
| SRVLDG00504  | 400       | 비활성화 후 수정                     | 비활성화된 DID Document는 수정 불가        |
| SRVLDG00600  | 500       | DID Document 수정 실패               | 블록체인 트랜잭션 상태 확인                |
| SRVLDG00601  | 500       | 버전 충돌 감지                       | 최신 버전으로 재시도                       |

### 4.4. VC Metadata 등록 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG00700  | 400       | 유효하지 않은 VC Metadata 형식       | VC Metadata JSON 구조 확인                 |
| SRVLDG00701  | 400       | 이미 존재하는 VC                     | 고유한 VC 식별자 사용                      |
| SRVLDG00702  | 400       | 유효하지 않은 서명                   | 발급자 키로 서명 검증                      |
| SRVLDG00703  | 400       | 유효하지 않은 VC 식별자              | 유효한 UUID 형식 사용                      |
| SRVLDG00704  | 400       | 필수 필드 누락                       | 모든 필수 VC Metadata 필드 포함            |
| SRVLDG00800  | 500       | VC Metadata 등록 실패                | 시스템 상태 확인 후 재시도                 |
| SRVLDG00801  | 500       | 메타데이터 저장 실패                 | 데이터베이스 연결 상태 확인                |

### 4.5. VC Metadata 조회 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG00900  | 400       | 존재하지 않는 VC                     | VC 식별자가 올바른지 확인                  |
| SRVLDG00901  | 400       | 유효하지 않은 VC ID 형식             | 유효한 UUID 형식 사용                      |
| SRVLDG00902  | 400       | 폐기된 VC                            | 폐기된 VC Metadata는 조회 불가             |
| SRVLDG01000  | 500       | VC Metadata 조회 실패                | 시스템 상태 확인 후 재시도                 |
| SRVLDG01001  | 500       | 데이터베이스 쿼리 실패               | 데이터베이스 연결 상태 확인                |

### 4.6. 인증/권한 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG02000  | 401       | 인증 필요                            | 유효한 인증 토큰 제공                      |
| SRVLDG02001  | 401       | 유효하지 않은 인증 토큰              | 인증 토큰 갱신                             |
| SRVLDG02002  | 401       | 만료된 인증 토큰                     | 새로운 인증 토큰 획득                      |
| SRVLDG02003  | 403       | 권한 부족                            | 권한 부여를 위해 관리자 문의               |
| SRVLDG02004  | 403       | 접근 거부                            | 필요한 접근 권한 확인                      |
| SRVLDG02005  | 403       | 허용되지 않은 IP 주소                | 승인된 IP 주소에서 접근                    |

### 4.7. 블록체인 트랜잭션 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG03000  | 500       | 블록체인 연결 실패                   | 블록체인 노드 상태 확인                    |
| SRVLDG03001  | 500       | 트랜잭션 타임아웃                    | 트랜잭션 재시도 또는 네트워크 확인         |
| SRVLDG03002  | 500       | 가스 부족                            | 트랜잭션 가스 한도 증가                    |
| SRVLDG03003  | 500       | 트랜잭션 되돌림                      | 트랜잭션 매개변수 확인                     |
| SRVLDG03004  | 500       | 블록 확인 실패                       | 블록 확인 대기                             |
| SRVLDG03005  | 500       | 스마트 컨트랙트 에러                 | 컨트랙트 상태 및 매개변수 확인             |

### 4.8. 시스템 에러

| 에러 코드    | HTTP 상태 | 설명                                  | 해결 방법                                   |
| ------------ | --------- | ------------------------------------ | ------------------------------------------- |
| SRVLDG04000  | 500       | 내부 서버 에러                       | 시스템 관리자 문의                         |
| SRVLDG04001  | 500       | 데이터베이스 연결 실패               | 데이터베이스 연결 상태 확인                |
| SRVLDG04002  | 500       | 설정 에러                            | 시스템 설정 확인                           |
| SRVLDG04003  | 503       | 서비스 일시적으로 사용 불가          | 서비스 복구 후 재시도                      |
| SRVLDG04004  | 500       | 메모리 할당 실패                     | 시스템 리소스 확인                         |
| SRVLDG04005  | 500       | 파일 시스템 에러                     | 디스크 공간 및 권한 확인                   |

**■ 일반 에러 응답 형식**

모든 API 에러는 다음 표준 응답 형식을 따른다:

```json
{
  "error": {
    "code": "SRVLDG00100",
    "message": "유효하지 않은 DID Document 형식",
    "detail": "필수 필드 누락: '@context'",
    "timestamp": "2025-06-12T10:00:00Z",
    "requestId": "req-12345"
  }
}
```

**■ 에러 처리 모범 사례**

1. **재시도 로직**: 5xx 에러에 대해 지수 백오프 재시도 구현
2. **로깅**: 디버깅 및 모니터링을 위한 에러 상세 정보 로그
3. **사용자 피드백**: 최종 사용자에게 의미 있는 에러 메시지 제공
4. **모니터링**: 중요한 에러 패턴에 대한 알림 설정
5. **문서화**: API 변경에 따른 에러 문서 업데이트 유지