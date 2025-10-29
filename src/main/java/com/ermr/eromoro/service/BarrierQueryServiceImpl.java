package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.domain.Barrier;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.BarrierRequestDto;
import com.ermr.eromoro.repository.BarrierRepository;
import com.ermr.eromoro.repository.FeedbackRepository;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarrierQueryServiceImpl implements BarrierQueryService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final BarrierRepository barrierRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public void checkAuthorization(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<Barrier> getAllBarriers() {
        return barrierRepository.findAll();
    }

    @Override
    public List<Feedback> getPagingFeedbacks(BarrierRequestDto.GetPagingBarrierDto request) {
        Coordinate[] coordinates = getCoordinatesFromPoints(request);
        Polygon boundingBox = geometryFactory.createPolygon(coordinates);
        boundingBox.setSRID(4326);
        return feedbackRepository.findByLocationWithin(boundingBox);
    }

    @Override
    public List<Barrier> getPagingBarriers(BarrierRequestDto.GetPagingBarrierDto request) {
        Coordinate[] coordinates = getCoordinatesFromPoints(request);
        Polygon boundingBox = geometryFactory.createPolygon(coordinates);
        boundingBox.setSRID(4326);
        return barrierRepository.findByLocationWithin(boundingBox);
    }

    private Coordinate[] getCoordinatesFromPoints(BarrierRequestDto.GetPagingBarrierDto request) {
        Coordinate[] coordinates = new Coordinate[]{
//                new Coordinate(request.getSw().getLongitude().doubleValue(), request.getSw().getLatitude().doubleValue()), // 좌측 하단
//                new Coordinate(request.getSe().getLongitude().doubleValue(), request.getSe().getLatitude().doubleValue()), // 우측 하단
//                new Coordinate(request.getNe().getLongitude().doubleValue(), request.getNe().getLatitude().doubleValue()), // 우측 상단
//                new Coordinate(request.getSw().getLongitude().doubleValue(), request.getNw().getLatitude().doubleValue()), // 좌측 상단
//                new Coordinate(request.getSw().getLongitude().doubleValue(), request.getSw().getLatitude().doubleValue()), // 좌측 하단
                new Coordinate(request.getSw().getLongitude().doubleValue(), request.getSw().getLatitude().doubleValue()), // 좌측 하단
                new Coordinate(request.getSe().getLongitude().doubleValue(), request.getSe().getLatitude().doubleValue()), // 우측 하단
                new Coordinate(request.getNe().getLongitude().doubleValue(), request.getNe().getLatitude().doubleValue()), // 우측 상단
                new Coordinate(request.getNw().getLongitude().doubleValue(), request.getNw().getLatitude().doubleValue()), // 좌측 상단
                new Coordinate(request.getSw().getLongitude().doubleValue(), request.getSw().getLatitude().doubleValue()), // 좌측 하단
        };

        return coordinates;
    }

}
