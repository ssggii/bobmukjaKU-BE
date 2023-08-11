package bobmukjaku.bobmukjakuDemo.domain.chatroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FilterInfo {

    private String filterType;
    private String filterValue;

}
