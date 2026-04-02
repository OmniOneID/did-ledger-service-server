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

import org.omnione.did.base.db.constant.TokenType;
import org.omnione.did.base.db.domain.AdminPasswordResetToken;
import org.omnione.did.base.db.repository.AdminPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Query service for AdminPasswordResetToken operations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminPasswordResetTokenQueryService {

    private final AdminPasswordResetTokenRepository adminPasswordResetTokenRepository;

    /**
     * Saves an admin password reset token.
     *
     * @param token the token to save
     * @return the saved token
     */
    public AdminPasswordResetToken save(AdminPasswordResetToken token) {
        log.debug("Saving admin password reset token for loginId: {}", token.getLoginId());
        return adminPasswordResetTokenRepository.save(token);
    }

    /**
     * Finds the latest valid token for the given login ID and token type.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return Optional containing the latest valid token if found
     */
    public Optional<AdminPasswordResetToken> findLatestValidTokenByLoginIdAndType(String loginId, TokenType tokenType) {
        log.debug("Finding latest valid token for loginId: {} and tokenType: {}", loginId, tokenType);
        return adminPasswordResetTokenRepository.findLatestValidTokenByLoginIdAndType(loginId, tokenType, Instant.now());
    }

    /**
     * Finds the latest token for the given login ID and token type (regardless of used status).
     * This is useful for checking attempt counts even on used tokens.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return Optional containing the latest token if found
     */
    public Optional<AdminPasswordResetToken> findLatestTokenByLoginIdAndType(String loginId, TokenType tokenType) {
        log.debug("Finding latest token (any status) for loginId: {} and tokenType: {}", loginId, tokenType);
        List<AdminPasswordResetToken> tokens = adminPasswordResetTokenRepository
                .findLatestTokensByLoginIdAndType(loginId, tokenType, Instant.now(), PageRequest.of(0, 1));
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    /**
     * Finds a valid token by login ID, token value, and token type.
     *
     * @param loginId   the login ID
     * @param token     the token value
     * @param tokenType the token type
     * @return Optional containing the token if found and valid
     */
    public Optional<AdminPasswordResetToken> findValidTokenByLoginIdAndToken(String loginId, String token, TokenType tokenType) {
        log.debug("Finding valid token for loginId: {} and token: {}", loginId, token);
        return adminPasswordResetTokenRepository.findValidTokenByLoginIdAndToken(loginId, token, tokenType, Instant.now());
    }

    /**
     * Marks all unused tokens for the given login ID as used.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return the number of tokens marked as used
     */
    public int markAllUnusedTokensAsUsed(String loginId, TokenType tokenType) {
        log.debug("Marking all unused tokens as used for loginId: {} and tokenType: {}", loginId, tokenType);
        return adminPasswordResetTokenRepository.markAllUnusedTokensAsUsed(loginId, tokenType);
    }

    /**
     * Deletes expired tokens.
     *
     * @return the number of deleted tokens
     */
    public int deleteExpiredTokens() {
        log.debug("Deleting expired tokens");
        return adminPasswordResetTokenRepository.deleteExpiredTokens(Instant.now());
    }

    /**
     * Finds all unused tokens for the given login ID and token type.
     *
     * @param loginId   the login ID
     * @param tokenType the token type
     * @return list of unused tokens
     */
    public List<AdminPasswordResetToken> findUnusedTokensByLoginIdAndType(String loginId, TokenType tokenType) {
        log.debug("Finding unused tokens for loginId: {} and tokenType: {}", loginId, tokenType);
        return adminPasswordResetTokenRepository.findByLoginIdAndTokenTypeAndUsedFalse(loginId, tokenType);
    }
}
