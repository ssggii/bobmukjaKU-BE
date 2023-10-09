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
    public void 만료된_모집방_삭제_성공() throws Exception {

        // given
        // 현재 시간에서 1시간 이전의 시간을 계산
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        LocalTime currentTime = LocalTime.now();
        LocalTime oneHourAgo = currentTime.minusHours(1);

        // 만료되어야 할 모집방을 생성하고 저장
        ChatRoom expiredRoom1 = ChatRoom.builder().roomName("모집방1").meetingDate(yesterday).endTime(currentTime).build();
        chatRoomRepository.save(expiredRoom1);

        ChatRoom expiredRoom3 = ChatRoom.builder().roomName("모집방3").meetingDate(yesterday).endTime(oneHourAgo).build();
        chatRoomRepository.save(expiredRoom3);

        ChatRoom expiredRoom2 = ChatRoom.builder().roomName("모집방2").meetingDate(today).endTime(oneHourAgo).build();
        chatRoomRepository.save(expiredRoom2);

        ChatRoom notExpiredRoom = ChatRoom.builder().roomName("모집방4").meetingDate(tomorrow).endTime(oneHourAgo).build();
        chatRoomRepository.save(notExpiredRoom);

        // when
        chatRoomService.deleteExpiredRooms();

        // then
        assertThat(chatRoomRepository.findAll().size()).isEqualTo(1);
    }
}
