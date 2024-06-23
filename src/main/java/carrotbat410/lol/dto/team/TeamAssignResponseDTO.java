package carrotbat410.lol.dto.team;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//@RequiredArgsConstructor
@NoArgsConstructor //TODO RequiredArgsConstructor 사용하기
@AllArgsConstructor //TODO RequiredArgsConstructor 사용하기
@ToString
@Getter
public class TeamAssignResponseDTO {
    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private String team1AvgMmr;
    private String team2AvgMmr;
}
