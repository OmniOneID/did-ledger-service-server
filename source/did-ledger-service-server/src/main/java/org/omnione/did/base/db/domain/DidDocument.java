package org.omnione.did.base.db.domain;

import org.omnione.did.base.constants.DidDocStatus;
import jakarta.persistence.*;
import lombok.*;

import javax.print.attribute.standard.MediaSize;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "did_document")
public class DidDocument extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "did_id", nullable = false)
    private Long didId;

    @Column(name = "did", nullable = false, length = 50)
    private String did;

    @Column(name = "document", nullable = false)
    private String document;

    @Column(name = "version", nullable = false, columnDefinition = "SMALLINT")
    private Short version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private DidDocStatus status;
}
