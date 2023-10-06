package bobmukjaku.bobmukjakuDemo.domain.friend.dto;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendInfoDto {

    private Long friendUid;
    private String friendNickname;
    private Integer friendRate;
    private String friendProfileColor;

    public static FriendInfoDto toDto(Member friend) {
        return FriendInfoDto.builder()
                .friendUid(friend.getUid())
                .friendNickname(friend.getMemberNickName())
                .friendRate(friend.getRate())
                .friendProfileColor(friend.getProfileColor())
                .build();
    }

}
