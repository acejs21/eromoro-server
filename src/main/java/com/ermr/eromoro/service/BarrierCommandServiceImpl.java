package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Barrier;
import com.ermr.eromoro.domain.enums.BarrierType;
import com.ermr.eromoro.dto.BarrierRequestDto;
import com.ermr.eromoro.dto.StepAnalyzeFastApiResponseDto;
import com.ermr.eromoro.location.GeoHelper;
import com.ermr.eromoro.repository.BarrierRepository;
import com.ermr.eromoro.util.NcpObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarrierCommandServiceImpl implements BarrierCommandService {

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;
    private final WebClient.Builder webClientBuilder;
    private final BarrierRepository barrierRepository;
    private final NcpObjectStorageClient storage;
    private final GeoHelper geoHelper;

    @Override
    public void collectAndSaveStepData(Long userId, List<MultipartFile> photos, List<BarrierRequestDto.StepDataPhotoMetaDataDto> request) {
//        System.out.println("Collecting and saving step data for user: " + userId);
//        System.out.println("Number of photos received: " + photos.size());
//        for (BarrierRequestDto.StepDataPhotoMetaDataDto metaData : request) {
//            System.out.println("Metadata Id: " + metaData.getPanoId());
//            System.out.println("Latitude: " + metaData.getLatitude());
//            System.out.println("Longitude: " + metaData.getLongitude());
//        }

        if (photos == null || request == null || photos.isEmpty() || request.isEmpty()) {
            System.out.println("No photos or metadata provided. Skipping.");
            return;
        }
        if (photos.size() != request.size()) {
            throw new IllegalArgumentException("photos 와 metadata 개수가 일치해야 합니다.");
        }

        // 1) id CSV 생성 (FastAPI는 id_list의 첫 원소를 CSV로 파싱함)
        List<String> idOrder = request.stream()
                .map(BarrierRequestDto.StepDataPhotoMetaDataDto::getPanoId)
                .toList();
        String idsCsv = String.join(",", idOrder);

        // 2) 멀티파트 빌드 (files + id_list)
        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        for (int i = 0; i < photos.size(); i++) {
            MultipartFile mf = photos.get(i);
            String filename = Optional.ofNullable(mf.getOriginalFilename())
                    .filter(StringUtils::hasText)
                    .orElse(idOrder.get(i) + ".jpg");

            mb.part("files", mf.getResource())
                    .filename(filename)
                    .contentType(parseOrDefault(mf.getContentType()));
        }
        mb.part("id_list", idsCsv) // FastAPI 파라미터명 정확히 일치
                .contentType(MediaType.TEXT_PLAIN);

        // ID -> 파일 매핑 (업로드용)
        Map<String, MultipartFile> fileById = new LinkedHashMap<>();
        for (int i = 0; i < idOrder.size(); i++) fileById.put(idOrder.get(i), photos.get(i));

        // 3) FastAPI 호출
        WebClient webClient = webClientBuilder
                .baseUrl(fastApiBaseUrl)
                .build();
        StepAnalyzeFastApiResponseDto.AnalyzeResponse resp = webClient.post()
                .uri("/analyze/step")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(mb.build())
                .retrieve()
                .bodyToMono(StepAnalyzeFastApiResponseDto.AnalyzeResponse.class)
                .block();

        List<String> stairIds = Optional.ofNullable(resp)
                .map(StepAnalyzeFastApiResponseDto.AnalyzeResponse::getStairIds)
                .orElseGet(List::of);

        System.out.println("FastAPI detected stair_ids: " + stairIds);

        if (stairIds.isEmpty()) {
            System.out.println("No stairs detected. Nothing to save.");
            return;
        }

        // 4) id -> metadata 매핑 (빠른 조회용)
        Map<String, BarrierRequestDto.StepDataPhotoMetaDataDto> metaById  = request.stream()
                .collect(Collectors.toMap(BarrierRequestDto.StepDataPhotoMetaDataDto::getPanoId, m -> m, (a, b) -> a, LinkedHashMap::new));

        // 5) stair_ids 에 해당하는 것만 Barrier(STEP)로 저장
        List<Barrier> toSave = new ArrayList<>();
        for (String id : stairIds) {
            var meta = metaById.get(id);
            var file = fileById.get(id);
            if (meta == null || file == null) continue;

            // 계단 이미지 저장
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            String ext = extractExt(file.getOriginalFilename());
            String key = buildKey(ext); // STEP 용 별도 prefix

            String imageUrl;
            try {
                storage.putObject(key, file.getInputStream(), file.getSize(), contentType);
                imageUrl = storage.publicUrl(key);
            } catch (IOException e) {
                // 업로드 실패 시 해당 건만 스킵 (전체 실패 방지)
                System.out.println("Upload failed for id=" + id + " : " + e.getMessage());
                continue;
            }

            Barrier barrier = Barrier.builder()
                    .course(null) // 필요 시 코스 연결하세요
                    .latitude(meta.getLatitude())
                    .longitude(meta.getLongitude())
                    .location(geoHelper.toPoint(meta.getLongitude().doubleValue(), meta.getLatitude().doubleValue()))
                    .barrierType(BarrierType.STEP)
                    .image(imageUrl) // 필요 시 파일명/경로 저장
                    .build();
            toSave.add(barrier);
        }

        if (!toSave.isEmpty()) {
            barrierRepository.saveAll(toSave);
            System.out.println("Saved STEP barriers: " + toSave.size());
        }
    }

    private static MediaType parseOrDefault(String contentType) {
        try {
            return (contentType != null) ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

//    private static BigDecimal scale6(double v) {
//        return BigDecimal.valueOf(v).setScale(6, RoundingMode.HALF_UP);
//    }

    // 이미지 저장 util 메소드
    private String extractExt(String name) {
        if (name == null) return "bin";
        int dot = name.lastIndexOf('.');
        return (dot > -1 && dot < name.length() - 1) ? name.substring(dot + 1) : "bin";
    }

    private String buildKey(String ext) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "photos/" + "step" + "/" + date + "/" + System.nanoTime() + "." + ext;
    }
}
