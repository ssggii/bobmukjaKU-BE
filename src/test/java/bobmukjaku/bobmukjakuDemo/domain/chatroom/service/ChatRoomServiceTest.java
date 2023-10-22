package bobmukjaku.bobmukjakuDemo.domain.chatroom.service;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomCreateDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.ChatRoomInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.repository.ChatRoomRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.memberchatroom.repository.MemberChatRoomRepository;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChatRoomServiceTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    ChatRoomService chatRoomService;

    @Test
    public void 만료된_모집방_필터링_성공() throws Exception {
        // given
        LocalDate today = LocalDate.now(); // 현재 날짜
        LocalDate yesterday = today.minusDays(1);
        LocalDate theDayBeforeYesterday = today.minusDays(2);
        LocalDate tomorrow = today.plusDays(1);
        LocalTime currentTime = LocalTime.now();
        LocalTime futureTime = currentTime.plusHours(1);

        ChatRoom expiredRoom1 = ChatRoom.builder().roomName("모집방1").meetingDate(yesterday).endTime(currentTime).build();
        ChatRoom expiredRoom2 = ChatRoom.builder().roomName("모집방2").meetingDate(theDayBeforeYesterday).endTime(futureTime).build();
        ChatRoom notExpiredRoom = ChatRoom.builder().roomName("모집방3").meetingDate(tomorrow).endTime(currentTime).build();
        chatRoomRepository.save(expiredRoom1);
        chatRoomRepository.save(expiredRoom2);
        chatRoomRepository.save(notExpiredRoom);

        // when
        Specification<ChatRoom> specification = ChatRoomSpecification.filterExpiredChatRooms();
        List<ChatRoom> expiredChatRooms = chatRoomRepository.findAll(specification); // 모임날짜가 현재 날짜보다 이전인 모집방 검색
        if(!expiredChatRooms.isEmpty()){
            chatRoomRepository.deleteAll(expiredChatRooms); // 데이터베이스에서 삭제
        }

        // then
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(1);
        assertThat(chatRoomRepository.findAll().get(0).getRoomName()).isEqualTo(notExpiredRoom.getRoomName());

    }
}
