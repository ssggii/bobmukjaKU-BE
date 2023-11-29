package bobmukjaku.bobmukjakuDemo.domain.place;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class PlaceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resultCode;
    private String resultMsg;
    private String numOfRows;
    private String pageNo;
    private String totalCount;
    private String description;
    private String columns;
    private String stdrYm;
    private String bizesId;
    private String bizesNm;
    private String brchNm;
    private String indsLclsCd;
    private String indsLclsNm;
    private String indsMclsCd;
    private String indsMclsNm;
    private String indsSclsCd;
    private String indsSclsNm;
    private String ksicCd;
    private String ksicNm;
    private String ctprvnCd;
    private String ctprvnNm;
    private String signguCd;
    private String signguNm;
    private String adongCd;
    private String adongNm;
    private String ldongCd;
    private String ldongNm;
    private String lnoCd;
    private String plotSctCd;
    private String plotSctNm;
    private String lnoMnno;
    private String lnoSlno;
    private String lnoAdr;
    private String rdnmCd;
    private String rdnm;
    private String bldMnno;
    private String bldSlno;
    private String bldMngNo;
    private String bldNm;
    private String rdnmAdr;
    private String oldZipcd;
    private String newZipcd;
    private String dongNo;
    private String flrNo;
    private String hoNo;
    private String lon;
    private String lat;

}
