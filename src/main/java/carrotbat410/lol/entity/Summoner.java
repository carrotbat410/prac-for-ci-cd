package carrotbat410.lol.entity;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@ToString
@Table(name = "summoners")
public class Summoner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summoner_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String summonerName;
    private String tagLine;
    private String tier;
    private Integer rank1; //! rank 예약어여서 rank로 설정 불가능함. //TODO 개발 좀 진행되면 필드명, DB컬럼명 rank로 바꾸기.
    private int mmr;
    private int level;
    private int wins;
    private int losses;
    private int iconId;

    public static Summoner of(Long userId, String summonerName, String tagLine, String tier, Integer rank,
                              int mmr, int level, int wins, int losses, int iconId){
        return new Summoner(null, userId, summonerName, tagLine, tier, rank, mmr, level, wins, losses, iconId);
    }

    public void updateSummoner(SummonerApiTotalDTO apiResult) {
        summonerName = apiResult.getSummonerName();
        tagLine = apiResult.getTagLine();
        tier = apiResult.getTier();
        rank1 = apiResult.getRank();
        mmr = apiResult.getMmr();
        level = apiResult.getLevel();
        wins = apiResult.getWins();
        losses = apiResult.getLosses();
        iconId = apiResult.getIconId();
    }
}
