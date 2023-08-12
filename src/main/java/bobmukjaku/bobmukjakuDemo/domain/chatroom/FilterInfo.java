package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterInfo {

    private String filterType;
    private String filterValue;

}
