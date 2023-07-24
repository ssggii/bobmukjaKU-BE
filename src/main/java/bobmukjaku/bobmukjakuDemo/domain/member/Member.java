package bobmukjaku.bobmukjakuDemo.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.Role;

@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(name = "member_id", nullable = false, length = 30, unique = true)
    private String memberId; // 사용자 입력 아이디

    private String password; // 비밀번호

    @Column(nullable = false, length = 30, unique = true)
    private String nickName; // 닉네임

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Enumerated(EnumType.STRING)
    private Role role; // 권한 (USER, ADMIN)


    /* 회원 정보 수정 */
    // 아이디 변경
    public void updateMemberId(String memberId){
        this.memberId = memberId;
    }

    // 비밀번호 변경
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    // 닉네임 변경
    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    /* 비밀번호 암호화 */
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
}
