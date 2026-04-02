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

package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entity representing admin OTP send limitations.
 * This entity stores information about OTP sending limits and restrictions for administrators.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "admin_otp_send_limit")
public class AdminOtpSendLimit extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(name = "send_date", nullable = false)
    private LocalDate sendDate;

    @Column(name = "daily_send_count", nullable = false)
    @Builder.Default
    private Short dailySendCount = 0;

    @Column(name = "last_sent_at")
    private Instant lastSentAt;

    @Column(name = "cooldown_until")
    private Instant cooldownUntil;

    @Column(name = "blocked", nullable = false)
    @Builder.Default
    private Boolean blocked = false;
}
