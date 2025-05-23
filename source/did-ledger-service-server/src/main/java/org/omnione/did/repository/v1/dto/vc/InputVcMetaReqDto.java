package org.omnione.did.repository.v1.dto.vc;

import org.omnione.did.base.datamodel.Proof;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.omnione.did.data.model.vc.VcMeta;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : InputVcMetaReqDto
 * @since : 6/12/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class InputVcMetaReqDto {
    @NotNull(message = "id cannot be null")
    private String id;
    @Valid
    @NotNull(message = "vcMeta cannot be null")
    private VcMeta vcMeta;

    private Proof proof;
}
