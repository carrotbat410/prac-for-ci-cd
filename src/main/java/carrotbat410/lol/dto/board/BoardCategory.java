package carrotbat410.lol.dto.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardCategory {
    RECRUIT("멤버 모집"),
    FREE("자유게시글"),
    MATCH("스크림");

    private final String text;
}
