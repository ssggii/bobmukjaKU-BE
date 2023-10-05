package bobmukjaku.bobmukjakuDemo.domain.friend.dto;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
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

    public static FriendInfoDto toDto(Friend friend) {
        return FriendInfoDto.builder()
                .friendUid(friend.getFriendUid())
                .friendNickname(friend.getMember().getMemberNickName())
                .friendRate(friend.getMember().getRate())
                .friendProfileColor(friend.getMember().getProfileColor())
                .build();
    }

}
