package carrotbat410.lol.controller;

import carrotbat410.lol.controller.customMockUser.WithMockCustomUser;
import carrotbat410.lol.dto.summoner.AddSummonerReqeustDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.service.SummonerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(controllers = SummonerController.class)
@WithMockCustomUser
public class SummonerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SummonerService summonerService;

    @Test
    @DisplayName("추가한 소환사 목록을 조회한다.")
    void getSummoners() throws Exception {
        // given
        SummonerDTO summoner1 = createSummonerDTO(1L, "summoner1", "KR1");
        SummonerDTO summoner2 = createSummonerDTO(2L, "summoner2", "KR1");
        List<SummonerDTO> content = Arrays.asList(summoner1, summoner2);

        PageRequest pageRequest = PageRequest.of(0, 30);
        PageImpl<SummonerDTO> pageImpl = new PageImpl<>(content, pageRequest, 2);

        // stubbing
        //! Stubbing 인자가 완전 일치해야함.
        //* 완전일치하기 어려울떄 any()사용하기. any(Long.class)처럼 타입 명시해서 좀 더 타입을 제한할 수 있음
        when(summonerService.getSummoners(any(Long.class), any(Pageable.class))).thenReturn(pageImpl);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/summoners")
//*                        .queryParam("size", "1") 필요하다면 queryParam 사용 가능
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));


        //* 아래 부분은 서비스 레이어에서 검증할 부분이기 떄문에, 여기서 검증할 필요 X.
//                .andExpect(jsonPath("$.data[0].summonerName").value("Summoner1"))
//                .andExpect(jsonPath("$.data[0].tagLine").value("KR1"))
    }

    @Test
    @DisplayName("소환사를 추가할 떄 소환사명이 공백이거나 짧으면 예외를 던진다.")
    void addSummonerWithEmptySummonerName() throws Exception{
        // given
        AddSummonerReqeustDTO request1 = new AddSummonerReqeustDTO(null, "KR1");
        AddSummonerReqeustDTO request2 = new AddSummonerReqeustDTO("", "KR1");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NotNull"))
                .andExpect(jsonPath("$.message").value("소환사이름을 입력해주세요."))
                .andExpect(jsonPath("$.fieldName").value("summonerName"));

        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Length"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 소환사이름 양식입니다."))
                .andExpect(jsonPath("$.fieldName").value("summonerName"));
    }

    @Test
    @DisplayName("소환사를 추가할 떄 소환사명의 길이는 30 이하이어야 한다.")
    void addSummonerWithLongerSummonerName() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("1234567890123456789012345678901");//length:31

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Length"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 소환사이름 양식입니다."))
                .andExpect(jsonPath("$.fieldName").value("summonerName"));
    }

    @Test
    @DisplayName("소환사를 추가할 떄 소환사명의 길이는 30 이하이어야 한다.")
    void addSummonerWithLongerTagLine() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("summoner1");
        request.setTagLine("1234567890123456789012345678901");//length:31

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Length"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 태그라인 양식입니다."))
                .andExpect(jsonPath("$.fieldName").value("tagLine"));
    }

    @Test
    @DisplayName("소환사를 추가할 떄 태그라인이 없으면 KR1이 기본값으로 들어간다.")
    void addSummonerWithoutTagLine() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("summoner1");
        request.setTagLine("");

        // stubbing: SummonerService의 addSummoner 메서드가 호출되었을 때 반환할 값 설정
        SummonerDTO mockSummonerDTO = createSummonerDTO(1L, "summoner1", "KR1");
        when(summonerService.addSummoner(1L, "summoner1", "KR1")).thenReturn(mockSummonerDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("소환사를 성공적으로 추가하면 상태코드 200을 반환한다.")
    void addSummoner() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("summoner1");
        request.setTagLine("KR1");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("소환사 정보를 갱신할 수 있다.")
    void updateSummoner() throws Exception {
        // given
        Long summonerId = 1L;
        String summonerName = "summoner1";
        String tagLine = "KR1";

        // stubbing
        SummonerDTO mockSummonerDTO = createSummonerDTO(summonerId, summonerName, tagLine);
        when(summonerService.updateSummoner(summonerId)).thenReturn(mockSummonerDTO);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/summoner/{summonerId}", summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.id").value(summonerId));
    }

    @Test
    @DisplayName("소환사를 삭제할 수 있다.")
    void deleteSummoner() throws Exception {
        // given
        Long summonerId = 1L;

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/summoner/{summonerId}", summonerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    private static SummonerDTO createSummonerDTO(Long id, String summonerName, String tagLine) {
        return new SummonerDTO(id, summonerName, tagLine, "GOLD", 1, 22, 100, 70, 70, 123, LocalDateTime.now());
    }

}
