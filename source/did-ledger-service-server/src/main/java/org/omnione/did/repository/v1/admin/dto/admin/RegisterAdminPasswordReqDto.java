package org.omnione.did.repository.v1.admin.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterAdminPasswordReqDto {
    @NotNull(message = "minLength cannot be null")
    private Short minLength;
    @NotNull(message = "requireUppercase cannot be null")
    private Boolean requireUppercase;
    @NotNull(message = "requireNumber cannot be null")
    private Boolean requireNumber;
    @NotNull(message = "requireSpecial cannot be null")
    private Boolean requireSpecial;
    @NotNull(message = "passwordExpiryDays cannot be null")
    private Short passwordExpiryDays;
}
