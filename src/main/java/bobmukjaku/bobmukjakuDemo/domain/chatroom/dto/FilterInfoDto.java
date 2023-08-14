package bobmukjaku.bobmukjakuDemo.domain.chatroom.dto;

import bobmukjaku.bobmukjakuDemo.domain.chatroom.FilterInfo;

public record FilterInfoDto(String filterType, String filterValue) {

    public FilterInfo toEntity(FilterInfoDto filterInfoDto){
        return FilterInfo.builder().filterType(filterInfoDto.filterType).filterValue(filterInfoDto.filterValue).build();
    }
}
