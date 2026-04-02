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

import org.omnione.did.base.db.constant.TokenType;
import org.omnione.did.base.db.domain.AdminPasswordResetToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AdminPasswordResetToken entity.
 */
@Repository
public interface AdminPasswordResetTokenRepository extends JpaRepository<AdminPasswordResetToken, Long> {

    /**
     * Finds the latest valid token for the given login ID and token type.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return Optional containing the latest valid token if found
     */
    @Query("SELECT t FROM AdminPasswordResetToken t " +
           "WHERE t.loginId = :loginId " +
           "AND t.tokenType = :tokenType " +
           "AND t.used = false " +
           "AND t.expiredAt > :now " +
           "ORDER BY t.createdAt DESC")
    Optional<AdminPasswordResetToken> findLatestValidTokenByLoginIdAndType(
            @Param("loginId") String loginId,
            @Param("tokenType") TokenType tokenType,
            @Param("now") Instant now
    );

    /**
     * Finds a valid token by login ID, token value, and token type.
     *
     * @param loginId   the login ID
     * @param token     the token value
     * @param tokenType the token type
     * @return Optional containing the token if found and valid
     */
    @Query("SELECT t FROM AdminPasswordResetToken t " +
           "WHERE t.loginId = :loginId " +
           "AND t.token = :token " +
           "AND t.tokenType = :tokenType " +
           "AND t.used = false " +
           "AND t.expiredAt > :now")
    Optional<AdminPasswordResetToken> findValidTokenByLoginIdAndToken(
            @Param("loginId") String loginId,
            @Param("token") String token,
            @Param("tokenType") TokenType tokenType,
            @Param("now") Instant now
    );

    /**
     * Marks all unused tokens for the given login ID as used.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return the number of tokens marked as used
     */
    @Modifying
    @Query("UPDATE AdminPasswordResetToken t " +
           "SET t.used = true " +
           "WHERE t.loginId = :loginId " +
           "AND t.tokenType = :tokenType " +
           "AND t.used = false")
    int markAllUnusedTokensAsUsed(
            @Param("loginId") String loginId,
            @Param("tokenType") TokenType tokenType
    );

    /**
     * Deletes expired tokens.
     *
     * @param now the current timestamp
     * @return the number of deleted tokens
     */
    @Modifying
    @Query("DELETE FROM AdminPasswordResetToken t WHERE t.expiredAt < :now")
    int deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Finds the latest token for the given login ID and token type (regardless of used status).
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return Optional containing the latest token if found
     */
    @Query("SELECT t FROM AdminPasswordResetToken t " +
           "WHERE t.loginId = :loginId " +
           "AND t.tokenType = :tokenType " +
           "AND t.expiredAt > :now " +
           "ORDER BY t.createdAt DESC")
    List<AdminPasswordResetToken> findLatestTokensByLoginIdAndType(
            @Param("loginId") String loginId,
            @Param("tokenType") TokenType tokenType,
            @Param("now") Instant now,
            Pageable pageable
    );

    /**
     * Finds all unused tokens for the given login ID and token type.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return list of unused tokens
     */
    List<AdminPasswordResetToken> findByLoginIdAndTokenTypeAndUsedFalse(String loginId, TokenType tokenType);
}
