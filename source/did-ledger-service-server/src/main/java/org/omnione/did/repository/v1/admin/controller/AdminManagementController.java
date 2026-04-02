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
package org.omnione.did.repository.v1.admin.controller;

import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.admin.dto.admin.*;
import org.omnione.did.repository.v1.admin.service.AdminManagementService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing administrator accounts in the Admin Console.
 * <p>
 * Provides endpoints for admin registration, retrieval, password reset, and duplication checks.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    /**
     * Resets the password of an admin by verifying the old password.
     *
     * @param resetPasswordReqDto the request containing loginId, oldPassword, and newPassword
     * @return updated admin information
     */
    @ArticleLog(name = "[Admin Manage] Reset Password", description = "Reset admin password",
            targetType = "ADMIN", targetId = "#resetPasswordReqDto.loginId")
    @PostMapping(value = "/admins/reset-password")
    public AdminDto resetPassword(@Valid @RequestBody ResetPasswordReqDto resetPasswordReqDto) {
        return adminManagementService.resetPassword(resetPasswordReqDto);
    }

    /**
     * Retrieves a paginated list of admins based on search criteria.
     *
     * @param searchKey   the key to filter by
     * @param searchValue the value to match
     * @param pageable    pagination details
     * @return paginated list of admin DTOs
     */
    @GetMapping(value = "/admins/list")
    public Page<AdminDto> searchAdmins(String searchKey, String searchValue, Pageable pageable) {
        return adminManagementService.searchAdmins(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves admin details by ID.
     *
     * @param id the admin ID
     * @return admin DTO
     */
    @GetMapping(value = "/admins")
    public AdminDto getAdmin(@RequestParam Long id) {
        return adminManagementService.findById(id);
    }

    /**
     * Registers a new administrator account.
     *
     * @param registerAdminReqDto the request data for registration
     * @return an empty response DTO upon success
     */
    @ArticleLog(name = "[Admin Manage] Register Admin", description = "Register admin",
            targetType = "ADMIN", targetId = "#registerAdminReqDto.loginId", actionType = ActionType.CREATE)
    @PostMapping(value = "/admins", produces = "application/json")
    public EmptyResDto registerAdmin(@RequestBody RegisterAdminReqDto registerAdminReqDto) {
        return adminManagementService.registerAdmin(registerAdminReqDto);
    }

    /**
     * Checks whether a login ID is already in use.
     *
     * @param loginId the login ID to check
     * @return result indicating if the ID is unique
     */
    @GetMapping(value = "/admins/check-admin-id")
    public VerifyAdminIdUniqueResDto verifyAdminIdUnique(@RequestParam String loginId) {
        return adminManagementService.verifyAdminIdUnique(loginId);
    }

    /**
     * Deletes an administrator account by ID.
     *
     * @param id the admin ID
     * @return an empty response DTO
     */
    @ArticleLog(name = "[Admin Manage] Delete Admin", description = "Delete admin",
            targetType = "ADMIN", targetId = "#id", actionType = ActionType.DELETE)
    @RequestMapping(value = "/admins", method = RequestMethod.DELETE)
    public EmptyResDto deleteAdmin(@RequestParam Long id) {
        return adminManagementService.deleteAdmin(id);
    }

    /**
     * Resets the password of an admin using root authority.
     *
     * @param resetPasswordByRootReqDto the request containing loginId and newPassword
     * @return an empty response DTO
     */
    @ArticleLog(name = "[Admin Manage] Reset Password by Root", description = "Reset admin password by root admin",
            targetType = "ADMIN", targetId = "#resetPasswordByRootReqDto.loginId", actionType = ActionType.UPDATE)
    @PostMapping(value = "/admins/root/reset-password")
    public EmptyResDto resetPasswordByRoot(@RequestBody ResetPasswordByRootReqDto resetPasswordByRootReqDto) {
        return adminManagementService.resetPasswordByRoot(resetPasswordByRootReqDto);
    }

    /**
     * Changes both the ID and password of an admin (typically for first login).
     *
     * @param changeAdminIdAndPasswordReqDto the request containing old/new loginId and passwords
     * @return updated admin information
     */
    @ArticleLog(name = "[Admin Manage] Change ID and Password", description = "Change admin ID and password",
            targetType = "ADMIN", targetId = "#changeAdminIdAndPasswordReqDto.oldLoginId", actionType = ActionType.UPDATE)
    @PostMapping(value = "/admins/change-id-and-password")
    public AdminDto changeAdminIdAndPassword(@Valid @RequestBody ChangeAdminIdAndPasswordReqDto changeAdminIdAndPasswordReqDto) {
        return adminManagementService.changeAdminIdAndPassword(changeAdminIdAndPasswordReqDto);
    }
}
