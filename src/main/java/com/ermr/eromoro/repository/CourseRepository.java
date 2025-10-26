package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.CourseResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
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
      null 
    )
    from Course c
      join c.user u
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
      null
    )
    from Course c
      join c.user u
    where u.id = :userId
      and lower(c.title) like lower(concat('%', :keyword, '%'))
    order by c.createdAt desc
    """)
    List<CourseResponseDto.getCourseResultDto> findByUserWithLikeCourseAndTitleContainingOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );}
