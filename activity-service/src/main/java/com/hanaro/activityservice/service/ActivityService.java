package com.hanaro.activityservice.service;

import com.hanaro.activityservice.domain.Activity;
import com.hanaro.activityservice.domain.ActivityApply;
import com.hanaro.activityservice.domain.ActivityCategory;
import com.hanaro.activityservice.domain.ActivityState;
import com.hanaro.activityservice.domain.ActivityType;
import com.hanaro.activityservice.domain.DayOfWeekType;
import com.hanaro.activityservice.domain.RecurrenceType;
import com.hanaro.activityservice.dto.request.ActivityRequest;
import com.hanaro.activityservice.dto.response.ActivityResponse;
import com.hanaro.activityservice.repository.ActivityApplyRepository;
import com.hanaro.activityservice.repository.ActivityRepository;
import com.hanaro.common.storage.StorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private static final DateTimeFormatter ONE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("M.d(E) a h:mm", Locale.KOREAN);

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN);

    private final ActivityRepository activityRepository;
    private final ActivityApplyRepository activityApplyRepository;
    private final StorageService storageService;

    private static final int MAX_IMAGE_COUNT = 3;

    public List<ActivityResponse.Summary> getActivities(
            Long userId,
            Long orgId,
            String category,
            String keyword
    ) {
        Long resolvedUserId = resolveUserId(userId);
        Long resolvedOrgId = resolveOrgId(orgId);

        List<Activity> activities = activityRepository.findAllByOrgIdOrderByCreatedAtDesc(resolvedOrgId);

        return activities.stream()
                .filter(activity -> matchesCategory(activity, category))
                .filter(activity -> matchesKeyword(activity, keyword))
                .map(activity -> toSummary(activity, resolvedUserId))
                .toList();
    }

    public ActivityResponse.Detail getActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findByActivityId(activityId)
                .orElseThrow(() -> new NoSuchElementException("활동을 찾을 수 없습니다."));

        return toDetail(activity, resolveUserId(userId));
    }

    public ActivityResponse.Detail createActivity(Long userId, Long orgId, ActivityRequest request, List<MultipartFile> files) {
        // 1. 트랜잭션 외부에서 파일 업로드 수행
        List<String> imageUrls = new ArrayList<>();
        if (files != null) {
            try {
                for (int i = 0; i < files.size() && i < MAX_IMAGE_COUNT; i++) {
                    imageUrls.add(storageService.upload(files.get(i), "activity"));
                }
            } catch (Exception e) {
                // [보상 조치] 업로드 루프 중 실패 시 이미 올라간 파일 삭제
                for (String imageUrl : imageUrls) {
                    try {
                        storageService.delete(imageUrl);
                    } catch (Exception ex) {
                        log.error("Failed to delete file during initial upload failure: {}, error: {}", imageUrl, ex.getMessage());
                    }
                }
                throw e; // 원본 예외 전파
            }
        }
        try {
            return createActivityInternal(userId, orgId, request, imageUrls);
        } catch (Exception e) {
            // [보상 조치] DB 저장 실패 시 S3에 업로드된 파일 삭제
            for (String imageUrl : imageUrls) {
                try {
                    storageService.delete(imageUrl);
                } catch (Exception ex) {
                    // 개별 삭제 실패 시 로그만 남기고 다음 파일 삭제 시도
                    log.error("Failed to delete file during cleanup: {}, error: {}", imageUrl, ex.getMessage());
                }
            }
            throw e; // 원본 예외 전파
        }
    }

    @Transactional
    public ActivityResponse.Detail createActivityInternal(Long userId, Long orgId, ActivityRequest request, List<String> imageUrls) {
        validateCreateRequest(request);

        Long resolvedUserId = resolveUserId(userId);
        Long resolvedOrgId = resolveOrgId(orgId);

        ScheduleInfo scheduleInfo = createScheduleInfo(request);

        Activity activity = Activity.builder()
                .orgId(resolvedOrgId)
                .creatorId(resolvedUserId)
                .activityCategory(parseCategory(request.getCategory()))
                .activityType(parseActivityType(request.getMeetingType()))
                .activityState(ActivityState.RECRUITING)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .location(request.getLocation().trim())
                .maxMembers(request.getMaxMembers())
                .startAt(scheduleInfo.startAt())
                .endAt(scheduleInfo.endAt())
                .recurrence(scheduleInfo.recurrence())
                .recurrenceDays(scheduleInfo.recurrenceDays())
                .recurrenceMonthDays(scheduleInfo.recurrenceMonthDays())
                .pointAmount(request.getPointAmount() != null ? request.getPointAmount() : 30)
                .build();

        // 이미지 연결
        for (int i = 0; i < imageUrls.size(); i++) {
            activity.addPhoto(imageUrls.get(i), i + 1);
        }

        Activity savedActivity = activityRepository.save(activity);

        return toDetail(savedActivity, resolvedUserId);
    }

    @Transactional
    public ActivityResponse.Detail applyActivity(Long activityId, Long userId) {
        Long resolvedUserId = resolveUserId(userId);

        Activity activity = activityRepository.findByActivityId(activityId)
                .orElseThrow(() -> new NoSuchElementException("활동을 찾을 수 없습니다."));

        if (Objects.equals(activity.getCreatorId(), resolvedUserId)) {
            throw new IllegalArgumentException("본인이 만든 활동에는 참여할 수 없습니다.");
        }

        if (activityApplyRepository.existsByActivity_ActivityIdAndUserId(activityId, resolvedUserId)) {
            throw new IllegalArgumentException("이미 참여한 활동입니다.");
        }

        if (!ActivityState.RECRUITING.equals(activity.getActivityState())) {
            throw new IllegalArgumentException("모집 중인 활동만 참여할 수 있습니다.");
        }

        int currentCount = getCurrentCount(activity);

        if (currentCount >= activity.getMaxMembers()) {
            throw new IllegalArgumentException("모집이 마감된 활동입니다.");
        }

        ActivityApply apply = ActivityApply.builder()
                .activity(activity)
                .userId(resolvedUserId)
                .pointEarned(activity.getPointAmount())
                .build();

        activity.getApplies().add(apply);
        activityApplyRepository.save(apply);

        if (getCurrentCount(activity) >= activity.getMaxMembers()) {
            activity.close();
        }

        return toDetail(activity, resolvedUserId);
    }

    private ActivityResponse.Summary toSummary(Activity activity, Long userId) {
        return ActivityResponse.Summary.builder()
                .id(activity.getActivityId())
                .category(activity.getActivityCategory().name())
                .title(activity.getTitle())
                .location(activity.getLocation())
                .schedule(formatSchedule(activity))
                .currentCount(getCurrentCount(activity))
                .maxCount(activity.getMaxMembers())
                .point(activity.getPointAmount())
                .isApplied(isApplied(activity, userId))
                .isOwner(Objects.equals(activity.getCreatorId(), userId))
                .status(resolveStatus(activity))
                .build();
    }

    private ActivityResponse.Detail toDetail(Activity activity, Long userId) {
        List<String> imageUrls = activity.getPhotos().stream()
                .sorted(Comparator.comparingInt(photo -> photo.getOrderNum()))
                .map(photo -> storageService.getPresignedUrl(photo.getPhotoUrl())) // 변환 로직 추가
                .toList();

        return ActivityResponse.Detail.builder()
                .id(activity.getActivityId())
                .category(activity.getActivityCategory().name())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .location(activity.getLocation())
                .schedule(formatSchedule(activity))
                .currentCount(getCurrentCount(activity))
                .maxCount(activity.getMaxMembers())
                .point(activity.getPointAmount())
                .isApplied(isApplied(activity, userId))
                .isOwner(Objects.equals(activity.getCreatorId(), userId))
                .status(resolveStatus(activity))
                .imageUrls(imageUrls)
                .members(toMembers(activity))
                .build();
    }

    private List<ActivityResponse.Member> toMembers(Activity activity) {
        List<ActivityResponse.Member> members = new ArrayList<>();

        members.add(
                ActivityResponse.Member.builder()
                        .id(activity.getCreatorId())
                        .name("모임장")
                        .initial("모")
                        .isLeader(true)
                        .build()
        );

        activity.getApplies().stream()
                .sorted(Comparator.comparing(ActivityApply::getApplyId))
                .forEach(apply -> {
                    String displayName = "참여자" + apply.getUserId();

                    members.add(
                            ActivityResponse.Member.builder()
                                    .id(apply.getUserId())
                                    .name(displayName)
                                    .initial(displayName.substring(0, 1))
                                    .isLeader(false)
                                    .build()
                    );
                });

        return members;
    }

    private boolean matchesCategory(Activity activity, String category) {
        if (!StringUtils.hasText(category) || "전체".equals(category)) {
            return true;
        }

        return activity.getActivityCategory().name().equals(category);
    }

    private boolean matchesKeyword(Activity activity, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }

        String normalizedKeyword = keyword.trim().toLowerCase();

        return activity.getTitle().toLowerCase().contains(normalizedKeyword)
                || activity.getLocation().toLowerCase().contains(normalizedKeyword)
                || formatSchedule(activity).toLowerCase().contains(normalizedKeyword)
                || activity.getActivityCategory().name().toLowerCase().contains(normalizedKeyword);
    }

    private String formatSchedule(Activity activity) {
        if (ActivityType.ONE_TIME.equals(activity.getActivityType())) {
            return activity.getStartAt().format(ONE_TIME_FORMATTER);
        }

        if (RecurrenceType.DAILY.equals(activity.getRecurrence())) {
            return "매일 " + activity.getStartAt().format(TIME_FORMATTER);
        }

        if (RecurrenceType.WEEKLY.equals(activity.getRecurrence())) {
            String weekdays = activity.getRecurrenceDays().stream()
                    .map(this::toKoreanWeekday)
                    .sorted()
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("");

            return "매주 " + weekdays + " " + activity.getStartAt().format(TIME_FORMATTER);
        }

        if (RecurrenceType.MONTHLY.equals(activity.getRecurrence())) {
            String monthDays = activity.getRecurrenceMonthDays().stream()
                    .sorted()
                    .map(day -> day + "일")
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("");

            return "매월 " + monthDays + " " + activity.getStartAt().format(TIME_FORMATTER);
        }

        return activity.getStartAt().format(ONE_TIME_FORMATTER);
    }

    private String toKoreanWeekday(DayOfWeekType dayOfWeekType) {
        return switch (dayOfWeekType) {
            case SUNDAY -> "일";
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
        };
    }

    private boolean isApplied(Activity activity, Long userId) {
        if (Objects.equals(activity.getCreatorId(), userId)) {
            return true;
        }

        return activity.getApplies().stream()
                .anyMatch(apply -> Objects.equals(apply.getUserId(), userId));
    }

    private int getCurrentCount(Activity activity) {
        return 1 + activity.getApplies().size();
    }

    private String resolveStatus(Activity activity) {
        if (!ActivityState.RECRUITING.equals(activity.getActivityState())) {
            return activity.getActivityState().name();
        }

        if (getCurrentCount(activity) >= activity.getMaxMembers()) {
            return ActivityState.CLOSED.name();
        }

        return ActivityState.RECRUITING.name();
    }

    private void validateCreateRequest(ActivityRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 값이 없습니다.");
        }

        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }

        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("설명글을 입력해주세요.");
        }

        if (!StringUtils.hasText(request.getLocation())) {
            throw new IllegalArgumentException("장소를 입력해주세요.");
        }

        if (request.getMaxMembers() < 2) {
            throw new IllegalArgumentException("최대 인원은 2명 이상이어야 합니다.");
        }

        if (!StringUtils.hasText(request.getCategory())) {
            throw new IllegalArgumentException("카테고리를 선택해주세요.");
        }

        if (!StringUtils.hasText(request.getMeetingType())) {
            throw new IllegalArgumentException("모임 유형을 선택해주세요.");
        }
    }

    private ScheduleInfo createScheduleInfo(ActivityRequest request) {
        if ("single".equals(request.getMeetingType())) {
            if (!StringUtils.hasText(request.getSingleDate()) || !StringUtils.hasText(request.getSingleTime())) {
                throw new IllegalArgumentException("일회성 일정 날짜와 시간을 입력해주세요.");
            }

            LocalDateTime startAt = LocalDateTime.of(
                    LocalDate.parse(request.getSingleDate()),
                    LocalTime.parse(request.getSingleTime())
            );

            return new ScheduleInfo(
                    startAt,
                    startAt.plusHours(2),
                    null,
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        if (!StringUtils.hasText(request.getRecurringStartDate())
                || !StringUtils.hasText(request.getRecurringEndDate())
                || !StringUtils.hasText(request.getRecurringTime())) {
            throw new IllegalArgumentException("정기 일정의 시작일, 종료일, 시간을 입력해주세요.");
        }

        LocalDateTime startAt = LocalDateTime.of(
                LocalDate.parse(request.getRecurringStartDate()),
                LocalTime.parse(request.getRecurringTime())
        );

        LocalDateTime endAt = LocalDateTime.of(
                LocalDate.parse(request.getRecurringEndDate()),
                LocalTime.parse(request.getRecurringTime())
        );

        if (endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        RecurrenceType recurrenceType = parseRecurrenceType(request.getRecurringType());

        List<DayOfWeekType> recurrenceDays = new ArrayList<>();
        List<Integer> recurrenceMonthDays = new ArrayList<>();

        if (RecurrenceType.WEEKLY.equals(recurrenceType)) {
            recurrenceDays = safeList(request.getRecurringWeekdays()).stream()
                    .map(this::parseWeekday)
                    .distinct()
                    .toList();

            if (recurrenceDays.isEmpty()) {
                throw new IllegalArgumentException("요일 선택값이 없습니다.");
            }
        }

        if (RecurrenceType.MONTHLY.equals(recurrenceType)) {
            recurrenceMonthDays = safeList(request.getRecurringMonthDays()).stream()
                    .filter(day -> day >= 1 && day <= 31)
                    .distinct()
                    .sorted()
                    .toList();

            if (recurrenceMonthDays.isEmpty()) {
                throw new IllegalArgumentException("매월 일자 선택값이 없습니다.");
            }
        }

        return new ScheduleInfo(
                startAt,
                endAt,
                recurrenceType,
                recurrenceDays,
                recurrenceMonthDays
        );
    }

    private ActivityCategory parseCategory(String category) {
        try {
            return ActivityCategory.valueOf(category);
        } catch (Exception exception) {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
    }

    private ActivityType parseActivityType(String meetingType) {
        if ("single".equals(meetingType)) {
            return ActivityType.ONE_TIME;
        }

        if ("recurring".equals(meetingType)) {
            return ActivityType.RECURRING;
        }

        throw new IllegalArgumentException("유효하지 않은 모임 유형입니다.");
    }

    private RecurrenceType parseRecurrenceType(String recurringType) {
        if ("daily".equals(recurringType)) {
            return RecurrenceType.DAILY;
        }

        if ("weekday".equals(recurringType)) {
            return RecurrenceType.WEEKLY;
        }

        if ("monthly".equals(recurringType)) {
            return RecurrenceType.MONTHLY;
        }

        throw new IllegalArgumentException("유효하지 않은 정기 유형입니다.");
    }

    private DayOfWeekType parseWeekday(String weekday) {
        return switch (weekday) {
            case "일" -> DayOfWeekType.SUNDAY;
            case "월" -> DayOfWeekType.MONDAY;
            case "화" -> DayOfWeekType.TUESDAY;
            case "수" -> DayOfWeekType.WEDNESDAY;
            case "목" -> DayOfWeekType.THURSDAY;
            case "금" -> DayOfWeekType.FRIDAY;
            case "토" -> DayOfWeekType.SATURDAY;
            default -> throw new IllegalArgumentException("유효하지 않은 요일입니다.");
        };
    }

    private Long resolveUserId(Long userId) {
        return userId != null ? userId : 1L;
    }

    private Long resolveOrgId(Long orgId) {
        return orgId != null ? orgId : 1L;
    }

    private <T> List<T> safeList(List<T> source) {
        return source != null ? source : new ArrayList<>();
    }

    private record ScheduleInfo(
            LocalDateTime startAt,
            LocalDateTime endAt,
            RecurrenceType recurrence,
            List<DayOfWeekType> recurrenceDays,
            List<Integer> recurrenceMonthDays
    ) {
    }
}