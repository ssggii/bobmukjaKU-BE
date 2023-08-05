package bobmukjaku.bobmukjakuDemo.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class HashedAuthCodeDto {
    @JsonProperty
    private String hashedAuthCode;

    @Builder
    public HashedAuthCodeDto(String hashedAuthCode){
        this.hashedAuthCode = hashedAuthCode;
    }
}
