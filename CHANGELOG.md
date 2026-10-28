# Changelog

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