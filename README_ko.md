Ledger Service Server
==

LedgerService 서버 Repository에 오신 것을 환영합니다. <br>
이 Repository는 LedgerService 서버(LSS)와 LSS Admin Console의 소스 코드, 문서, 관련 리소스를 포함하고 있습니다.

## S/W 사양
| 구분              | 내용                                               |
|-------------------|----------------------------------------------------|
| OS                | macOS / Linux / Windows 10 이상                     |
| Language          | Java 21 이상                                        |
| IDE               | IntelliJ IDEA                                       |
| Build System      | Gradle 8.7 (Gradle Wrapper 포함)                    |
| Compatibility     | JDK 21 이상                                         |
| Database          | PostgreSQL                                          |
| Frontend          | Node.js 22.9.0 (Gradle 빌드 시 자동 다운로드)       |
| 기타 요구사항      | 최소 2GB RAM, 10GB 디스크 공간 이상                  |

## 주요 기능
- DID Document 등록, 조회, 상태 변경
- VC Meta 등록, 조회, VC 상태 변경
- VC Schema, ZKP Credential Schema, ZKP Credential Definition 등록 및 조회
- `X-API-Key` Header를 이용한 Agent API(`/lss/api/v1`) 호출 API Key 인증 (Role: `TAS`, `ISSUER`, `READ`)
- 서버에 포함되어 제공되는 LSS Admin Console(`http://{host}:8098/`): DID, VC Metadata, VC Schema 조회, API Key 관리, 관리자 관리, 서버 설정, API 로그 및 감사 로그 조회

## 폴더 구조
프로젝트 디렉터리 내 주요 폴더와 문서에 대한 개요:

```
did-ledger-service-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE
├── dependencies-license.md
├── MAINTAINERS.md
├── README.md
├── README_ko.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   ├── api
│   │   ├── LedgerService_API.md
│   │   └── LedgerService_API_ko.md
│   ├── errorCode
│   │   ├── LedgerService_ErrorCode.md
│   │   └── LedgerService_ErrorCode_ko.md
│   └── installation
│       ├── OpenDID_LedgerService_InstallationAndOperation_Guide.md
│       └── OpenDID_LedgerService_InstallationAndOperation_Guide_ko.md
└── source
    ├── did-ledger-service-admin
    │   └── frontend
    └── did-ledger-service-server

```

<br/>

각 폴더와 파일에 대한 설명은 다음과 같습니다:

| 이름                         | 설명                                                  |
| ---------------------------- | ----------------------------------------------------- |
| CHANGELOG.md                 | 프로젝트의 버전별 변경 사항                           |
| CODE_OF_CONDUCT.md           | 기여자 행동 강령                                      |
| CONTRIBUTING.md              | 기여 지침과 절차                                      |
| LICENSE                      | 라이선스                                              |
| dependencies-license.md      | 프로젝트 의존 라이브러리의 라이선스 정보              |
| MAINTAINERS.md               | 프로젝트 유지 관리자 지침                             |
| RELEASE-PROCESS.md           | 새 버전 릴리스 절차                                   |
| SECURITY.md                  | 보안 정책 및 취약성 보고 방법                         |
| docs                         | 문서화                                                |
| ┖ api                        | API 가이드 문서                                       |
| ┖ errorCode                  | 오류 코드 및 문제 해결 가이드                         |
| ┖ installation               | 설치, 설정 및 운영 지침                               |
| source                       | 서버 및 관리자 콘솔 소스 코드 프로젝트                |
| ┖ did-ledger-service-admin   | LSS Admin Console 프론트엔드(React, TypeScript) 소스 코드 |
| ┖ did-ledger-service-server  | Ledger Service 서버(Spring Boot) 소스 코드            |

<br/>

## 설치 및 운영 가이드

Admin Console 사용 방법과 API Key 운영 방법을 포함한 Ledger Service 서버의 설치 및 운영에 대한 자세한 지침은 아래 가이드를 참조하십시오:
- [OpenDID Ledger Service Server Installation And Operation Guide](docs/installation/OpenDID_LedgerService_InstallationAndOperation_Guide_ko.md)


## API 참고 문서

API 문서는 두 가지 주요 유형으로 제공됩니다:

- **Ledger API**: Ledger 서버의 API 엔드포인트 및 사용법에 대한 자세한 참고 자료입니다.
   - [Ledger API Reference](docs/api/LedgerService_API_ko.md)
- **Error Code**: Ledger 서버가 반환하는 오류 코드에 대한 참고 자료입니다.
   - [Ledger Error Code Reference](docs/errorCode/LedgerService_ErrorCode_ko.md)

## Change Log

Change Log에는 버전별 변경 사항과 업데이트가 자세히 기록되어 있습니다. 다음에서 확인할 수 있습니다:
- [Change Log](./CHANGELOG.md)  
 
## OpenDID 시연 영상

OpenDID 시스템의 시연 영상을 보려면 [데모 Repository](https://github.com/OmniOneID/did-demo-server)를 방문하십시오. <br>

이 영상에서는 사용자 등록, VC 발급, VP 제출 프로세스 등 주요 기능을 시연합니다.

## 기여

기여 절차와 행동 강령에 대한 자세한 내용은 [CONTRIBUTING.md](CONTRIBUTING.md)와 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)를 참조해 주십시오.

## 라이선스
[Apache 2.0](LICENSE)
