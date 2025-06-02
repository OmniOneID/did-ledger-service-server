package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.omnione.did.data.model.enums.vc.VcStatus;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "vc_metadata")
public class VcMetadata extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vc_id", nullable = false, length = 100)
    private String vcId;

    @Column(name = "issuer_did", nullable = false, length = 100)
    private String issuerDid;

    @Column(name = "subject_did", nullable = false, length = 100)
    private String subjectDid;

    @Column(name = "vc_schema", nullable = false, length = 200)
    private String vcSchema;

    @Column(name = "issuance_date", nullable = false)
    private Instant issuanceDate;

    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;

    @Column(name = "format_version")
    private String formatVersion;

    @Column(name = "language")
    private String language;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VcStatus status;

    @Column(name = "metadata", nullable = false)
    private String metadata;
}
