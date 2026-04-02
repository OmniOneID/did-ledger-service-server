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

import org.omnione.did.base.db.constant.PasswordResetReason;
import org.omnione.did.base.db.domain.Admin;
import org.omnione.did.base.db.repository.AdminRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.repository.v1.admin.dto.admin.*;
import org.omnione.did.repository.v1.admin.service.query.AdminQueryService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminManagementService {
    private final AdminQueryService adminQueryService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder delegatingPasswordEncoder;

    public AdminDto resetPassword(ResetPasswordReqDto resetPasswordReqDto) {
        log.debug("=== Starting resetPassword ===");

        log.debug("\t--> Finding admin by loginId and old password");
        Admin admin = adminQueryService.findByLoginId(
                resetPasswordReqDto.getLoginId()
        );

        String clientPassword = resetPasswordReqDto.getOldPassword();
        String stored = admin.getLoginPassword();
        String algo = admin.getPasswordAlgo();

        boolean matches = false;
        if ("sha256".equalsIgnoreCase(algo) || looksLikeSha256Hex(stored)) {
            matches = stored.equalsIgnoreCase(clientPassword);
        } else {
            matches = delegatingPasswordEncoder.matches(clientPassword, stored);
        }

        if (!matches) {
            throw new OpenDidException(ErrorCode.ADMIN_INFO_NOT_FOUND);
        }

        log.debug("\t--> Updating admin password and reset status");
        String newHash = delegatingPasswordEncoder.encode(resetPasswordReqDto.getNewPassword());
        admin.setLoginPassword(newHash);
        admin.setPasswordAlgo("bcrypt");
        admin.setRequirePasswordReset(false);
        admin.setPasswordResetReason(null); // Clear the reason after successful password reset
        admin.setLastPasswordChangedAt(Instant.now());

        log.debug("=== Finished resetPassword ===");
        return AdminDto.fromAdmin(adminRepository.save(admin));
    }

    private boolean looksLikeSha256Hex(String s) {
        return s != null && s.matches("(?i)^[0-9a-f]{64}$");
    }

    public Page<AdminDto> searchAdmins(String searchKey, String searchValue, Pageable pageable) {
        log.debug("=== Starting searchAdmins ===");

        log.debug("\t--> Searching admin list with key: {}, value: {}", searchKey, searchValue);
        Page<AdminDto> result = adminQueryService.searchAdminList(searchKey, searchValue, pageable);

        log.debug("=== Finished searchAdmins ===");
        return result;
    }

    public AdminDto findById(Long id) {
        log.debug("=== Starting findById ===");

        log.debug("\t--> Finding admin by id: {}", id);
        AdminDto result = AdminDto.fromAdmin(adminQueryService.findById(id));

        log.debug("=== Finished findById ===");
        return result;
    }

    public EmptyResDto registerAdmin(RegisterAdminReqDto registerAdminReqDto) {
        log.debug("=== Starting registerAdmin ===");

        log.debug("\t--> Checking if admin with loginId already exists");
        Admin existingAdmin = adminQueryService.findByLoginIdOrNull(registerAdminReqDto.getLoginId());
        if (existingAdmin != null) {
            throw new OpenDidException(ErrorCode.ADMIN_ALREADY_EXISTS);
        }

        log.debug("\t--> Creating new admin with loginId: {}", registerAdminReqDto.getLoginId());
        // TODO: Check if the role is valid
        // TODO: createdBy should be the logged in user
        String newHash = delegatingPasswordEncoder.encode(registerAdminReqDto.getLoginPassword());

        Admin admin = Admin.builder()
                .loginId(registerAdminReqDto.getLoginId())
                .role(registerAdminReqDto.getRole())
                .passwordAlgo("bcrypt")
                .loginPassword(newHash)
                .requirePasswordReset(true)
                .passwordResetReason(PasswordResetReason.FIRST_LOGIN) // Set first login reason
                .emailVerified(false)
                .createdBy("SYSTEM")
                .lastPasswordChangedAt(Instant.now())
                .build();

        adminRepository.save(admin);

        log.debug("=== Finished registerAdmin ===");
        return new EmptyResDto();
    }

    public VerifyAdminIdUniqueResDto verifyAdminIdUnique(String loginId) {
        log.debug("=== Starting verifyAdminIdUnique ===");

        log.debug("\t--> Checking if loginId is unique: {}", loginId);
        long count = adminQueryService.countByLoginId(loginId);
        VerifyAdminIdUniqueResDto result = VerifyAdminIdUniqueResDto.builder()
                .isUnique(count == 0)
                .build();

        log.debug("=== Finished verifyAdminIdUnique ===");
        return result;
    }

    public EmptyResDto deleteAdmin(Long id) {
        log.debug("=== Starting deleteAdmin ===");

        log.debug("\t--> Finding admin to delete with id: {}", id);
        adminQueryService.findById(id);
        
        log.debug("\t--> Deleting admin");
        adminRepository.deleteById(id);

        log.debug("=== Finished deleteAdmin ===");
        return new EmptyResDto();
    }

    public EmptyResDto resetPasswordByRoot(ResetPasswordByRootReqDto resetPasswordByRootReqDto) {
        log.debug("=== Starting resetPasswordByRoot ===");

        log.debug("\t--> Finding admin by loginId");
        Admin admin = adminQueryService.findByLoginId(resetPasswordByRootReqDto.getLoginId());
        
        log.debug("\t--> Resetting password by root authority");
        String newHash = delegatingPasswordEncoder.encode(resetPasswordByRootReqDto.getNewPassword());
        admin.setLoginPassword(newHash);
        admin.setPasswordAlgo("bcrypt");
        admin.setRequirePasswordReset(true);
        admin.setPasswordResetReason(PasswordResetReason.ADMIN_FORCED); // Set admin forced reason
        admin.setLastPasswordChangedAt(Instant.now());


        adminRepository.save(admin);

        log.debug("=== Finished resetPasswordByRoot ===");
        return new EmptyResDto();
    }

    public AdminDto changeAdminIdAndPassword(ChangeAdminIdAndPasswordReqDto changeAdminIdAndPasswordReqDto) {
        log.debug("=== Starting changeAdminIdAndPassword ===");

        log.debug("\t--> Finding admin by old loginId");
        Admin admin = adminQueryService.findByLoginId(
                changeAdminIdAndPasswordReqDto.getOldLoginId()
        );

        String clientPassword = changeAdminIdAndPasswordReqDto.getOldPassword();
        String stored = admin.getLoginPassword();
        String algo = admin.getPasswordAlgo();

        boolean matches = false;
        if ("sha256".equalsIgnoreCase(algo) || looksLikeSha256Hex(stored)) {
            matches = stored.equalsIgnoreCase(clientPassword);
        } else {
            matches = delegatingPasswordEncoder.matches(clientPassword, stored);
        }

        if (!matches) {
            throw new OpenDidException(ErrorCode.ADMIN_INFO_NOT_FOUND);
        }

        log.debug("\t--> Checking if new loginId is unique");
        Admin existingAdmin = adminQueryService.findByLoginIdOrNull(changeAdminIdAndPasswordReqDto.getNewLoginId());
        if (existingAdmin != null) {
            throw new OpenDidException(ErrorCode.ADMIN_ALREADY_EXISTS);
        }

        log.debug("\t--> Updating admin loginId and password");
        String newHash = delegatingPasswordEncoder.encode(changeAdminIdAndPasswordReqDto.getNewPassword());
        admin.setLoginId(changeAdminIdAndPasswordReqDto.getNewLoginId());
        admin.setLoginPassword(newHash);
        admin.setPasswordAlgo("bcrypt");
        admin.setRequirePasswordReset(false);
        admin.setPasswordResetReason(null);
        admin.setLastPasswordChangedAt(Instant.now());

        log.debug("=== Finished changeAdminIdAndPassword ===");
        return AdminDto.fromAdmin(adminRepository.save(admin));
    }
}
