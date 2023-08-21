package bobmukjaku.bobmukjakuDemo.domain.place.dto;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;

public record ScrapCreateDto(Long uid, String placeId) {

    public Scrap toEntity(Member member) {
        return Scrap.builder().placeId(placeId).member(member).build();
    }
}
