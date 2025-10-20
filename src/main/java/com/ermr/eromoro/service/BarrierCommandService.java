package com.ermr.eromoro.service;

import com.ermr.eromoro.dto.BarrierRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BarrierCommandService {

    void collectAndSaveStepData(Long userId, List<MultipartFile> photos, List<BarrierRequestDto.StepDataPhotoMetaDataDto> request);
}
