package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Barrier;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.FeedbackResponseDto;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
    select new com.ermr.eromoro.dto.FeedbackResponseDto$MyFeedbackDto(
        f.id,
        f.photoUrl,
        f.title,
        f.address,
        f.type,
        f.createdAt,
        f.likeCount,
        f.dislikeCount,
        f.isReport,
        f.accepted,
        case when exists (
            select 1 from LikeFeedback lf
            where lf.feedback = f
            and lf.user.id = :userId
        ) then true else false end
    )
    from Feedback f
    where f.user.id = :userId
    order by f.createdAt desc
    """)
    List<FeedbackResponseDto.MyFeedbackDto> findAllByUserIdWithLikeFeedback(@Param("userId") Long userId);

    @Query("SELECT f FROM Feedback f WHERE within(f.location, :expandedBoundingBox) = true")
    List<Feedback> findByLocationWithin(Polygon expandedBoundingBox);
}
