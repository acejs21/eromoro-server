package com.ermr.eromoro.apiResponse.exception.handler;

import com.ermr.eromoro.apiResponse.BaseErrorCode;
import com.ermr.eromoro.apiResponse.exception.GeneralException;

public class FeedbackHandler extends GeneralException {

    public FeedbackHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
