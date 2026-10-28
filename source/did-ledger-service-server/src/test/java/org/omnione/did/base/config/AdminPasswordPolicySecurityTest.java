package org.omnione.did.base.config;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.service.ApiKeyValidationService;
import org.omnione.did.repository.v1.admin.controller.AdminPasswordPolicyManagementController;
import org.omnione.did.repository.v1.admin.service.AdminPasswordPolicyManagementService;
import org.omnione.did.repository.v1.admin.service.JwtService;
import org.omnione.did.repository.v1.common.service.ApiLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminPasswordPolicyManagementController.class)
@Import(SecurityConfig.class)
class AdminPasswordPolicySecurityTest {

    private static final String PASSWORD_POLICY_URL = "/lss/admin/v1/admin-password-policy";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminPasswordPolicyManagementService adminPasswordPolicyManagementService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ApiKeyValidationService apiKeyValidationService;
    @MockBean
    private ApiLogService apiLogService;

    @Test
    void passwordPolicyCanBeReadWithoutLogin() throws Exception {
        mockMvc.perform(get(PASSWORD_POLICY_URL))
                .andExpect(status().isOk());
    }

    @Test
    void passwordPolicyCannotBeChangedWithoutLogin() throws Exception {
        mockMvc.perform(post(PASSWORD_POLICY_URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is4xxClientError());

        verify(adminPasswordPolicyManagementService, never()).registerAdminPasswordPolicy(any());
    }

    @Test
    void passwordPolicyCanBeChangedByLoggedInAdmin() throws Exception {
        mockMvc.perform(post(PASSWORD_POLICY_URL).with(user("admin").roles("ROOT"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());

        verify(adminPasswordPolicyManagementService).registerAdminPasswordPolicy(any());
    }
}
