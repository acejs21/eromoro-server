package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
