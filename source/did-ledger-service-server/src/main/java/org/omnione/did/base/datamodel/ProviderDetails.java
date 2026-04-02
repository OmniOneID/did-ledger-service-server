package org.omnione.did.base.datamodel;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : ProviderDetails
 * @since : 6/12/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class ProviderDetails extends Provider {
    private String name;
}
