package bobmukjaku.bobmukjakuDemo.domain.chatroom.service;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChatRoomServiceTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Test
    public void 필터링_추가_성공() throws Exception {
        // given
        ChatRoomCreateDto chatRoomCreateDto1 = new ChatRoomCreateDto("모집방1", "2023-08-07", "17:30", "19:30", "한식", 2);
        ChatRoomCreateDto chatRoomCreateDto2 = new ChatRoomCreateDto("모집방2", "2023-08-07", "17:30", "19:30", "한식", 3);
        ChatRoomCreateDto chatRoomCreateDto3 = new ChatRoomCreateDto("모집방3", "2023-08-07", "17:30", "19:30", "일식", 4);
        ChatRoomCreateDto chatRoomCreateDto4 = new ChatRoomCreateDto("모집방4", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto5 = new ChatRoomCreateDto("모집방5", "2023-08-08", "17:30", "19:30", "중식", 4);
        ChatRoomCreateDto chatRoomCreateDto6 = new ChatRoomCreateDto("모집방6", "2023-08-08", "17:30", "19:30", "일식", 3);
        ChatRoom chatRoom1 = chatRoomCreateDto1.toEntity();
        ChatRoom chatRoom2 = chatRoomCreateDto2.toEntity();
        ChatRoom chatRoom3 = chatRoomCreateDto3.toEntity();
        ChatRoom chatRoom4 = chatRoomCreateDto4.toEntity();
        ChatRoom chatRoom5 = chatRoomCreateDto5.toEntity();
        ChatRoom chatRoom6 = chatRoomCreateDto6.toEntity();
        List<ChatRoom> initial = Arrays.asList(chatRoom1, chatRoom2, chatRoom3, chatRoom4, chatRoom5, chatRoom6);
        chatRoomRepository.saveAll(initial);

        // when
        Specification<ChatRoom> specification1 = ChatRoomSpecification.addAllFilters(null, "filterByTotal", "4");
        List<ChatRoom> firstChatRooms = chatRoomRepository.findAll(specification1); // 정원으로 필터링한 chatRooms

        Specification<ChatRoom> specification2 = ChatRoomSpecification.addAllFilters(firstChatRooms, "filterByFood", "중식");
        List<ChatRoom> secondChatRooms = chatRoomRepository.findAll(specification2);

        Specification<ChatRoom> specification3 = ChatRoomSpecification.addAllFilters(secondChatRooms, "filterByRoomName", "모집방89");
        List<ChatRoom> thirdChatRooms = chatRoomRepository.findAll(specification3);

        Specification<ChatRoom> specification4 = ChatRoomSpecification.addAllFilters(null, "filterByAvailable", null);
        List<ChatRoom> fourthChatRooms = chatRoomRepository.findAll(specification4);

        Specification<ChatRoom> specification5 = ChatRoomSpecification.addAllFilters(fourthChatRooms, "filterByDate", "2023-08-08");
        List<ChatRoom> fifthChatRooms = chatRoomRepository.findAll(specification5);

        // then
        System.out.println("<정원 수로 필터링한 결과>");
        firstChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        System.out.println("<음식 종류로 2차 필터링한 결과>");
        secondChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        System.out.println("<모집방 이름으로 3차 필터링한 결과>");
        thirdChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);

        System.out.println("<참여 가능 여부로 필터링한 결과>");
        fourthChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        System.out.println("<날짜로 2차 필터링한 결과>");
        fifthChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);

    }
}
