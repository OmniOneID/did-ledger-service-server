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
package org.omnione.did.repository.v1.admin.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Data Transfer Object for changing both admin ID and password on first login.
 * <p>
 * Includes old login ID, new login ID, old password for verification, and new password.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChangeAdminIdAndPasswordReqDto {
    @NotNull(message = "oldLoginId cannot be null")
    private String oldLoginId;

    @NotNull(message = "newLoginId cannot be null")
    private String newLoginId;

    @NotNull(message = "oldPassword cannot be null")
    private String oldPassword;

    @NotNull(message = "newPassword cannot be null")
    private String newPassword;
}
