package com.ermr.eromoro.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
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

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TourListApiResponse {
        private Response response;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private Body body;
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private Items items;
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            private List<Item> item = Collections.emptyList();;

            @JsonCreator
            public static Items create(JsonNode node) {
                if (node.isTextual() && node.asText().trim().isEmpty()) {
                    return new Items(); // 빈 리스트
                }
                Items result = new Items();
                JsonNode itemNode = node.get("item");
                if (itemNode != null && !itemNode.isNull()) {
                    ObjectMapper m = new ObjectMapper();
                    if (itemNode.isArray()) {
                        result.item = m.convertValue(itemNode, new TypeReference<List<Item>>() {});
                    } else {
                        result.item = List.of(m.convertValue(itemNode, Item.class));
                    }
                }
                return result;
            }
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String contentid;
            private String title;
            private String addr1;
            private String addr2;
            private String contenttypeid;
            private String cat2;
            private String firstimage;
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TourOverviewApiResponse {

        private Response response;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private Body body;
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private Items items;
//            private Integer numOfRows;
//            private Integer pageNo;
//            private Integer totalCount;
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            private List<Item> item;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String overview;
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TourBarrierFreeApiResponse {

        private Response response;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private Body body;
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private Items items;
//            private Integer numOfRows;
//            private Integer pageNo;
//            private Integer totalCount;
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            private List<Item> item = Collections.emptyList();;

            @JsonCreator
            public static Items create(JsonNode node) {
                if (node.isTextual() && node.asText().trim().isEmpty()) {
                    return new Items(); // 빈 리스트
                }
                Items result = new Items();
                JsonNode itemNode = node.get("item");
                if (itemNode != null && !itemNode.isNull()) {
                    ObjectMapper m = new ObjectMapper();
                    if (itemNode.isArray()) {
                        result.item = m.convertValue(itemNode, new TypeReference<List<Item>>() {});
                    } else {
                        result.item = List.of(m.convertValue(itemNode, Item.class));
                    }
                }
                return result;
            }
        }

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String elevator;
            private String exit;
            private String wheelchair;
            private String parking;
            private String route;
            private String restroom;
            private String stroller;
            private String lactationroom;
        }
    }
}
