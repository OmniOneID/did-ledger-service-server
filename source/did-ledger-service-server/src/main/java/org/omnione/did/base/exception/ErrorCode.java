package org.omnione.did.base.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 01xxx: DID or DID Document Error
    DID_NOT_FOUND("01000", "Failed to find DID: DID not found", 400),
    DID_DOC_NOT_FOUND("00100", "Failed to find DID Document: DID Document not found", 400),
    ROLE_DID_NOT_FOUND("01000", "Failed to find DID: DID not found by Role", 400),
    TA_DID_DOC_NOT_FOUND("01000", "Failed to find DID Document: TA DID Document not found", 400),

    DID_DOC_VERSION_MISMATCH("01001", "DID Document version mismatch", 400),

    // 02xxx: Crypto Error
    VERIFY_SIGN_FAIL("02000", "Verify Signature Fail.", 400),

    // 03xxx: Policy Error,
    TERMINATED_STATUS_CAN_NOT_CHANGE("03000", "Terminated DIDs can't change their status.", 400),
    REVOKED_STATUS_CAN_NOT_CHANGE("03001", "Revoked DIDs can't change their status to (De)Activate.", 400),
    REVOKED_VC_CANNOT_UPDATE("03002", "A revoked VC cannot be updated.", 400),
    DID_ROLE_MISMATCH_TA("03003", "The DID's Role is not TA.", 400),

    REQUEST_BODY_UNREADABLE("90003", "Request Data unreadable", 400),

    CLIENT_ERROR("90001", "Client Error", 400),
    SERVER_ERROR("90002", "Server Error", 500),
    TODO("99999", "TODO.", 500),
    ;


    private final String code;
    private final String message;
    private final int httpStatus;

    /**
     * Constructor for ErrorCode enum.
     *
     * @param code       Error Code
     * @param message    Error Message
     * @param httpStatus HTTP Status Code
     */
    ErrorCode(String code, String message, int httpStatus) {
        this.code = "S" + "SRV" + "LSS" + code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * Get the error code.
     *
     * @return Error Code
     */
    public static String getMessageByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return "Unknown error code: " + code;
    }
}
