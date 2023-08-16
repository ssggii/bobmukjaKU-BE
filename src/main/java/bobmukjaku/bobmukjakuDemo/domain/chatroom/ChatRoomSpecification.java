package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import com.fasterxml.jackson.core.io.CharTypes;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
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
    *
    *  2. 다중 조건 필터링
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

    // 시간표 데이터로 필터링
    /*public static Specification<ChatRoom> containTimeBlock() {
        return (root, query, criteriaBuilder) -> {

        };
    }*/

    // FilterInfo 객체로부터 Specification 생성
    public static Specification<ChatRoom> createSpecification(FilterInfo filter) {
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
