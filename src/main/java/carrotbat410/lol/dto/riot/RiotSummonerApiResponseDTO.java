package carrotbat410.lol.dto.riot;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RiotSummonerApiResponseDTO {
    private String id;
    private int profileIconId;
    private int summonerLevel;
}