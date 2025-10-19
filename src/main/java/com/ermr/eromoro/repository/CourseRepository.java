package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserOrderByCreatedAtDesc(User user);
    List<Course> findByUserAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(User user, String title);
}
