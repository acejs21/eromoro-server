package com.ermr.eromoro.service;

import com.ermr.eromoro.dto.BarrierRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BarrierCommandServiceImpl implements BarrierCommandService {

    @Override
    public void collectAndSaveStepData(Long userId, List<MultipartFile> photos, List<BarrierRequestDto.StepDataPhotoMetaDataDto> request) {
        System.out.println("Collecting and saving step data for user: " + userId);
        System.out.println("Number of photos received: " + photos.size());
        for (BarrierRequestDto.StepDataPhotoMetaDataDto metaData : request) {
            System.out.println("Metadata Id: " + metaData.getPanoId());
            System.out.println("Latitude: " + metaData.getLatitude());
            System.out.println("Longitude: " + metaData.getLongitude());
        }
    }

}
