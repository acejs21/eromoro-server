package com.ermr.eromoro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

public class TourApiResponseDto {

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AreaCodeApiResponse {
        private Response response;

        @Getter @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private Body body;
        }

        @Getter @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private Items items;
        }

        @Getter @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            private List<Item> item;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private int code;   // "1", "31" 등 → 숫자로 쓰려면 Integer로
            private String name;   // "서울", "경기도" 등
        }
    }
}
