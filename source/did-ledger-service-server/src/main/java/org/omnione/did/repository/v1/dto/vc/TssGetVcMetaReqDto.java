package org.omnione.did.repository.v1.dto.vc;

import lombok.*;
import org.omnione.did.base.datamodel.VcMeta;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TssGetVcMetaReqDto {
    private String vcId;
    private VcMeta vcMeta;
}
