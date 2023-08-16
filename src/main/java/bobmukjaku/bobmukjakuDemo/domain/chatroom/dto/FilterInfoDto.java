package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;

public record FilterInfoDto(String filterType, String filterValue) {

    public FilterInfo toEntity(Member member) {
        FilterInfo filterInfo = FilterInfo.builder().filterType(filterType).filterValue(filterValue).member(member).build();
        return filterInfo;
    }

}
