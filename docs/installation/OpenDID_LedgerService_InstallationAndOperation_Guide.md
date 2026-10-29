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
- Version: v3.0.0

Table of Contents
==

- [1. Introduction](#1-introduction)
  - [1.1. Overview](#11-overview)
  - [1.2. Ledger Service Server Definition](#12-ledger-service-server-definition)
  - [1.3. System Requirements](#13-system-requirements)
- [2. Prerequisites](#2-prerequisites)
  - [2.1. Git Installation](#21-git-installation)
  - [2.2. PostgreSQL Installation](#22-postgresql-installation)
  - [2.3. Node.js Installation](#23-nodejs-installation)
- [3. Cloning Source Code from GitHub](#3-cloning-source-code-from-github)
  - [3.1. Source Code Cloning](#31-source-code-cloning)
  - [3.2. Directory Structure](#32-directory-structure)
- [4. Server Execution Methods](#4-server-execution-methods)
  - [4.1. Running with IntelliJ IDEA (Gradle Support)](#41-running-with-intellij-idea-gradle-support)
    - [4.1.1. IntelliJ IDEA Installation and Setup](#411-intellij-idea-installation-and-setup)
    - [4.1.2. Opening Project in IntelliJ](#412-opening-project-in-intellij)
    - [4.1.3. Gradle Build](#413-gradle-build)
    - [4.1.4. Server Execution](#414-server-execution)
    - [4.1.5. Server Configuration](#415-server-configuration)
  - [4.2. Running with Console Commands](#42-running-with-console-commands)
    - [4.2.1. Gradle Build Commands](#421-gradle-build-commands)
    - [4.2.2. Server Execution Method](#422-server-execution-method)
    - [4.2.3. Server Configuration Method](#423-server-configuration-method)
- [5. Configuration Guide](#5-configuration-guide)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Spring Basic Configuration](#511-spring-basic-configuration)
    - [5.1.2. Jackson Basic Configuration](#512-jackson-basic-configuration)
    - [5.1.3. Server Configuration](#513-server-configuration)
    - [5.1.4. Actuator Configuration](#514-actuator-configuration)
  - [5.2. application-logging.yml](#52-application-loggingyml)
    - [5.2.1. Logging Configuration](#521-logging-configuration)
  - [5.3. application-database.yml](#53-application-databaseyml)
    - [5.3.1. Database Integration Configuration](#531-database-integration-configuration)
    - [5.3.2. Database Schema (Liquibase)](#532-database-schema-liquibase)
  - [5.4. application-auth.yml](#54-application-authyml)
    - [5.4.1. Admin Console Authentication (JWT) Configuration](#541-admin-console-authentication-jwt-configuration)
  - [5.5. application-tas.yml](#55-application-tasyml)
    - [5.5.1. TAS Integration Configuration](#551-tas-integration-configuration)
  - [5.6. application-spring-docs.yml](#56-application-spring-docsyml)
    - [5.6.1. API Documentation (Swagger) Configuration](#561-api-documentation-swagger-configuration)
  - [5.7. application-jasypt.yml](#57-application-jasyptyml)
    - [5.7.1. Jasypt Configuration](#571-jasypt-configuration)
  - [5.8. application-wallet.yml](#58-application-walletyml)
- [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)
  - [6.1. Profile Overview (`sample`, `dev`, `prod`)](#61-profile-overview-sample-dev-prod)
    - [6.1.1. `sample` Profile](#611-sample-profile)
    - [6.1.2. `dev` Profile](#612-dev-profile)
    - [6.1.3. `prod` Profile](#613-prod-profile)
  - [6.2. Profile Configuration Method](#62-profile-configuration-method)
    - [6.2.1. When Running Server with IDE](#621-when-running-server-with-ide)
    - [6.2.2. When Running Server with Console Commands](#622-when-running-server-with-console-commands)
- [7. Admin Console](#7-admin-console)
  - [7.1. Admin Console Overview](#71-admin-console-overview)
  - [7.2. Building and Deploying the Admin Console](#72-building-and-deploying-the-admin-console)
    - [7.2.1. Deploying as Part of the Gradle Build](#721-deploying-as-part-of-the-gradle-build)
    - [7.2.2. Running the Frontend Development Server](#722-running-the-frontend-development-server)
  - [7.3. First Login and Initial Setup](#73-first-login-and-initial-setup)
    - [7.3.1. Initial Admin Account](#731-initial-admin-account)
    - [7.3.2. Changing ID and Password on First Login](#732-changing-id-and-password-on-first-login)
    - [7.3.3. Adding Admins and Password Policy](#733-adding-admins-and-password-policy)
  - [7.4. Resetting a Forgotten Password](#74-resetting-a-forgotten-password)
- [8. API Key Operation](#8-api-key-operation)
  - [8.1. API Key Overview](#81-api-key-overview)
  - [8.2. Issuing an API Key](#82-issuing-an-api-key)
  - [8.3. Deactivating, Activating, and Renewing an API Key](#83-deactivating-activating-and-renewing-an-api-key)
  - [8.4. Enabling API Key Validation](#84-enabling-api-key-validation)
  - [8.5. Calling Server (TAS, Issuer) Configuration](#85-calling-server-tas-issuer-configuration)

# 1. Introduction

## 1.1. Overview
This document provides a guide for the installation and operation of the DID Ledger Service server. It explains the installation process, configuration methods, and execution procedures step by step to enable users to efficiently install and operate the service. It also explains how to use the Admin Console included in the Ledger Service server and how to operate API keys.

For the complete installation guide of OpenDID, please refer to the [Open DID Installation Guide].

<br/>

## 1.2. Ledger Service Server Definition

The DID Ledger Service Server (LSS) is an alternative service that utilizes PostgreSQL database to provide the same functionality as blockchain.

Key Features:
- DID Document creation, registration, and status changes
- VC Meta creation and VC status changes
- VC Schema registration and management
- ZKP Credential Schema and ZKP Credential Definition registration and management
- API key based authentication of Agent API calls (Roles: `TAS`, `ISSUER`, `READ`)
- LSS Admin Console: DID, VC Metadata, and VC Schema lookup, API key management, admin management, server configuration, and API log and audit log lookup

Through this server, you can build a stable and efficient decentralized identity management system without the need for complex blockchain infrastructure.

<br/>

## 1.3. System Requirements
- **Java 21** or higher
- **Gradle**: Use the Gradle Wrapper (Gradle 8.7) included in the repository.
- **PostgreSQL 13** or higher (required for all profiles)
- **Node.js 22.9.0**: Downloaded and used automatically by the Gradle Node Plugin during the Gradle build, so no separate installation is needed. A local Node.js and npm installation is required only when running the frontend development server directly.
- Network access during the first build to download Gradle, Maven dependencies, Node.js, and npm packages
- Minimum **2GB RAM** and **10GB disk space**

<br/>

# 2. Prerequisites

This chapter guides you through the prerequisites needed before installing the components of the Open DID project.

## 2.1. Git Installation

`Git` is a distributed version control system that tracks changes in source code and supports collaboration among multiple developers. Git is essential for managing and version controlling the source code of the Open DID project.

If installation is successful, you can check the Git version using the following command:

```bash
git --version
```

> **Reference Links**
> - [Git Installation Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 2.2. PostgreSQL Installation

The Ledger Service server stores ledger data such as DID Documents and VC Meta, as well as admin, API key, server configuration, and log data, in PostgreSQL. Before running the server, install PostgreSQL and create the database and user that the Ledger Service server will use.

The default configuration in the repository (`application-database.yml`) uses the following connection information.

| Item       | Default       |
| ---------- | ------------- |
| Host       | `127.0.0.1`   |
| Port       | `5430`        |
| Database   | `lss`         |
| Username   | `omn`         |
| Password   | `omn`         |

The following is an example of creating a user and database that match the default configuration. In production environments, use a secure password and apply the changed values to [5.3. application-database.yml](#53-application-databaseyml).

```sql
CREATE USER omn WITH PASSWORD 'omn';
CREATE DATABASE lss OWNER omn;
```

> **Note**
> - Tables are created automatically by Liquibase when the server starts. For details, refer to [5.3.2. Database Schema (Liquibase)](#532-database-schema-liquibase).

> **Reference Links**
> - [PostgreSQL Download](https://www.postgresql.org/download/)

<br/>

## 2.3. Node.js Installation

The Admin Console (React) is built automatically together with the Gradle build, and Gradle automatically downloads the required Node.js (22.9.0). Therefore, installing Node.js is necessary only when running or building the frontend separately, as described in [7.2.2. Running the Frontend Development Server](#722-running-the-frontend-development-server).

After installation, you can verify the installation with the following commands:

```bash
node --version
npm --version
```

> **Reference Links**
> - [Node.js Official Download Page](https://nodejs.org/)

<br/>

# 3. Cloning Source Code from GitHub

## 3.1. Source Code Cloning

The `git clone` command clones source code from a remote repository hosted on GitHub to your local computer. Using this command, you can work with the entire source code and related files of the project locally. After cloning, you can proceed with necessary work within the repository and push changes back to the remote repository.

Open a terminal and execute the following command to copy the Ledger Service server repository to your local computer:
```bash
# Clone repository from Git repository
git clone https://github.com/OmniOneID/did-ledger-service-server.git

# Navigate to the cloned repository
cd did-ledger-service-server
```

> **Reference Links**
> - [Git Clone Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 3.2. Directory Structure
The main directory structure of the cloned project is as follows:

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
| CHANGELOG.md               | Version-specific changes of the project              |
| CODE_OF_CONDUCT.md         | Code of conduct for contributors                     |
| CONTRIBUTING.md            | Contribution guidelines and procedures               |
| LICENSE                    | License                                              |
| dependencies-license.md    | License information for project dependencies         |
| MAINTAINERS.md             | Guidelines for project maintainers                   |
| RELEASE-PROCESS.md         | Procedures for releasing new versions                |
| SECURITY.md                | Security policy and vulnerability reporting          |
| docs                       | Documentation                                        |
| ┖ api                      | API guide documentation                              |
| ┖ errorCode                | Error codes and troubleshooting guides               |
| ┖ installation             | Installation and configuration guides                |
| source                     | Server and Admin Console source code                 |
| ┖ did-ledger-service-admin | Admin Console frontend (React, TypeScript) source code |
| ┖ did-ledger-service-server | Ledger Service server (Spring Boot) source code     |
| &nbsp;&nbsp;┖ gradle       | Gradle Wrapper settings and scripts                  |
| &nbsp;&nbsp;┖ libs         | Open DID SDK libraries                               |
| &nbsp;&nbsp;┖ sample       | Sample data files (DID Document, VC Meta)            |
| &nbsp;&nbsp;┖ src/main/java | Java source code                                    |
| &nbsp;&nbsp;┖ src/main/resources | Configuration files (`config`), Liquibase changelogs (`db`), Admin Console build output (`static`) |
| &nbsp;&nbsp;┖ build.gradle | Gradle build configuration file (includes Admin Console build tasks) |
| &nbsp;&nbsp;┖ README.md    | Source code overview and guidance                    |

<br/>

# 4. Server Execution Methods
This chapter guides you through two methods of running the server. With both methods, the Admin Console is built during the Gradle build and included in the server.

1. **Using IDE**: You can open the project in an Integrated Development Environment (IDE), set up execution configuration, and run the server directly. This method is useful for quickly testing code changes during development.

2. **Using console commands after build**: After building the project, you can run the server by executing the generated JAR file from the console using commands (`java -jar`). This method is mainly used when deploying the server or running it in production environments.

> **Note**
> - The repository currently does not include a `Dockerfile` or `docker-compose.yml`, so running with Docker is not covered in this document.

## 4.1. Running with IntelliJ IDEA (Gradle Support)

IntelliJ IDEA is an Integrated Development Environment (IDE) widely used for Java development. It supports build tools like Gradle, making project setup and dependency management very easy. Since the Open DID server is built using Gradle, you can easily set up the project and run the server in IntelliJ IDEA.

### 4.1.1. IntelliJ IDEA Installation and Setup
1. Install IntelliJ. (Refer to the link below for installation instructions)

> **Reference Links**
> - [IntelliJ IDEA Download](https://www.jetbrains.com/idea/download/)

### 4.1.2. Opening Project in IntelliJ
- Launch IntelliJ and select `File -> Open`. When the file selection window appears, select the `source/did-ledger-service-server` folder of the repository cloned in [3.1. Source Code Cloning](#31-source-code-cloning).
- When you open the project, the build.gradle file is automatically recognized.
- Gradle automatically downloads the necessary dependency files. Wait for this process to complete.

### 4.1.3. Gradle Build
- In IntelliJ IDEA's `Gradle` tab, execute `Tasks -> build -> build`.
- During the build, the Admin Console frontend (`source/did-ledger-service-admin/frontend`) is built and copied to `src/main/resources/static`. For details, refer to [7.2.1. Deploying as Part of the Gradle Build](#721-deploying-as-part-of-the-gradle-build).
- When the build completes successfully, the project is ready to run.

### 4.1.4. Server Execution
- In IntelliJ IDEA's Gradle tab, select and run `Tasks -> application -> bootRun`.
- Gradle automatically builds and runs the server.
- Check the console log for the "Started [ApplicationName] in [time] seconds" message to confirm that the server has started normally.
- Once the server is running, you can access the following addresses in a browser.
  - Admin Console: `http://localhost:8098/`
  - Swagger UI: `http://localhost:8098/swagger-ui.html`

> **Note**
> - The default profile of the Ledger Service server is `dev`. The `dev` profile requires a PostgreSQL database connection, so complete [2.2. PostgreSQL Installation](#22-postgresql-installation) before running the server.
> - The `sample` profile also requires a database connection. For details, please refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage).

<br/>

### 4.1.5. Server Configuration
- The server needs to modify necessary configurations according to the deployment environment to ensure stable operation. For example, database connection information, port numbers, the JWT signing key, TAS integration information, and other configuration components need to be adjusted for each environment.
- The server configuration files are located in the `source/did-ledger-service-server/src/main/resources/config` path.
- For detailed configuration methods, please refer to [5. Configuration Guide](#5-configuration-guide).

<br/>

## 4.2. Running with Console Commands

This section guides you through running the DID Ledger Service server using console commands. It explains the process of building the project using Gradle and running the server using the generated JAR file.

### 4.2.1. Gradle Build Commands

- Build the source using gradlew:
  ```shell
    # Navigate to the server source folder of the cloned repository
    cd did-ledger-service-server/source/did-ledger-service-server

    # Grant execution permission to Gradle Wrapper
    chmod 755 ./gradlew

    # Clean build the project (delete previous build files and build anew)
    ./gradlew clean build
  ```
  > Note
  > - gradlew is short for Gradle Wrapper, a script used to execute Gradle in a project. Even if Gradle is not installed locally, it automatically downloads and executes the version of Gradle specified by the project. This allows developers to build the project in the same environment regardless of whether Gradle is installed.
  > - The Admin Console frontend is built automatically during the Gradle build and included in the JAR. If the frontend build is not needed (for example, when testing only the backend or when the frontend build output already exists in `src/main/resources/static`), you can skip it by adding the following option:
  >   - `./gradlew clean build -DskipFrontendBuild=true`

- Navigate to the built folder and confirm that the JAR file has been created:
    ```shell
      cd build/libs
      ls
    ```
- This command creates the `did-ledger-service-server-2.0.0.jar` file. (The version in the file name follows the `version` value in `build.gradle`.)
- The `did-ledger-service-server-2.0.0-plain.jar` file created alongside it does not contain dependencies, so use the JAR file without `-plain` to run the server.

<br/>

### 4.2.2. Server Execution Method
Run the server using the built JAR file:

```bash
java -jar did-ledger-service-server-2.0.0.jar
```

- Once the server is running, you can access `http://localhost:8098/` (Admin Console) and `http://localhost:8098/swagger-ui.html` (Swagger UI) in a browser.

> **Note**
> - The default profile of the Ledger Service server is `dev`, which requires a PostgreSQL database connection.
> - To change the profile, use the `--spring.profiles.active={profile}` option. For details, please refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage).

<br/>

### 4.2.3. Server Configuration Method
- The server needs to modify necessary configurations according to the deployment environment to ensure stable operation. For example, database connection information, port numbers, the JWT signing key, TAS integration information, and other configuration components need to be adjusted for each environment.
- The server configuration files are located in the `source/did-ledger-service-server/src/main/resources/config` path.
- When running the built JAR, you can override the default configuration without modifying the configuration inside the JAR by placing the configuration files to change in a `config` folder in the working directory. For details, refer to [6.2.2. When Running Server with Console Commands](#622-when-running-server-with-console-commands).
- For detailed configuration methods, please refer to [5. Configuration Guide](#5-configuration-guide).

<br/>

# 5. Configuration Guide

This chapter describes the configuration files in the `source/did-ledger-service-server/src/main/resources/config` path and each configuration item. Items marked with 🔒 are fixed values required for server operation, and it is recommended not to change them.

| Configuration File            | Profile       | Main Contents                               |
| ----------------------------- | ------------- | ------------------------------------------- |
| `application.yml`             | (common)      | Application name, profile groups, port, Actuator |
| `application-logging.yml`     | `logging`     | Log groups and log levels                   |
| `application-database.yml`    | `database`    | PostgreSQL, JPA, Liquibase                  |
| `application-auth.yml`        | `auth`        | Admin Console JWT signing key and token lifetimes |
| `application-tas.yml`         | `tas`         | TAS address for sending password reset OTP emails |
| `application-spring-docs.yml` | `spring-docs` | Swagger UI and OpenAPI documentation        |
| `application-jasypt.yml`      | `jasypt`      | Jasypt encryption settings                  |
| `application-wallet.yml`      | (not used)    | Not included in any profile group           |

## 5.1. application.yml

- Role: The application.yml file defines the basic configuration of a Spring Boot application. Through this file, you can specify various environment variables such as application name, profile settings, port, etc., which significantly affect the application's behavior.

- Location: `src/main/resources/config`

### 5.1.1. Spring Basic Configuration
* `spring.application.name`:
  - Specifies the name of the application.
  - Purpose: Used by other services to identify this application.
  - Example: `Ledger-Service-Server`

* `spring.profiles.default`:
  - Defines the default profile to activate.
  - Purpose: Sets the default profile to use when no other profile is specified.
  - Supported profiles: `sample`, `dev`, `prod`
  - Default: `dev`

* `spring.profiles.group.dev`: 🔒
  - Defines individual profiles included in the `dev` profile group.
  - Included profiles: `auth`, `database`, `jasypt`, `logging`, `spring-docs`, `tas`
  - Profile file naming convention: Configuration files corresponding to each profile use the names defined in the group as-is. For example, the auth profile is written as application-auth.yml, and the database profile as application-database.yml.

* `spring.profiles.group.sample`: 🔒
  - Defines individual profiles included in the `sample` profile group.
  - Included profiles: Same as the `dev` group

* `spring.profiles.group.prod`: 🔒
  - Defines individual profiles included in the `prod` profile group.
  - Included profiles: Same as the `dev` group

<br/>

### 5.1.2. Jackson Basic Configuration

Jackson is a JSON serialization/deserialization library used by default in Spring Boot. Through Jackson configuration, you can adjust the serialization method or format of JSON data, improving performance and efficiency during data transmission.

* `spring.jackson.default-property-inclusion`: 🔒
  - Sets not to serialize when property values are null.
  - Value: `non_null`

* `spring.jackson.serialization.fail-on-empty-beans`: 🔒
  - Prevents an error when serializing an object that has no properties to serialize.
  - Value: `false`

<br/>

### 5.1.3. Server Configuration
Server configuration defines the port number where the application will receive requests. The Admin Console is also served on the same port.

* `server.port`:
  - The port number where the application will run. The default value for the Ledger Service server port configuration is 8098.
  - Value: `8098`

<br/>

### 5.1.4. Actuator Configuration
Actuator provides server status check functionality. In the default configuration, only the health check endpoint (`/actuator/health`) is enabled.

* `management.endpoints.enabled-by-default`:
  - Whether all Actuator endpoints are enabled by default.
  - Value: `false`

* `management.endpoints.web.exposure.include`:
  - List of endpoints exposed over the web.
  - Value: `health`, `shutdown`

* `management.endpoint.health.enabled` / `management.endpoint.health.show-details`:
  - Whether the health endpoint is enabled and whether details are shown.
  - Value: `true` / `never`

* `management.endpoint.shutdown.enabled`:
  - Whether the shutdown endpoint is enabled. It is included in the exposure list but disabled.
  - Value: `false`

<br/>

## 5.2. application-logging.yml
- Role: Sets log groups and log levels. Through this configuration file, you can define log groups for specific packages or modules and specify log levels individually for each group.

- Location: `src/main/resources/config`

### 5.2.1. Logging Configuration
* `logging.group`:
  - Defines log groups.
  - `repository`: `org.omnione.did.repository` (API controllers and services)
  - `base`: `org.omnione.did.base` (common configuration, DB, utilities)

* `logging.level`:
  - Sets the log level for each log group.
  - By setting the level to debug, you can see all log messages at DEBUG level and above (INFO, WARN, ERROR, FATAL) for specified packages.
  - Default: `repository: debug`, `base: info`

<br/>

## 5.3. application-database.yml

- Role: Configures database connection information to be used by the DID Ledger Service server. It includes configurations for integration with PostgreSQL database and also manages JPA and Liquibase settings.

- Location: `src/main/resources/config`

### 5.3.1. Database Integration Configuration

#### PostgreSQL Database Configuration

- `spring.datasource.url`:
  - Sets the PostgreSQL database connection URL.
  - Default: `jdbc:postgresql://127.0.0.1:5430/lss`

- `spring.datasource.username`:
  - Sets the database username.
  - Default: `omn`

- `spring.datasource.password`:
  - Sets the database password.
  - Default: `omn`

- `spring.datasource.driver-class-name`: 🔒
  - Sets the PostgreSQL JDBC driver class name.
  - Value: `org.postgresql.Driver`

#### JPA Configuration

- `spring.jpa.hibernate.ddl-auto`: 🔒
  - Sets Hibernate's DDL auto-generation option. Tables are created by Liquibase, and Hibernate validates that entities match the table structure when the server starts.
  - Value: `validate`

- `spring.jpa.hibernate.naming.physical-strategy`: 🔒
  - Strategy for converting entity field names (camel case) to table column names (snake case).
  - Value: `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`

- `spring.jpa.show-sql`:
  - Sets whether to output SQL queries executed by JPA to the console.
  - Default: `true` (`false` is recommended for production environments)

- `spring.jpa.properties.hibernate.format_sql`:
  - Sets whether to format the output SQL.
  - Default: `false`

- `spring.jpa.open-in-view`:
  - Sets whether to keep the persistence context open until request processing completes.
  - Default: `true`

#### Liquibase Configuration

- `spring.liquibase.enabled`:
  - Sets whether to run Liquibase when the server starts.
  - Default: `true`

- `spring.liquibase.change-log`: 🔒
  - Sets the path to the Liquibase change log file.
  - Value: `classpath:/db/changelog/master.xml`

<br/>

### 5.3.2. Database Schema (Liquibase)

When the server starts, Liquibase reads the changelogs in the order `db/changelog/master.xml` → `changeset/set_master.xml` and applies the changesets that have not yet been applied, in order. Therefore, you do not need to create tables manually, and when upgrading from v2.0.0, only the `set.1` and `set.2` changesets added after v2.0.0 are applied automatically.

| Changeset | Tables | Description |
| --------- | ------ | ----------- |
| `set.0` | `did`, `did_document`, `did_document_status_history`, `did_document_revoked` | DIDs, DID Documents, DID Document status change history, and revocation information |
| `set.0` | `vc_metadata`, `vc_status_history` | VC Meta and VC status change history |
| `set.0` | `vc_schema`, `zkp_credential_schema`, `zkp_credential_definition` | VC Schema, ZKP Credential Schema, ZKP Credential Definition |
| `set.1` | `admin` | Admin Console admin accounts (including the initial ROOT account, see [7.3.1. Initial Admin Account](#731-initial-admin-account)) |
| `set.1` | `admin_password_policy` | Admin password policy (including initial values) |
| `set.1` | `admin_password_reset_token`, `admin_otp_send_limit` | Password reset OTPs and OTP sending limits |
| `set.1` | `api_key` | API keys for calling the Agent API (stored as SHA-256 hash values) |
| `set.1` | `server_config` | Server configuration values (`API_KEY_VALIDATION_ENABLED`, initial value `false`) |
| `set.1` | `api_log` | Admin API and Agent API call logs and audit logs |
| `set.1` | Existing tables | Adds lookup indexes, adds the `did_document_revoked.revoked_at` column, allows NULL for `vc_status_history.from_status` |
| `set.2` | `admin` | Adds the `password_algo` column, extends the `login_password` column length, corrects `password_reset_reason` of the initial ROOT account |

<br/>

## 5.4. application-auth.yml

- Role: Configures the signing key and lifetimes of the JWTs (access token, refresh token) issued at Admin Console login.

- Location: `src/main/resources/config`

### 5.4.1. Admin Console Authentication (JWT) Configuration

- `security.jwt.secret`:
  - Secret key string used for JWT signing (HMAC).
  - The repository contains a sample value, so in production environments be sure to change it to a random string of at least 32 bytes. When the value is changed, previously issued tokens can no longer be used and admins must log in again.

- `security.jwt.access-ttl-seconds`:
  - Lifetime of the access token (seconds).
  - Default: `1800` (30 minutes)

- `security.jwt.refresh-ttl-seconds`:
  - Lifetime of the refresh token (seconds). When the access token expires, the Admin Console obtains a new access token using the refresh token.
  - Default: `1209600` (14 days)

<br/>

## 5.5. application-tas.yml

- Role: Configures the address of the TAS (Trust Agent Server) that the Ledger Service server integrates with.

- Location: `src/main/resources/config`

### 5.5.1. TAS Integration Configuration

- `tas.url`:
  - Base URL of the TAS server.
  - Purpose: Admin Console password reset OTP emails are sent through the TAS notification API (`{tas.url}/noti/api/v1/send-email`).
  - Default: `http://127.0.0.1:8090`

> **Note**
> - If the TAS server cannot be reached or TAS email sending is not configured, sending the OTP email fails. This setting is used only for the password reset function and does not affect the Agent API or normal login.

<br/>

## 5.6. application-spring-docs.yml

- Role: Configures the SpringDoc-based API documentation (OpenAPI) and Swagger UI.

- Location: `src/main/resources/config`

### 5.6.1. API Documentation (Swagger) Configuration

- `springdoc.swagger-ui.path`:
  - Swagger UI access path.
  - Default: `/swagger-ui.html`

- `springdoc.api-docs.path`:
  - OpenAPI document (JSON) path.
  - Default: `/api-docs`

- `springdoc.server.url`:
  - Server URL used when calling APIs from Swagger UI. Set this when the server is exposed at an external address, for example through a reverse proxy.
  - If the value is empty, the default SpringDoc behavior applies. If the value is entered without `http://` or `https://`, `https://` is prepended automatically.
  - Default: (empty)

- `springdoc.swagger-ui.groups-order`, `operations-sorter`, `disable-swagger-default-url`, `display-request-duration`, `show-actuator`, `default-consumes-media-type`, `default-produces-media-type`:
  - Configure how Swagger UI is displayed and the default media type (`application/json`).

> **Note**
> - The OpenAPI document defines the `X-API-Key` header authentication scheme, so when API key validation is enabled you can call the Agent API from Swagger UI by entering an API key with the `Authorize` button.

<br/>

## 5.7. application-jasypt.yml

- Role: Jasypt settings required when configuration values are encrypted in the `ENC(...)` format.

- Location: `src/main/resources/config`

### 5.7.1. Jasypt Configuration

- `jasypt.encryptor.algorithm`:
  - Encryption/decryption algorithm. `application.yml` also declares `PBEWithHMACSHA512AndAES_256`, but profile configuration files take precedence, so the value in `application-jasypt.yml` is applied.
  - Default: `PBEWithMD5AndDES`

- `jasypt.encryptor.key`:
  - Declared as the `${jasypt-key}` placeholder.

> **Note**
> - The default configuration files in the repository contain no values encrypted in the `ENC(...)` format.

<br/>

## 5.8. application-wallet.yml

- `application-wallet.yml` (`wallet.file-path`, `wallet.password`) is currently not included in any profile group, so it is not loaded at default startup and is not used in the current version. No configuration is required.

<br/>

# 6. Profile Configuration and Usage

## 6.1. Profile Overview (`sample`, `dev`, `prod`)
The DID Ledger Service server supports three profiles, `sample`, `dev`, and `prod`, to run in various environments. If no profile is specified, the server runs with the `dev` profile.

All three profile groups include the same individual profiles (`auth`, `database`, `jasypt`, `logging`, `spring-docs`, `tas`). Therefore, a PostgreSQL database connection is required regardless of the profile, and the Admin Console, API key validation, and API log functions operate using the database. What differs by profile is how the Agent API (`/lss/api/v1`) is processed.

| Profile  | Agent API Processing | Purpose |
| -------- | -------------------- | ------- |
| `sample` | Returns fixed sample responses (registration requests are not stored) | API call testing |
| `dev`    | Actually stores data in and reads data from the database (default profile) | Development environment |
| `prod`   | Actually stores data in and reads data from the database | Production environment |

### 6.1.1. `sample` Profile
The `sample` profile replaces the Agent API with sample implementations, returning fixed response data for all lookup API calls and not storing registration and change requests. This is useful when developers want to quickly check the API call flow of an integrating server. However, because the `database` profile is included, a database connection is still required.

### 6.1.2. `dev` Profile
The `dev` profile is the default profile designed to perform actual operations. It stores and reads DID Documents, VC Meta, VC Schemas, and ZKP Credential Schemas/Definitions in the PostgreSQL database. To use this profile, database configuration for the development environment is required.

### 6.1.3. `prod` Profile
The `prod` profile uses the same individual profiles and actual implementations as `dev` and is intended for production environments. The repository does not include an `application-prod.yml` file; if production-specific configuration is needed, you can add an `application-prod.yml` file to override the default configuration.

## 6.2. Profile Configuration Method
This section explains how to change profiles for each execution method.

### 6.2.1. When Running Server with IDE
- **Configuration File Selection:** Select the `application.yml` file from the `src/main/resources/config` path.
- **Profile Specification:** Add the `--spring.profiles.active={profile}` option in the IDE's run configuration (Run/Debug Configurations) to activate the desired profile. When running with Gradle `bootRun`, specify it as `bootRun --args='--spring.profiles.active={profile}'`.
- **Configuration Application:** The corresponding configuration file is applied according to the activated profile.

### 6.2.2. When Running Server with Console Commands
- **Configuration File Selection:** The JAR contains the default configuration files from `src/main/resources/config`. To change the configuration, create a `config` folder in the directory where the JAR is run and put the configuration files to change (for example, `application-database.yml`) in it. Settings in this folder take precedence over the default configuration inside the JAR.
- **Profile Specification:** Add the `--spring.profiles.active={profile}` option to the server execution command to activate the desired profile.

  ```bash
  java -jar build/libs/did-ledger-service-server-2.0.0.jar --spring.profiles.active={profile}
  ```

- **Configuration Application:** The corresponding configuration file is applied according to the activated profile.

You can flexibly change profile-specific configurations according to each method and easily apply configurations suitable for your project environment.

<br/>

# 7. Admin Console

## 7.1. Admin Console Overview

The LSS Admin Console is a web interface for managing the Ledger Service server. Its source code is located in `source/did-ledger-service-admin/frontend` and is written with React, TypeScript, MUI (Toolpad Core), and Vite. The built Admin Console is included as static resources of the Ledger Service server and is served on the same port as the server without a separate web server.

- Access address: `http://{server address}:8098/`
- Admin API: `/lss/admin/v1` (Protected by the JWT access token issued at login. Some APIs, such as login, token reissue, and password reset OTP, can be called without authentication.)

The Admin Console provides the following menus.

| Menu | Submenus | Description |
| ---- | -------- | ----------- |
| DID List | DID List, DID Change History | DID and DID Document lookup, DID Document status change history lookup |
| VC Metadata List | VC Metadata List, VC Change History | VC Meta lookup, VC status change history lookup |
| Schema Management | VC Schema List | VC Schema lookup |
| API Key Management | - | Issuing, deactivating/activating, and renewing API keys for calling the Agent API (see [8. API Key Operation](#8-api-key-operation)) |
| Admin Management | Admin Management, Password Policy Settings | Admin account management and password policy settings |
| Server Configuration | - | Setting whether to use API key validation (see [8.4. Enabling API Key Validation](#84-enabling-api-key-validation)) |
| Log Management | API Log, Audit API Log | API call log and audit log lookup |

<br/>

## 7.2. Building and Deploying the Admin Console

### 7.2.1. Deploying as Part of the Gradle Build

`source/did-ledger-service-server/build.gradle` uses the Gradle Node Plugin (`com.github.node-gradle.node`) to build the Admin Console together with the server. If you build the server as described in [4. Server Execution Methods](#4-server-execution-methods), the Admin Console is included in the JAR without any additional steps.

The build process is as follows.

1. `nodeSetup`: Downloads Node.js 22.9.0 to the `build/nodejs` path.
2. `deleteFrontResources`: Deletes the existing `src/main/resources/static` directory and the `frontend/dist` directory.
3. `npm_install`: Runs `npm install` in `source/did-ledger-service-admin/frontend`.
4. `npm_build`: Runs `npm run build` (`tsc -b && vite build`) to create the build output in `frontend/dist`.
5. `copyFrontResources`: Copies the contents of `frontend/dist` to `src/main/resources/static`.
6. `processResources`: Includes the resources, including the `static` directory, in the JAR.

> **Note**
> - If you specify the `-DskipFrontendBuild=true` option, steps 1 to 5 are skipped and the existing files in the `src/main/resources/static` directory are included in the JAR as-is. If this directory does not contain the Admin Console build output (including `index.html`), the Admin Console screen is not displayed.
> - Because the `src/main/resources/static` directory is deleted and recreated during the frontend build, do not put other files in this directory.
> - When a browser directly accesses an Admin Console sub-path such as `/sign-in`, the server returns `index.html` so that the Admin Console can display the screen.

### 7.2.2. Running the Frontend Development Server

When developing the Admin Console or modifying screens, you can run the frontend standalone with the Vite development server. Node.js and npm must be installed locally (see [2.3. Node.js Installation](#23-nodejs-installation)), and the Ledger Service server must be running on `localhost:8098`.

```bash
# Navigate to the Admin Console frontend folder
cd did-ledger-service-server/source/did-ledger-service-admin/frontend

# Install dependencies
npm install

# Run the development server
npm run dev
```

- `vite.config.ts` configures a proxy that forwards `/lss/admin/v1` requests to `http://localhost:8098`. If you run the Ledger Service server at a different address or port, change the `target` value in `vite.config.ts`.
- The development server binds to `0.0.0.0`; access it at the address printed to the console (Vite default: `http://localhost:5173`).
- Running `npm run build` creates the build output in the `dist` directory, and `npm run preview` lets you preview the build output.

<br/>

## 7.3. First Login and Initial Setup

### 7.3.1. Initial Admin Account

When the server starts for the first time, the following ROOT admin account is created by a Liquibase changeset (`insert-initial-admin-data` in `set.1/create-table_admin.xml`).

| Item     | Value                       |
| -------- | --------------------------- |
| ID       | `admin@opendid.omnione.net` |
| Password | `password`                  |
| Role     | `ROOT`                      |

> **Caution**
> - The initial account information is included in the public repository, so be sure to log in and change the ID and password immediately after installing the server.
> - The Admin Console hashes the entered password with SHA-256 before sending it to the server, and the server hashes it again with BCrypt before storing it. The initial account is registered with a SHA-256 value, which is converted to a BCrypt value after the first successful login.

### 7.3.2. Changing ID and Password on First Login

1. Access `http://{server address}:8098/` in a browser. If you are not logged in, the login screen (`LS Admin Login`) is displayed.
2. Log in with the ID and password in [7.3.1. Initial Admin Account](#731-initial-admin-account).
3. When you log in with the ROOT account for the first time, the ID and password change screen is displayed. Enter a new ID and a new password.
   - The new ID must be in email format. Because password reset OTPs are sent to this address (see [7.4. Resetting a Forgotten Password](#74-resetting-a-forgotten-password)), use an address that can actually receive email.
   - The new password must satisfy the password policy. (See [7.3.3. Adding Admins and Password Policy](#733-adding-admins-and-password-policy))
4. When the change is complete, you are logged in automatically with the new ID and password and moved to the DID List screen.

> **Note**
> - The login session is maintained with an access token (default 30 minutes) and a refresh token (default 14 days). The lifetimes can be changed in [5.4. application-auth.yml](#54-application-authyml).

### 7.3.3. Adding Admins and Password Policy

- **Adding admins:** A ROOT admin can register or delete normal admins (`NORMAL`) and change the passwords of other admins on the `Admin Management` screen. The ROOT admin account cannot be deleted in the Admin Console.
- **First login of normal admins:** Newly registered admins must change their password on the password change screen at their first login.
- **Password policy:** Set the password policy on the `Password Policy Settings` screen. The initial values are as follows.

  | Item | Initial Value |
  | ---- | ------------- |
  | Minimum length | 8 |
  | Uppercase letters | Not required |
  | Numbers | Not required |
  | Special characters | Not required |
  | Password expiry period | 365 days |

- **Password expiry:** When the expiry period has passed since the password was last changed, the password change screen is displayed at the next login. If the expiry period is 0 or less, expiry is not checked.

<br/>

## 7.4. Resetting a Forgotten Password

If an admin forgets the password, the password can be reset from the login screen using an email OTP.

1. Select `Forgot your password?` on the login screen.
2. Enter the admin ID (email address) and request an OTP. A 6-digit OTP is sent to that address.
3. Enter the received OTP and a new password to reset the password.

| Item | Value |
| ---- | ----- |
| OTP validity | 5 minutes |
| OTP resend wait time | 1 minute |
| Maximum sends per day | 5 (when exceeded, OTP sending for that ID is blocked) |
| Maximum OTP verification attempts | 5 |

> **Caution**
> - OTP emails are sent through the notification API of the TAS server configured in `tas.url`. The TAS server must be running and its email sending must be configured. (See [5.5. application-tas.yml](#55-application-tasyml))
> - Instead of this procedure, a ROOT admin can change another admin's password on the `Admin Management` screen.

<br/>

# 8. API Key Operation

## 8.1. API Key Overview

Other servers such as TAS and Issuer call the Agent API of the Ledger Service server (base path: `/lss/api/v1`) to register and look up DID Documents, VC Meta, VC Schemas, and so on. When API key validation is enabled, the calling server must include an API key issued in the Admin Console in the `X-API-Key` header. For the request and response formats of the Agent API, refer to the [Ledger Service API](../api/LedgerService_API.md) document.

Each API key is assigned one of the following roles, and a higher role includes all permissions of the lower roles.

| Role     | Callable APIs |
| -------- | ------------- |
| `TAS`    | All Agent APIs (including DID Document registration and status change) |
| `ISSUER` | Registration and change of VC Meta, VC Schema, and ZKP Credential Schema/Definition, and all lookup APIs |
| `READ`   | Lookup APIs |

The roles required for each Agent API are as follows.

| API | Method | Path | Allowed Roles |
| --- | ------ | ---- | ------------- |
| DID Document registration | POST | `/lss/api/v1/did-doc` | `TAS` |
| DID Document lookup | GET | `/lss/api/v1/did-doc` | `TAS`, `ISSUER`, `READ` |
| DID Document status change | PUT | `/lss/api/v1/did-doc` | `TAS` |
| VC Meta registration | POST | `/lss/api/v1/vc-meta` | `TAS`, `ISSUER` |
| VC Meta lookup | GET | `/lss/api/v1/vc-meta` | `TAS`, `ISSUER`, `READ` |
| VC status change | PUT | `/lss/api/v1/vc-meta` | `TAS`, `ISSUER` |
| VC Schema registration | POST | `/lss/api/v1/vc-schema` | `TAS`, `ISSUER` |
| VC Schema lookup | GET | `/lss/api/v1/vc-schema` | `TAS`, `ISSUER`, `READ` |
| ZKP Credential Schema registration | POST | `/lss/api/v1/credential-schema` | `TAS`, `ISSUER` |
| ZKP Credential Schema lookup | GET | `/lss/api/v1/credential-schema` | `TAS`, `ISSUER`, `READ` |
| ZKP Credential Definition registration | POST | `/lss/api/v1/credential-definition` | `TAS`, `ISSUER` |
| ZKP Credential Definition lookup | GET | `/lss/api/v1/credential-definition` | `TAS`, `ISSUER`, `READ` |

When API key validation is enabled and validation fails, the following errors are returned.

| Error Code | HTTP Status | Cause |
| ---------- | ----------- | ----- |
| `SSRVLSS14502` | 401 | No API key in the request header |
| `SSRVLSS14503` | 401 | API key not registered or deactivated |
| `SSRVLSS14506` | 401 | The expiry date (`expires_at`) of the API key has passed |
| `SSRVLSS14504` | 403 | The role of the API key does not have permission to call the API |

<br/>

## 8.2. Issuing an API Key

1. In the Admin Console, go to the `API Key Management` menu and select API key registration.
2. Enter the following items.
   - Name (required): Name to identify the API key (for example, the calling server name)
   - Description (optional): Purpose of the API key
   - Role (required): One of `TAS`, `ISSUER`, `READ` (see [8.1. API Key Overview](#81-api-key-overview))
   - Expiration Period (required): `1 month`, `3 months`, `6 months`, `1 year`, `2 years`, `No Limit`, or `Custom` (entered in days)
3. When registration is complete, an API key starting with `lss_` is displayed on the screen. Copy this value and deliver it securely to the calling server.

> **Caution**
> - The plain-text API key is displayed only once at issuance. The server stores only the SHA-256 hash value and a masked value for display, so you cannot view the API key again after closing the screen. If an API key is lost, issue a new API key.

<br/>

## 8.3. Deactivating, Activating, and Renewing an API Key

On the `API Key Management` screen, you can select an issued API key and perform the following operations.

| Operation | Description | Admin API |
| --------- | ----------- | --------- |
| List | Look up the list of issued API keys | `GET /lss/admin/v1/api-keys/list` |
| Details | Look up API key details (masked value, role, expiry date, last used time, etc.) | `GET /lss/admin/v1/api-keys?id={id}` |
| Issue | Issue a new API key | `POST /lss/admin/v1/api-keys` |
| Deactivate | Deactivates the API key. When API key validation is enabled, subsequent requests are rejected with the `SSRVLSS14503` error. | `PUT /lss/admin/v1/api-keys/{id}/deactivate` |
| Activate | Reactivates a deactivated API key. | `PUT /lss/admin/v1/api-keys/{id}/activate` |
| Renew | Extends the expiry date of an active API key by the specified period. If the expiry date has already passed, it is extended from the current time, and the API key value does not change. | `PUT /lss/admin/v1/api-keys/{id}/renew` |

> **Caution**
> - API key validation checks registration, active status, expiry date (`expires_at`), and role. An API key whose expiry date has passed is rejected with the `SSRVLSS14506` error, so renew it before the expiry date. Deactivate API keys that are no longer used.

<br/>

## 8.4. Enabling API Key Validation

Whether API key validation is used is determined by the `API_KEY_VALIDATION_ENABLED` value in the `server_config` table. The initial value is `false`, and in this state all Agent APIs can be called without an API key.

1. In the Admin Console, go to the `Server Configuration` menu.
2. Turn on the `API Key Validation` switch and select `Update`.
3. Because the setting value is read from the database for each request, it takes effect immediately for subsequent requests without restarting the server.

> **Recommended Order**
> 1. In the Admin Console, issue an API key for TAS (`TAS` role) and an API key for Issuer (`ISSUER` role). ([8.2. Issuing an API Key](#82-issuing-an-api-key))
> 2. Configure the issued API key on each calling server. ([8.5. Calling Server (TAS, Issuer) Configuration](#85-calling-server-tas-issuer-configuration))
> 3. Enable API key validation on the Ledger Service server.
>
> If you enable validation before configuring the API key on a calling server, that server's Agent API calls fail.

<br/>

## 8.5. Calling Server (TAS, Issuer) Configuration

The TAS and Issuer servers send the value of the `API_KEY_LSS` item (initial value: `enter_lss_api_key_here`) in their own `server_config` table as the `X-API-Key` header of requests to the Ledger Service server. On the `Server Configuration` screen of each server's Admin Console, change the `API_KEY_LSS` value to the API key issued in [8.2. Issuing an API Key](#82-issuing-an-api-key).

| Calling Server | API Key Role to Issue | Configuration Location |
| -------------- | --------------------- | ---------------------- |
| TAS            | `TAS`                 | TAS `server_config.API_KEY_LSS` |
| Issuer         | `ISSUER`              | Issuer `server_config.API_KEY_LSS` |

> **Note**
> - For each calling server's LSS integration settings, such as the Ledger Service server address, refer to that server's installation guide.

[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0_ko.md
