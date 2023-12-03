package bobmukjaku.bobmukjakuDemo.domain.place.dto;

public record ReviewCountUpdateDto(String placeId, String placeName, int reviewCount) {
    public ReviewCountUpdateDto(String placeId, String placeName, int reviewCount) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.reviewCount = reviewCount;
    }
}
