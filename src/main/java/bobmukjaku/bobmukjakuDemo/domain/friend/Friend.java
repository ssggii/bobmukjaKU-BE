package bobmukjaku.bobmukjakuDemo.domain.friend;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Table(name = "friend")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Friend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; // PK

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member member;

    @Column(name = "friend_id")
    private Long friendUid;

    @Column(name = "is_block")
    private Boolean isBlock; // 차단 - true, 친구 - false

}
