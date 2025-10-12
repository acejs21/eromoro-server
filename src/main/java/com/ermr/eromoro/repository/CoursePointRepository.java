package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.CoursePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePointRepository extends JpaRepository<CoursePoint, Long> {
    List<CoursePoint> findByCourseIdOrderBySeqAsc(Long courseId);
}
