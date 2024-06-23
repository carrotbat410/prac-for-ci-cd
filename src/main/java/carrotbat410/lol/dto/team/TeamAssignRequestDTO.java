package carrotbat410.lol.dto.team;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamAssignRequestDTO {

    private TeamAssignMode assingMode;

    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private List<SummonerDTO> noTeamList;

}
