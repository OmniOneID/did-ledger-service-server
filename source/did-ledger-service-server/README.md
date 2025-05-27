# Ledger Service Server Source Code

Welcome to the Ledger Service Server source code repository. This directory contains the core source code and build configurations for the Ledger Service Server.

## Directory Structure

Here's an overview of the directory structure.

```
did-ledger-service-server
├── gradle
├── libs
    └── did-sdk-common-2.0.0.jar
    └── did-blockchain-sdk-server-2.0.0.jar
    └── did-core-sdk-server-2.0.0.jar
    └── did-crypto-sdk-server-2.0.0.jar
    └── did-datamodel-sdk-server-2.0.0.jar
    └── did-wallet-sdk-server-2.0.0.jar
    └── did-zkp-sdk-server-1.0.0.jar
└── src
└── build.gradle
└── README.md
```

<br/>

Below is a description of each folder and file in the directory:

| Name                      | Description                                  |
|---------------------------|----------------------------------------------|
| did-ledger-service-server | LS Server source code and build files        |
| ┖ gradle                  | Gradle build configurations and scripts      |
| ┖ libs                    | External libraries and dependencies          |
| ┖ src                     | Main source code directory                   |
| ┖ build.gradle            | Gradle build configuration file              |
| ┖ README.md               | Overview and instructions for the source code |


## Libraries

Libraries used in this project are organized into two main categories:

1. **Open DID Libraries**: These libraries are developed by the Open DID project and are available in the [libs folder](libs). They include:

    - `did-sdk-common-2.0.0.jar`
    - `did-blockchain-sdk-server-2.0.0.jar`
    - `did-core-sdk-server-2.0.0.jar`
    - `did-crypto-sdk-server-2.0.0.jar`
    - `did-datamodel-sdk-server-2.0.0.jar`
    - `did-wallet-sdk-server-2.0.0.jar`
    - `did-zkp-sdk-server-1.0.0.jar`

2. **Third-Party Libraries**: These libraries are open-source dependencies managed via the [build.gradle](build.gradle) file. For a detailed list of third-party libraries and their licenses, please refer to the [dependencies-license.md](../../dependencies-license.md) file.


## Documenttation

Refer to the following documents for more detailed information:



## Contributing

Please read [CONTRIBUTING.md](../../CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](../../CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License
[Apache 2.0](../../LICENSE)

## Contact
For questions or support, please contact [maintainers](../../MAINTAINERS.md).