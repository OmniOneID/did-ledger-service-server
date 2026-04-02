package org.omnione.did.repository.v1.common.dto;

import lombok.*;

/**
 * Represents an empty response DTO.
 * This class is used to indicate a successful operation with no specific data to return.
 * In JSON format, this would be represented as an empty object (e.g., {}).
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
public class EmptyResDto {
}