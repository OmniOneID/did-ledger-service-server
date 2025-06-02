package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "did_document_revoked")
public class DidDocumentRevoked extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version", nullable = false, columnDefinition = "SMALLINT")
    private Short version;

    @Column(name = "document", nullable = false)
    private String document;

    @Column(name = "controller", nullable = false)
    private String controller;

    @Column(name = "deactivated")
    private Boolean deactivated;

    @Column(name = "did_id", nullable = false)
    private Long didId;
}
