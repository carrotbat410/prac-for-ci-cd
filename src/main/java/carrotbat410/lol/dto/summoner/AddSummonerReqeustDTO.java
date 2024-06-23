package carrotbat410.lol.dto.summoner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddSummonerReqeustDTO {

    @NotNull(message = "소환사이름을 입력해주세요.")
    @Length(min = 1, max = 30, message = "올바르지 않은 소환사이름 양식입니다.")
    private String summonerName;

    @Length(max = 30, message = "올바르지 않은 태그라인 양식입니다.")
    private String tagLine;
}
