package org.omnione.did.repository.v1.dto.vc;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TssGetVcMetaResDto {
    private String vcId;
    private String vcMeta;
}
