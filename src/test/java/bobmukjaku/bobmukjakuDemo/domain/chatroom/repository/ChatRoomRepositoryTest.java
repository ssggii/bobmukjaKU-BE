package bobmukjaku.bobmukjakuDemo.domain.chatroom.repository;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.TimeBlock;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification.betweenTime;
import static bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoomSpecification.equalDayOfWeek;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void 요일_필터링_성공() {
        // Given
        LocalDate date1 = LocalDate.parse("2023-08-16");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방1").meetingDate(date1).build());

        LocalDate date2 = LocalDate.parse("2023-08-16");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방2").meetingDate(date2).build());

        LocalDate date3 = LocalDate.parse("2023-08-18");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방3").meetingDate(date3).build());

        LocalDate date4 = LocalDate.parse("2023-08-19");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방4").meetingDate(date4).build());

        Specification<ChatRoom> specification = equalDayOfWeek(3); // 모임 날짜가 수요일인 모집방 검색

        // When
        List<ChatRoom> filteredChatRooms;
        filteredChatRooms = chatRoomRepository.findAll(specification);

        // Then
        filteredChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        assertThat(filteredChatRooms.size()).isEqualTo(2);
        assertThat(filteredChatRooms.get(0).getMeetingDate().getDayOfWeek().getValue()).isEqualTo(3);
        assertThat(filteredChatRooms.get(1).getMeetingDate().getDayOfWeek().getValue()).isEqualTo(3);

    }

    @Test
    public void 시간_필터링_성공() {
        // Given
        LocalDate date1 = LocalDate.parse("2023-08-16");
        LocalTime time1 = LocalTime.parse("09:10");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방4").meetingDate(date1).startTime(time1).build());

        LocalDate date2 = LocalDate.parse("2023-08-16");
        LocalTime time2 = LocalTime.parse("09:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방3").meetingDate(date2).startTime(time2).build());

        LocalDate date3 = LocalDate.parse("2023-08-18");
        LocalTime time3 = LocalTime.parse("10:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방2").meetingDate(date3).startTime(time3).build());

        LocalDate date4 = LocalDate.parse("2023-08-19");
        LocalTime time4 = LocalTime.parse("11:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방1").meetingDate(date4).startTime(time4).build());

        // startTime이 09:00 ~ 09:30 사이에 있는 모집방 검색
        Specification<ChatRoom> specification = betweenTime(LocalTime.parse("09:00"));

        // When
        List<ChatRoom> filteredChatRooms;
        filteredChatRooms = chatRoomRepository.findAll(specification);

        // Then
        filteredChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        assertThat(filteredChatRooms.size()).isEqualTo(2);

    }

    @Test
    public void 요일AND시간_필터링_성공() {
        // Given
        LocalDate date1 = LocalDate.parse("2023-08-16");
        LocalTime time1 = LocalTime.parse("09:10");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방5").meetingDate(date1).startTime(time1).build());

        LocalDate date2 = LocalDate.parse("2023-08-16");
        LocalTime time2 = LocalTime.parse("09:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방6").meetingDate(date2).startTime(time2).build());

        LocalDate date3 = LocalDate.parse("2023-08-18");
        LocalTime time3 = LocalTime.parse("10:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방7").meetingDate(date3).startTime(time3).build());

        LocalDate date4 = LocalDate.parse("2023-08-19");
        LocalTime time4 = LocalTime.parse("11:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방8").meetingDate(date4).startTime(time4).build());

        // startTime이 09:00 ~ 09:30 사이에 있고, 요일이 수요일인 모집방 검색
        Specification<ChatRoom> specification = ChatRoomSpecification.filteredByDayOfWeekAndTime(3, LocalTime.parse("09:00"));

        // When
        List<ChatRoom> filteredChatRooms;
        filteredChatRooms = chatRoomRepository.findAll(specification);

        // Then
        filteredChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        assertThat(filteredChatRooms.size()).isEqualTo(2);

    }

    @Test
    public void TimeBlock_필터링_성공() {
        // Given
        LocalDate date1 = LocalDate.parse("2023-08-16");
        LocalTime time1 = LocalTime.parse("09:10");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방5").meetingDate(date1).startTime(time1).build());

        LocalDate date2 = LocalDate.parse("2023-08-16");
        LocalTime time2 = LocalTime.parse("09:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방6").meetingDate(date2).startTime(time2).build());

        LocalDate date3 = LocalDate.parse("2023-08-18");
        LocalTime time3 = LocalTime.parse("10:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방7").meetingDate(date3).startTime(time3).build());

        LocalDate date4 = LocalDate.parse("2023-08-19");
        LocalTime time4 = LocalTime.parse("11:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방8").meetingDate(date4).startTime(time4).build());

        // startTime이 09:00 ~ 09:30 사이에 있고, 요일이 수요일인 모집방을 제외한 나머지 결과 검색
        TimeBlock timeBlock = TimeBlock.builder().dayOfWeek(3).time(LocalTime.parse("09:00")).build();
        Specification<ChatRoom> specification = ChatRoomSpecification.filteredByTimeBlock(timeBlock);

        // When
        List<ChatRoom> filteredChatRooms;
        filteredChatRooms = chatRoomRepository.findAll(specification);

        // Then
        filteredChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        assertThat(filteredChatRooms.size()).isEqualTo(2);

    }

    @Test
    public void 시간표_필터링_성공() {
        // Given
        LocalDate date1 = LocalDate.parse("2023-08-16");
        LocalTime time1 = LocalTime.parse("09:10");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방5").meetingDate(date1).startTime(time1).build());

        LocalDate date2 = LocalDate.parse("2023-08-16");
        LocalTime time2 = LocalTime.parse("09:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방6").meetingDate(date2).startTime(time2).build());

        LocalDate date3 = LocalDate.parse("2023-08-18");
        LocalTime time3 = LocalTime.parse("10:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방7").meetingDate(date3).startTime(time3).build());

        LocalDate date4 = LocalDate.parse("2023-08-19");
        LocalTime time4 = LocalTime.parse("11:20");
        chatRoomRepository.save(ChatRoom.builder().roomName("모집방8").meetingDate(date4).startTime(time4).build());

        memberRepository.save(Member.builder().memberNickName("ssggii").memberEmail("ssggii@konkuk.ac.kr").memberPassword("password!@").build());

        // startTime이 09:00 ~ 09:30 사이에 있고, 요일이 수요일인 모집방을 제외한 나머지 결과 검색
        TimeBlock timeBlock = TimeBlock.builder().dayOfWeek(3).time(LocalTime.parse("09:00")).build();
        Member member = memberRepository.findByMemberEmail("ssggii@konkuk.ac.kr").get();
        member.getTimeBlockList().add(timeBlock);
        Specification<ChatRoom> specification = ChatRoomSpecification.filteredByTimeTable(memberRepository, member.getUid());

        // When
        List<ChatRoom> filteredChatRooms;
        filteredChatRooms = chatRoomRepository.findAll(specification);

        // Then
        filteredChatRooms.stream().map(chatRoom -> chatRoom.getRoomName()).forEach(System.out::println);
        assertThat(filteredChatRooms.size()).isEqualTo(2);

    }
}
