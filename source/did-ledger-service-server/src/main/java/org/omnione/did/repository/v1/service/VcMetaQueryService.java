package org.omnione.did.repository.v1.service;

import org.omnione.did.base.db.domain.VcMetadata;
import org.omnione.did.base.db.domain.VcStatusHistory;
import org.omnione.did.base.db.repository.VcMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.repository.VcStatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : VcMetaQueryService
 * @since : 6/12/24
 */
@RequiredArgsConstructor
@Service
public class VcMetaQueryService {
    private final VcMetadataRepository vcMetadataRepository;
    private final VcStatusHistoryRepository vcStatusHistoryRepository;

    public VcMetadata save(VcMetadata vcMetadata) {
        return vcMetadataRepository.save(vcMetadata);
    }

    public VcMetadata findByVcId(String vcId) {
        return vcMetadataRepository.findByVcId(vcId)
                .orElseThrow(IllegalArgumentException::new);
    }

    public VcStatusHistory save(VcStatusHistory vcStatusHistory) {
        return vcStatusHistoryRepository.save(vcStatusHistory);
    }
}
