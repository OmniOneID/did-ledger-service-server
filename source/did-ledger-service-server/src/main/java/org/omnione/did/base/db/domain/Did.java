package org.omnione.did.base.db.domain;

import org.omnione.did.base.constants.DidDocStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "did")
public class Did extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "did", nullable = false, length = 50)
    private String did;

    @Column(name = "version", nullable = false, columnDefinition = "SMALLINT")
    private Short version;

    @Column(name = "role")
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private DidDocStatus status;

    @Column(name = "terminated_time")
    private Instant terminatedTime;
}
