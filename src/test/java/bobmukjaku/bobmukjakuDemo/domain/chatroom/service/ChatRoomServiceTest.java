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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
/*
        // given
        Member member = Member.builder().memberEmail("username@konkuk.ac.kr").memberNickName("ssggii").memberPassword("password1!@").build();
        ChatRoom chatRoom1 = ChatRoom.builder().roomName("모집방1").currentNum(0).endTime(LocalTime.parse("")).build();
    */}
}
