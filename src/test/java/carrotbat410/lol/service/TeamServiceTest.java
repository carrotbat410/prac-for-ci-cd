package carrotbat410.lol.service;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static carrotbat410.lol.dto.team.TeamAssignMode.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class TeamServiceTest {


    @Autowired
    TeamService teamService;

    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private List<SummonerDTO> noTeamList;

    @BeforeEach
    void beforeEach() {
        SummonerDTO summoner1 = createSummonerDTO(1L, "summoner1");
        SummonerDTO summoner2 = createSummonerDTO(2L, "summoner2");
        SummonerDTO summoner3 = createSummonerDTO(3L, "summoner3");
        SummonerDTO summoner4 = createSummonerDTO(4L, "summoner4");
        SummonerDTO summoner5 = createSummonerDTO(5L, "summoner5");
        SummonerDTO summoner6 = createSummonerDTO(6L, "summoner6");
        SummonerDTO summoner7 = createSummonerDTO(7L, "summoner7");

        team1List = new ArrayList<>(List.of(summoner1, summoner2, summoner3, summoner4));
        team2List = new ArrayList<>(List.of(summoner5, summoner6, summoner7));
        noTeamList = new ArrayList<>();
    }

    @Test
    @DisplayName("[RandomMode]팀짜기의 결과로 각 팀의 인원은 5명이 되어야 한다.")
    void makeResultWithRandomMode() throws Exception {
        // given
        SummonerDTO summoner8 = createSummonerDTO(8L, "summoner8");
        SummonerDTO summoner9 = createSummonerDTO(9L, "summoner9");
        SummonerDTO summoner10 = createSummonerDTO(10L, "summoner10");
        noTeamList.addAll(List.of(summoner8, summoner9, summoner10));

        TeamAssignRequestDTO requestDTO = new TeamAssignRequestDTO(RANDOM, team1List, team2List, noTeamList);

        // when
        TeamAssignResponseDTO response = teamService.makeResultWithRandomMode(requestDTO);

        // then
        assertThat(response.getTeam1List()).hasSize(5);
        assertThat(response.getTeam2List()).hasSize(5);
        assertThat(response.getTeam1AvgMmr()).isEqualTo("EMERALD III");
        assertThat(response.getTeam2AvgMmr()).isEqualTo("EMERALD III");
    }

    @Test
    @DisplayName("[BalanceMode]팀짜기의 결과로 각 팀의 인원은 5명이 되어야 한다.")
    void makeResultWithBalanceMode() throws Exception {
        // given
        //* (worstSummonerMmr + bestSummonerMmr) / 2 = 22 를 만족하도록 mmr설정함.
        SummonerDTO worstSummoner = createSummonerDTO(8L, "worstSummoner", 16);
        SummonerDTO commonSummoner = createSummonerDTO(9L, "commonSummoner", 22);
        SummonerDTO bestSummoner = createSummonerDTO(10L, "bestSummoner", 28);
        noTeamList.addAll(List.of(worstSummoner, commonSummoner, bestSummoner));

        TeamAssignRequestDTO requestDTO = new TeamAssignRequestDTO(BALANCE, team1List, team2List, noTeamList);

        // when
        TeamAssignResponseDTO response = teamService.makeResultWithBalanceMode(requestDTO);

        // then
        assertThat(response.getTeam1List()).hasSize(5)
                .extracting("summonerName")
                .contains("summoner1", "summoner2", "summoner3", "summoner4");

        assertThat(response.getTeam2List()).hasSize(5)
                .extracting("summonerName")
                .contains("summoner5", "summoner6", "summoner7");
    }

    @Test
    @DisplayName("[GoldenBalanceMode]팀짜기의 결과로 각 팀의 인원은 5명이 되어야 한다.")
    void makeResultWithGoldenBalanceMode() throws Exception {
        // given
        //* (worstSummonerMmr + bestSummonerMmr) / 2 = 22 를 만족하도록 mmr 설정함.
        SummonerDTO worstSummoner = createSummonerDTO(8L, "worstSummoner", 16);
        SummonerDTO commonSummoner = createSummonerDTO(9L, "commonSummoner", 22);
        SummonerDTO bestSummoner = createSummonerDTO(10L, "bestSummoner", 28);
        noTeamList.addAll(List.of(worstSummoner, commonSummoner, bestSummoner));

        TeamAssignRequestDTO requestDTO = new TeamAssignRequestDTO(GOLDEN_BALANCE, team1List, team2List, noTeamList);

        // when
        TeamAssignResponseDTO response = teamService.makeResultWithGoldenBalanceMode(requestDTO);

        // then
        assertThat(response.getTeam1List()).hasSize(5)
                .extracting("summonerName")
                .containsExactlyInAnyOrder("summoner1", "summoner2", "summoner3", "summoner4", "commonSummoner");

        assertThat(response.getTeam2List()).hasSize(5)
                .extracting("summonerName")
                .containsExactlyInAnyOrder("summoner5", "summoner6", "summoner7", "worstSummoner", "bestSummoner");

        assertThat(response.getTeam1AvgMmr()).isEqualTo("EMERALD III");
        assertThat(response.getTeam2AvgMmr()).isEqualTo("EMERALD III");
    }


    private static SummonerDTO createSummonerDTO(Long id, String summonerName) {
        return new SummonerDTO(id, summonerName, "KR1", "GOLD", 1, 22, 100, 70, 70, 123, LocalDateTime.now());
    }
    private static SummonerDTO createSummonerDTO(Long id, String summonerName, int mmr) {
        return new SummonerDTO(id, summonerName, "KR1", "GOLD", 1, mmr, 100, 70, 70, 123, LocalDateTime.now());
    }
}