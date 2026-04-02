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
import org.omnione.did.repository.v1.admin.dto.admin.AdminDto;
import org.omnione.did.repository.v1.admin.dto.admin.RequestAdminLoginReqDto;
import org.omnione.did.repository.v1.admin.dto.admin.RequestPasswordResetOtpReqDto;
import org.omnione.did.repository.v1.admin.dto.admin.VerifyOtpAndResetPasswordReqDto;
import org.omnione.did.repository.v1.admin.service.JwtService;
import org.omnione.did.repository.v1.admin.service.SessionService;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for handling admin login sessions in the Admin Console.
 * <p>
 * Provides an endpoint for authenticating admin credentials and initiating a session.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LSS.ADMIN_V1)
public class SessionController {

    private final SessionService sessionService; // 기존 인증 절차 재사용 (id/pw 검증만)
    private final JwtService jwtService;
    @ArticleLog(name = "[Admin] Login", description = "Login",
            targetType = "ADMIN", targetId = "#reqDto.loginId", actionType = ActionType.LOGIN)
    @PostMapping("/login")
    public LoginRes login(@Valid @RequestBody RequestAdminLoginReqDto reqDto) {
        AdminDto admin = sessionService.requestAdminLogin(reqDto); // 여기서 자격검증

        String accessToken = jwtService.createAccessToken(admin);
        String refreshToken = jwtService.createRefreshToken(admin.getId());

        return new LoginRes(admin, accessToken, refreshToken);
    }

    @ArticleLog(name = "[Admin] Refresh Token", description = "Refresh Token by admin.",
            targetType = "ADMIN", actionType = ActionType.CREATE)
    @PostMapping("/refresh-token")
    public TokenRes refresh(@RequestBody RefreshReq req) {
        var jws = jwtService.parse(req.refreshToken());
        if (!jwtService.isRefreshToken(jws)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        Long adminId = Long.valueOf(jws.getPayload().getSubject());

        AdminDto admin = sessionService.requestAdminLoginById(adminId);
        String newAccess = jwtService.createAccessToken(admin);
        return new TokenRes(newAccess);
    }

    @ArticleLog(name = "[Admin] OTP Send for Password reset", description = "OTP sent for password reset.",
            targetType = "ADMIN", targetId = "#request.loginId", actionType = ActionType.CREATE)
    @PostMapping(value = "/password-reset/send-otp")
    @ResponseBody
    public EmptyResDto requestPasswordResetOtp(@Valid @RequestBody RequestPasswordResetOtpReqDto request) {
        return sessionService.requestPasswordResetOtp(request);
    }
    @ArticleLog(name = "[Admin] OTP Verify for Password reset", description = "OTP verify for Password reset.",
            targetType = "NAMESPACE", targetId = "#request.loginId", actionType = ActionType.CREATE)
    @PostMapping(value = "/password-reset/verify-otp")
    @ResponseBody
    public EmptyResDto verifyOtpAndResetPassword(@Valid @RequestBody VerifyOtpAndResetPasswordReqDto request) {
        return sessionService.verifyOtpAndResetPassword(request);
    }

    public record LoginRes(AdminDto admin, String accessToken, String refreshToken) {}
    public record RefreshReq(String refreshToken) {}
    public record TokenRes(String accessToken) {}
}
