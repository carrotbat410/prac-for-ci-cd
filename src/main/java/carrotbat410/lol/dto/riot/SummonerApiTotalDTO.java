package carrotbat410.lol.dto.riot;

import carrotbat410.lol.entity.Summoner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class SummonerApiTotalDTO {
    private final String summonerName;
    private final String tagLine;
    private final String tier;
    private final Integer rank;
    private final int mmr;
    private final int level;
    private final int wins;
    private final int losses;
    private final int iconId;


    public static SummonerApiTotalDTO from(RiotAccountApiResponseDTO accountInfo, RiotSummonerApiResponseDTO summonerInfo, RiotLeagueApiResponseDTO leagueInfo, int mmr, Integer numberRank) {

        return new SummonerApiTotalDTO(accountInfo.getGameName(), accountInfo.getTagLine(), leagueInfo.getTier(),
                numberRank, mmr, summonerInfo.getSummonerLevel(), leagueInfo.getWins(), leagueInfo.getLosses(),
                summonerInfo.getProfileIconId());
    }

}