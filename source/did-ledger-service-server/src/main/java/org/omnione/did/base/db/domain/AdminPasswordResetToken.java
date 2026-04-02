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

import org.omnione.did.base.db.constant.DeliveryMethod;
import org.omnione.did.base.db.constant.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Entity representing admin password reset tokens.
 * This entity stores information about password reset tokens for administrators.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "\"admin_password_reset_token\"")
public class AdminPasswordResetToken extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(name = "token", nullable = false, length = 200)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 20)
    @Builder.Default
    private TokenType tokenType = TokenType.PASSWORD_RESET;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false, length = 20)
    @Builder.Default
    private DeliveryMethod deliveryMethod = DeliveryMethod.EMAIL;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = false;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private Short attemptCount = 0;
}
