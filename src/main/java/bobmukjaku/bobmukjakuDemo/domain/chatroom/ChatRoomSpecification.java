package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatRoomSpecification {

    /*
    * 필터링 조건에 맞춰 동적 쿼리를 생성
    * 1. 단일 조건 필터링
    * - 모집방 이름 검색
    * - 모임 날짜로 검색
    * - 참여 가능 여부로 검색
    * - 음식 종류로 검색
    * - 정원 수로 검색
    * - 최근 순으로 정렬
    * - 마감 임박 순으로 정렬
    * - 모임 시간으로 검색
    * - 시간표 데이터로 검색
    *
    *  2. 다중 조건 필터링
    *
    * */

    // 검색어로 필터링 (검색어를 포함하면 반환)
    public static Specification<ChatRoom> containChatRoomName(String roomName){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("roomName"), "%" + roomName + "%"));
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

    // 다중 조건 검색
    public static Specification<ChatRoom> allFilters(List<ChatRoom> filteredChatRooms, String nextFilter, String input) {

        Specification<ChatRoom> filter = null;

        if (nextFilter != null) {
            // 적용할 필터 결정
            switch (nextFilter) {
                case "filterByRoomName":
                    filter = containChatRoomName(input);
                    break;
                case "filterByDate":
                    filter = equalMeetingDate(LocalDate.parse(input));
                    break;
                case "filterByAvailable":
                    filter = lessThanTotal();
                    break;
                case "filterByFood":
                    filter = equalKindOfFood(input);
                    break;
                case "filterByTotal":
                    filter = equalTotal(Integer.valueOf(input));
                    break;
                default:
                    filter = null;
                    break;
            }

            if (filteredChatRooms != null && !filteredChatRooms.isEmpty()) { // 이전 필터링 결과를 받는 경우
                Specification<ChatRoom> finalFilter = filter;
                return (root, query, criteriaBuilder) -> {
                    List<Long> filteredChatRoomIds = filteredChatRooms.stream()
                            .map(ChatRoom::getChatRoomId)
                            .collect(Collectors.toList());

                    return criteriaBuilder.and(
                            criteriaBuilder.in(root.get("chatRoomId")).value(filteredChatRoomIds),
                            finalFilter.toPredicate(root, query, criteriaBuilder)
                    );
                };
            }
        } else {
            System.out.println("nextFilter 또는 input 인자가 없습니다.");
        }
        return filter;
    }

}
