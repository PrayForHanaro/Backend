package com.hanaro.activityservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ActivityResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Summary {
        private Long id;
        private String category;
        private String title;
        private String location;
        private String schedule;
        private int currentCount;
        private int maxCount;
        private int point;
        private boolean isApplied;
        private boolean isOwner;
        private String status;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Member {
        private Long id;
        private String name;
        private String initial;
        private boolean isLeader;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private Long id;
        private String category;
        private String title;
        private String description;
        private String location;
        private String schedule;
        private int currentCount;
        private int maxCount;
        private int point;
        private boolean isApplied;
        private boolean isOwner;
        private String status;
        private List<String> imageUrls;
        private List<Member> members;
    }
}