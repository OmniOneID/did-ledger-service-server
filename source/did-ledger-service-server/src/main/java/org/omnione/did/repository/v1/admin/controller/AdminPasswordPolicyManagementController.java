package org.omnione.did.repository.v1.admin.controller;

import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.admin.dto.admin.AdminPasswordPolicyDto;
import org.omnione.did.repository.v1.admin.dto.admin.RegisterAdminPasswordReqDto;
import org.omnione.did.repository.v1.admin.service.AdminPasswordPolicyManagementService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class AdminPasswordPolicyManagementController {
    private final AdminPasswordPolicyManagementService adminPasswordPolicyManagementService;

    @GetMapping(value = "/admin-password-policy")
    public AdminPasswordPolicyDto getAdminPasswordPolicy() {
        return adminPasswordPolicyManagementService.findAdminPasswordPolicy();
    }

    @ArticleLog(name = "[Admin Manage] Register Admin Password Policy", description = "Register Admin Password Policy",
            targetType = "ADMIN", actionType = ActionType.UPDATE)
    @PostMapping(value = "/admin-password-policy", produces = "application/json")
    public EmptyResDto registerAdminPasswordPolicy(@RequestBody RegisterAdminPasswordReqDto registerAdminPasswordReqDto) {
        return adminPasswordPolicyManagementService.registerAdminPasswordPolicy(registerAdminPasswordReqDto);
    }
}
