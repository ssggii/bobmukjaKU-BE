package bobmukjaku.bobmukjakuDemo.domain.place.dto;

public record PlaceInfoDto(String placeId, String placeName) {
    public PlaceInfoDto(String placeId, String placeName) {
        this.placeId = placeId;
        this.placeName = placeName;
    }
}
