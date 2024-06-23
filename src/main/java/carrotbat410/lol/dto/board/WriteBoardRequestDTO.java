package carrotbat410.lol.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WriteBoardRequestDTO {
    @NotBlank(message = "게시글의 제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "게시글의 내용을 입력해주세요.")
    private String content;

    //TODO Enum타입은 어떤 어노테이션 붙이는게 좋지? enum 값을 검증하는 커스텀 애너테이션만들어야 하나?
    @NotNull(message = "게시글의 카테고리를 입력해주세요.")
    private BoardCategory boardCategory;
}
