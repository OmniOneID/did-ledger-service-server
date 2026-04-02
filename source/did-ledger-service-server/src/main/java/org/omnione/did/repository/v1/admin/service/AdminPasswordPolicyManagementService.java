package org.omnione.did.repository.v1.admin.service;

import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.repository.AdminPasswordPolicyRepository;
import org.omnione.did.repository.v1.admin.dto.admin.AdminPasswordPolicyDto;
import org.omnione.did.repository.v1.admin.dto.admin.RegisterAdminPasswordReqDto;
import org.omnione.did.repository.v1.admin.service.query.AdminPasswordPolicyQueryService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminPasswordPolicyManagementService {
    private final AdminPasswordPolicyQueryService adminPasswordPolicyQueryService;
    private final AdminPasswordPolicyRepository adminPasswordPolicyRepository;

    public AdminPasswordPolicyDto findAdminPasswordPolicy() {
        log.debug("=== Starting findAdminPasswordPolicy ===");

        // Retrieve Admin Password Policy
        log.debug("\t--> Retrieving Admin Password Policy");
        AdminPasswordPolicyDto adminPasswordPolicyDto = AdminPasswordPolicyDto.fromAdminPasswordPolicy(adminPasswordPolicyQueryService.findAdminPasswordPolicy());

        log.debug("=== Finished findAdminPasswordPolicy ===");
        return adminPasswordPolicyDto;
    }

    public EmptyResDto registerAdminPasswordPolicy(RegisterAdminPasswordReqDto registerAdminPasswordReqDto) {
        log.debug("=== Starting registerAdminPasswordPolicy ===");

        AdminPasswordPolicy adminPasswordPolicy = adminPasswordPolicyQueryService.findAdminPasswordPolicyOrNull();

        if (adminPasswordPolicy == null) {
            log.debug("\t--> Registering new Admin Password Policy");

            adminPasswordPolicyRepository.save(AdminPasswordPolicy.builder()
                    .minLength(registerAdminPasswordReqDto.getMinLength())
                    .requireUppercase(registerAdminPasswordReqDto.getRequireUppercase())
                    .requireNumber(registerAdminPasswordReqDto.getRequireNumber())
                    .requireSpecial(registerAdminPasswordReqDto.getRequireSpecial())
                    .passwordExpiryDays(registerAdminPasswordReqDto.getPasswordExpiryDays())
                    .build());
        } else {
            log.debug("\t--> Updating existing Admin Password Policy");
            adminPasswordPolicy.setMinLength(registerAdminPasswordReqDto.getMinLength());
            adminPasswordPolicy.setRequireUppercase(registerAdminPasswordReqDto.getRequireUppercase());
            adminPasswordPolicy.setRequireNumber(registerAdminPasswordReqDto.getRequireNumber());
            adminPasswordPolicy.setRequireSpecial(registerAdminPasswordReqDto.getRequireSpecial());
            adminPasswordPolicy.setPasswordExpiryDays(registerAdminPasswordReqDto.getPasswordExpiryDays());
            adminPasswordPolicyRepository.save(adminPasswordPolicy);
        }

        log.debug("=== Finished registerAdminPasswordPolicy ===");
        return new EmptyResDto();
    }


}
