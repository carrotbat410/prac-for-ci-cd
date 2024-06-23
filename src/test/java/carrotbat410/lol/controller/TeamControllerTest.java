package carrotbat410.lol.controller;

import carrotbat410.lol.controller.customMockUser.WithMockCustomUser;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignMode;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static carrotbat410.lol.dto.team.TeamAssignMode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(controllers = TeamController.class)
@WithMockCustomUser
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    private ArrayList<SummonerDTO> team1List;
    private ArrayList<SummonerDTO> team2List;
    private ArrayList<SummonerDTO> noTeamList;

    @BeforeEach
    void beforeEach() {
        //! 8명까지 채워져있다.
        SummonerDTO summoner1 = createSummonerDTO(1L, "summoner1");
        SummonerDTO summoner2 = createSummonerDTO(2L, "summoner2");
        SummonerDTO summoner3 = createSummonerDTO(3L, "summoner3");
        SummonerDTO summoner4 = createSummonerDTO(4L, "summoner4");
        SummonerDTO summoner5 = createSummonerDTO(5L, "summoner5");
        SummonerDTO summoner6 = createSummonerDTO(6L, "summoner6");
        SummonerDTO summoner7 = createSummonerDTO(7L, "summoner7");
        SummonerDTO summoner8 = createSummonerDTO(8L, "summoner8");

        team1List = new ArrayList<>(List.of(summoner1, summoner2, summoner3, summoner4));
        team2List = new ArrayList<>(List.of(summoner5, summoner6, summoner7, summoner8));
        noTeamList = new ArrayList<>();
    }

    @Test
    @DisplayName("총 인원이 10명이 아니면 예외가 발생한다.")
    void makeTeamResultWith9Summoners() throws Exception {
        // given
        TeamAssignRequestDTO request = new TeamAssignRequestDTO(RANDOM, team1List, team1List, team1List);

        // when // then
        mockMvc.perform(post("/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("필요 인원은 10명 입니다."));
    }

    @Test
    @DisplayName("team1에 6명 이상의 소환사가 있으면 예외가 발생한다.")
    void makeTeamResultWithTeam1SizeExceeded() throws Exception {
        // given
        SummonerDTO summoner9 = createSummonerDTO(9L, "summoner9");
        SummonerDTO summoner10 = createSummonerDTO(10L, "summoner10");
        team1List.add(summoner9);
        team1List.add(summoner10);
        TeamAssignRequestDTO request = new TeamAssignRequestDTO(RANDOM, team1List, team2List, noTeamList);

        // when // then
        mockMvc.perform(post("/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("각 팀 최대 인원은 5명입니다."));
    }

    @Test
    @DisplayName("team2에 6명 이상의 소환사가 있으면 예외가 발생한다.")
    void makeTeamResultWithTeam2SizeExceeded() throws Exception {
        // given
        SummonerDTO summoner9 = createSummonerDTO(9L, "summoner9");
        SummonerDTO summoner10 = createSummonerDTO(10L, "summoner10");
        team2List.add(summoner9);
        team2List.add(summoner10);
        TeamAssignRequestDTO request = new TeamAssignRequestDTO(RANDOM, team1List, team2List, noTeamList);

        // when // then
        mockMvc.perform(post("/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("각 팀 최대 인원은 5명입니다."));
    }

    @Test
    @DisplayName("팀 생성 요청이 유효한 경우 성공적으로 팀을 생성한다.")
    void makeTeamResult() throws Exception {
        // given
        SummonerDTO summoner9 = createSummonerDTO(9L, "summoner9");
        SummonerDTO summoner10 = createSummonerDTO(10L, "summoner10");
        team1List.add(summoner9);
        team2List.add(summoner10);

        TeamAssignRequestDTO requestDTO = new TeamAssignRequestDTO(RANDOM, team1List, team2List, noTeamList);
        TeamAssignResponseDTO responseDTO = new TeamAssignResponseDTO(team1List, team2List, "GOLD I", "GOLD I");
        when(teamService.makeResultWithRandomMode(any())).thenReturn(responseDTO);

        // when // then
        mockMvc.perform(post("/team")
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.team1List").isArray())
                .andExpect(jsonPath("$.data.team2List").isArray())
                .andExpect(jsonPath("$.data.team1List.length()").value(team1List.size()))
                .andExpect(jsonPath("$.data.team2List.length()").value(team2List.size()));
    }


    private static SummonerDTO createSummonerDTO(Long id, String summonerName) {
        return new SummonerDTO(id, summonerName, "KR1", "GOLD", 1, 22, 100, 70, 70, 123, LocalDateTime.now());
    }
}