package org.omnione.did.base.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 1. General errors (10000 ~ 10999)
    CLIENT_ERROR("SSRVLSS10000", "Client Error", 400),
    SERVER_ERROR("SSRVLSS10001", "Server Error", 500),


    // 2. Error during API processing (11000 ~ 11499)
    VERIFY_SIGN_FAIL("SSRVLSS11000", "Verify Signature Fail", 400),
    REQUEST_BODY_UNREADABLE("SSRVLSS11001", "", 500),


    // 3. DID-related errors (11500 ~ 11999)
    DID_DOC_VERSION_MISMATCH("SSRVLSS11500", "DidDoc version mismatch", 400),
    DID_NOT_FOUND("SSRVLSS11501", "DID not found", 400),
    ROLE_TYPE_MISMATCH("SSRVLSS11502", "Role type mismatch", 400),
    DID_NOT_ACTIVATED("SSRVLSS11503", "DID not activated", 400),


    // 4. VC-related errors (12000 ~ 12499)
    REVOKED_VC_CANNOT_UPDATE("SSRVLSS12000", "A revoked VC cannot be updated.", 400),


    // 5. ZKP-related errors (12500 ~ 12999)
    INVALID_CREDENTIAL_SCHEMA_ID("SSRVLSS12500", "Invalid Credential Schema ID.", 400),
    CREDENTIAL_SCHEMA_ALREADY_REGISTERED("SSRVLSS12501", "Failed to register Credential Schema: Credential Schemma already exists.", 400),
    CREDENTIAL_SCHEMA_NOT_FOUND("SSRVLSS12502", "Credential Schema not found.", 500),


    // 99. Miscellaneous errors (90000 ~ 99999)
    TODO("SSRVLSS99999", "TODO.", 500)
    ;

    private final String code;
    private final String message;
    private final int httpStatus;

    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static String getMessageByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return "Unknown error code: " + code;
    }
}
