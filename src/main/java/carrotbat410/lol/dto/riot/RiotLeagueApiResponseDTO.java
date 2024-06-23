package carrotbat410.lol.dto.riot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class RiotLeagueApiResponseDTO {
    private String queueType;
    private String tier;
    private String rank; //! API 응답 결과는 null, I, II, III, IV String형식으로 날라온다.
    private int leaguePoints;
    private int wins;
    private int losses;
}