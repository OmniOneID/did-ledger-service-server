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

import org.omnione.did.base.db.constant.TokenType;
import org.omnione.did.base.db.domain.AdminOtpSendLimit;
import org.omnione.did.base.db.domain.AdminPasswordResetToken;
import org.omnione.did.repository.v1.admin.service.query.AdminOtpSendLimitQueryService;
import org.omnione.did.repository.v1.admin.service.query.AdminPasswordResetTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpAttemptService {

    private static final int MAX_VERIFY_ATTEMPTS = 5;        // Maximum OTP verification attempts

    private final AdminPasswordResetTokenQueryService adminPasswordResetTokenQueryService;
    private final AdminOtpSendLimitQueryService adminOtpSendLimitQueryService;

    /**
     * Handles invalid OTP attempt by incrementing attempt count.
     * Uses REQUIRES_NEW propagation to ensure this operation commits
     * even if the calling transaction is rolled back.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleInvalidOtpAttempt(String loginId, String inputOtp) {
        log.debug("Handling invalid OTP attempt for loginId: {}", loginId);
        
        Optional<AdminPasswordResetToken> latestTokenOpt =
                adminPasswordResetTokenQueryService
                        .findLatestValidTokenByLoginIdAndType(loginId, TokenType.PASSWORD_RESET);

        if (latestTokenOpt.isPresent()) {
            AdminPasswordResetToken token = latestTokenOpt.get();
            short currentAttempts = token.getAttemptCount();
            token.setAttemptCount((short)(currentAttempts + 1));
            
            log.info("Incrementing OTP attempt count for loginId: {} from {} to {}", 
                    loginId, currentAttempts, token.getAttemptCount());

            // attemptCount가 최대값에 도달하면 토큰을 무효화
            if (token.getAttemptCount() >= MAX_VERIFY_ATTEMPTS) {
                token.setUsed(true);
                log.warn("Token invalidated due to max attempts exceeded for loginId: {}", loginId);
            }

            adminPasswordResetTokenQueryService.save(token);
            log.debug("Successfully updated attempt count for loginId: {}", loginId);
        } else {
            log.warn("No valid token found to update attempt count for loginId: {}", loginId);
        }
    }

    /**
     * Blocks OTP sending for the user when daily limit is exceeded.
     * Uses REQUIRES_NEW propagation to ensure this operation commits
     * even if the calling transaction is rolled back.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void blockUserForDailyLimitExceeded(String loginId) {
        log.info("Blocking OTP sending for loginId: {} due to daily limit exceeded", loginId);
        
        AdminOtpSendLimit sendLimit = adminOtpSendLimitQueryService.getOrCreateTodayLimit(loginId);
        
        if (!Boolean.TRUE.equals(sendLimit.getBlocked())) {
            sendLimit.setBlocked(true);
            adminOtpSendLimitQueryService.save(sendLimit);
            log.info("Successfully blocked OTP sending for loginId: {}", loginId);
        } else {
            log.debug("OTP sending was already blocked for loginId: {}", loginId);
        }
    }
}
