package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.User;

public class LikeCourseConverter {

    public static LikeCourse toLikeCourse(User user, Course course) {
        return LikeCourse.builder()
                .user(user)
                .course(course)
                .build();
    }
}
