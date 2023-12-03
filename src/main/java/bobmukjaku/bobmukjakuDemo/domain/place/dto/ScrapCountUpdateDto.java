package bobmukjaku.bobmukjakuDemo.domain.place.dto;

public record ScrapCountUpdateDto(String placeId, String placeName, int scrapCount) {
    public ScrapCountUpdateDto(String placeId, String placeName, int scrapCount) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.scrapCount = scrapCount;
    }
}
