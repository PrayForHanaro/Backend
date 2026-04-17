package com.hanaro.activityservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRequest {

    private String category;
    private String meetingType;
    private String recurringType;

    private String title;
    private String description;
    private String location;

    private int maxMembers;
    private Integer pointAmount;

    private String singleDate;
    private String singleTime;

    private String recurringStartDate;
    private String recurringEndDate;
    private String recurringTime;

    @Builder.Default
    private List<String> recurringWeekdays = new ArrayList<>();

    @Builder.Default
    private List<Integer> recurringMonthDays = new ArrayList<>();

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
}