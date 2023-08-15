package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;

public record FilterInfoDto(String filterType, String filterValue) {

    public FilterInfo toEntity() {
        FilterInfo filterInfo = FilterInfo.builder().filterType(this.filterType).filterValue(this.filterValue).build();
        return filterInfo;
    }

}
