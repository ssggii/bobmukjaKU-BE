package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomSpecification {

    /*
    * 필터링 조건에 맞춰 동적 쿼리를 생성
    * 1. 단일 조건 필터링
    * - 최신 순으로 정렬
    * - 오래된 순으로 정렬
    * - 모집방 이름 검색
    * - 모임 날짜로 검색
    * - 참여 가능 여부로 검색
    * - 음식 종류로 검색
    * - 정원 수로 검색
    * - 시간표 데이터로 검색
    * - 친구가 참여 중인 모집방 검색
    * - 차단한 사용자가 참여 중인 모집방 검색
    * - 모임 종료 시간이 현재 시간보다 이전인 모집방 검색
    *
    * 2. 다중 조건 필터링
    * - 필터링 (최종)
    * */

    // 최신 순으로 정렬
    public static Specification<ChatRoom> orderByCreatedAtDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.conjunction();
        };
    }

    // 오래된 순으로 정렬
    public static Specification<ChatRoom> orderByCreatedAtAsc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
            return criteriaBuilder.conjunction();
        };
    }

    // 모임 날짜로 필터링
    public static Specification<ChatRoom> equalMeetingDate(LocalDate date){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("meetingDate"), date));
    }

    // 참여 가능 여부로 필터링 (현재 인원 < 총 정원)
    public static Specification<ChatRoom> lessThanTotal(){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("currentNum"), root.get("total")));
    }

    // 음식 종류로 필터링
    public static Specification<ChatRoom> equalKindOfFood(String kindOfFood) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("kindOfFood"), kindOfFood));
    }

    // 정원 수로 필터링
    public static Specification<ChatRoom> equalTotal(Integer total) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("total"), total));
    }

    // 요일 필터링
    public static Specification<ChatRoom> equalDayOfWeek(Integer dayOfWeek) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.function("weekday", Integer.class, root.get("meetingDate")), dayOfWeek-1
        ));
    }

    // 시간 필터링
    public static Specification<ChatRoom> betweenTime(LocalTime blockStartTime) {
        // ChatRoom의 startTime 값이 inputTime ~ inputTime+30분 사이에 있으면 해당 chatroom 반환하는 조건
        return (root, query, criteriaBuilder) -> {
            LocalTime blockEndTime = blockStartTime.plusMinutes(30);
            return criteriaBuilder.between(root.get("startTime"), blockStartTime, blockEndTime);
        };
    }

    public static Specification<ChatRoom> filteredByDayOfWeekAndTime(Integer dayOfWeek, LocalTime blockStartTime) {
        return (root, query, criteriaBuilder) -> {
            LocalTime blockEndTime = blockStartTime.plusMinutes(30);

            // 요일 필터링 조건
            Specification<ChatRoom> dayOfWeekSpec = equalDayOfWeek(dayOfWeek);
            // 시간 필터링 조건
            Specification<ChatRoom> timeSpec = betweenTime(blockStartTime);

            // AND 연산으로 조건 합치기
            return dayOfWeekSpec.and(timeSpec).toPredicate(root, query, criteriaBuilder);
        };
    }

    // TimeBlock으로 필터링
    public static Specification<ChatRoom> filteredByTimeBlock(TimeBlock timeBlock) {
        Integer dayOfWeek = timeBlock.getDayOfWeek();
        LocalTime blockStartTime = timeBlock.getTime();

        Specification<ChatRoom> filteredSpec = filteredByDayOfWeekAndTime(dayOfWeek, blockStartTime);
        Specification<ChatRoom> convertFilteredSpec = Specification.not(filteredSpec);

        return convertFilteredSpec;
    }

    // 시간표로 필터링
    public static Specification<ChatRoom> filteredByTimeTable(MemberRepository memberRepository, Long uid) {
        Member member = memberRepository.findById(uid).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<TimeBlock> timeBlockList = member.getTimeBlockList();
        Specification<ChatRoom> specification = Specification.where(null);

        for(TimeBlock timeBlock : timeBlockList){
            specification = specification.and(filteredByTimeBlock(timeBlock));
        }

        return specification;
    }

    // 사용자의 uid로 참여 중인 모집방 필터링
    public static Specification<ChatRoom> filteredByParticipantUid(Long uid) {
        return (root, query, criteriaBuilder) -> {
            root.join("participants"); // ChatRoom 엔티티와 MemberChatRoom 엔티티 조인
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("participants").get("joiner").get("uid"), uid)); // 참여자 uid와 일치하는 ChatRoom 필터링
            Predicate[] predicateArray = predicates.toArray(predicates.toArray(new Predicate[0])); // OR 조건으로 Predicate 결합
            return criteriaBuilder.or(predicateArray);
        };
    }

    // 친구가 참여 중인 모집방 필터링
    public static Specification<ChatRoom> filteredByFriend(MemberRepository memberRepository, Long uid) {
        Member member = memberRepository.findById(uid).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<Long> friendUidList = member.getFriendList().stream()
                .filter(friend -> friend.getIsBlock().equals(false)) // 사용자의 친구 uid 추출
                .map(Friend::getFriendUid).toList();

        Specification<ChatRoom> specification = Specification.where(null);
        for(Long friendUid : friendUidList){
            specification = specification.or(filteredByParticipantUid(friendUid));
        }
        return specification;
    }

    // 차단한 사용자가 참여 중인 모집방 필터링
    public static Specification<ChatRoom> filteredByBlock(MemberRepository memberRepository, Long uid) {
        Member member = memberRepository.findById(uid).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<Long> blockUidList = member.getFriendList().stream()
                .filter(friend -> friend.getIsBlock().equals(true)) // 사용자가 차단한 사용자 uid 추출
                .map(Friend::getFriendUid).toList();

        Specification<ChatRoom> specification = Specification.where(null);
        for(Long blockUid : blockUidList){
            specification = specification.or(filteredByParticipantUid(blockUid));
        }
        return specification;
    }

    // 모임 종료 시간이 현재 시간보다 이전인 모집방 검색
    public static Specification<ChatRoom> getExpiredChatRooms() {
        LocalDate currentDate = LocalDate.now(); // 현재 날짜
        LocalTime currentTime = LocalTime.now(); // 현재 시간
        return (root, query, criteriaBuilder) -> {
            root.fetch("meetingDate");
            root.fetch("endTime");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(
                            root.get("meetingDate").as(LocalDate.class),
                            currentDate
                    ),
                    criteriaBuilder.lessThan(
                            root.get("endTime").as(LocalTime.class),
                            currentTime
                    )
            );
        };
    }

    // FilterInfo 객체로부터 Specification 생성
    public static Specification<ChatRoom> createSpecification(FilterInfo filter, MemberRepository memberRepository) {
        String filterType = filter.getFilterType();
        String filterValue = filter.getFilterValue();
        Specification<ChatRoom> specification = null;

        switch (filterType) {
            case "latest":
                specification = ChatRoomSpecification.orderByCreatedAtDesc();
                break;
            case "oldest":
                specification = ChatRoomSpecification.orderByCreatedAtAsc();
                break;
            case "meetingDate":
                specification = ChatRoomSpecification.equalMeetingDate(LocalDate.parse(filterValue));
                break;
            case "kindOfFood":
                specification = ChatRoomSpecification.equalKindOfFood(filterValue);
                break;
            case "total":
                specification = ChatRoomSpecification.equalTotal(Integer.parseInt(filterValue));
                break;
            case "timeTable":
                specification = ChatRoomSpecification.filteredByTimeTable(memberRepository, Long.valueOf(filterValue));
                break;
            case "friend":
                specification = ChatRoomSpecification.filteredByFriend(memberRepository, Long.valueOf(filterValue));
                break;
            case "block":
                specification = ChatRoomSpecification.filteredByBlock(memberRepository, Long.valueOf(filterValue));
                break;
            default:
                break; // 유효한 필터 타입이 아닐 경우 null 반환
        }
        return specification;
    }

    // Specification 조합
    public static Specification<ChatRoom> combineSpecifications(List<Specification<ChatRoom>> specifications) {
        specifications.add(0, lessThanTotal()); // 디폴트 필터링을 참여 가능 여부로 설정
        return specifications.stream().reduce(Specification::and).orElse(null); // 필터 조건 AND로 조합하여 반환
    }

}
