package carrotbat410.lol.dto.summoner;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.entity.Summoner;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SummonerDTO {
    private final Long id;
    private final String summonerName;
    private final String tagLine;
    private final String tier;
    private final Integer rank;
    private final int mmr;
    private final int level;
    private final int wins;
    private final int losses;
    private final int iconId;
    private final LocalDateTime updatedAt;

    public static SummonerDTO from(Summoner summoner) {

        return new SummonerDTO(
                summoner.getId(), summoner.getSummonerName(), summoner.getTagLine(), summoner.getTier(), summoner.getRank1(),
                summoner.getMmr(), summoner.getLevel(), summoner.getWins(), summoner.getLosses(),summoner.getIconId(), summoner.getUpdatedAt());
    }

    @QueryProjection
    public SummonerDTO(Long id, String summonerName, String tagLine, String tier, Integer rank, int mmr, int level, int wins, int losses, int iconId, LocalDateTime updatedAt) {
        this.id = id;
        this.summonerName = summonerName;
        this.tagLine = tagLine;
        this.tier = tier;
        this.rank = rank;
        this.mmr = mmr;
        this.level = level;
        this.wins = wins;
        this.losses = losses;
        this.iconId = iconId;
        this.updatedAt = updatedAt;
    }

}