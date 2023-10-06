package bobmukjaku.bobmukjakuDemo.domain.friend.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockInfoDto {

    private Long blockUid;
    private String blockNickname;
    private Integer blockRate;
    private String blockProfileColor;

    public static BlockInfoDto toDto(Member friend) {
        return BlockInfoDto.builder()
                .blockUid(friend.getUid())
                .blockNickname(friend.getMemberNickName())
                .blockRate(friend.getRate())
                .blockProfileColor(friend.getProfileColor())
                .build();
    }

}
