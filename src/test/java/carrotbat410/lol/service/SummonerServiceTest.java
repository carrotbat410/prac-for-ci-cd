package carrotbat410.lol.service;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.utils.RiotUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.groups.Tuple.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class SummonerServiceTest {
    @Autowired
    SummonerService summonerService;
    @Autowired
    SummonerRepository summonerRepository;
    @MockBean
    private RiotUtils riotUtils;

    @Test
    @DisplayName("추가한 소환사 목록을 가져올 수 있다.")
    void getSummoners() throws Exception {
        // given
        Long userId = 1L;
        for (int i = 1; i <= 3; i++) {
            Summoner newSummoner = new Summoner(null, userId, "summoner"+i, "KR1", "Gold", 1, 22, 100, 50, 20, 123);
            summonerRepository.save(newSummoner);
        }

        // when
        PageRequest pageRequest = PageRequest.of(0, 30);
        Page<SummonerDTO> summonersCase1 = summonerService.getSummoners(userId, pageRequest);
        Page<SummonerDTO> summonersCase2 = summonerService.getSummoners(100L, pageRequest);

        // then
        Assertions.assertThat(summonersCase1.getContent()).hasSize(3);
        Assertions.assertThat(summonersCase2.getContent()).isEmpty();
    }

    @Test
    @DisplayName("이미 추가한 소환사를 추가할 경우 예외를 던진다.")
    void addAlreadyAddedSummoner() throws Exception {
        // given
        Long userId = 1L;
        String summonerName = "summoner1";
        String tagLine = "KR1";

        Summoner existingSummoner = new Summoner(null, userId, summonerName, tagLine, "Gold", 1, 22, 100, 50, 20, 123);
        summonerRepository.save(existingSummoner);

        // stubbing
        //* stubbing 실행되기 전에 예외 터지기 떄문에 이런경우에는 stubbing 선언할 필요 없음.
        //* 참고로 실행된다 한들, riotUtils은 @MockBean이기 떄문에 null로, nullPointException 뜰거.
//        SummonerApiTotalDTO apiResult = createSummonerApiTotalDTO(summonerName, tagLine);
//        when(riotUtils.getSummoner(summonerName, tagLine)).thenReturn(apiResult);

        // when // then
        Assertions.assertThatThrownBy(() -> summonerService.addSummoner(userId, summonerName, tagLine))
                .isInstanceOf(DataConflictException.class)
                .hasMessage("이미 존재하는 유저입니다.");
    }

    @Test
    @DisplayName("소환사 수는 30명을 초과할 수 없다.")
    void addLimitExceededSummoner() throws Exception {
        // given
        Long userId = 1L;

        for (int i = 1; i <= 30; i++) {
            Summoner newSummoner = new Summoner(null, userId, "summoner"+i, "KR1", "Gold", 1, 22, 100, 50, 20, 123);
            summonerRepository.save(newSummoner);
        }

        // when // then
        Assertions.assertThatThrownBy(() -> summonerService.addSummoner(userId, "summoner31", "KR1"))
                .isInstanceOf(DataConflictException.class)
                .hasMessage("추가할 수 있는 최대 인원은 30명입니다.");
    }

    @Test
    @DisplayName("소환사를 추가할 수 있다.")
    void addSummoner() throws Exception {
        // given
        Long userId = 1L;
        String summonerName = "summoner1";
        String tagLine = "KR1";

        // stubbing
        SummonerApiTotalDTO apiResult = createSummonerApiTotalDTO(summonerName, tagLine);
        when(riotUtils.getSummoner(summonerName, tagLine)).thenReturn(apiResult);

        // when
        SummonerDTO result = summonerService.addSummoner(userId, summonerName, tagLine);

        // then
        assertThat(result.getSummonerName()).isEqualTo(summonerName);
        verify(riotUtils).getSummoner(summonerName, tagLine);

        Page<SummonerDTO> savedSummoners = summonerRepository.findMySummoners(userId, PageRequest.of(0, 30));

        Assertions.assertThat(savedSummoners)
                .isNotNull()
                .hasSize(1)
                .extracting("summonerName", "tagLine")
                .contains(
                        tuple(summonerName, tagLine)
                );
    }

    @Test
    @DisplayName("소환사정보를 갱신할 수 있다.")
    void updateSummoner() throws Exception {
        // given
        Long userId = 1L;
        String summonerName = "summoner1";
        String tagLine = "KR1";

        Summoner summoner = new Summoner(null, userId, summonerName, tagLine, "Gold", 1, 22, 100, 50, 20, 123);
        summonerRepository.save(summoner);

        String updatedSummonerName = "summoner2";
        String updatedTagLine = "KR1";

        // stubbing
        SummonerApiTotalDTO apiResult = createSummonerApiTotalDTO(updatedSummonerName, updatedTagLine);
        when(riotUtils.getSummoner(summonerName, tagLine)).thenReturn(apiResult);

        // when
        SummonerDTO updatedSummoner = summonerService.updateSummoner(summoner.getId());

        // then
        assertThat(updatedSummoner.getSummonerName()).isEqualTo(updatedSummonerName);
        assertThat(updatedSummoner.getTagLine()).isEqualTo(updatedTagLine);
    }

    @Test
    @DisplayName("추가한 소환사를 삭제할 수 있다.")
    void deleteSummoner() throws Exception {
        // given
        Long userId = 1L;
        Summoner summoner = new Summoner(null, userId, "summoner1", "KR1", "Gold", 1, 22, 100, 50, 20, 123);
        summonerRepository.save(summoner);

        int cnt = summonerRepository.countByUserId(userId);
        assertThat(cnt).isEqualTo(1);

        // when
        summonerService.deleteSummoner(summoner.getId());

        // then
        Assertions.assertThat(summonerRepository.findMySummoners(userId, PageRequest.of(0, 30))).isEmpty();
    }

    private SummonerApiTotalDTO createSummonerApiTotalDTO(String summonerName, String tagLine) {
        return new SummonerApiTotalDTO(summonerName, tagLine, "GOLD", 4, 11, 100, 50, 20, 123);
    }
}