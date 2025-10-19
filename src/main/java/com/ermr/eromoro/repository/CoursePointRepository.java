package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.CoursePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePointRepository extends JpaRepository<CoursePoint, Long> {
}
