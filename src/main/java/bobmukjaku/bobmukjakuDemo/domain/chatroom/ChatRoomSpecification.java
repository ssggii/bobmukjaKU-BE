package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

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
    * - 필터링 추가
    * - 필터링 해제
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

    // FilterInfo 객체로부터 Specification 생성
    public static Specification<ChatRoom> createSpecification(FilterInfo filter) {
        String filterType = filter.getFilterType();
        String filterValue = filter.getFilterValue();
        Specification<ChatRoom> specification = null;

        switch (filterType) {
            case "chatRoomName":
                specification = ChatRoomSpecification.containChatRoomName(filterValue);
                break;
            case "meetingDate":
                specification = ChatRoomSpecification.equalMeetingDate(LocalDate.parse(filterValue));
                break;
            case "available":
                specification = ChatRoomSpecification.lessThanTotal();
                break;
            case "kindOfFood":
                specification = ChatRoomSpecification.equalKindOfFood(filterValue);
                break;
            case "total":
                specification = ChatRoomSpecification.equalTotal(Integer.parseInt(filterValue));
                break;
            default:
                break; // 유효한 필터 타입이 아닐 경우 null 반환
        }
        return specification;
    }

    // Specification 조합
    public static Specification<ChatRoom> combineSpecifications(List<Specification<ChatRoom>> specifications) {
        return specifications.stream().reduce(Specification::and).orElse(null); // 필터 조건 AND로 조합하여 반환
    }

}
