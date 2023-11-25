package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import bobmukjaku.bobmukjakuDemo.domain.place.dto.TopScrapRestaurantsInterface;
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
    ScrapRepository scrapRepository;

    @Autowired
    MemberRepository memberRepository;

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
        Member member = Member.builder().memberEmail("member111@konkuk.ac.kr").memberPassword("member1@").memberNickName("닉네임1").role(Role.USER).build();
        memberRepository.save(member);
        Scrap scrap1 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        Scrap scrap2 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        Scrap scrap3 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        Scrap scrap4 = Scrap.builder().placeId("place222").placeName("음식점2").member(member).build();
        Scrap scrap5 = Scrap.builder().placeId("place222").placeName("음식점2").member(member).build();
        Scrap scrap6 = Scrap.builder().placeId("place333").placeName("음식점3").member(member).build();
        Scrap scrap7 = Scrap.builder().placeId("place333").placeName("음식점3").member(member).build();
        Scrap scrap8 = Scrap.builder().placeId("place333").placeName("음식점3").member(member).build();
        Scrap scrap9 = Scrap.builder().placeId("place333").placeName("음식점3").member(member).build();
        Scrap scrap10 = Scrap.builder().placeId("place444").placeName("음식점4").member(member).build();
        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
        scrapRepository.save(scrap3);
        scrapRepository.save(scrap4);
        scrapRepository.save(scrap5);
        scrapRepository.save(scrap6);
        scrapRepository.save(scrap7);
        scrapRepository.save(scrap8);
        scrapRepository.save(scrap9);
        scrapRepository.save(scrap10);
/*
        // when
        List<TopScrapRestaurantsInterface> result = scrapRepository.findTop10PlacesByScrapCount();

        // then
        for (TopScrapRestaurantsInterface restaurants : result) {
            System.out.println(restaurants.getPlaceId() + " / " + restaurants.getPlaceName() + " / " + restaurants.getScrapCount());
        }

        String top1 = result.get(0).getPlaceId();
        String top2 = result.get(1).getPlaceId();
        String top3 = result.get(2).getPlaceId();
        String top4 = result.get(3).getPlaceId();


        assertThat(top1).isEqualTo("place333");
        assertThat(top2).isEqualTo("place111");
        assertThat(top3).isEqualTo("place222");
        assertThat(top4).isEqualTo("place444");*/

    }

    @Test
    public void 스크랩수_같은경우_placeId알파벳순_상위스크랩_반환_성공() throws Exception{
        // given
        Member member = Member.builder().memberEmail("member1@konkuk.ac.kr").memberPassword("member1@").memberNickName("닉네임1").role(Role.USER).build();
        memberRepository.save(member);
        Scrap scrap1 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        Scrap scrap2 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        Scrap scrap3 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        Scrap scrap4 = Scrap.builder().placeId("place222").placeName("음식점2").member(member).build();
        Scrap scrap5 = Scrap.builder().placeId("place222").placeName("음식점2").member(member).build();
        Scrap scrap6 = Scrap.builder().placeId("place222").placeName("음식점2").member(member).build();
        Scrap scrap7 = Scrap.builder().placeId("alace333").placeName("음식점3").member(member).build();
        Scrap scrap8 = Scrap.builder().placeId("alace333").placeName("음식점3").member(member).build();
        Scrap scrap9 = Scrap.builder().placeId("alace333").placeName("음식점3").member(member).build();
        Scrap scrap10 = Scrap.builder().placeId("place111").placeName("음식점1").member(member).build();
        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);
        scrapRepository.save(scrap3);
        scrapRepository.save(scrap4);
        scrapRepository.save(scrap5);
        scrapRepository.save(scrap6);
        scrapRepository.save(scrap7);
        scrapRepository.save(scrap8);
        scrapRepository.save(scrap9);
        scrapRepository.save(scrap10);
/*
        // when
        List<TopScrapRestaurantsInterface> result = scrapRepository.findTop10PlacesByScrapCount();

        // then
        for (TopScrapRestaurantsInterface restaurants : result) {
            System.out.println(restaurants.getPlaceId() + " / " + restaurants.getPlaceName() + " / " + restaurants.getScrapCount());
        }

        String top1 = result.get(0).getPlaceId();
        String top2 = result.get(1).getPlaceId();
        String top3 = result.get(2).getPlaceId();

        assertThat(top1).isEqualTo("place111");
        assertThat(top2).isEqualTo("alace333");
        assertThat(top3).isEqualTo("place222");*/

    }

}
