package bobmukjaku.bobmukjakuDemo.domain.member;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import bobmukjaku.bobmukjakuDemo.domain.MemberChatRoom.MemberChatRoom;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.ChatRoom;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // PK

    @Column(name = "email", nullable = false, unique = true)
    private String memberEmail; // 이메일

    private String memberPassword; // 비밀번호

    @Column(name = "nickname", nullable = false , length = 8, unique = true)
    private String memberNickName; // 닉네임

    @Column(name = "certificated_at", nullable = false)
    private LocalDate certificatedAt; // 인증 날짜

    @Column(name = "rate", length = 4)
    private Integer rate; // 평가 점수

    @Column(name = "profile_color", length = 4)
    private String profileColor; // 프로필 배경색

    @Enumerated(EnumType.STRING)
    private Role role; // 권한 (USER or ADMIN)

    @Column(name = "refresh_Token", length = 1000)
    private String refreshToken;

    // member-chatroom 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "joiner", cascade = ALL, orphanRemoval = true)
    private List<MemberChatRoom> JoiningRooms = new ArrayList<>();

/*    // member-TimeTable 연관관계 매핑
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private TimeTable timeTable;*/

    // 회원 가입 시 USER 권한 부여
    public void giveUserAuthority(){
        this.role = Role.USER;
    }

    // 회원 가입 or 회원 탈퇴 시 비밀번호 확인하여 비밀번호 일치 여부 판단
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
        boolean result = passwordEncoder.matches(checkPassword, getMemberPassword());
        return result;
    }

    /* 연관관계 메서드 */
    // 참여 모집방 추가
    public void addChatRoom(ChatRoom chatRoom) {
        MemberChatRoom memberChatRoom = MemberChatRoom.builder().joiner(this).chatRoom(chatRoom).build();
        JoiningRooms.add(memberChatRoom);
    }

    /* 회원 정보 수정 */
    // 닉네임 변경
    public void updateNickName(String nickName) {
        this.memberNickName = nickName;
    }

    // 비밀번호 변경
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.memberPassword = passwordEncoder.encode(password);
    }

    // 인증 날짜 변경
    public void updateCertificatedAt(String certificatedAt){
        LocalDate date = LocalDate.parse(certificatedAt);
        this.certificatedAt = date;
    }

    // 평가점수 변경
    public void updateRate(int rate){
        this.rate = rate;
    }

    // 프로필 색상 변경
    public void updateProfileColor(String profileColor){
        this.profileColor = profileColor;
    }

    // refreshToken 갱신
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    // refreshToken 삭제
    public void deleteRefreshToken(){
        this.refreshToken = null;
    }

    /* 비밀번호 암호화 */
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.memberPassword = passwordEncoder.encode(memberPassword);
    }

    /* 인증날짜, 평가 점수, 프로필 색상 기본값 설정 */
    @PrePersist
    public void defaultSetting(){
        this.certificatedAt = LocalDate.now(); // 인증 날짜 default = 회원가입 날짜
        this.rate = 45; // 평가 점수 default = 45
        this.profileColor = "bg1"; // 프로필 색상 default = bg1
    }
}
