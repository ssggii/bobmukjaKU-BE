package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.Place;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.PlaceInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.TopScrapRestaurantsInterface;
import bobmukjaku.bobmukjakuDemo.domain.place.service.PlaceService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PlaceRepositoryTest {

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PlaceService placeService;

    @Autowired
    EntityManager em;

    private void clear(){
        em.flush();
        em.clear();
    }
    @BeforeEach
    private void before(){
        clear();
    }

    @AfterEach
    private void after(){
        clear();
    }
    @Test
    public void 상위스크랩_음식점10개_반환_성공() throws Exception{

        // given
        // 1,2,3,4,6,5,7,8,10,9
        Place place1 = Place.builder().placeId("placeId1").placeName("음식점1").scrapCount(10).reviewCount(0).build();
        Place place2 = Place.builder().placeId("placeId2").placeName("음식점2").scrapCount(9).reviewCount(5).build();
        Place place3 = Place.builder().placeId("placeId3").placeName("음식점3").scrapCount(8).reviewCount(4).build();
        Place place4 = Place.builder().placeId("placeId4").placeName("음식점4").scrapCount(7).reviewCount(3).build();
        Place place5 = Place.builder().placeId("placeId5").placeName("bbb5").scrapCount(6).reviewCount(2).build();
        Place place6 = Place.builder().placeId("placeId6").placeName("aaa6").scrapCount(6).reviewCount(2).build();
        Place place7 = Place.builder().placeId("placeId7").placeName("음식점7").scrapCount(6).reviewCount(1).build();
        Place place8 = Place.builder().placeId("placeId8").placeName("음식점8").scrapCount(6).reviewCount(0).build();
        Place place9 = Place.builder().placeId("placeId9").placeName("ddd9").scrapCount(5).reviewCount(0).build();
        Place place10 = Place.builder().placeId("placeId10").placeName("ccc10").scrapCount(5).reviewCount(0).build();
        placeRepository.saveAll(List.of(place1,place2,place3,place4,place5,place6,place7,place8,place9,place10));

        // when
        List<TopScrapRestaurantsInterface> result = placeRepository.findTop10CustomSort();

        // then
        for (TopScrapRestaurantsInterface restaurants : result) {
            System.out.println(restaurants.getPlaceId());
        }

    }

/*
    @Test
    void 키워드를_이름에_포함하는_음식점_반환() throws Exception {
        // given
        String keyword = "내찜닭";
        *//*Place place1 = Place.builder().placeId("1").placeName("닭맛집").scrapCount(10).reviewCount(5).build();
        Place place2 = Place.builder().placeId("2").placeName("내가찜한닭").scrapCount(15).reviewCount(8).build();
        Place place3 = Place.builder().placeId("3").placeName("시홍쓰").scrapCount(7).reviewCount(7).build();
        List<Place> places = List.of(place1, place2, place3);
        placeRepository.saveAll(places);*//*

        // when
        List<PlaceInfoDto> result = placeService.getPlacesByKeyword(keyword);

        // then
        assertThat(result).hasSize(2);

    }*/

}
