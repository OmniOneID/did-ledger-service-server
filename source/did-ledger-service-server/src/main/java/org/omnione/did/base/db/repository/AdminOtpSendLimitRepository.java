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

package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.AdminOtpSendLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for AdminOtpSendLimit entity.
 */
@Repository
public interface AdminOtpSendLimitRepository extends JpaRepository<AdminOtpSendLimit, Long> {

    /**
     * Finds the send limit record for the given login ID and date.
     *
     * @param loginId  the login ID
     * @param sendDate the send date
     * @return Optional containing the send limit record if found
     */
    Optional<AdminOtpSendLimit> findByLoginIdAndSendDate(String loginId, LocalDate sendDate);

    /**
     * Finds the latest send limit record for the given login ID.
     *
     * @param loginId the login ID
     * @return Optional containing the latest send limit record if found
     */
    @Query("SELECT a FROM AdminOtpSendLimit a " +
           "WHERE a.loginId = :loginId " +
           "ORDER BY a.sendDate DESC")
    Optional<AdminOtpSendLimit> findLatestByLoginId(@Param("loginId") String loginId);

    /**
     * Deletes old send limit records before the specified date.
     *
     * @param beforeDate the date before which records should be deleted
     * @return the number of deleted records
     */
    @Modifying
    @Query("DELETE FROM AdminOtpSendLimit a WHERE a.sendDate < :beforeDate")
    int deleteByDateBefore(@Param("beforeDate") LocalDate beforeDate);

    /**
     * Checks if there are any blocked records for the given login ID.
     *
     * @param loginId the login ID
     * @return true if there are blocked records, false otherwise
     */
    boolean existsByLoginIdAndBlockedTrue(String loginId);
}
