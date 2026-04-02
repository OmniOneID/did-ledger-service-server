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

package org.omnione.did.repository.v1.admin.service.query;

import org.omnione.did.base.db.domain.AdminOtpSendLimit;
import org.omnione.did.base.db.repository.AdminOtpSendLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Query service for AdminOtpSendLimit operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminOtpSendLimitQueryService {

    private final AdminOtpSendLimitRepository adminOtpSendLimitRepository;

    /**
     * Saves an admin OTP send limit record.
     *
     * @param sendLimit the send limit record to save
     * @return the saved send limit record
     */
    public AdminOtpSendLimit save(AdminOtpSendLimit sendLimit) {
        log.debug("Saving admin OTP send limit for loginId: {} and date: {}", 
                  sendLimit.getLoginId(), sendLimit.getSendDate());
        return adminOtpSendLimitRepository.save(sendLimit);
    }

    /**
     * Finds the send limit record for the given login ID and date.
     *
     * @param loginId  the login ID
     * @param sendDate the send date
     * @return Optional containing the send limit record if found
     */
    public Optional<AdminOtpSendLimit> findByLoginIdAndSendDate(String loginId, LocalDate sendDate) {
        log.debug("Finding send limit for loginId: {} and date: {}", loginId, sendDate);
        return adminOtpSendLimitRepository.findByLoginIdAndSendDate(loginId, sendDate);
    }

    /**
     * Finds the latest send limit record for the given login ID.
     *
     * @param loginId the login ID
     * @return Optional containing the latest send limit record if found
     */
    public Optional<AdminOtpSendLimit> findLatestByLoginId(String loginId) {
        log.debug("Finding latest send limit for loginId: {}", loginId);
        return adminOtpSendLimitRepository.findLatestByLoginId(loginId);
    }

    /**
     * Deletes old send limit records before the specified date.
     *
     * @param beforeDate the date before which records should be deleted
     * @return the number of deleted records
     */
    public int deleteOldRecords(LocalDate beforeDate) {
        log.debug("Deleting send limit records before date: {}", beforeDate);
        return adminOtpSendLimitRepository.deleteByDateBefore(beforeDate);
    }

    /**
     * Checks if there are any blocked records for the given login ID.
     *
     * @param loginId the login ID
     * @return true if there are blocked records, false otherwise
     */
    public boolean hasBlockedRecords(String loginId) {
        log.debug("Checking for blocked records for loginId: {}", loginId);
        return adminOtpSendLimitRepository.existsByLoginIdAndBlockedTrue(loginId);
    }

    /**
     * Gets or creates today's send limit record for the given login ID.
     * Automatically handles daily reset by creating new records for new dates.
     *
     * @param loginId the login ID
     * @return the send limit record for today
     */
    public AdminOtpSendLimit getOrCreateTodayLimit(String loginId) {
        LocalDate today = LocalDate.now();
        log.debug("Getting or creating today's limit for loginId: {} and date: {}", loginId, today);
        
        Optional<AdminOtpSendLimit> todayLimitOpt = findByLoginIdAndSendDate(loginId, today);
        
        if (todayLimitOpt.isPresent()) {
            log.debug("Found existing limit record for today for loginId: {}", loginId);
            return todayLimitOpt.get();
        } else {
            // Check if there was a record from yesterday that was blocked
            LocalDate yesterday = today.minusDays(1);
            Optional<AdminOtpSendLimit> yesterdayLimitOpt = findByLoginIdAndSendDate(loginId, yesterday);
            
            if (yesterdayLimitOpt.isPresent() && Boolean.TRUE.equals(yesterdayLimitOpt.get().getBlocked())) {
                log.info("Previous day's block status found for loginId: {}, automatically resetting for new day", loginId);
            }
            
            // Create new record for today (effectively resetting daily limits)
            log.info("Creating new limit record for today for loginId: {} - daily limits reset", loginId);
            AdminOtpSendLimit newLimit = AdminOtpSendLimit.builder()
                    .loginId(loginId)
                    .sendDate(today)
                    .dailySendCount((short) 0)
                    .blocked(false)
                    .build();
            return save(newLimit);
        }
    }

    /**
     * Unblocks OTP sending for the given login ID on today's date.
     *
     * @param loginId the login ID to unblock
     * @return true if unblocked successfully, false if no blocked record found
     */
    public boolean unblockOtpSending(String loginId) {
        LocalDate today = LocalDate.now();
        log.info("Attempting to unblock OTP sending for loginId: {} on date: {}", loginId, today);
        
        Optional<AdminOtpSendLimit> sendLimitOpt = findByLoginIdAndSendDate(loginId, today);
        
        if (sendLimitOpt.isPresent()) {
            AdminOtpSendLimit sendLimit = sendLimitOpt.get();
            
            if (Boolean.TRUE.equals(sendLimit.getBlocked())) {
                sendLimit.setBlocked(false);
                save(sendLimit);
                log.info("Successfully unblocked OTP sending for loginId: {}", loginId);
                return true;
            } else {
                log.debug("OTP sending was not blocked for loginId: {}", loginId);
                return false;
            }
        } else {
            log.debug("No send limit record found for loginId: {} on date: {}", loginId, today);
            return false;
        }
    }

    /**
     * Resets daily send count and unblocks for the given login ID on today's date.
     *
     * @param loginId the login ID to reset
     * @return true if reset successfully, false if no record found
     */
    public boolean resetDailySendLimit(String loginId) {
        LocalDate today = LocalDate.now();
        log.info("Attempting to reset daily send limit for loginId: {} on date: {}", loginId, today);
        
        Optional<AdminOtpSendLimit> sendLimitOpt = findByLoginIdAndSendDate(loginId, today);
        
        if (sendLimitOpt.isPresent()) {
            AdminOtpSendLimit sendLimit = sendLimitOpt.get();
            sendLimit.setDailySendCount((short) 0);
            sendLimit.setBlocked(false);
            sendLimit.setCooldownUntil(null);
            save(sendLimit);
            log.info("Successfully reset daily send limit for loginId: {}", loginId);
            return true;
        } else {
            log.debug("No send limit record found for loginId: {} on date: {}", loginId, today);
            return false;
        }
    }
}
