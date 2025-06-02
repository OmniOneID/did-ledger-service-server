package org.omnione.did.repository.v1.dto.did;

import lombok.*;
import org.omnione.did.data.model.did.InvokedDidDoc;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDidDocReqDto {
    private String status;
    private String did;
}
