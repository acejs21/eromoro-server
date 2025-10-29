package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.CourseStatus;
import com.ermr.eromoro.dto.CourseResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
    select new com.ermr.eromoro.dto.CourseResponseDto$getCourseResultDto(
      c.id,
      c.title,
      c.likeCount,
      case when exists (
        select 1 from LikeCourse lc
         where lc.course = c
           and lc.user.id = :userId
      ) then true else false end,
      c.rampCount,
      c.stepCount,
      c.duration,
      c.distance,
      c.createdAt,
      null,
      null,
      case
        when s is null or s.imageUrl is null or s.imageUrl = '' then null
        else s.imageUrl 
      end
    )
    from Course c
      join c.user u
      left join c.spot s
    where u.id = :userId
    order by c.createdAt desc
    """)
    List<CourseResponseDto.getCourseResultDto> findByUserWithLikeCourseOrderByCreatedAtDesc( // 나중에 관광지 사진도 포함시켜야함.
            @Param("userId") Long userId
    );

    @Query("""
    select new com.ermr.eromoro.dto.CourseResponseDto$getCourseResultDto(
      c.id,
      c.title,
      c.likeCount,
      case when exists (
        select 1 from LikeCourse lc
         where lc.course = c
           and lc.user.id = :userId
      ) then true else false end,
      c.rampCount,
      c.stepCount,
      c.duration,
      c.distance,
      c.createdAt,
      null,
      null,
      case
        when s is null or s.imageUrl is null or s.imageUrl = '' then null
        else s.imageUrl 
      end
    )
    from Course c
      join c.user u
      left join c.spot s
    where u.id = :userId
      and lower(c.title) like lower(concat('%', :keyword, '%'))
    order by c.createdAt desc
    """)
    List<CourseResponseDto.getCourseResultDto> findByUserWithLikeCourseAndTitleContainingOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    int countByUser(User getUser);

    @Query("""
    select new com.ermr.eromoro.dto.CourseResponseDto$getCourseResultDto(
      c.id,
      c.title,
      c.likeCount,
      case when exists (
        select 1 from LikeCourse lc
         where lc.course = c
           and lc.user.id = :userId
      ) then true else false end,
      c.rampCount,
      c.stepCount,
      c.duration,
      c.distance,
      c.createdAt,
      c.rating,
      u.userType,
      null
    )
    from Course c
      left join c.user u
      join c.spot s
    where s.id = :spotId
    order by c.createdAt desc
    """)
    List<CourseResponseDto.getCourseResultDto> findBySpotWithLikeCourseOrderByCreatedAtDesc(Long userId, Long spotId);

    @Query("""
    select new com.ermr.eromoro.dto.CourseResponseDto$getCourseResultDto(
      c.id,
      c.title,
      c.likeCount,
      case when exists (
        select 1 from LikeCourse lc
         where lc.course = c
           and lc.user.id = :userId
      ) then true else false end,
      c.rampCount,
      c.stepCount,
      c.duration,
      c.distance,
      c.createdAt,
      c.rating,
      u.userType,
      null
    )
    from Course c
      left join c.user u
      join c.spot s
    where s.id = :spotId
    and lower(c.title) like lower(concat('%', :keyword, '%'))
    order by c.createdAt desc
    """)
    List<CourseResponseDto.getCourseResultDto> findBySpotWithLikeCourseAndTitleContainingOrderByCreatedAtDesc(Long userId, Long spotId, String keyword);

    /**
     * 특정 courseId를 제외하고, 주어진 userId에 속하며 PENDING 상태인 모든 Course를 삭제합니다.
     * @param status 삭제할 Course의 상태 (e.g., CourseStatus.PENDING)
     * @param courseId 삭제에서 제외할 Course의 ID
     * @param userId 삭제 대상 Course를 소유한 User의 ID
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Course c WHERE c.user.id = :userId AND c.courseStatus = :status AND c.id <> :courseId")
    void deletePendingCoursesByUserIdExceptOne(@Param("status") CourseStatus status, @Param("courseId") Long courseId, @Param("userId") Long userId);

    /**
     * 특정 사용자의 특정 상태를 가진 모든 Course를 삭제합니다.
     * @param courseStatus 삭제할 Course의 상태
     * @param userId Course를 소유한 User의 ID
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Course c WHERE c.user.id = :userId AND c.courseStatus = :status")
    void deletePendingCoursesByUserId(@Param("status") CourseStatus courseStatus,
                                      @Param("userId") Long userId);
}
