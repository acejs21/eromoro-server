package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeCourseRepository extends JpaRepository<LikeCourse, Long> {
    List<LikeCourse> findByUser(User user);
    LikeCourse findByUserAndCourse(User user, Course course);
}
