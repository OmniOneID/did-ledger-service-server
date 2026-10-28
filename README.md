# Ledger Service Server Source Code

Welcome to the Ledger Service Server source code repository. This repository contains the source code, documentation, and build configurations for the Ledger Service Server (LSS) and the LSS Admin Console.

## S/W Specifications
| Category           | Details                                                        |
|--------------------|----------------------------------------------------------------|
| OS                 | macOS / Linux / Windows 10 or higher                           |
| Language           | Java 21 or higher                                              |
| IDE                | IntelliJ IDEA                                                  |
| Build System       | Gradle 8.7 (Gradle Wrapper included)                           |
| Compatibility      | Requires JDK 21 or higher                                      |
| Database           | PostgreSQL                                                     |
| Frontend           | Node.js 22.9.0 (downloaded automatically by the Gradle build)  |
| Other Requirements | Minimum 2GB RAM and 10GB of disk space required                |

## Key Features
- DID Document registration, lookup, and status changes
- VC Meta registration, lookup, and VC status changes
- VC Schema, ZKP Credential Schema, and ZKP Credential Definition registration and lookup
- API key authentication for Agent API (`/lss/api/v1`) calls using the `X-API-Key` header (roles: `TAS`, `ISSUER`, `READ`)
- LSS Admin Console, served by the server itself (`http://{host}:8098/`): DID, VC Metadata, and VC Schema lookup, API key management, admin management, server configuration, and API and audit log lookup

## Folder Structure
Here's an overview of the directory structure.

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

Below is a description of each folder and file in the directory:

| Name                         | Description                                          |
| ---------------------------- |------------------------------------------------------|
| CHANGELOG.md                 | Version-specific changes in the project              |
| CODE_OF_CONDUCT.md           | Code of conduct for contributors                     |
| CONTRIBUTING.md              | Contribution guidelines and procedures               |
| LICENSE                      | License                                              |
| dependencies-license.md      | Licenses for the project’s dependency libraries      |
| MAINTAINERS.md               | Guidelines for project maintainers                   |
| RELEASE-PROCESS.md           | Procedures for releasing new versions                |
| SECURITY.md                  | Security policies and vulnerability reporting        |
| docs                         | Documentation                                        |
| ┖ api                        | API guide documentation                              |
| ┖ errorCode                  | Error codes and troubleshooting guides               |
| ┖ installation               | Installation, setup, and operation instructions      |
| source                       | Source code for the server and admin console         |
| ┖ did-ledger-service-admin   | LSS Admin Console frontend (React, TypeScript) source code |
| ┖ did-ledger-service-server  | Ledger Service Server (Spring Boot) source code      |


## Installation And Operation Guide

For detailed instructions on installing and operating the Ledger Service Server, including the Admin Console and API key operation, please refer to the guide below:
- [OpenDID Ledger Service Server Installation And Operation Guide](docs/installation/OpenDID_LedgerService_InstallationAndOperation_Guide.md)

## API Reference

API documentation is available in two main types:

- **Ledger API**: Detailed reference for the Ledger Server's API endpoints and usage.
   - [Ledger API Reference](docs/api/LedgerService_API.md)
- **Error Code**: Reference for the error codes returned by the Ledger Server.
   - [Ledger Error Code Reference](docs/errorCode/LedgerService_ErrorCode.md)

## Change Log

The Change Log provides a detailed record of version-specific changes and updates. You can find it here:
- [Change Log](./CHANGELOG.md)

## OpenDID Demonstration Videos <br>
To watch our demonstration videos of the OpenDID system in action, please visit our [Demo Repository](https://github.com/OmniOneID/did-demo-server). <br>

These videos showcase key features including user registration, VC issuance, and VP submission processes.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License
[Apache 2.0](LICENSE)
