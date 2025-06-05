package org.omnione.did.repository.v1.dto.did;

import lombok.*;
import org.omnione.did.base.datamodel.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputDidDocReqDto {
    private RoleType roleType;
    private InvokedDidDoc didDoc;
}
