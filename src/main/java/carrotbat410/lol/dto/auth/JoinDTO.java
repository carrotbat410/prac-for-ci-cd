package carrotbat410.lol.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

//Entity에 비해,  DTO는 Setter 맘껏 써도됨,
//@RequestBody->DTO mapping떄 Setter필요함.
@Setter
@Getter
@AllArgsConstructor
public class JoinDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Length(min = 6, max = 20, message = "올바르지 않은 아이디 양식입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디에 특수 문자는 사용할 수 없습니다.")
    private String username;

    @NotBlank(message = "패스워드를 입력해주세요")
    @Length(min = 6, max = 20, message = "올바르지 않은 패스워드 양식입니다.")
    private String password;
}
