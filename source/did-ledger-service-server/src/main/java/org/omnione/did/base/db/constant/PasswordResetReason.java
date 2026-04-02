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
package org.omnione.did.base.db.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration representing password reset reasons
 * Manages and categorizes the reasons why administrators need to change passwords
 */
@Getter
@RequiredArgsConstructor
public enum PasswordResetReason {
    /**
     * First login - When accessing for the first time after account creation
     */
    FIRST_LOGIN("FIRST_LOGIN", "Please set a new password for secure account usage"),

    /**
     * Password expired - When policy period has been exceeded
     */
    EXPIRED("EXPIRED", "Password validity period has expired"),

    /**
     * Admin forced - When administrator sets a temporary password
     */
    ADMIN_FORCED("ADMIN_FORCED", "You are logged in with a temporary password. Please change to a new password");

    private final String code;
    private final String defaultMessage;

    /**
     * Method to find PasswordResetReason by code value
     *
     * @param code The code value to search for
     * @return The corresponding PasswordResetReason, null if not found
     */
    public static PasswordResetReason fromCode(String code) {
        if (code == null) {
            return null;
        }

        for (PasswordResetReason reason : values()) {
            if (reason.getCode().equals(code)) {
                return reason;
            }
        }
        return null;
    }

    /**
     * Returns UI theme color for each reason
     *
     * @return CSS class name or color code
     */
    public String getUIColorTheme() {
        switch (this) {
            case FIRST_LOGIN:
                return "info";
            case EXPIRED:
                return "warning";
            case ADMIN_FORCED:
                return "danger";
            default:
                return "secondary";
        }
    }

    /**
     * Returns icon type for each reason
     *
     * @return Icon identifier
     */
    public String getIconType() {
        switch (this) {
            case FIRST_LOGIN:
                return "shield";
            case EXPIRED:
                return "clock";
            case ADMIN_FORCED:
                return "key";
            default:
                return "lock";
        }
    }
}