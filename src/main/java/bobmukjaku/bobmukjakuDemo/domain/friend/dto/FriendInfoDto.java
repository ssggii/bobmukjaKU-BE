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

    private Long id;
    private Member member;
    private Long friendId;
    private Boolean isBlock;

    // 필요없을수도?
}
