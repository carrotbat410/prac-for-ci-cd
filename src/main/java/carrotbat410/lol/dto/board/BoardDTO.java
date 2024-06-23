package carrotbat410.lol.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDTO {
    private Long id;
    private String title;
    private String content;

    //User 관련 필드
    private Long userId;
    private String username;
}
