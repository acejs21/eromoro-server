package com.ermr.eromoro.apiResponse.exception.handler;

import com.ermr.eromoro.apiResponse.BaseErrorCode;
import com.ermr.eromoro.apiResponse.exception.GeneralException;

public class CourseHandler extends GeneralException {
    public CourseHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
