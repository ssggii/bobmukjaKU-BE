package bobmukjaku.bobmukjakuDemo.domain.place.repository;

import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.Role;
import bobmukjaku.bobmukjakuDemo.domain.place.Scrap;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
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
    EntityManager em;

    private void clear(){
        em.flush();
        em.clear();
    }

    @AfterEach
    private void after(){
        em.clear();
    }

    @Test
    public void 상위스크랩_음식점10개_반환_성공() throws Exception{
        // given
        Member member = Member.builder().memberEmail("member1@konkuk.ac.kr").memberPassword("member1@").memberNickName("닉네임1").role(Role.USER).build();
        Scrap scrap1 = Scrap.builder().placeId("place1").placeName("음식점1").member(member).build();
        Scrap scrap2 = Scrap.builder().placeId("place1").placeName("음식점1").member(member).build();
        Scrap scrap3 = Scrap.builder().placeId("place1").placeName("음식점1").member(member).build();
        Scrap scrap4 = Scrap.builder().placeId("place1").placeName("음식점1").member(member).build();
        Scrap scrap5 = Scrap.builder().placeId("place2").placeName("음식점2").member(member).build();
        Scrap scrap6 = Scrap.builder().placeId("place2").placeName("음식점2").member(member).build();
        Scrap scrap7 = Scrap.builder().placeId("place2").placeName("음식점2").member(member).build();
        Scrap scrap8 = Scrap.builder().placeId("place3").placeName("음식점3").member(member).build();
        Scrap scrap9 = Scrap.builder().placeId("place3").placeName("음식점3").member(member).build();
        Scrap scrap10 = Scrap.builder().placeId("place4").placeName("음식점4").member(member).build();

        // when
        List<Scrap> findScrap = scrapRepository.findTop10ByOrderByPlaceIdDesc();

        // then
        findScrap.stream().map(scrap -> scrap.getPlaceId()).forEach(System.out::println);
    }
}
