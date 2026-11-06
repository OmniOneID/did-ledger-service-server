# Changelog

## [Unreleased] (v3.0.0)

### 🚀 New Features
    - Ledger Service Server
        - API key authentication of Agent API calls (`X-API-Key`) with role-based access (`TAS`, `ISSUER`, `READ`)
        - DID Document and VC status change history
        - API logs, audit logs, and server configuration
    - LSS Admin Console
        - DID Document, VC Metadata, VC Schema, ZKP Credential Schema, and ZKP Credential Definition lookup
        - API key issuance, activation, deactivation, and renewal
        - Admin accounts, password policy, API logs, audit logs, and server configuration

### 🔧 Changes
- Added the Admin Console operation guide and the table definition document
    - Server version 3.0.0 (`did-ledger-service-server-3.0.0.jar`)
    - Uses `did-common-sdk-server` 3.0.0 instead of local utilities
    - Versioned DID lookup reads `versionId` from the DID key URL

### 🐛 Bug Fixes
    - Expired API keys are rejected
    - Changing the admin password policy requires authentication
    - Session tokens are cleared on logout and login failure
    - Added the missing Admin Console `index.html`

## v2.0.1 (2026-04-14)

### 🐛 Bug Fixes
    - Ledger Service Server
        - Fixed an issue where the existing `did` and `role` values were not preserved during DID Document update
        - Resolved a not-null constraint violation on the `did` column during DID Document version update
        - Fixed a 500 error in the DID Document update flow (`propose-update-diddoc` → `request-update-diddoc`)

## v2.0.0 (2025-06-12)

### 🚀 New Features
    - Ledger Service Server
        - DID Document creation, registration, and status changes
        - VC Meta creation and VC status changes
        - VC Schema registration and management
        - ZKP Credential Schema and ZKP Credential Definition registration and management