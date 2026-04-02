package org.omnione.did.base.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 1. General errors (10000 ~ 10999)
    CLIENT_ERROR("SSRVLSS10000", "Client Error", 400),
    SERVER_ERROR("SSRVLSS10001", "Server Error", 500),
    ENCODING_FAILED("SSRVLSS10002", "Failed to encoding data.", 500),
    DECODING_FAILED("SSRVLSS10003", "Failed to decoding data.", 400),
    ENCRYPTION_FAILED("SSRVLSS10004", "Failed to encrypt data.", 500),
    DECRYPTION_FAILED("SSRVLSS100015", "Failed to decrypt data.", 400),


    // 2. Error during API processing (11000 ~ 11499)
    VERIFY_SIGN_FAIL("SSRVLSS11000", "Verify Signature Fail", 400),
    REQUEST_BODY_UNREADABLE("SSRVLSS11001", "", 500),


    // 3. DID-related errors (11500 ~ 11999)
    DID_DOC_VERSION_MISMATCH("SSRVLSS11500", "DidDoc version mismatch", 400),
    DID_NOT_FOUND("SSRVLSS11501", "DID not found", 400),
    ROLE_TYPE_MISMATCH("SSRVLSS11502", "Role type mismatch", 400),
    DID_NOT_ACTIVATED("SSRVLSS11503", "DID not activated", 400),
    DID_DOC_NOT_FOUND("SSRVLSS11504", "Failed to find DID Document: DID Document not found", 400),
    ROLE_DID_NOT_FOUND("SSRVLSS11505", "Failed to find DID: DID not found by Role", 400),
    TA_DID_DOC_NOT_FOUND("SSRVLSS11506", "Failed to find DID Document: TA DID Document not found", 400),


    // 4. VC-related errors (12000 ~ 12499)
    REVOKED_VC_CANNOT_UPDATE("SSRVLSS12000", "A revoked VC cannot be updated.", 400),
    INVALID_VC_SCHEMA("SSRVLSS12001", "Invalid VC Schema.", 400),
    VC_SCHEMA_ALREADY_REGISTERED("SSRVLSS12002", "Failed to register VC Schema: VC Schema already exists.", 400),
    VC_SCHEMA_NOT_FOUND("SSRVLSS12003", "VC Schema not found.", 500),
    VC_STATUS_HISTORY_NOT_FOUND("SSRVLSS12004", "VC Status History not found.", 400),


    // 5. ZKP-related errors (12500 ~ 12999)
    INVALID_CREDENTIAL_SCHEMA_ID("SSRVLSS12500", "Invalid Credential Schema ID.", 400),
    CREDENTIAL_SCHEMA_ALREADY_REGISTERED("SSRVLSS12501", "Failed to register Credential Schema: Credential Schema already exists.", 400),
    CREDENTIAL_SCHEMA_NOT_FOUND("SSRVLSS12502", "Credential Schema not found.", 500),
    CREDENTIAL_DEFINITION_ALREADY_EXISTS("SSRVLSS12503", "Failed to register Credential Definition: Credential Definition already exists.", 400),
    CREDENTIAL_DEFINITION_NOT_FOUND("SSRVLSS12504", "Credential Definition not found.", 400),
    INVALID_CREDENTIAL_SCHEMA("SSRVLSS12505", "Invalid Credential Schema.", 400),
    INVALID_CREDENTIAL_DEFINITION("SSRVLSS12506", "Invalid Credential Definition.", 400),


    // 6. Policy errors (13000 ~ 13499)
    TERMINATED_STATUS_CAN_NOT_CHANGE("SSRVLSS13000", "Terminated DIDs can't change their status.", 400),
    REVOKED_STATUS_CAN_NOT_CHANGE("SSRVLSS13001", "Revoked DIDs can't change their status to (De)Activate.", 400),
    DID_ROLE_MISMATCH_TA("SSRVLSS13002", "The DID's Role is not TA.", 400),


    // 7. DB-related errors (13500 ~ 13999)
    DB_CONNECTION_ERROR("SSRVLSS13500", "Database connection error.", 500),
    DB_QUERY_ERROR("SSRVLSS13501", "Database query error.", 500),
    DB_INSERT_ERROR("SSRVLSS13502", "Database insert error.", 500),
    DB_UPDATE_ERROR("SSRVLSS13503", "Database update error.", 500),
    DB_DELETE_ERROR("SSRVLSS13504", "Database delete error.", 500),


    // 8. Admin-related errors (14000 ~ 14499)
    ADMIN_INFO_NOT_FOUND("SSRVLSS14000", "Admin not found.", 400),
    ADMIN_ALREADY_EXISTS("SSRVLSS14001", "Failed to register admin: admin is already registered.", 400),
    ADMIN_PASSWORD_POLICY_NOT_FOUND("SSRVLSS14002", "Failed to find admin password policy: policy is not registered.", 500),
    DAILY_OTP_SEND_LIMIT_EXCEEDED("SSRVLSS14003", "Daily OTP limit exceeded.", 400),
    OTP_COOLDOWN_NOT_EXPIRED("SSRVLSS14004", "Please wait before requesting another OTP.", 400),
    INVALID_OTP("SSRVLSS14005", "Invalid OTP code.", 400),
    OTP_EXPIRED("SSRVLSS14006", "OTP has expired. Please request a new one.", 400),
    OTP_VERIFY_LIMIT_EXCEEDED("SSRVLSS14007", "Too many verification attempts.", 400),
    ADMIN_NOT_FOUND_FOR_PASSWORD_RESET("SSRVLSS14008", "Admin not found.", 400),
    EMAIL_SEND_FAILED("SSRVLSS14009", "Failed to send email.", 500),


    // 9. ApiKey-related errors (14500 ~ 14999)
    API_KEY_NOT_FOUND("SSRVLSS14500", "API Key not found.", 400),
    API_KEY_GENERATION_FAILED("SSRVLSS14501", "Failed to generate unique API key after maximum attempts.", 500),
    API_KEY_MISSING("SSRVLSS14502", "API Key is missing in request header.", 401),
    API_KEY_INVALID("SSRVLSS14503", "Invalid API key.", 401),
    API_KEY_INSUFFICIENT_PERMISSION("SSRVLSS14504", "Insufficient permissions for this API.", 403),
    API_KEY_VALIDATION_ERROR("SSRVLSS14505", "API key validation failed.", 500),


    // 10. ServerConfig-related errors (15000 ~ 15499)
    SERVER_CONFIG_NOT_FOUND("SSRVLSS15000", "Server configuration not found.", 400),
    SERVER_CONFIG_KEY_NOT_FOUND("SSRVLSS15001", "Server configuration key not found.", 400),
    SERVER_CONFIG_UPDATE_FAILED("SSRVLSS15002", "Failed to update server configuration.", 500),


    // 99. Miscellaneous errors (90000 ~ 99999)
    TODO("SSRVLSS99999", "TODO.", 500),
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
        this.code = code;
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
