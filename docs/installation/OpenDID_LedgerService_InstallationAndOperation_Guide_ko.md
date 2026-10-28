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

Open DID Ledger Service Server Installation And Operation Guide
==

- Date: 2026-10-27
- Version: v2.1.0

목차
==

- [1. 소개](#1-소개)
  - [1.1. 개요](#11-개요)
  - [1.2. Ledger Service 서버 정의](#12-ledger-service-서버-정의)
  - [1.3. 시스템 요구 사항](#13-시스템-요구-사항)
- [2. 사전 준비 사항](#2-사전-준비-사항)
  - [2.1. Git 설치](#21-git-설치)
  - [2.2. PostgreSQL 설치](#22-postgresql-설치)
  - [2.3. Node.js 설치](#23-nodejs-설치)
- [3. GitHub에서 소스 코드 복제하기](#3-github에서-소스-코드-복제하기)
  - [3.1. 소스코드 복제](#31-소스코드-복제)
  - [3.2. 디렉토리 구조](#32-디렉토리-구조)
- [4. 서버 구동 방법](#4-서버-구동-방법)
  - [4.1. IntelliJ IDEA로 구동하기 (Gradle 지원)](#41-intellij-idea로-구동하기-gradle-지원)
    - [4.1.1. IntelliJ IDEA 설치 및 설정](#411-intellij-idea-설치-및-설정)
    - [4.1.2. IntelliJ에서 프로젝트 열기](#412-intellij에서-프로젝트-열기)
    - [4.1.3. Gradle 빌드](#413-gradle-빌드)
    - [4.1.4. 서버 구동](#414-서버-구동)
    - [4.1.5. 서버 설정](#415-서버-설정)
  - [4.2. 콘솔 명령어로 구동하기](#42-콘솔-명령어로-구동하기)
    - [4.2.1. Gradle 빌드 명령어](#421-gradle-빌드-명령어)
    - [4.2.2. 서버 구동 방법](#422-서버-구동-방법)
    - [4.2.3. 서버 설정 방법](#423-서버-설정-방법)
- [5. 설정 가이드](#5-설정-가이드)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Spring 기본 설정](#511-spring-기본-설정)
    - [5.1.2. Jackson 기본 설정](#512-jackson-기본-설정)
    - [5.1.3. 서버 설정](#513-서버-설정)
    - [5.1.4. Actuator 설정](#514-actuator-설정)
  - [5.2. application-logging.yml](#52-application-loggingyml)
    - [5.2.1. 로깅 설정](#521-로깅-설정)
  - [5.3. application-database.yml](#53-application-databaseyml)
    - [5.3.1. 데이터베이스 연동 설정](#531-데이터베이스-연동-설정)
    - [5.3.2. 데이터베이스 스키마 (Liquibase)](#532-데이터베이스-스키마-liquibase)
  - [5.4. application-auth.yml](#54-application-authyml)
    - [5.4.1. Admin Console 인증(JWT) 설정](#541-admin-console-인증jwt-설정)
  - [5.5. application-tas.yml](#55-application-tasyml)
    - [5.5.1. TAS 연동 설정](#551-tas-연동-설정)
  - [5.6. application-spring-docs.yml](#56-application-spring-docsyml)
    - [5.6.1. API 문서(Swagger) 설정](#561-api-문서swagger-설정)
  - [5.7. application-jasypt.yml](#57-application-jasyptyml)
    - [5.7.1. Jasypt 설정](#571-jasypt-설정)
  - [5.8. application-wallet.yml](#58-application-walletyml)
- [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용)
  - [6.1. 프로파일 개요 (`sample`, `dev`, `prod`)](#61-프로파일-개요-sample-dev-prod)
    - [6.1.1. `sample` 프로파일](#611-sample-프로파일)
    - [6.1.2. `dev` 프로파일](#612-dev-프로파일)
    - [6.1.3. `prod` 프로파일](#613-prod-프로파일)
  - [6.2. 프로파일 설정 방법](#62-프로파일-설정-방법)
    - [6.2.1. IDE를 사용한 서버 구동 시](#621-ide를-사용한-서버-구동-시)
    - [6.2.2. 콘솔 명령어를 사용한 서버 구동 시](#622-콘솔-명령어를-사용한-서버-구동-시)
- [7. Admin Console](#7-admin-console)
  - [7.1. Admin Console 개요](#71-admin-console-개요)
  - [7.2. Admin Console 빌드 및 배포](#72-admin-console-빌드-및-배포)
    - [7.2.1. Gradle 빌드에 포함하여 배포](#721-gradle-빌드에-포함하여-배포)
    - [7.2.2. 프론트엔드 개발 서버 실행](#722-프론트엔드-개발-서버-실행)
  - [7.3. 최초 로그인 및 초기 설정](#73-최초-로그인-및-초기-설정)
    - [7.3.1. 초기 관리자 계정](#731-초기-관리자-계정)
    - [7.3.2. 최초 로그인 시 ID 및 비밀번호 변경](#732-최초-로그인-시-id-및-비밀번호-변경)
    - [7.3.3. 관리자 추가 및 비밀번호 정책](#733-관리자-추가-및-비밀번호-정책)
  - [7.4. 비밀번호 분실 시 재설정](#74-비밀번호-분실-시-재설정)
- [8. API Key 운영](#8-api-key-운영)
  - [8.1. API Key 개요](#81-api-key-개요)
  - [8.2. API Key 발급](#82-api-key-발급)
  - [8.3. API Key 비활성화, 활성화 및 갱신](#83-api-key-비활성화-활성화-및-갱신)
  - [8.4. API Key 검증 활성화](#84-api-key-검증-활성화)
  - [8.5. 호출 서버(TAS, Issuer) 설정](#85-호출-서버tas-issuer-설정)

# 1. 소개

## 1.1. 개요
본 문서는 DID Ledger Service 서버의 설치 및 구동에 관한 가이드를 제공합니다. 설치 과정, 설정 방법, 그리고 구동 절차를 단계별로 설명하여, 사용자가 이를 효율적으로 설치하고 운영할 수 있도록 안내합니다. 또한 Ledger Service 서버에 포함된 Admin Console의 사용 방법과 API Key 운영 방법을 함께 설명합니다.

OpenDID의 전체 설치에 대한 가이드는 [Open DID Installation Guide]를 참고해 주세요.

<br/>

## 1.2. Ledger Service 서버 정의

DID Ledger Service Server(LSS)는 PostgreSQL 데이터베이스를 활용하여 블록체인과 동일한 기능을 제공하는 대안 서비스입니다.

주요 기능:
- DID Document 생성, 등록, 상태변경
- VC Meta 생성, VC 상태 변경
- VC Schema 등록 및 관리
- ZKP Credential Schema, ZKP Credential Definition 등록 및 관리
- API Key 기반 Agent API 호출 인증 (Role: `TAS`, `ISSUER`, `READ`)
- LSS Admin Console: DID, VC Metadata, VC Schema 조회, API Key 관리, 관리자 관리, 서버 설정, API 로그 및 감사 로그 조회

이 서버를 통해 복잡한 블록체인 인프라 구축 없이도 안정적이고 효율적인 분산 신원 관리 시스템을 구축할 수 있습니다.

<br/>

## 1.3. 시스템 요구 사항
- **Java 21** 이상
- **Gradle**: 저장소에 포함된 Gradle Wrapper(Gradle 8.7)를 사용합니다.
- **PostgreSQL 13** 이상 (모든 프로파일에서 필요)
- **Node.js 22.9.0**: Gradle 빌드 시 Gradle Node Plugin이 자동으로 다운로드하여 사용하므로 별도로 설치할 필요가 없습니다. 프론트엔드 개발 서버를 직접 실행하는 경우에만 로컬에 Node.js와 npm이 필요합니다.
- 최초 빌드 시 Gradle, Maven 의존성, Node.js, npm 패키지를 다운로드하기 위한 네트워크 연결
- 최소 **2GB RAM** 및 **10GB 디스크 공간**

<br/>

# 2. 사전 준비 사항

이 장에서는 Open DID 프로젝트의 구성요소를 설치하기 전, 사전에 필요한 준비 항목들을 안내합니다.

## 2.1. Git 설치

`Git`은 분산 버전 관리 시스템으로, 소스 코드의 변경 사항을 추적하고 여러 개발자 간의 협업을 지원합니다. Git은 Open DID 프로젝트의 소스 코드를 관리하고 버전 관리를 위해 필수적입니다.

설치가 성공하면 다음 명령어를 사용하여 Git의 버전을 확인할 수 있습니다.

```bash
git --version
```

> **참고 링크**
> - [Git 설치 가이드](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 2.2. PostgreSQL 설치

Ledger Service 서버는 DID Document, VC Meta 등의 원장 데이터와 관리자, API Key, 서버 설정, 로그 데이터를 PostgreSQL에 저장합니다. 서버를 구동하기 전에 PostgreSQL을 설치하고 Ledger Service 서버가 사용할 데이터베이스와 사용자를 생성합니다.

저장소의 기본 설정(`application-database.yml`)은 다음 접속 정보를 사용합니다.

| 항목       | 기본값        |
| ---------- | ------------- |
| Host       | `127.0.0.1`   |
| Port       | `5430`        |
| Database   | `lss`         |
| Username   | `omn`         |
| Password   | `omn`         |

다음은 기본 설정에 맞추어 사용자와 데이터베이스를 생성하는 예시입니다. 운영 환경에서는 안전한 비밀번호를 사용하고, 변경한 값을 [5.3. application-database.yml](#53-application-databaseyml)에 반영하십시오.

```sql
CREATE USER omn WITH PASSWORD 'omn';
CREATE DATABASE lss OWNER omn;
```

> **참고**
> - 테이블은 서버 구동 시 Liquibase가 자동으로 생성합니다. 자세한 내용은 [5.3.2. 데이터베이스 스키마 (Liquibase)](#532-데이터베이스-스키마-liquibase)를 참고해 주세요.

> **참고 링크**
> - [PostgreSQL 다운로드](https://www.postgresql.org/download/)

<br/>

## 2.3. Node.js 설치

Admin Console(React)은 Gradle 빌드 시 자동으로 함께 빌드되며, 이때 필요한 Node.js(22.9.0)는 Gradle이 자동으로 다운로드합니다. 따라서 Node.js 설치는 [7.2.2. 프론트엔드 개발 서버 실행](#722-프론트엔드-개발-서버-실행)과 같이 프론트엔드를 별도로 실행하거나 빌드하는 경우에만 필요합니다.

설치가 완료되면 다음 명령어로 정상적으로 설치되었는지 확인할 수 있습니다.

```bash
node --version
npm --version
```

> **참고 링크**
> - [Node.js 공식 다운로드 페이지](https://nodejs.org/)

<br/>

# 3. GitHub에서 소스 코드 복제하기

## 3.1. 소스코드 복제

`git clone` 명령은 GitHub에 호스팅된 원격 저장소에서 로컬 컴퓨터로 소스 코드를 복제하는 명령어입니다. 이 명령을 사용하면 프로젝트의 전체 소스 코드와 관련 파일들을 로컬에서 작업할 수 있게 됩니다. 복제한 후에는 저장소 내에서 필요한 작업을 진행할 수 있으며, 변경 사항은 다시 원격 저장소에 푸시할 수 있습니다.

터미널을 열고 다음 명령어를 실행하여 Ledger Service 서버의 리포지토리를 로컬 컴퓨터에 복사합니다.
```bash
# Git 저장소에서 리포지토리 복제
git clone https://github.com/OmniOneID/did-ledger-service-server.git

# 복제한 리포지토리로 이동
cd did-ledger-service-server
```

> **참고 링크**
> - [Git Clone 가이드](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 3.2. 디렉토리 구조
복제된 프로젝트의 주요 디렉토리 구조는 다음과 같습니다:

```
did-ledger-service-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE
├── MAINTAINERS.md
├── README.md
├── README_ko.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── dependencies-license.md
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
    │       ├── public
    │       ├── src
    │       ├── package.json
    │       └── vite.config.ts
    └── did-ledger-service-server
        ├── gradle
        ├── libs
        │   ├── did-blockchain-sdk-server-2.0.0.jar
        │   ├── did-core-sdk-server-2.0.0.jar
        │   ├── did-crypto-sdk-server-2.0.0.jar
        │   ├── did-datamodel-sdk-server-2.0.0.jar
        │   ├── did-sdk-common-2.0.0.jar
        │   ├── did-wallet-sdk-server-2.0.0.jar
        │   └── did-zkp-sdk-server-2.0.0.jar
        ├── sample
        │   └── data
        │       ├── diddoc
        │       └── vcmeta
        ├── src
        │   └── main
        │       ├── java
        │       └── resources
        │           ├── config
        │           ├── db
        │           └── static
        ├── build.gradle
        ├── settings.gradle
        ├── gradlew
        └── README.md
```

| Name                       | Description                                          |
| -------------------------- | ---------------------------------------------------- |
| CHANGELOG.md               | 프로젝트의 버전별 변경 사항                          |
| CODE_OF_CONDUCT.md         | 기여자들을 위한 행동 강령                            |
| CONTRIBUTING.md            | 기여 지침 및 절차                                    |
| LICENSE                    | 라이선스                                             |
| dependencies-license.md    | 프로젝트 의존 라이브러리의 라이선스 정보             |
| MAINTAINERS.md             | 프로젝트 관리자를 위한 지침                          |
| RELEASE-PROCESS.md         | 새로운 버전을 릴리스하는 절차                        |
| SECURITY.md                | 보안 정책 및 취약성 보고 방법                        |
| docs                       | 문서                                                 |
| ┖ api                      | API 가이드 문서                                      |
| ┖ errorCode                | 오류 코드 및 문제 해결 가이드                        |
| ┖ installation             | 설치 및 설정 가이드                                  |
| source                     | 서버 및 Admin Console 소스 코드                      |
| ┖ did-ledger-service-admin | Admin Console 프론트엔드(React, TypeScript) 소스 코드 |
| ┖ did-ledger-service-server | Ledger Service 서버(Spring Boot) 소스 코드          |
| &nbsp;&nbsp;┖ gradle       | Gradle Wrapper 설정 및 스크립트                      |
| &nbsp;&nbsp;┖ libs         | Open DID SDK 라이브러리                              |
| &nbsp;&nbsp;┖ sample       | 샘플 데이터 파일 (DID Document, VC Meta)             |
| &nbsp;&nbsp;┖ src/main/java | Java 소스 코드                                      |
| &nbsp;&nbsp;┖ src/main/resources | 설정 파일(`config`), Liquibase Changelog(`db`), Admin Console 빌드 결과물(`static`) |
| &nbsp;&nbsp;┖ build.gradle | Gradle 빌드 설정 파일 (Admin Console 빌드 Task 포함) |
| &nbsp;&nbsp;┖ README.md    | 소스 코드 개요 및 안내                               |

<br/>

# 4. 서버 구동 방법
이 장에서는 서버를 구동하는 두 가지 방법을 안내합니다. 두 방법 모두 Gradle 빌드 시 Admin Console이 함께 빌드되어 서버에 포함됩니다.

1. **IDE를 사용하는 방법**: 통합 개발 환경(IDE)에서 프로젝트를 열고, 실행 구성을 설정한 후 서버를 직접 실행할 수 있습니다. 이 방법은 개발 중에 코드 변경 사항을 빠르게 테스트할 때 유용합니다.

2. **Build 후 콘솔 명령어를 사용하는 방법**: 프로젝트를 빌드한 후, 생성된 JAR 파일을 콘솔에서 명령어(`java -jar`)로 실행하여 서버를 구동할 수 있습니다. 이 방법은 서버를 배포하거나 운영 환경에서 실행할 때 주로 사용됩니다.

> **참고**
> - 현재 저장소에는 `Dockerfile`과 `docker-compose.yml`이 포함되어 있지 않으므로, Docker를 이용한 구동 방법은 이 문서에서 다루지 않습니다.

## 4.1. IntelliJ IDEA로 구동하기 (Gradle 지원)

IntelliJ IDEA는 Java 개발에 널리 사용되는 통합 개발 환경(IDE)으로, Gradle과 같은 빌드 도구를 지원하여 프로젝트 설정 및 의존성 관리가 매우 용이합니다. Open DID의 서버는 Gradle을 사용하여 빌드되므로, IntelliJ IDEA에서 쉽게 프로젝트를 설정하고 서버를 실행할 수 있습니다.

### 4.1.1. IntelliJ IDEA 설치 및 설정
1. IntelliJ를 설치합니다. (설치 방법은 아래 링크를 참조)

> **참고 링크**
> - [IntelliJ IDEA 다운로드](https://www.jetbrains.com/idea/download/)

### 4.1.2. IntelliJ에서 프로젝트 열기
- IntelliJ를 실행시키고 `File -> Open`을 선택합니다. 파일 선택 창이 나타나면 [3.1. 소스코드 복제](#31-소스코드-복제)에서 복제한 리포지토리의 `source/did-ledger-service-server` 폴더를 선택합니다.
- 프로젝트를 열면 build.gradle 파일이 자동으로 인식됩니다.
- Gradle이 자동으로 필요한 의존성 파일들을 다운로드하며, 이 과정이 완료될 때까지 기다립니다.

### 4.1.3. Gradle 빌드
- IntelliJ IDEA의 `Gradle` 탭에서 `Tasks -> build -> build`를 실행합니다.
- 빌드 과정에서 Admin Console 프론트엔드(`source/did-ledger-service-admin/frontend`)가 함께 빌드되어 `src/main/resources/static`에 복사됩니다. 자세한 내용은 [7.2.1. Gradle 빌드에 포함하여 배포](#721-gradle-빌드에-포함하여-배포)를 참고해 주세요.
- 빌드가 성공적으로 완료되면, 프로젝트가 실행 가능한 상태로 준비됩니다.

### 4.1.4. 서버 구동
- IntelliJ IDEA의 Gradle 탭에서 `Tasks -> application -> bootRun`을 선택하고 실행합니다.
- Gradle이 자동으로 서버를 빌드하고 실행합니다.
- 콘솔 로그에서 "Started [ApplicationName] in [time] seconds" 메시지를 확인하여 서버가 정상적으로 실행되었는지 확인합니다.
- 서버가 정상적으로 구동되면 브라우저에서 다음 주소에 접속할 수 있습니다.
  - Admin Console: `http://localhost:8098/`
  - Swagger UI: `http://localhost:8098/swagger-ui.html`

> **주의**
> - Ledger Service 서버의 기본 프로파일은 `dev`입니다. `dev` 프로파일은 PostgreSQL 데이터베이스 연결이 필요하므로, 서버 구동 전에 [2.2. PostgreSQL 설치](#22-postgresql-설치)를 완료해야 합니다.
> - `sample` 프로파일도 데이터베이스 연결이 필요합니다. 자세한 내용은 [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용) 장을 참고해 주세요.

<br/>

### 4.1.5. 서버 설정
- 서버는 배포 환경에 맞게 필요한 설정을 수정해야 하며, 이를 통해 서버가 안정적으로 작동할 수 있도록 해야 합니다. 예를 들어, 데이터베이스 연결 정보, 포트 번호, JWT 서명 키, TAS 연동 정보 등 각 환경에 맞는 구성 요소들을 조정해야 합니다.
- 서버의 설정 파일은 `source/did-ledger-service-server/src/main/resources/config` 경로에 위치해 있습니다.
- 자세한 설정 방법은 [5. 설정 가이드](#5-설정-가이드)를 참고해 주세요.

<br/>

## 4.2. 콘솔 명령어로 구동하기

콘솔 명령어를 사용하여 DID Ledger Service 서버를 구동하는 방법을 안내합니다. Gradle을 이용해 프로젝트를 빌드하고, 생성된 JAR 파일을 사용하여 서버를 구동하는 과정을 설명합니다.

### 4.2.1. Gradle 빌드 명령어

- gradlew를 사용하여 소스를 빌드합니다.
  ```shell
    # 복제한 리포지토리의 서버 소스 폴더로 이동
    cd did-ledger-service-server/source/did-ledger-service-server

    # Gradle Wrapper 실행 권한을 부여
    chmod 755 ./gradlew

    # 프로젝트를 클린 빌드 (이전 빌드 파일을 삭제하고 새로 빌드)
    ./gradlew clean build
  ```
  > 참고
  > - gradlew은 Gradle Wrapper의 줄임말로, 프로젝트에서 Gradle을 실행하는 데 사용되는 스크립트입니다. 로컬에 Gradle이 설치되어 있지 않더라도, 프로젝트에서 지정한 버전의 Gradle을 자동으로 다운로드하고 실행할 수 있도록 해줍니다. 따라서 개발자는 Gradle 설치 여부와 상관없이 동일한 환경에서 프로젝트를 빌드할 수 있게 됩니다.
  > - Gradle 빌드 시 Admin Console 프론트엔드가 자동으로 함께 빌드되어 JAR에 포함됩니다. 프론트엔드 빌드가 필요 없는 경우(예: 백엔드만 테스트하거나 `src/main/resources/static`에 프론트엔드 빌드 결과물이 이미 있는 경우)에는 다음과 같이 옵션을 추가하여 프론트엔드 빌드를 생략할 수 있습니다.
  >   - `./gradlew clean build -DskipFrontendBuild=true`

- 빌드된 폴더로 이동하여 JAR 파일이 생성된 것을 확인합니다.
    ```shell
      cd build/libs
      ls
    ```
- 이 명령어는 `did-ledger-service-server-2.0.0.jar` 파일을 생성합니다. (파일명의 버전은 `build.gradle`의 `version` 값을 따릅니다.)
- 함께 생성되는 `did-ledger-service-server-2.0.0-plain.jar`는 의존성이 포함되지 않은 JAR이므로, 서버 실행에는 `-plain`이 붙지 않은 JAR 파일을 사용합니다.

<br/>

### 4.2.2. 서버 구동 방법
빌드된 JAR 파일을 사용하여 서버를 구동합니다:

```bash
java -jar did-ledger-service-server-2.0.0.jar
```

- 서버가 정상적으로 구동되면 브라우저에서 `http://localhost:8098/`(Admin Console)과 `http://localhost:8098/swagger-ui.html`(Swagger UI)에 접속할 수 있습니다.

> **주의**
> - Ledger Service 서버의 기본 프로파일은 `dev`이며, PostgreSQL 데이터베이스 연결이 필요합니다.
> - 프로파일을 변경하려면 `--spring.profiles.active={profile}` 옵션을 사용합니다. 자세한 내용은 [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용) 장을 참고해 주세요.

<br/>

### 4.2.3. 서버 설정 방법
- 서버는 배포 환경에 맞게 필요한 설정을 수정해야 하며, 이를 통해 서버가 안정적으로 작동할 수 있도록 해야 합니다. 예를 들어, 데이터베이스 연결 정보, 포트 번호, JWT 서명 키, TAS 연동 정보 등 각 환경에 맞는 구성 요소들을 조정해야 합니다.
- 서버의 설정 파일은 `source/did-ledger-service-server/src/main/resources/config` 경로에 위치해 있습니다.
- 빌드된 JAR를 실행할 때는 JAR 내부 설정을 수정하지 않고, 실행 디렉토리의 `config` 폴더에 변경할 설정 파일을 두어 기본 설정을 덮어쓸 수 있습니다. 자세한 내용은 [6.2.2. 콘솔 명령어를 사용한 서버 구동 시](#622-콘솔-명령어를-사용한-서버-구동-시)를 참고하십시오.
- 자세한 설정 방법은 [5. 설정 가이드](#5-설정-가이드)를 참고하십시오.

<br/>

# 5. 설정 가이드

이 장에서는 `source/did-ledger-service-server/src/main/resources/config` 경로의 설정 파일과 각 설정 항목을 설명합니다. 🔒 표시가 있는 항목은 서버 동작에 필요한 고정 값이므로 변경하지 않는 것을 권장합니다.

| 설정 파일                     | 프로파일      | 주요 내용                                   |
| ----------------------------- | ------------- | ------------------------------------------- |
| `application.yml`             | (공통)        | 애플리케이션 이름, 프로파일 그룹, 포트, Actuator |
| `application-logging.yml`     | `logging`     | 로그 그룹 및 로그 레벨                      |
| `application-database.yml`    | `database`    | PostgreSQL, JPA, Liquibase                  |
| `application-auth.yml`        | `auth`        | Admin Console JWT 서명 키 및 토큰 유효 시간 |
| `application-tas.yml`         | `tas`         | 비밀번호 재설정 OTP 메일 발송용 TAS 주소    |
| `application-spring-docs.yml` | `spring-docs` | Swagger UI 및 OpenAPI 문서                  |
| `application-jasypt.yml`      | `jasypt`      | Jasypt 암호화 설정                          |
| `application-wallet.yml`      | (미사용)      | 어떤 프로파일 그룹에도 포함되지 않음         |

## 5.1. application.yml

- 역할: application.yml 파일은 Spring Boot 애플리케이션의 기본 설정을 정의하는 파일입니다. 이 파일을 통해 애플리케이션의 이름, 프로파일 설정, 포트 등 다양한 환경 변수를 지정할 수 있으며, 애플리케이션의 동작 방식에 중요한 영향을 미칩니다.

- 위치: `src/main/resources/config`

### 5.1.1. Spring 기본 설정
* `spring.application.name`:
  - 애플리케이션의 이름을 지정합니다.
  - 용도: 다른 서비스에서 이 애플리케이션을 식별하는 데 사용됩니다.
  - 예시: `Ledger-Service-Server`

* `spring.profiles.default`:
  - 기본 활성화할 프로파일을 정의합니다.
  - 용도: 별도의 프로파일 지정이 없을 때 사용할 기본 프로파일을 설정합니다.
  - 지원 프로파일: `sample`, `dev`, `prod`
  - 기본값: `dev`

* `spring.profiles.group.dev`: 🔒
  - `dev` 프로파일 그룹에 포함된 개별 프로파일을 정의합니다.
  - 포함 프로파일: `auth`, `database`, `jasypt`, `logging`, `spring-docs`, `tas`
  - 프로파일 파일명 규칙: 각 프로파일에 해당하는 설정 파일은 그룹 내에 정의된 이름 그대로 사용됩니다. 예를 들어, auth 프로파일은 application-auth.yml, database 프로파일은 application-database.yml로 작성됩니다.

* `spring.profiles.group.sample`: 🔒
  - `sample` 프로파일 그룹에 포함된 개별 프로파일을 정의합니다.
  - 포함 프로파일: `dev` 그룹과 동일

* `spring.profiles.group.prod`: 🔒
  - `prod` 프로파일 그룹에 포함된 개별 프로파일을 정의합니다.
  - 포함 프로파일: `dev` 그룹과 동일

<br/>

### 5.1.2. Jackson 기본 설정

Jackson은 Spring Boot에서 기본적으로 사용되는 JSON 직렬화/역직렬화 라이브러리입니다. Jackson의 설정을 통해 JSON 데이터의 직렬화 방식이나 포맷을 조정할 수 있으며, 데이터 전송 시 성능과 효율성을 높일 수 있습니다.

* `spring.jackson.default-property-inclusion`: 🔒
  - 속성 값이 null일 때 직렬화하지 않도록 설정합니다.
  - 값: `non_null`

* `spring.jackson.serialization.fail-on-empty-beans`: 🔒
  - 직렬화할 속성이 없는 객체를 직렬화할 때 오류를 발생시키지 않도록 설정합니다.
  - 값: `false`

<br/>

### 5.1.3. 서버 설정
서버 설정은 애플리케이션이 요청을 수신할 포트 번호 등을 정의합니다. Admin Console도 같은 포트로 제공됩니다.

* `server.port`:
  - 애플리케이션이 실행될 포트 번호입니다. Ledger Service 서버의 포트 설정의 기본값은 8098입니다.
  - 값: `8098`

<br/>

### 5.1.4. Actuator 설정
Actuator는 서버 상태 확인 기능을 제공합니다. 기본 설정에서는 Health Check Endpoint(`/actuator/health`)만 활성화되어 있습니다.

* `management.endpoints.enabled-by-default`:
  - 모든 Actuator Endpoint의 기본 활성화 여부입니다.
  - 값: `false`

* `management.endpoints.web.exposure.include`:
  - 웹으로 노출할 Endpoint 목록입니다.
  - 값: `health`, `shutdown`

* `management.endpoint.health.enabled` / `management.endpoint.health.show-details`:
  - Health Endpoint 활성화 여부와 상세 정보 표시 여부입니다.
  - 값: `true` / `never`

* `management.endpoint.shutdown.enabled`:
  - Shutdown Endpoint 활성화 여부입니다. 노출 목록에 포함되어 있지만 비활성화되어 있습니다.
  - 값: `false`

<br/>

## 5.2. application-logging.yml
- 역할: 로그 그룹과 로그 레벨을 설정합니다. 이 설정 파일을 통해 특정 패키지나 모듈에 대해 로그 그룹을 정의하고, 각 그룹에 대한 로그 레벨을 개별적으로 지정할 수 있습니다.

- 위치: `src/main/resources/config`

### 5.2.1. 로깅 설정
* `logging.group`:
  - 로그 그룹을 정의합니다.
  - `repository`: `org.omnione.did.repository` (API Controller 및 Service)
  - `base`: `org.omnione.did.base` (공통 설정, DB, 유틸리티)

* `logging.level`:
  - 로그 그룹별 로그 레벨을 설정합니다.
  - 레벨을 debug 설정함으로써, 지정된 패키지에 대해 DEBUG 레벨 이상(INFO, WARN, ERROR, FATAL)의 모든 로그 메시지를 볼 수 있습니다.
  - 기본값: `repository: debug`, `base: info`

<br/>

## 5.3. application-database.yml

- 역할: DID Ledger Service 서버에서 사용할 데이터베이스 연결 정보를 설정합니다. PostgreSQL 데이터베이스와의 연동을 위한 설정들이 포함되어 있으며, JPA와 Liquibase 설정도 함께 관리합니다.

- 위치: `src/main/resources/config`

### 5.3.1. 데이터베이스 연동 설정

#### PostgreSQL 데이터베이스 설정

- `spring.datasource.url`:
  - PostgreSQL 데이터베이스 연결 URL을 설정합니다.
  - 기본값: `jdbc:postgresql://127.0.0.1:5430/lss`

- `spring.datasource.username`:
  - 데이터베이스 사용자명을 설정합니다.
  - 기본값: `omn`

- `spring.datasource.password`:
  - 데이터베이스 비밀번호를 설정합니다.
  - 기본값: `omn`

- `spring.datasource.driver-class-name`: 🔒
  - PostgreSQL JDBC 드라이버 클래스명을 설정합니다.
  - 값: `org.postgresql.Driver`

#### JPA 설정

- `spring.jpa.hibernate.ddl-auto`: 🔒
  - Hibernate의 DDL 자동 생성 옵션을 설정합니다. 테이블은 Liquibase가 생성하며, Hibernate는 서버 시작 시 Entity와 테이블 구조가 일치하는지 검증합니다.
  - 값: `validate`

- `spring.jpa.hibernate.naming.physical-strategy`: 🔒
  - Entity 필드명(Camel Case)을 테이블 컬럼명(Snake Case)으로 변환하는 전략입니다.
  - 값: `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`

- `spring.jpa.show-sql`:
  - JPA가 실행하는 SQL 쿼리를 콘솔에 출력할지 여부를 설정합니다.
  - 기본값: `true` (운영 환경에서는 `false` 권장)

- `spring.jpa.properties.hibernate.format_sql`:
  - 출력되는 SQL의 포맷팅 여부를 설정합니다.
  - 기본값: `false`

- `spring.jpa.open-in-view`:
  - 요청 처리가 끝날 때까지 영속성 컨텍스트를 유지할지 여부를 설정합니다.
  - 기본값: `true`

#### Liquibase 설정

- `spring.liquibase.enabled`:
  - 서버 시작 시 Liquibase를 실행할지 여부를 설정합니다.
  - 기본값: `true`

- `spring.liquibase.change-log`: 🔒
  - Liquibase 변경 로그 파일의 경로를 설정합니다.
  - 값: `classpath:/db/changelog/master.xml`

<br/>

### 5.3.2. 데이터베이스 스키마 (Liquibase)

서버가 시작되면 Liquibase가 `db/changelog/master.xml` → `changeset/set_master.xml` 순서로 Changelog를 읽어 아직 적용되지 않은 Changeset을 순서대로 적용합니다. 따라서 테이블을 수동으로 생성할 필요가 없으며, v2.0.0에서 업그레이드하는 경우에도 v2.0.0 이후 추가된 `set.1`, `set.2` Changeset만 자동으로 적용됩니다.

| Changeset | 테이블 | 설명 |
| --------- | ------ | ---- |
| `set.0` | `did`, `did_document`, `did_document_status_history`, `did_document_revoked` | DID, DID Document, DID Document 상태 변경 이력 및 폐기 정보 |
| `set.0` | `vc_metadata`, `vc_status_history` | VC Meta 및 VC 상태 변경 이력 |
| `set.0` | `vc_schema`, `zkp_credential_schema`, `zkp_credential_definition` | VC Schema, ZKP Credential Schema, ZKP Credential Definition |
| `set.1` | `admin` | Admin Console 관리자 계정 (초기 ROOT 계정 포함, [7.3.1. 초기 관리자 계정](#731-초기-관리자-계정) 참고) |
| `set.1` | `admin_password_policy` | 관리자 비밀번호 정책 (초기값 포함) |
| `set.1` | `admin_password_reset_token`, `admin_otp_send_limit` | 비밀번호 재설정 OTP 및 OTP 발송 제한 정보 |
| `set.1` | `api_key` | Agent API 호출용 API Key (SHA-256 해시 값으로 저장) |
| `set.1` | `server_config` | 서버 설정 값 (`API_KEY_VALIDATION_ENABLED`, 초기값 `false`) |
| `set.1` | `api_log` | Admin API 및 Agent API 호출 로그와 감사 로그 |
| `set.1` | 기존 테이블 | 조회용 Index 추가, `did_document_revoked.revoked_at` 컬럼 추가, `vc_status_history.from_status` NULL 허용 |
| `set.2` | `admin` | `password_algo` 컬럼 추가, `login_password` 컬럼 길이 확장, 초기 ROOT 계정의 `password_reset_reason` 보정 |

<br/>

## 5.4. application-auth.yml

- 역할: Admin Console 로그인 시 발급되는 JWT(Access Token, Refresh Token)의 서명 키와 유효 시간을 설정합니다.

- 위치: `src/main/resources/config`

### 5.4.1. Admin Console 인증(JWT) 설정

- `security.jwt.secret`:
  - JWT 서명(HMAC)에 사용하는 비밀 키 문자열입니다.
  - 저장소에는 샘플 값이 설정되어 있으므로, 운영 환경에서는 반드시 32바이트 이상의 임의 문자열로 변경하십시오. 값을 변경하면 기존에 발급된 토큰은 더 이상 사용할 수 없으며 관리자는 다시 로그인해야 합니다.

- `security.jwt.access-ttl-seconds`:
  - Access Token의 유효 시간(초)입니다.
  - 기본값: `1800` (30분)

- `security.jwt.refresh-ttl-seconds`:
  - Refresh Token의 유효 시간(초)입니다. Admin Console은 Access Token이 만료되면 Refresh Token으로 Access Token을 재발급받습니다.
  - 기본값: `1209600` (14일)

<br/>

## 5.5. application-tas.yml

- 역할: Ledger Service 서버가 연동하는 TAS(Trust Agent Server)의 주소를 설정합니다.

- 위치: `src/main/resources/config`

### 5.5.1. TAS 연동 설정

- `tas.url`:
  - TAS 서버의 Base URL입니다.
  - 용도: Admin Console의 비밀번호 재설정 OTP 메일을 TAS의 알림 API(`{tas.url}/noti/api/v1/send-email`)를 통해 발송합니다.
  - 기본값: `http://127.0.0.1:8090`

> **참고**
> - TAS 서버에 접속할 수 없거나 TAS의 메일 발송 설정이 되어 있지 않으면 OTP 메일 발송이 실패합니다. 이 설정은 비밀번호 재설정 기능에만 사용되며, Agent API 및 일반 로그인에는 영향을 주지 않습니다.

<br/>

## 5.6. application-spring-docs.yml

- 역할: SpringDoc 기반의 API 문서(OpenAPI)와 Swagger UI를 설정합니다.

- 위치: `src/main/resources/config`

### 5.6.1. API 문서(Swagger) 설정

- `springdoc.swagger-ui.path`:
  - Swagger UI 접속 경로입니다.
  - 기본값: `/swagger-ui.html`

- `springdoc.api-docs.path`:
  - OpenAPI 문서(JSON) 경로입니다.
  - 기본값: `/api-docs`

- `springdoc.server.url`:
  - Swagger UI에서 API를 호출할 때 사용할 서버 URL입니다. 리버스 프록시 등을 통해 외부 주소로 서비스하는 경우에 설정합니다.
  - 값이 비어 있으면 SpringDoc 기본 동작을 따르며, `http://` 또는 `https://` 없이 입력하면 `https://`가 자동으로 붙습니다.
  - 기본값: (빈 값)

- `springdoc.swagger-ui.groups-order`, `operations-sorter`, `disable-swagger-default-url`, `display-request-duration`, `show-actuator`, `default-consumes-media-type`, `default-produces-media-type`:
  - Swagger UI 표시 방식과 기본 Media Type(`application/json`)을 설정합니다.

> **참고**
> - OpenAPI 문서에는 `X-API-Key` Header 인증 방식이 정의되어 있으므로, API Key 검증이 활성화된 경우 Swagger UI의 `Authorize` 버튼에서 API Key를 입력하여 Agent API를 호출할 수 있습니다.

<br/>

## 5.7. application-jasypt.yml

- 역할: 설정 파일의 값을 `ENC(...)` 형식으로 암호화하여 사용할 때 필요한 Jasypt 설정입니다.

- 위치: `src/main/resources/config`

### 5.7.1. Jasypt 설정

- `jasypt.encryptor.algorithm`:
  - 암복호화 알고리즘입니다. `application.yml`에도 `PBEWithHMACSHA512AndAES_256`이 선언되어 있지만, 프로파일 설정 파일이 우선하므로 `application-jasypt.yml`의 값이 적용됩니다.
  - 기본값: `PBEWithMD5AndDES`

- `jasypt.encryptor.key`:
  - `${jasypt-key}` Placeholder로 선언되어 있습니다.

> **참고**
> - 저장소의 기본 설정 파일에는 `ENC(...)` 형식으로 암호화된 값이 없습니다.

<br/>

## 5.8. application-wallet.yml

- `application-wallet.yml`(`wallet.file-path`, `wallet.password`)은 현재 어떤 프로파일 그룹에도 포함되어 있지 않아 기본 구동 시 로드되지 않으며, 현재 버전에서는 사용되지 않습니다. 별도로 설정할 필요가 없습니다.

<br/>

# 6. 프로파일 설정 및 사용

## 6.1. 프로파일 개요 (`sample`, `dev`, `prod`)
DID Ledger Service 서버는 다양한 환경에서 실행될 수 있도록 `sample`, `dev`, `prod` 세 가지 프로파일을 지원합니다. 별도의 프로파일을 지정하지 않으면 `dev` 프로파일로 구동됩니다.

세 프로파일 그룹은 모두 동일한 개별 프로파일(`auth`, `database`, `jasypt`, `logging`, `spring-docs`, `tas`)을 포함합니다. 따라서 어떤 프로파일을 사용하더라도 PostgreSQL 데이터베이스 연결이 필요하며, Admin Console, API Key 검증, API 로그 기능은 데이터베이스를 사용하여 동작합니다. 프로파일에 따라 달라지는 것은 Agent API(`/lss/api/v1`)의 처리 방식입니다.

| 프로파일 | Agent API 처리 방식 | 용도 |
| -------- | ------------------- | ---- |
| `sample` | 고정된 샘플 응답 반환 (등록 요청은 저장하지 않음) | API 호출 테스트 |
| `dev`    | 데이터베이스에 실제 저장 및 조회 (기본 프로파일) | 개발 환경 |
| `prod`   | 데이터베이스에 실제 저장 및 조회 | 운영 환경 |

### 6.1.1. `sample` 프로파일
`sample` 프로파일은 Agent API를 샘플 구현으로 대체하여, 모든 조회 API 호출에 대해 고정된 응답 데이터를 반환하고 등록 및 변경 요청은 저장하지 않습니다. 개발자가 연동 서버의 API 호출 흐름을 빠르게 확인할 때 유용합니다. 단, `database` 프로파일이 포함되어 있으므로 데이터베이스 연결은 필요합니다.

### 6.1.2. `dev` 프로파일
`dev` 프로파일은 실제 동작을 수행하도록 설계된 기본 프로파일입니다. DID Document, VC Meta, VC Schema, ZKP Credential Schema/Definition을 PostgreSQL 데이터베이스에 저장하고 조회합니다. 이 프로파일을 사용하려면 개발 환경의 데이터베이스 설정이 필요합니다.

### 6.1.3. `prod` 프로파일
`prod` 프로파일은 `dev`와 동일한 개별 프로파일과 실제 구현을 사용하며, 운영 환경에서 사용합니다. 저장소에는 `application-prod.yml` 파일이 포함되어 있지 않으며, 운영 환경 전용 설정이 필요한 경우 `application-prod.yml` 파일을 추가하여 기본 설정을 덮어쓸 수 있습니다.

## 6.2. 프로파일 설정 방법
각 구동 방법별로 프로파일을 변경하는 방법을 설명합니다.

### 6.2.1. IDE를 사용한 서버 구동 시
- **설정 파일 선택:** `src/main/resources/config` 경로에서 `application.yml` 파일을 선택합니다.
- **프로파일 지정:** IDE의 실행 설정(Run/Debug Configurations)에서 `--spring.profiles.active={profile}` 옵션을 추가해 원하는 프로파일을 활성화합니다. Gradle `bootRun`으로 실행하는 경우 `bootRun --args='--spring.profiles.active={profile}'`와 같이 지정합니다.
- **설정 적용:** 활성화된 프로파일에 따라 해당 설정 파일이 적용됩니다.

### 6.2.2. 콘솔 명령어를 사용한 서버 구동 시
- **설정 파일 선택:** JAR 내부에는 `src/main/resources/config`의 기본 설정 파일이 포함되어 있습니다. 설정을 변경하려면 JAR를 실행하는 디렉토리에 `config` 폴더를 만들고 변경할 설정 파일(예: `application-database.yml`)을 넣습니다. 이 폴더의 설정은 JAR 내부의 기본 설정보다 우선 적용됩니다.
- **프로파일 지정:** 서버 구동 명령어에 `--spring.profiles.active={profile}` 옵션을 추가하여 원하는 프로파일을 활성화합니다.

  ```bash
  java -jar build/libs/did-ledger-service-server-2.0.0.jar --spring.profiles.active={profile}
  ```

- **설정 적용:** 활성화된 프로파일에 따라 해당 설정 파일이 적용됩니다.

각 방법에 따라 프로파일별 설정을 유연하게 변경하여 사용할 수 있으며, 프로젝트 환경에 맞는 설정을 쉽게 적용할 수 있습니다.

<br/>

# 7. Admin Console

## 7.1. Admin Console 개요

LSS Admin Console은 Ledger Service 서버를 관리하기 위한 웹 화면입니다. 소스 코드는 `source/did-ledger-service-admin/frontend`에 있으며 React, TypeScript, MUI(Toolpad Core), Vite로 작성되어 있습니다. 빌드된 Admin Console은 Ledger Service 서버의 정적 리소스로 포함되어, 별도의 웹 서버 없이 서버와 같은 포트로 제공됩니다.

- 접속 주소: `http://{서버 주소}:8098/`
- Admin API: `/lss/admin/v1` (로그인 시 발급되는 JWT Access Token으로 보호되며, 로그인, 토큰 재발급, 비밀번호 재설정 OTP 등 일부 API는 인증 없이 호출할 수 있습니다.)

Admin Console은 다음 메뉴를 제공합니다.

| 메뉴 | 하위 메뉴 | 설명 |
| ---- | --------- | ---- |
| DID List | DID List, DID Change History | DID 및 DID Document 조회, DID Document 상태 변경 이력 조회 |
| VC Metadata List | VC Metadata List, VC Change History | VC Meta 조회, VC 상태 변경 이력 조회 |
| Schema Management | VC Schema List | VC Schema 조회 |
| API Key Management | - | Agent API 호출용 API Key 발급, 비활성화/활성화, 갱신 ([8. API Key 운영](#8-api-key-운영) 참고) |
| Admin Management | Admin Management, Password Policy Settings | 관리자 계정 관리 및 비밀번호 정책 설정 |
| Server Configuration | - | API Key 검증 사용 여부 설정 ([8.4. API Key 검증 활성화](#84-api-key-검증-활성화) 참고) |
| Log Management | API Log, Audit API Log | API 호출 로그 및 감사 로그 조회 |

<br/>

## 7.2. Admin Console 빌드 및 배포

### 7.2.1. Gradle 빌드에 포함하여 배포

`source/did-ledger-service-server/build.gradle`은 Gradle Node Plugin(`com.github.node-gradle.node`)을 사용하여 서버 빌드 시 Admin Console을 함께 빌드합니다. 별도의 작업 없이 [4. 서버 구동 방법](#4-서버-구동-방법)에 따라 서버를 빌드하면 Admin Console이 JAR에 포함됩니다.

빌드 과정은 다음과 같습니다.

1. `nodeSetup`: Node.js 22.9.0을 `build/nodejs` 경로에 다운로드합니다.
2. `deleteFrontResources`: 기존 `src/main/resources/static` 디렉토리와 `frontend/dist` 디렉토리를 삭제합니다.
3. `npm_install`: `source/did-ledger-service-admin/frontend`에서 `npm install`을 실행합니다.
4. `npm_build`: `npm run build`(`tsc -b && vite build`)를 실행하여 `frontend/dist`에 빌드 결과물을 생성합니다.
5. `copyFrontResources`: `frontend/dist`의 내용을 `src/main/resources/static`으로 복사합니다.
6. `processResources`: `static` 디렉토리를 포함한 리소스를 JAR에 포함합니다.

> **참고**
> - `-DskipFrontendBuild=true` 옵션을 지정하면 1~5 단계를 생략하고, `src/main/resources/static` 디렉토리에 있는 기존 파일을 그대로 JAR에 포함합니다. 이 디렉토리에 Admin Console 빌드 결과물(`index.html` 포함)이 없으면 Admin Console 화면이 표시되지 않습니다.
> - 프론트엔드 빌드 시 `src/main/resources/static` 디렉토리는 삭제 후 다시 생성되므로, 이 디렉토리에 다른 파일을 두지 마십시오.
> - 브라우저에서 `/sign-in`과 같은 Admin Console 하위 경로로 직접 접근하면 서버가 `index.html`을 반환하여 Admin Console이 화면을 표시합니다.

### 7.2.2. 프론트엔드 개발 서버 실행

Admin Console을 개발하거나 화면을 수정하는 경우, Vite 개발 서버로 프론트엔드를 단독 실행할 수 있습니다. 로컬에 Node.js와 npm이 설치되어 있어야 하며([2.3. Node.js 설치](#23-nodejs-설치) 참고), Ledger Service 서버가 `localhost:8098`에서 실행 중이어야 합니다.

```bash
# Admin Console 프론트엔드 폴더로 이동
cd did-ledger-service-server/source/did-ledger-service-admin/frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

- `vite.config.ts`에는 `/lss/admin/v1` 요청을 `http://localhost:8098`로 전달하는 Proxy가 설정되어 있습니다. Ledger Service 서버를 다른 주소나 포트에서 실행하는 경우 `vite.config.ts`의 `target` 값을 변경하십시오.
- 개발 서버는 `0.0.0.0`에 바인딩되며, 콘솔에 출력되는 주소(Vite 기본값: `http://localhost:5173`)로 접속합니다.
- `npm run build`를 실행하면 `dist` 디렉토리에 빌드 결과물이 생성되며, `npm run preview`로 빌드 결과물을 미리 볼 수 있습니다.

<br/>

## 7.3. 최초 로그인 및 초기 설정

### 7.3.1. 초기 관리자 계정

서버를 처음 구동하면 Liquibase Changeset(`set.1/create-table_admin.xml`의 `insert-initial-admin-data`)에 의해 다음 ROOT 관리자 계정이 생성됩니다.

| 항목     | 값                          |
| -------- | --------------------------- |
| ID       | `admin@opendid.omnione.net` |
| Password | `password`                  |
| Role     | `ROOT`                      |

> **주의**
> - 초기 계정 정보는 공개 저장소에 포함되어 있으므로, 서버 설치 직후 반드시 로그인하여 ID와 비밀번호를 변경하십시오.
> - Admin Console은 입력한 비밀번호를 SHA-256으로 해시하여 서버에 전송하며, 서버는 이를 BCrypt로 다시 해시하여 저장합니다. 초기 계정은 SHA-256 값으로 등록되어 있으며, 최초 로그인에 성공하면 BCrypt 값으로 변환됩니다.

### 7.3.2. 최초 로그인 시 ID 및 비밀번호 변경

1. 브라우저에서 `http://{서버 주소}:8098/`에 접속합니다. 로그인되어 있지 않으면 로그인 화면(`LS Admin Login`)이 표시됩니다.
2. [7.3.1. 초기 관리자 계정](#731-초기-관리자-계정)의 ID와 비밀번호로 로그인합니다.
3. ROOT 계정으로 처음 로그인하면 ID 및 비밀번호 변경 화면이 표시됩니다. 새 ID와 새 비밀번호를 입력합니다.
   - 새 ID는 이메일 형식이어야 합니다. 비밀번호 재설정 OTP가 이 주소로 발송되므로([7.4. 비밀번호 분실 시 재설정](#74-비밀번호-분실-시-재설정) 참고), 실제로 메일을 받을 수 있는 주소를 사용하십시오.
   - 새 비밀번호는 비밀번호 정책을 충족해야 합니다. ([7.3.3. 관리자 추가 및 비밀번호 정책](#733-관리자-추가-및-비밀번호-정책) 참고)
4. 변경이 완료되면 새 ID와 비밀번호로 자동 로그인되어 DID List 화면으로 이동합니다.

> **참고**
> - 로그인 세션은 Access Token(기본 30분)과 Refresh Token(기본 14일)으로 유지됩니다. 유효 시간은 [5.4. application-auth.yml](#54-application-authyml)에서 변경할 수 있습니다.

### 7.3.3. 관리자 추가 및 비밀번호 정책

- **관리자 추가:** ROOT 관리자는 `Admin Management` 화면에서 일반 관리자(`NORMAL`)를 등록하거나 삭제하고, 다른 관리자의 비밀번호를 변경할 수 있습니다. Admin Console에서 ROOT 관리자 계정은 삭제할 수 없습니다.
- **일반 관리자의 최초 로그인:** 새로 등록된 관리자는 최초 로그인 시 비밀번호 변경 화면에서 비밀번호를 변경해야 합니다.
- **비밀번호 정책:** `Password Policy Settings` 화면에서 비밀번호 정책을 설정합니다. 초기값은 다음과 같습니다.

  | 항목 | 초기값 |
  | ---- | ------ |
  | 최소 길이 | 8 |
  | 대문자 포함 | 요구하지 않음 |
  | 숫자 포함 | 요구하지 않음 |
  | 특수문자 포함 | 요구하지 않음 |
  | 비밀번호 만료 기간 | 365일 |

- **비밀번호 만료:** 마지막으로 비밀번호를 변경한 후 만료 기간이 지나면 다음 로그인 시 비밀번호 변경 화면이 표시됩니다. 만료 기간이 0 이하이면 만료 여부를 검사하지 않습니다.

<br/>

## 7.4. 비밀번호 분실 시 재설정

관리자가 비밀번호를 분실한 경우, 로그인 화면에서 이메일 OTP를 이용하여 비밀번호를 재설정할 수 있습니다.

1. 로그인 화면에서 `Forgot your password?`를 선택합니다.
2. 관리자 ID(이메일 주소)를 입력하고 OTP를 요청합니다. 6자리 OTP가 해당 주소로 발송됩니다.
3. 수신한 OTP와 새 비밀번호를 입력하여 비밀번호를 재설정합니다.

| 항목 | 값 |
| ---- | -- |
| OTP 유효 시간 | 5분 |
| OTP 재요청 대기 시간 | 1분 |
| 하루 최대 발송 횟수 | 5회 (초과 시 해당 ID의 OTP 발송이 차단됨) |
| OTP 최대 검증 시도 횟수 | 5회 |

> **주의**
> - OTP 메일은 `tas.url`에 설정된 TAS 서버의 알림 API를 통해 발송됩니다. TAS 서버가 구동 중이고 메일 발송 설정이 완료되어 있어야 합니다. ([5.5. application-tas.yml](#55-application-tasyml) 참고)
> - ROOT 관리자는 이 절차 대신 `Admin Management` 화면에서 다른 관리자의 비밀번호를 변경할 수 있습니다.

<br/>

# 8. API Key 운영

## 8.1. API Key 개요

TAS, Issuer 등 다른 서버는 Ledger Service 서버의 Agent API(Base Path: `/lss/api/v1`)를 호출하여 DID Document, VC Meta, VC Schema 등을 등록하고 조회합니다. API Key 검증이 활성화되면, 호출 서버는 Admin Console에서 발급한 API Key를 `X-API-Key` Header에 담아 요청해야 합니다. Agent API의 요청 및 응답 형식은 [Ledger Service API](../api/LedgerService_API_ko.md) 문서를 참고해 주세요.

API Key에는 다음 Role 중 하나가 지정되며, 상위 Role은 하위 Role의 권한을 모두 포함합니다.

| Role     | 호출 가능한 API |
| -------- | --------------- |
| `TAS`    | 모든 Agent API (DID Document 등록 및 상태 변경 포함) |
| `ISSUER` | VC Meta, VC Schema, ZKP Credential Schema/Definition 등록 및 변경, 모든 조회 API |
| `READ`   | 조회 API |

Agent API별 필요 Role은 다음과 같습니다.

| API | Method | Path | 허용 Role |
| --- | ------ | ---- | --------- |
| DID Document 등록 | POST | `/lss/api/v1/did-doc` | `TAS` |
| DID Document 조회 | GET | `/lss/api/v1/did-doc` | `TAS`, `ISSUER`, `READ` |
| DID Document 상태 변경 | PUT | `/lss/api/v1/did-doc` | `TAS` |
| VC Meta 등록 | POST | `/lss/api/v1/vc-meta` | `TAS`, `ISSUER` |
| VC Meta 조회 | GET | `/lss/api/v1/vc-meta` | `TAS`, `ISSUER`, `READ` |
| VC 상태 변경 | PUT | `/lss/api/v1/vc-meta` | `TAS`, `ISSUER` |
| VC Schema 등록 | POST | `/lss/api/v1/vc-schema` | `TAS`, `ISSUER` |
| VC Schema 조회 | GET | `/lss/api/v1/vc-schema` | `TAS`, `ISSUER`, `READ` |
| ZKP Credential Schema 등록 | POST | `/lss/api/v1/credential-schema` | `TAS`, `ISSUER` |
| ZKP Credential Schema 조회 | GET | `/lss/api/v1/credential-schema` | `TAS`, `ISSUER`, `READ` |
| ZKP Credential Definition 등록 | POST | `/lss/api/v1/credential-definition` | `TAS`, `ISSUER` |
| ZKP Credential Definition 조회 | GET | `/lss/api/v1/credential-definition` | `TAS`, `ISSUER`, `READ` |

API Key 검증이 활성화된 상태에서 검증에 실패하면 다음 오류가 반환됩니다.

| 오류 코드 | HTTP Status | 원인 |
| --------- | ----------- | ---- |
| `SSRVLSS14502` | 401 | 요청 Header에 API Key가 없음 |
| `SSRVLSS14503` | 401 | 등록되지 않았거나 비활성화된 API Key |
| `SSRVLSS14506` | 401 | 만료일(`expires_at`)이 지난 API Key |
| `SSRVLSS14504` | 403 | API Key의 Role이 해당 API를 호출할 권한이 없음 |

<br/>

## 8.2. API Key 발급

1. Admin Console에서 `API Key Management` 메뉴로 이동하여 API Key 등록을 선택합니다.
2. 다음 항목을 입력합니다.
   - Name (필수): API Key를 식별할 이름 (예: 호출 서버 이름)
   - Description (선택): API Key의 용도
   - Role (필수): `TAS`, `ISSUER`, `READ` 중 선택 ([8.1. API Key 개요](#81-api-key-개요) 참고)
   - Expiration Period (필수): `1 month`, `3 months`, `6 months`, `1 year`, `2 years`, `No Limit` 또는 `Custom`(일 단위 직접 입력)
3. 등록이 완료되면 `lss_`로 시작하는 API Key가 화면에 표시됩니다. 이 값을 복사하여 호출 서버에 안전하게 전달합니다.

> **주의**
> - 평문 API Key는 발급 시 한 번만 표시됩니다. 서버에는 SHA-256 해시 값과 화면 표시용 마스킹 값만 저장되므로, 화면을 닫은 후에는 API Key를 다시 확인할 수 없습니다. API Key를 분실한 경우 새 API Key를 발급하십시오.

<br/>

## 8.3. API Key 비활성화, 활성화 및 갱신

`API Key Management` 화면에서 발급된 API Key를 선택하여 다음 작업을 수행할 수 있습니다.

| 작업 | 설명 | Admin API |
| ---- | ---- | --------- |
| 목록 조회 | 발급된 API Key 목록 조회 | `GET /lss/admin/v1/api-keys/list` |
| 상세 조회 | API Key 상세 정보(마스킹 값, Role, 만료일, 마지막 사용 시각 등) 조회 | `GET /lss/admin/v1/api-keys?id={id}` |
| 발급 | 새 API Key 발급 | `POST /lss/admin/v1/api-keys` |
| 비활성화 | API Key를 비활성화합니다. API Key 검증이 활성화된 경우 이후 요청은 `SSRVLSS14503` 오류로 거부됩니다. | `PUT /lss/admin/v1/api-keys/{id}/deactivate` |
| 활성화 | 비활성화된 API Key를 다시 활성화합니다. | `PUT /lss/admin/v1/api-keys/{id}/activate` |
| 갱신 | 활성 상태인 API Key의 만료일을 지정한 기간만큼 연장합니다. 이미 만료일이 지난 경우 현재 시각을 기준으로 연장하며, API Key 값은 변경되지 않습니다. | `PUT /lss/admin/v1/api-keys/{id}/renew` |

> **주의**
> - API Key 검증은 등록 여부, 활성 상태, 만료일(`expires_at`), Role을 확인합니다. 만료일이 지난 API Key는 `SSRVLSS14506` 오류로 거부되므로, 만료일이 지나기 전에 갱신하십시오. 더 이상 사용하지 않는 API Key는 비활성화하십시오.

<br/>

## 8.4. API Key 검증 활성화

API Key 검증 사용 여부는 `server_config` 테이블의 `API_KEY_VALIDATION_ENABLED` 값으로 결정됩니다. 초기값은 `false`이며, 이 상태에서는 API Key 없이도 모든 Agent API를 호출할 수 있습니다.

1. Admin Console에서 `Server Configuration` 메뉴로 이동합니다.
2. `API Key Validation` 스위치를 켜고 `Update`를 선택합니다.
3. 설정 값은 요청마다 데이터베이스에서 조회되므로, 서버를 재시작하지 않아도 이후 요청부터 즉시 적용됩니다.

> **권장 적용 순서**
> 1. Admin Console에서 TAS용(`TAS` Role), Issuer용(`ISSUER` Role) API Key를 발급합니다. ([8.2. API Key 발급](#82-api-key-발급))
> 2. 각 호출 서버에 발급한 API Key를 설정합니다. ([8.5. 호출 서버(TAS, Issuer) 설정](#85-호출-서버tas-issuer-설정))
> 3. Ledger Service 서버에서 API Key 검증을 활성화합니다.
>
> 호출 서버에 API Key를 설정하기 전에 검증을 활성화하면 해당 서버의 Agent API 호출이 실패합니다.

<br/>

## 8.5. 호출 서버(TAS, Issuer) 설정

TAS와 Issuer 서버는 각자의 `server_config` 테이블에 있는 `API_KEY_LSS` 항목(초기값: `enter_lss_api_key_here`)의 값을 Ledger Service 서버 요청의 `X-API-Key` Header로 전송합니다. 각 서버의 Admin Console `Server Configuration` 화면에서 `API_KEY_LSS` 값을 [8.2. API Key 발급](#82-api-key-발급)에서 발급한 API Key로 변경하십시오.

| 호출 서버 | 발급할 API Key Role | 설정 위치 |
| --------- | ------------------- | --------- |
| TAS       | `TAS`               | TAS `server_config.API_KEY_LSS` |
| Issuer    | `ISSUER`            | Issuer `server_config.API_KEY_LSS` |

> **참고**
> - Ledger Service 서버 주소 등 각 호출 서버의 LSS 연동 설정은 해당 서버의 설치 가이드를 참고해 주세요.

[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0_ko.md
