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
@Table(name = "zkp_credential_definition")
public class ZkpCredentialDefinition extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "definition_id", nullable = false, length = 100)
    private String definitionId;

    @Column(name = "schema_id", nullable = false, length = 100)
    private String schemaId;

    @Column(name = "version", nullable = false, length = 20)
    private String version;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "tag", nullable = false, length = 100)
    private String tag;

    @Column(name = "definition", nullable = false)
    private String definition;

    @Column(name = "zkp_credential_schema_id")
    private Long zkpCredentialSchemaId;
}
