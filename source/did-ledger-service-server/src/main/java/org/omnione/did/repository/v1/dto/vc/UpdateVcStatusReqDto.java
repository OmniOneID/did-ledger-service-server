package org.omnione.did.repository.v1.dto.vc;

import lombok.Getter;
import lombok.Setter;
import org.omnione.did.data.model.enums.vc.VcStatus;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : UpdateVcStatusReqDto
 * @since : 8/7/24
 */
@Getter
@Setter
public class UpdateVcStatusReqDto {
    private String vcId;
    private VcStatus vcStatus;
}
