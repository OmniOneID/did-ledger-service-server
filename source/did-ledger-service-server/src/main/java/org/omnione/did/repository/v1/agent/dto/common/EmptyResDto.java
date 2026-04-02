package org.omnione.did.repository.v1.agent.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Represents an empty response DTO.
 * This class is used to indicate a successful operation with no specific data to return.
 * In JSON format, this would be represented as an empty object (e.g., {}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class EmptyResDto {
    // Jackson 직렬화를 위한 더미 필드
    private String status = "success";
}
