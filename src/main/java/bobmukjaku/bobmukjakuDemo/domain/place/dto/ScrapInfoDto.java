package bobmukjaku.bobmukjakuDemo.domain.place.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;

public record ScrapInfoDto(Long uid, String placeName ,String placeId) {

    public Scrap toEntity(Member member) {
        return Scrap.builder().placeId(placeId).placeName(placeName).member(member).build();
    }
}
