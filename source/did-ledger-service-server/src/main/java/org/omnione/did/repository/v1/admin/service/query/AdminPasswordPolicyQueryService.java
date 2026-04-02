package org.omnione.did.repository.v1.admin.service.query;

import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.repository.AdminPasswordPolicyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPasswordPolicyQueryService {
    private final AdminPasswordPolicyRepository adminPasswordPolicyRepository;

    public AdminPasswordPolicy findAdminPasswordPolicy() {
        try {
            return adminPasswordPolicyRepository.findTop1ByOrderByIdAsc()
                    .orElseThrow(() -> new OpenDidException(ErrorCode.ADMIN_PASSWORD_POLICY_NOT_FOUND));
        } catch (OpenDidException e) {
            log.error("Admin Password Policy not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while finding Admin Password Policy: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ADMIN_PASSWORD_POLICY_NOT_FOUND);
        }
    }

    public AdminPasswordPolicy findAdminPasswordPolicyOrNull() {
        return adminPasswordPolicyRepository.findTop1ByOrderByIdAsc().orElse(null);
    }
}
