package org.omnione.did.repository.v1.admin.api;

import org.omnione.did.repository.v1.admin.api.dto.RequestSendEmailReqDto;
import org.omnione.did.repository.v1.common.dto.EmptyResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * The NotiFeign interface is a Feign client that provides endpoints for notification services.
 * It is used to communicate with the Noti service. (currently Noti service is TAS service)
 */
@FeignClient(value = "Noti", url = "${tas.url}" + "/noti", path = "/api/v1")
public interface NotiFeign {

    /**
     * Sends an email using a predefined HTML template.
     *
     * The specific email template is determined by the `templateType` provided in the request.
     * Each template includes placeholders that can be dynamically replaced with actual content.
     * The dynamic content to be inserted into the template is provided in the `contentData` map,
     * where each key corresponds to a placeholder in the template, and each value is the content to replace it.
     *
     * @param request The request DTO containing the email details, including the template type
     *                and content data for dynamic insertion.
     * @return An {@link EmptyResDto} indicating that the email sending operation was completed.
     */
    @PostMapping("/send-email")
    EmptyResDto requestSendEmail(@RequestBody RequestSendEmailReqDto request);
}
