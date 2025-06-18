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

- Date: 2025-06-12
- Version: v2.0.0

목차
==

- [1. 소개](#1-소개)
  - [1.1. 개요](#11-개요)
  - [1.2. Ledger Service 서버 정의](#12-ledger-service-서버-정의)
  - [1.3. 시스템 요구 사항](#13-시스템-요구-사항)
- [2. 사전 준비 사항](#2-사전-준비-사항)
  - [2.1. Git 설치](#21-git-설치)
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
  - [4.3. Docker로 구동하기](#43-docker로-구동하기)
- [5. 설정 가이드](#5-설정-가이드)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Spring 기본 설정](#511-spring-기본-설정)
    - [5.1.2. Jackson 기본 설정](#512-jackson-기본-설정)
    - [5.1.3. 서버 설정](#513-서버-설정)
  - [5.2. application-logging.yml](#52-application-loggingyml)
    - [5.2.1. 로깅 설정](#521-로깅-설정)
  - [5.3. application-database.yml](#53-application-databaseyml)
    - [5.3.1. 데이터베이스 연동 설정](#531-데이터베이스-연동-설정)
- [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용)
  - [6.1. 프로파일 개요 (`sample`, `dev`)](#61-프로파일-개요-sample-dev)
    - [6.1.1. `sample` 프로파일](#611-sample-프로파일)
    - [6.1.2. `dev` 프로파일](#612-dev-프로파일)
  - [6.2. 프로파일 설정 방법](#62-프로파일-설정-방법)
    - [6.2.1. IDE를 사용한 서버 구동 시](#621-ide를-사용한-서버-구동-시)
    - [6.2.2. 콘솔 명령어를 사용한 서버 구동 시](#622-콘솔-명령어를-사용한-서버-구동-시)
    - [6.2.3. Docker를 사용한 서버 구동 시](#623-docker를-사용한-서버-구동-시)
- [7. Docker로 빌드 후 구동하기](#7-docker로-빌드-후-구동하기)
  - [7.1. Docker 이미지 빌드 방법 (`Dockerfile` 기반)](#71-docker-이미지-빌드-방법-dockerfile-기반)
  - [7.2. Docker 이미지 실행](#72-docker-이미지-실행)
  - [7.3. Docker Compose를 이용한 구동](#73-docker-compose를-이용한-구동)
    - [7.3.1. `docker-compose.yml` 파일 설명](#731-docker-composeyml-파일-설명)
    - [7.3.2. 컨테이너 실행 및 관리](#732-컨테이너-실행-및-관리)
    - [7.3.3. 서버 설정 방법](#733-서버-설정-방법)

# 1. 소개

## 1.1. 개요
본 문서는 DID Ledger Service 서버의 설치 및 구동에 관한 가이드를 제공합니다. 설치 과정, 설정 방법, 그리고 구동 절차를 단계별로 설명하여, 사용자가 이를 효율적으로 설치하고 운영할 수 있도록 안내합니다.

OpenDID의 전체 설치에 대한 가이드는 [Open DID Installation Guide]를 참고해 주세요.

<br/>

## 1.2. Ledger Service 서버 정의

DID Ledger Service Server는 PostgreSQL 데이터베이스를 활용하여 블록체인과 동일한 기능을 제공하는 대안 서비스입니다.

주요 기능:
- DID Document 생성, 등록, 상태변경
- VC Meta 생성, VC 상태 변경
- VC Schema 등록 및 관리
- ZKP Credential Schema, ZKP Credential Definition 등록 및 관리

이 서버를 통해 복잡한 블록체인 인프라 구축 없이도 안정적이고 효율적인 분산 신원 관리 시스템을 구축할 수 있습니다.

<br/>

## 1.3. 시스템 요구 사항
- **Java 21** 이상
- **Gradle 7.0** 이상
- **Docker** 및 **Docker Compose** (Docker 사용 시)
- **PostgreSQL 13** 이상 (dev 프로파일 사용 시)
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
├── dependencies-license.md
├── MAINTAINERS.md
├── README.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   ├── api
│       └── LedgerService_API.md
│   ├── errorCode
│       └── LedgerService_ErrorCode.md
│   └── installation
│       └── OpenDID_LedgerService_InstallationAndOperation_Guide.md
├── gradle
├── libs
│   └── did-sdk-common-2.0.0.jar
│   └── did-blockchain-sdk-server-2.0.0.jar
│   └── did-core-sdk-server-2.0.0.jar
│   └── did-crypto-sdk-server-2.0.0.jar
│   └── did-datamodel-sdk-server-2.0.0.jar
│   └── did-wallet-sdk-server-2.0.0.jar
│   └── did-zkp-sdk-server-2.0.0.jar
├── sample
│   └── data
│       ├── diddoc
│       └── vcmeta
└── src
    └── main
        ├── java
        └── resources
            ├── config
            └── db
└── build.gradle
└── README.md
```

| Name                    | Description                              |
| ----------------------- | ---------------------------------------- |
| CHANGELOG.md            | 프로젝트의 버전별 변경 사항              |
| CODE_OF_CONDUCT.md      | 기여자들을 위한 행동 강령                |
| CONTRIBUTING.md         | 기여 지침 및 절차                        |
| dependencies-license.md | 프로젝트 의존 라이브러리의 라이선스 정보 |
| MAINTAINERS.md          | 프로젝트 관리자를 위한 지침              |
| RELEASE-PROCESS.md      | 새로운 버전을 릴리스하는 절차            |
| SECURITY.md             | 보안 정책 및 취약성 보고 방법            |
| docs                    | 문서                                     |
| ┖ api                   | API 가이드 문서                          |
| ┖ errorCode             | 오류 코드 및 문제 해결 가이드            |
| ┖ installation          | 설치 및 설정 가이드                      |
| gradle                  | Gradle 빌드 설정 및 스크립트             |
| libs                    | 외부 라이브러리 및 의존성                |
| sample                  | 샘플 파일                                |
| src                     | 주요 소스 코드 디렉토리                  |
| ┖ main/java             | Java 소스 코드                           |
| ┖ main/resources        | 설정 파일 및 리소스                      |
| build.gradle            | Gradle 빌드 설정 파일                    |
| README.md               | 소스 코드 개요 및 안내                   |

<br/>

# 4. 서버 구동 방법
이 장에서는 서버를 구동하는 세 가지 방법을 안내합니다.

1. **IDE를 사용하는 방법**: 통합 개발 환경(IDE)에서 프로젝트를 열고, 실행 구성을 설정한 후 서버를 직접 실행할 수 있습니다. 이 방법은 개발 중에 코드 변경 사항을 빠르게 테스트할 때 유용합니다.

2. **Build 후 콘솔 명령어를 사용하는 방법**: 프로젝트를 빌드한 후, 생성된 JAR 파일을 콘솔에서 명령어(`java -jar`)로 실행하여 서버를 구동할 수 있습니다. 이 방법은 서버를 배포하거나 운영 환경에서 실행할 때 주로 사용됩니다.

3. **Docker로 빌드하는 방법**: 서버를 Docker 이미지로 빌드하고, Docker 컨테이너로 실행할 수 있습니다. 이 방법은 환경 간 일관성을 유지하며, 배포 및 스케일링이 용이한 장점이 있습니다.

## 4.1. IntelliJ IDEA로 구동하기 (Gradle 지원)

IntelliJ IDEA는 Java 개발에 널리 사용되는 통합 개발 환경(IDE)으로, Gradle과 같은 빌드 도구를 지원하여 프로젝트 설정 및 의존성 관리가 매우 용이합니다. Open DID의 서버는 Gradle을 사용하여 빌드되므로, IntelliJ IDEA에서 쉽게 프로젝트를 설정하고 서버를 실행할 수 있습니다.

### 4.1.1. IntelliJ IDEA 설치 및 설정
1. IntelliJ를 설치합니다. (설치 방법은 아래 링크를 참조)

> **참고 링크**
> - [IntelliJ IDEA 다운로드](https://www.jetbrains.com/idea/download/)

### 4.1.2. IntelliJ에서 프로젝트 열기
- IntelliJ를 실행시키고 `File -> Open`을 선택합니다. 파일 선택 창이 나타나면 [3.1. 소스코드 복제](#31-소스코드-복제)에서 복제한 리포지토리의 루트 폴더를 선택합니다.
- 프로젝트를 열면 build.gradle 파일이 자동으로 인식됩니다.
- Gradle이 자동으로 필요한 의존성 파일들을 다운로드하며, 이 과정이 완료될 때까지 기다립니다.

### 4.1.3. Gradle 빌드
- IntelliJ IDEA의 `Gradle` 탭에서 `Tasks -> build -> build`를 실행합니다.
- 빌드가 성공적으로 완료되면, 프로젝트가 실행 가능한 상태로 준비됩니다.

### 4.1.4. 서버 구동
- IntelliJ IDEA의 Gradle 탭에서 Tasks -> application -> bootRun을 선택하고 실행합니다.
- Gradle이 자동으로 서버를 빌드하고 실행합니다.
- 콘솔 로그에서 "Started [ApplicationName] in [time] seconds" 메시지를 확인하여 서버가 정상적으로 실행되었는지 확인합니다.

> **주의**
> - Ledger Service 서버는 초기에 sample 프로파일로 설정되어 있습니다.
> - sample 프로파일로 설정시, 필수 설정(예: 데이터베이스)을 무시하고 서버가 구동됩니다. 자세한 내용은 [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용) 장을 참고해 주세요.

<br/>

### 4.1.5. 서버 설정
- 서버는 배포 환경에 맞게 필요한 설정을 수정해야 하며, 이를 통해 서버가 안정적으로 작동할 수 있도록 해야 합니다. 예를 들어, 데이터베이스 연결 정보, 포트 번호, 블록체인 연동 정보 등 각 환경에 맞는 구성 요소들을 조정해야 합니다.
- 서버의 설정 파일은 `src/main/resources/config` 경로에 위치해 있습니다.
- 자세한 설정 방법은 [5. 설정 가이드](#5-설정-가이드)를 참고해 주세요.

<br/>

## 4.2. 콘솔 명령어로 구동하기

콘솔 명령어를 사용하여 DID Ledger Service 서버를 구동하는 방법을 안내합니다. Gradle을 이용해 프로젝트를 빌드하고, 생성된 JAR 파일을 사용하여 서버를 구동하는 과정을 설명합니다.

### 4.2.1. Gradle 빌드 명령어

- gradlew를 사용하여 소스를 빌드합니다.
  ```shell
    # 복제한 리포지토리로 이동
    cd did-ledger-service-server

    # Gradle Wrapper 실행 권한을 부여
    chmod 755 ./gradlew

    # 프로젝트를 클린 빌드 (이전 빌드 파일을 삭제하고 새로 빌드)
    ./gradlew clean build
  ```
  > 참고
  > - gradlew은 Gradle Wrapper의 줄임말로, 프로젝트에서 Gradle을 실행하는 데 사용되는 스크립트입니다. 로컬에 Gradle이 설치되어 있지 않더라도, 프로젝트에서 지정한 버전의 Gradle을 자동으로 다운로드하고 실행할 수 있도록 해줍니다. 따라서 개발자는 Gradle 설치 여부와 상관없이 동일한 환경에서 프로젝트를 빌드할 수 있게 됩니다.

- 빌드된 폴더로 이동하여 JAR 파일이 생성된 것을 확인합니다.
    ```shell
      cd build/libs
      ls
    ```
- 이 명령어는 `did-ledger-service-server-2.0.0.jar` 파일을 생성합니다.

<br/>

### 4.2.2. 서버 구동 방법
빌드된 JAR 파일을 사용하여 서버를 구동합니다:

```bash
java -jar did-ledger-service-server-2.0.0.jar
```

> **주의**
> - Ledger Service 서버는 초기에 sample 프로파일로 설정되어 있습니다.
> - sample 프로파일로 설정시, 필수 설정(예: 데이터베이스)을 무시하고 서버가 구동됩니다. 자세한 내용은 [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용) 장을 참고해 주세요.

<br/>

### 4.2.3. 서버 설정 방법
- 서버는 배포 환경에 맞게 필요한 설정을 수정해야 하며, 이를 통해 서버가 안정적으로 작동할 수 있도록 해야 합니다. 예를 들어, 데이터베이스 연결 정보, 포트 번호, 블록체인 연동 정보 등 각 환경에 맞는 구성 요소들을 조정해야 합니다.
- 서버의 설정 파일은 `src/main/resources/config` 경로에 위치해 있습니다.
- 자세한 설정 방법은 [5. 설정 가이드](#5-설정-가이드)를 참고하십시오.

<br/>

## 4.3. Docker로 구동하기
- Docker 이미지 빌드, 설정, 실행 등의 과정은 아래 [7. Docker로 빌드 후 구동하기](#7-docker로-빌드-후-구동하기)를 참고하세요.

<br/>

# 5. 설정 가이드

## 5.1. application.yml

- 역할: application.yml 파일은 Spring Boot 애플리케이션의 기본 설정을 정의하는 파일입니다. 이 파일을 통해 애플리케이션의 이름, 데이터베이스 설정, 프로파일 설정 등 다양한 환경 변수를 지정할 수 있으며, 애플리케이션의 동작 방식에 중요한 영향을 미칩니다.

- 위치: `src/main/resources/config`

### 5.1.1. Spring 기본 설정
* `spring.application.name`:
  - 애플리케이션의 이름을 지정합니다.
  - 용도: 다른 서비스에서 이 애플리케이션을 식별하는 데 사용됩니다.
  - 예시: `Ledger-Service-Server`

* `spring.profiles.default`:
  - 기본 활성화할 프로파일을 정의합니다.
  - 용도: 별도의 프로파일 지정이 없을 때 사용할 기본 프로파일을 설정합니다.
  - 지원 프로파일: sample, dev
  - 예시: `sample`

* `spring.profiles.group.dev`: 🔒
  - `dev` 프로파일 그룹에 포함된 개별 프로파일을 정의합니다.
  - 용도: 개발 환경에서 사용할 설정들을 묶어 관리합니다.
  - 프로파일 파일명 규칙: 각 프로파일에 해당하는 설정 파일은 그룹 내에 정의된 이름 그대로 사용됩니다. 예를 들어, auth 프로파일은 application-auth.yml, database 프로파일은 application-database.yml로 작성됩니다.

* `spring.profiles.group.sample`: 🔒
  - `sample` 프로파일 그룹에 포함된 개별 프로파일을 정의합니다.
  - 용도: 샘플 환경에서 사용할 설정들을 묶어 관리합니다.
  - 프로파일 파일명 규칙: 각 프로파일에 해당하는 설정 파일은 그룹 내에 정의된 이름 그대로 사용됩니다.

<br/>

### 5.1.2. Jackson 기본 설정

Jackson은 Spring Boot에서 기본적으로 사용되는 JSON 직렬화/역직렬화 라이브러리입니다. Jackson의 설정을 통해 JSON 데이터의 직렬화 방식이나 포맷을 조정할 수 있으며, 데이터 전송 시 성능과 효율성을 높일 수 있습니다.

* `spring.jackson.default-property-inclusion`: 🔒
  - 속성 값이 null일 때 직렬화하지 않도록 설정합니다.
  - 예시: non_null

<br/>

### 5.1.3. 서버 설정
서버 설정은 애플리케이션이 요청을 수신할 포트 번호 등을 정의합니다.

* `server.port`:
  - 애플리케이션이 실행될 포트 번호입니다. Ledger Service 서버의 포트 설정의 기본값은 8098입니다.
  - 값: 8098

<br/>

## 5.2. application-logging.yml
- 역할: 로그 그룹과 로그 레벨을 설정합니다. 이 설정 파일을 통해 특정 패키지나 모듈에 대해 로그 그룹을 정의하고, 각 그룹에 대한 로그 레벨을 개별적으로 지정할 수 있습니다.

- 위치: `src/main/resources/config`

### 5.2.1. 로깅 설정
* `logging.level`:
  - 로그 레벨을 설정합니다.
  - 레벨을 debug 설정함으로써, 지정된 패키지에 대해 DEBUG 레벨 이상(INFO, WARN, ERROR, FATAL)의 모든 로그 메시지를 볼 수 있습니다.

<br/>

## 5.3. application-database.yml

- 역할: DID Ledger Service 서버에서 사용할 데이터베이스 연결 정보를 설정합니다. PostgreSQL 데이터베이스와의 연동을 위한 설정들이 포함되어 있으며, JPA와 Liquibase 설정도 함께 관리합니다.

- 위치: `src/main/resources/config`

### 5.3.1. 데이터베이스 연동 설정

#### PostgreSQL 데이터베이스 설정

- `spring.datasource.url`:
  - PostgreSQL 데이터베이스 연결 URL을 설정합니다.
  - 예시: `jdbc:postgresql://localhost:5432/ledger_service`

- `spring.datasource.username`:
  - 데이터베이스 사용자명을 설정합니다.
  - 예시: `ledger_user`

- `spring.datasource.password`:
  - 데이터베이스 비밀번호를 설정합니다.
  - 예시: `your_password_here`

- `spring.datasource.driver-class-name`:
  - PostgreSQL JDBC 드라이버 클래스명을 설정합니다.
  - 예시: `org.postgresql.Driver`

#### JPA 설정

- `spring.jpa.hibernate.ddl-auto`:
  - Hibernate의 DDL 자동 생성 옵션을 설정합니다.
  - 옵션: `none`, `validate`, `update`, `create`, `create-drop`
  - 예시: `none` (운영 환경에서는 none 권장)

- `spring.jpa.show-sql`:
  - JPA가 실행하는 SQL 쿼리를 콘솔에 출력할지 여부를 설정합니다.
  - 예시: `false` (운영 환경에서는 false 권장)

#### Liquibase 설정

- `spring.liquibase.change-log`:
  - Liquibase 변경 로그 파일의 경로를 설정합니다.
  - 예시: `classpath:db/changelog/db.changelog-master.xml`

<br/>

# 6. 프로파일 설정 및 사용

## 6.1. 프로파일 개요 (`sample`, `dev`)
DID Ledger Service 서버는 다양한 환경에서 실행될 수 있도록 `sample`, `dev` 두 가지 프로파일을 지원합니다.

각 프로파일은 해당 환경에 맞는 설정을 적용하도록 설계되었습니다. 기본적으로 DID Ledger Service 서버는 `sample` 프로파일로 설정되어 있으며, 이 프로파일은 데이터베이스나 블록체인과 같은 외부 서비스와의 연동 없이 서버를 독립적으로 구동할 수 있도록 설계되었습니다. `sample` 프로파일은 API 호출 테스트에 적합하여, 개발자가 애플리케이션의 기본 동작을 빠르게 확인할 수 있도록 지원합니다. 이 프로파일은 모든 API 호출에 대해 고정된 응답 데이터를 반환하므로, 초기 개발환경에서 유용합니다.

샘플 API 호출은 JUnit 테스트로 작성되어 있으므로, 테스트 작성 시 이를 참고할 수 있습니다.

반면, `dev` 프로파일은 실제 동작을 수행하도록 설계되었습니다. 이 프로파일을 사용하면 실데이터에 대한 테스트와 검증이 가능합니다. `dev` 프로파일을 활성화하면 실제 데이터베이스, 블록체인 등 외부 서비스와 연동되어, 실제 환경에서의 애플리케이션 동작을 테스트할 수 있습니다.

### 6.1.1. `sample` 프로파일
`sample` 프로파일은 외부 서비스(DB, 블록체인 등)와의 연동 없이 서버를 독립적으로 구동할 수 있도록 설계되었습니다. 이 프로파일은 API 호출 테스트에 적합하며, 개발자가 애플리케이션의 기본 동작을 빠르게 확인할 수 있습니다. 모든 API 호출에 대해 고정된 응답 데이터를 반환하므로, 초기 개발 단계나 기능 테스트에 유용합니다. 외부 시스템과의 연동이 전혀 필요하지 않기 때문에, 단독으로 서버를 실행하고 테스트할 수 있는 환경을 제공합니다.

### 6.1.2. `dev` 프로파일
`dev` 프로파일은 개발 환경에 적합한 설정을 포함하며, 개발 서버에서 사용됩니다. 이 프로파일을 사용하려면 개발 환경의 데이터베이스와 블록체인 노드에 대한 설정이 필요합니다.

## 6.2. 프로파일 설정 방법
각 구동 방법별로 프로파일을 변경하는 방법을 설명합니다.

### 6.2.1. IDE를 사용한 서버 구동 시
- **설정 파일 선택:** `src/main/resources/config` 경로에서 `application.yml` 파일을 선택합니다.
- **프로파일 지정:** IDE의 실행 설정(Run/Debug Configurations)에서 `--spring.profiles.active={profile}` 옵션을 추가해 원하는 프로파일을 활성화합니다.
- **설정 적용:** 활성화된 프로파일에 따라 해당 설정 파일이 적용됩니다.

### 6.2.2. 콘솔 명령어를 사용한 서버 구동 시
- **설정 파일 선택:** 빌드된 JAR 파일과 동일한 디렉토리 또는 설정 파일이 위치한 경로에 프로파일별 설정 파일을 준비합니다.
- **프로파일 지정:** 서버 구동 명령어에 `--spring.profiles.active={profile}` 옵션을 추가하여 원하는 프로파일을 활성화합니다.

  ```bash
  java -jar build/libs/did-ledger-service-server-2.0.0.jar --spring.profiles.active={profile}
  ```

- **설정 적용:** 활성화된 프로파일에 따라 해당 설정 파일이 적용됩니다.

### 6.2.3. Docker를 사용한 서버 구동 시
- **설정 파일 선택:** Docker 이미지 생성 시, Dockerfile에서 설정 파일 경로를 지정하거나, 외부 설정 파일을 Docker 컨테이너에 마운트합니다.
- **프로파일 지정:** Docker Compose 파일 또는 Docker 실행 명령어에서 `SPRING_PROFILES_ACTIVE` 환경 변수를 설정하여 프로파일을 지정합니다.

  ```yaml
  environment:
    - SPRING_PROFILES_ACTIVE={profile}
  ```

- **설정 적용:** Docker 컨테이너 실행 시 지정된 프로파일에 따라 설정이 적용됩니다.

각 방법에 따라 프로파일별 설정을 유연하게 변경하여 사용할 수 있으며, 프로젝트 환경에 맞는 설정을 쉽게 적용할 수 있습니다.

<br/>

# 7. Docker로 빌드 후 구동하기

## 7.1. Docker 이미지 빌드 방법 (`Dockerfile` 기반)
다음 명령어로 Docker 이미지를 빌드합니다:

```bash
docker build -t did-ledger-service-server .
```

## 7.2. Docker 이미지 실행
빌드된 이미지를 실행합니다:

```bash
docker run -d -p 8098:8098 did-ledger-service-server
```

## 7.3. Docker Compose를 이용한 구동

### 7.3.1. `docker-compose.yml` 파일 설명
`docker-compose.yml` 파일을 사용하여 여러 컨테이너를 쉽게 관리할 수 있습니다.

```yaml
version: '3'
services:
  app:
    image: did-ledger-service-server
    ports:
      - "8098:8098"
    volumes:
      - ${your-config-dir}:/app/config
    environment:
      - SPRING_PROFILES_ACTIVE=sample
    depends_on:
      - postgres
  
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: ledger_service
      POSTGRES_USER: ledger_user
      POSTGRES_PASSWORD: your_password_here
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### 7.3.2. 컨테이너 실행 및 관리
다음 명령어로 Docker Compose를 사용해 컨테이너를 실행합니다:

```bash
docker-compose up -d
```

### 7.3.3. 서버 설정 방법
위의 예시에서 `${your-config-dir}` 디렉토리를 컨테이너 내 `/app/config`로 마운트하여 설정 파일을 공유합니다.
- 추가적인 설정이 필요한 경우, 마운트된 폴더에 별도의 property 파일을 추가하여 설정을 변경할 수 있습니다.
  - 예를 들어, `application.yml` 파일을 `${your-config-dir}`에 추가하고, 이 파일에 변경할 설정을 작성합니다.
  - `${your-config-dir}`에 위치한 `application.yml` 파일은 기본 설정 파일보다 우선적으로 적용됩니다.
- 자세한 설정은 [5. 설정 가이드](#5-설정-가이드)를 참고합니다.

[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0_ko.md