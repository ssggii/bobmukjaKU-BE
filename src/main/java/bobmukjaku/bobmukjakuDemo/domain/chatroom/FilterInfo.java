package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import bobmukjaku.bobmukjakuDemo.BaseTimeEntity;
import bobmukjaku.bobmukjakuDemo.domain.chatroom.dto.FilterInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FilterInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filterId;

    @Column(name = "type", length = 20)
    private String filterType;

    @Column(name = "value")
    private String filterValue;

    // member-filterInfo 연관관계 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private Member member;

    public FilterInfo(String type, String value){
        this.filterType = type;
        this.filterValue = value;
    }

    public FilterInfoDto toDto(FilterInfo filterInfo) {
        FilterInfoDto filterInfoDto = new FilterInfoDto(filterInfo.filterType, filterInfo.filterValue);
        return filterInfoDto;
    }

}
