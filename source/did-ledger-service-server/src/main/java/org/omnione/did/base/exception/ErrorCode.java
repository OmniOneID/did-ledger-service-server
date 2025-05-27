package org.omnione.did.base.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    TODO("9999", "TODO.", 500),
    VERIFY_SIGN_FAIL("1000", "Verify Signature Fail", 400),
    CLIENT_ERROR("2001", "Client Error", 400),
    SERVER_ERROR("2002", "Server Error", 500),
    REQUEST_BODY_UNREADABLE("", "", 500),
    DID_DOC_VERSION_MISMATCH("3001", "DidDoc version mismatch", 400),
    REVOKED_VC_CANNOT_UPDATE("0001", "A revoked VC cannot be updated.", 400)
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
