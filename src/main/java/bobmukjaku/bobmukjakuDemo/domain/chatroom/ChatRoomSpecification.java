package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChatRoomSpecification {

    /*
    * 필터링 조건을 설정하고, 동적 쿼리를 생성
    * */

    /*public static Specification<Person> equalFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), firstName);
    }*/

    // 검색어로 필터링 (검색어를 포함하면 반환)
    public static Specification<ChatRoom> containChatRoomName(String roomName){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("roomName"), "%" + roomName + "%"));
    }

    // 모임 날짜로 필터링
    public static Specification<ChatRoom> equalMeetingDate(LocalDate date){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("meetingDate"), date));
    }

    // 모집 인원이 남아 있는 방 필터링 (현재 인원 < 총 정원)
    public static Specification<ChatRoom> lessThanTotal(Integer total){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("currentNum"), total));
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
    /*public static Specification<ChatRoom> withFilters(String keyword, String kindOfFood, LocalDate meetingDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(root.get("roomName"), "%" + keyword + "%"),
                        cb.like(root.get("description"), "%" + keyword + "%")
                ));
            }

            if (kindOfFood != null && !kindOfFood.isEmpty()) {
                predicates.add(cb.equal(root.get("kindOfFood"), kindOfFood));
            }

            if (meetingDate != null) {
                predicates.add(cb.equal(root.get("meetingDate"), meetingDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }*/

}
