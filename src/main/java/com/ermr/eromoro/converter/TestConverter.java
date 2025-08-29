package com.ermr.eromoro.converter;

import com.ermr.eromoro.dto.TestResponse;

public class TestConverter {

    public static TestResponse.TempTestDTO toTempTestDTO() {
        return TestResponse.TempTestDTO.builder()
                .testString("This is test")
                .build();
    }
}
