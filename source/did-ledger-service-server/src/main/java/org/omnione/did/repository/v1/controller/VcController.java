package org.omnione.did.repository.v1.controller;

import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.repository.v1.dto.common.EmptyResDto;
import org.omnione.did.repository.v1.dto.vc.InputVcMetaReqDto;
import org.omnione.did.repository.v1.dto.vc.TssGetVcMetaResDto;
import org.omnione.did.repository.v1.dto.vc.UpdateVcStatusReqDto;
import org.omnione.did.repository.v1.service.VcService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Vc.V1)
public class VcController {

    private final VcService vcService;

    @PostMapping
    public ResponseEntity<EmptyResDto> inputVcMeta(@RequestBody @Valid VcMeta request) {
        log.debug("generate VC");

        vcService.inputVcMeta(request);
        return ResponseEntity.ok(new EmptyResDto());
    }

    @GetMapping
    public ResponseEntity<String> getVcMeta(@RequestParam("vcId") String vcId) {
        log.debug("Get VC Meta");

        return new ResponseEntity<>(vcService.getVcMetaByVcId(vcId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<EmptyResDto> updateVcMeta(@RequestBody @Valid UpdateVcStatusReqDto request) {
        log.debug("Update VC Status");
        vcService.updateVc(request);

        return ResponseEntity.ok(new EmptyResDto());
    }
}
