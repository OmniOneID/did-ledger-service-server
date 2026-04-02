/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.repository.v1.admin.service;

import org.omnione.did.base.db.constant.DeliveryMethod;
import org.omnione.did.base.db.constant.PasswordResetReason;
import org.omnione.did.base.db.constant.TokenType;
import org.omnione.did.base.db.domain.Admin;
import org.omnione.did.base.db.domain.AdminOtpSendLimit;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.domain.AdminPasswordResetToken;
import org.omnione.did.base.db.repository.AdminRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.OtpGenerator;
import org.omnione.did.repository.v1.admin.api.NotiFeign;
import org.omnione.did.repository.v1.admin.api.dto.EmailTemplate;
import org.omnione.did.repository.v1.admin.api.dto.RequestSendEmailReqDto;
import org.omnione.did.repository.v1.admin.api.enums.EmailTemplateType;
import org.omnione.did.repository.v1.admin.dto.admin.*;
import org.omnione.did.repository.v1.admin.service.query.AdminOtpSendLimitQueryService;
import org.omnione.did.repository.v1.admin.service.query.AdminPasswordPolicyQueryService;
import org.omnione.did.repository.v1.admin.service.query.AdminPasswordResetTokenQueryService;
import org.omnione.did.repository.v1.admin.service.query.AdminQueryService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionService {
    private static final int MAX_DAILY_SEND_COUNT = 5;      // Maximum daily send count
    private static final int MAX_VERIFY_ATTEMPTS = 5;        // Maximum OTP verification attempts
    private static final int OTP_EXPIRY_MINUTES = 5;         // OTP expiration time (minutes)
    private static final int COOLDOWN_MINUTES = 1;           // Cooldown time (minutes)

    private final AdminQueryService adminQueryService;
    private final AdminPasswordPolicyQueryService adminPasswordPolicyQueryService;
    private final AdminRepository adminRepository;
    private final AdminPasswordResetTokenQueryService adminPasswordResetTokenQueryService;
    private final AdminOtpSendLimitQueryService adminOtpSendLimitQueryService;

    private final NotiFeign notiFeign;
    private final OtpAttemptService otpAttemptService;

    private final PasswordEncoder delegatingPasswordEncoder;

    public AdminDto requestAdminLogin(RequestAdminLoginReqDto requestAdminLoginReqDto) {
        log.debug("=== Starting requestAdminLogin ===");

        log.debug("\t--> Finding admin by loginId");
        Admin admin = adminQueryService.findByLoginId(
                requestAdminLoginReqDto.getLoginId()
        );

        String clientPassword = requestAdminLoginReqDto.getLoginPassword();
        String stored = admin.getLoginPassword();
        String algo = admin.getPasswordAlgo();

        log.debug("\t--> Password Matches");
        boolean matches = false;
        if ("sha256".equalsIgnoreCase(algo) || looksLikeSha256Hex(stored)) {
            matches = stored.equalsIgnoreCase(clientPassword);
        } else {
            matches = delegatingPasswordEncoder.matches(clientPassword, stored);
        }

        if (!matches) {
            throw new OpenDidException(ErrorCode.ADMIN_INFO_NOT_FOUND);
        }

        if (needsRehash(algo)) {
            String newHash = delegatingPasswordEncoder.encode(clientPassword);
            admin.setLoginPassword(newHash);
            admin.setPasswordAlgo("bcrypt");
            adminRepository.save(admin);
        }

        log.debug("\t--> Checking password expiration and updating reason if needed");
        boolean isPasswordExpired = checkAndUpdatePasswordExpiration(admin);

        log.debug("=== Finished requestAdminLogin ===");
        return AdminDto.fromAdmin(admin, isPasswordExpired);
    }

    private boolean needsRehash(String algo) {
        if (algo == null) return true;
        return !"bcrypt".equalsIgnoreCase(algo);
    }

    private boolean looksLikeSha256Hex(String s) {
        return s != null && s.matches("(?i)^[0-9a-f]{64}$");
    }

    /**
     * Checks if admin's password has expired and updates the password reset reason
     *
     * @param admin Admin entity to check
     * @return true if password is expired, false otherwise
     */
    private boolean checkAndUpdatePasswordExpiration(Admin admin) {
        // Skip expiration check if password reset is already required for other reasons
        if (Boolean.TRUE.equals(admin.getRequirePasswordReset()) &&
                admin.getPasswordResetReason() != null &&
                admin.getPasswordResetReason() != PasswordResetReason.EXPIRED) {
            log.debug("Password reset already required for admin {} with reason: {}",
                    admin.getLoginId(), admin.getPasswordResetReason());
            return false;
        }

        boolean isExpired = isPasswordExpired(admin);

        if (isExpired) {
            log.info("Password expired for admin: {}", admin.getLoginId());
            admin.setPasswordResetReason(PasswordResetReason.EXPIRED);
            admin.setRequirePasswordReset(true);
            adminRepository.save(admin);
        }

        return isExpired;
    }

    /**
     * Checks if the admin's password has expired based on password policy
     *
     * @param admin Admin entity to check
     * @return true if password is expired, false otherwise
     */
    private boolean isPasswordExpired(Admin admin) {
        if (admin.getLastPasswordChangedAt() == null) {
            log.warn("Admin {} has no last password change date, considering as not expired",
                    admin.getLoginId());
            return false;
        }

        AdminPasswordPolicy policy = adminPasswordPolicyQueryService.findAdminPasswordPolicyOrNull();
        if (policy == null) {
            log.warn("No password policy found, skipping expiration check");
            return false;
        }

        if (policy.getPasswordExpiryDays() <= 0) {
            log.debug("Password expiry is disabled (days: {})", policy.getPasswordExpiryDays());
            return false;
        }

        Instant expirationDate = admin.getLastPasswordChangedAt()
                .plus(policy.getPasswordExpiryDays(), ChronoUnit.DAYS);

        boolean expired = Instant.now().isAfter(expirationDate);

        log.debug("Password expiration check for admin {}: lastChanged={}, expiryDays={}, expired={}",
                admin.getLoginId(),
                admin.getLastPasswordChangedAt(),
                policy.getPasswordExpiryDays(),
                expired);

        return expired;
    }

    /**
     * Requests password reset OTP for admin.
     *
     * @param request OTP request containing login ID
     * @return Empty response DTO
     * @throws OpenDidException if admin not found, send limit exceeded, or cooldown not expired
     */
    public EmptyResDto requestPasswordResetOtp(RequestPasswordResetOtpReqDto request) {
        log.debug("=== Starting requestPasswordResetOtp ===");
        log.info("Requesting password reset OTP for loginId: {}", request.getLoginId());

        log.debug("\t--> Checking for admin existence");
        // Step 1: Check for admin existence
        Admin admin = adminQueryService.findByLoginIdOrNull(request.getLoginId());
        if (admin == null) {
            log.warn("Admin not found for password reset: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.ADMIN_NOT_FOUND_FOR_PASSWORD_RESET);
        }

        log.debug("\t--> Checking send limit and cooldown");
        // Step 2: Check send limit (automatically resets daily with new date)
        AdminOtpSendLimit sendLimit = adminOtpSendLimitQueryService
                .getOrCreateTodayLimit(request.getLoginId());

        // Check block status first
        if (Boolean.TRUE.equals(sendLimit.getBlocked())) {
            log.warn("OTP sending blocked for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.DAILY_OTP_SEND_LIMIT_EXCEEDED);
        }

        // Check daily send count
        if (sendLimit.getDailySendCount() >= MAX_DAILY_SEND_COUNT) {
            log.warn("Daily OTP send limit exceeded for loginId: {}", request.getLoginId());
            // Block the user when daily limit is exceeded using separate service
            otpAttemptService.blockUserForDailyLimitExceeded(request.getLoginId());
            throw new OpenDidException(ErrorCode.DAILY_OTP_SEND_LIMIT_EXCEEDED);
        }

        // Check cooldown time
        if (sendLimit.getCooldownUntil() != null &&
                Instant.now().isBefore(sendLimit.getCooldownUntil())) {
            log.warn("OTP cooldown not expired for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.OTP_COOLDOWN_NOT_EXPIRED);
        }

        log.debug("\t--> Invalidating existing tokens");
        // Step 3: Invalidate existing tokens
        int invalidatedCount = adminPasswordResetTokenQueryService
                .markAllUnusedTokensAsUsed(request.getLoginId(), TokenType.PASSWORD_RESET);
        log.debug("Invalidated {} existing tokens for loginId: {}", invalidatedCount, request.getLoginId());

        log.debug("\t--> Generating and saving new OTP");
        // Step 4: Generate and save a new OTP
        String otpCode = OtpGenerator.generateOtp();
        Instant expiredAt = Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        AdminPasswordResetToken token = AdminPasswordResetToken.builder()
                .loginId(request.getLoginId())
                .token(otpCode)
                .tokenType(TokenType.PASSWORD_RESET)
                .deliveryMethod(DeliveryMethod.EMAIL)
                .adminId(admin.getId())
                .expiredAt(expiredAt)
                .attemptCount((short) 0)
                .used(false)
                .build();

        adminPasswordResetTokenQueryService.save(token);
        log.debug("Created new OTP token for loginId: {}", request.getLoginId());

        log.debug("\t--> Sending OTP email");
        // Step 5: Send email
        sendOtpEmail(admin, otpCode, expiredAt);

        log.debug("\t--> Updating send history");
        // Step 6: Update send history
        sendLimit.setDailySendCount((short)(sendLimit.getDailySendCount() + 1));
        sendLimit.setLastSentAt(Instant.now());
        sendLimit.setCooldownUntil(Instant.now().plus(COOLDOWN_MINUTES, ChronoUnit.MINUTES));
        adminOtpSendLimitQueryService.save(sendLimit);

        log.info("Successfully sent password reset OTP for loginId: {}", request.getLoginId());
        log.debug("=== Finished requestPasswordResetOtp ===");
        return new EmptyResDto();
    }

    /**
     * Verifies OTP and resets admin password.
     *
     * @param request OTP verification request containing login ID, OTP, and hashed password
     * @return Empty response DTO
     * @throws OpenDidException if verification fails
     */
    public EmptyResDto verifyOtpAndResetPassword(VerifyOtpAndResetPasswordReqDto request) {
        log.debug("=== Starting verifyOtpAndResetPassword ===");
        log.info("Verifying OTP and resetting password for loginId: {}", request.getLoginId());

        log.debug("\t--> Validating input data");
        // Step 1: Validate input data
        if (!OtpGenerator.isValidOtpFormat(request.getOtp())) {
            log.warn("Invalid OTP format for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.INVALID_OTP);
        }

        Admin admin = adminQueryService.findByLoginIdOrNull(request.getLoginId());
        if (admin == null) {
            log.warn("Admin not found for password reset: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.ADMIN_NOT_FOUND_FOR_PASSWORD_RESET);
        }

        log.debug("\t--> Checking for existing tokens");
        // Step 2: Check if there's any token
        Optional<AdminPasswordResetToken> anyTokenOpt = adminPasswordResetTokenQueryService
                .findLatestTokenByLoginIdAndType(request.getLoginId(), TokenType.PASSWORD_RESET);

        if (anyTokenOpt.isEmpty()) {
            log.warn("No token found for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.INVALID_OTP);
        }

        AdminPasswordResetToken anyToken = anyTokenOpt.get();

        log.debug("\t--> Checking token invalidation status");
        // Step 3: Check if token is already invalidated due to max attempts
        if (anyToken.getUsed() && anyToken.getAttemptCount() >= MAX_VERIFY_ATTEMPTS) {
            log.warn("OTP verify limit already exceeded for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.OTP_VERIFY_LIMIT_EXCEEDED);
        }

        log.debug("\t--> Finding valid token");
        // Step 4: Check if there's a valid (unused) token
        Optional<AdminPasswordResetToken> validTokenOpt = adminPasswordResetTokenQueryService
                .findLatestValidTokenByLoginIdAndType(request.getLoginId(), TokenType.PASSWORD_RESET);

        if (validTokenOpt.isEmpty()) {
            log.warn("No valid token found for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.INVALID_OTP);
        }

        AdminPasswordResetToken token = validTokenOpt.get();

        log.debug("\t--> Checking retry count");
        // Step 5: Check retry count before OTP verification
        if (token.getAttemptCount() >= MAX_VERIFY_ATTEMPTS) {
            log.warn("OTP verify limit exceeded for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.OTP_VERIFY_LIMIT_EXCEEDED);
        }

        log.debug("\t--> Verifying OTP");
        // Step 6: Check if the OTP matches
        if (!token.getToken().equals(request.getOtp())) {
            // OTP doesn't match - increment attempt count using separate service
            otpAttemptService.handleInvalidOtpAttempt(request.getLoginId(), request.getOtp());
            log.warn("OTP mismatch for loginId: {}", request.getLoginId());
            throw new OpenDidException(ErrorCode.INVALID_OTP);
        }

        log.debug("\t--> Resetting password");
        // Step 7: Change password (using pre-hashed password)
        String newHash = delegatingPasswordEncoder.encode(request.getHashedPassword());
        admin.setLoginPassword(newHash);
        admin.setPasswordAlgo("bcrypt");
        admin.setLoginPassword(newHash);
        admin.setLastPasswordChangedAt(Instant.now());
        admin.setRequirePasswordReset(false);
        admin.setPasswordResetReason(null);
        adminRepository.save(admin);

        log.debug("\t--> Marking token as used and invalidating other tokens");
        // Step 8: Mark token as used
        token.setUsed(true);
        adminPasswordResetTokenQueryService.save(token);

        // Invalidate all other unused tokens for the user
        adminPasswordResetTokenQueryService
                .markAllUnusedTokensAsUsed(request.getLoginId(), TokenType.PASSWORD_RESET);

        log.info("Successfully reset password for loginId: {}", request.getLoginId());
        log.debug("=== Finished verifyOtpAndResetPassword ===");
        return new EmptyResDto();
    }

    /**
     * Gets OTP send limit status for the specified admin.
     *
     * @param loginId the login ID to check
     * @return OTP send limit status DTO
     * @throws OpenDidException if admin not found
     */
    public OtpSendLimitStatusDto getOtpSendLimitStatus(String loginId) {
        log.info("Getting OTP send limit status for loginId: {}", loginId);

        // Check if admin exists
        Admin admin = adminQueryService.findByLoginIdOrNull(loginId);
        if (admin == null) {
            log.warn("Admin not found for status check: {}", loginId);
            throw new OpenDidException(ErrorCode.ADMIN_NOT_FOUND_FOR_PASSWORD_RESET);
        }

        AdminOtpSendLimit sendLimit = adminOtpSendLimitQueryService.getOrCreateTodayLimit(loginId);
        return OtpSendLimitStatusDto.fromAdminOtpSendLimit(sendLimit);
    }

    /**
     * Unblocks OTP sending for the specified admin.
     *
     * @param loginId the login ID to unblock
     * @return Empty response DTO
     * @throws OpenDidException if admin not found
     */
    public EmptyResDto unblockOtpSending(String loginId) {
        log.info("Unblocking OTP sending for loginId: {}", loginId);

        // Check if admin exists
        Admin admin = adminQueryService.findByLoginIdOrNull(loginId);
        if (admin == null) {
            log.warn("Admin not found for unblock: {}", loginId);
            throw new OpenDidException(ErrorCode.ADMIN_NOT_FOUND_FOR_PASSWORD_RESET);
        }

        boolean unblocked = adminOtpSendLimitQueryService.unblockOtpSending(loginId);

        if (unblocked) {
            log.info("Successfully unblocked OTP sending for loginId: {}", loginId);
        } else {
            log.info("OTP sending was not blocked for loginId: {}", loginId);
        }

        return new EmptyResDto();
    }

    /**
     * Resets daily OTP send limit for the specified admin.
     *
     * @param loginId the login ID to reset
     * @return Empty response DTO
     * @throws OpenDidException if admin not found
     */
    public EmptyResDto resetDailyOtpLimit(String loginId) {
        log.info("Resetting daily OTP limit for loginId: {}", loginId);

        // Check if admin exists
        Admin admin = adminQueryService.findByLoginIdOrNull(loginId);
        if (admin == null) {
            log.warn("Admin not found for limit reset: {}", loginId);
            throw new OpenDidException(ErrorCode.ADMIN_NOT_FOUND_FOR_PASSWORD_RESET);
        }

        boolean reset = adminOtpSendLimitQueryService.resetDailySendLimit(loginId);

        if (reset) {
            log.info("Successfully reset daily OTP limit for loginId: {}", loginId);
        } else {
            log.info("No limit record found to reset for loginId: {}", loginId);
        }

        return new EmptyResDto();
    }

    /**
     * Sends OTP email to admin.
     */
    private void sendOtpEmail(Admin admin, String otpCode, Instant expiredAt) {
        try {
            Map<String, String> contentData = Map.of(
                    "otp", otpCode,
                    "otpValidMinutes", String.valueOf(OTP_EXPIRY_MINUTES)
            );

            RequestSendEmailReqDto emailRequest = RequestSendEmailReqDto.builder()
                    .email(EmailTemplate.builder()
                            .templateType(EmailTemplateType.RESET_PASSWORD)
                            .recipientAddress(admin.getLoginId())
                            .title("[TEC] Password Reset Verification Code")
                            .contentData(contentData)
                            .build())
                    .build();

            notiFeign.requestSendEmail(emailRequest);
            log.debug("OTP email sent successfully for loginId: {}", admin.getLoginId());

        } catch (OpenDidException e) {
            log.error("Failed to send OTP email for loginId: {}", admin.getLoginId(), e);
            throw new OpenDidException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public AdminDto requestAdminLoginById(Long adminId) {
        log.debug("=== Starting requestAdminLoginById ===");

        log.debug("\t--> Finding admin by id: {}", adminId);
        Admin admin = adminQueryService.findById(adminId);

        AdminDto result = AdminDto.fromAdmin(admin);

        log.debug("=== Finished requestAdminLoginById ===");
        return result;
    }
}
