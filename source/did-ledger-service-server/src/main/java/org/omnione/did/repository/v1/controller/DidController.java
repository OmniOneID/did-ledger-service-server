package org.omnione.did.repository.v1.controller;

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.repository.v1.dto.common.EmptyResDto;
import org.omnione.did.repository.v1.dto.did.InputDidDocReqDto;
import org.omnione.did.repository.v1.dto.did.UpdateDidDocReqDto;
import org.omnione.did.repository.v1.service.DidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.LLS + UrlConstant.Did.V1)
public class DidController {

    private final DidService didService;

    @PostMapping
    public void generateDid(@RequestBody InputDidDocReqDto request) {
        didService.generateDid(request);
    }

    @GetMapping
    public String getDid(@RequestParam(value = "did", defaultValue = "did:omn:tas") String didDoc) {
        return didService.getDid(didDoc);
    }


    // TODO 기능 개발
    @PatchMapping
    public void updateDidStatus(@RequestBody UpdateDidDocReqDto request) {
        didService.updateStatus(request);
    }
}
