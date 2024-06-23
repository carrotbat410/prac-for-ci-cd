package carrotbat410.lol.utils;

import carrotbat410.lol.dto.riot.RiotAccountApiResponseDTO;
import carrotbat410.lol.dto.riot.RiotLeagueApiResponseDTO;
import carrotbat410.lol.dto.riot.RiotSummonerApiResponseDTO;
import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.exhandler.exception.NotFoundException;
import carrotbat410.lol.exhandler.exception.RateExceededException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

//TODO 환경변수값 가져오질 못해서 @Value사용함.(@Value사용하려면 스프링 빈으로 관리되어야해서 @Component사용한거.)
//TODO @Component vs static 유틸 클래스는 어떤게 더 나은지 찾아보기
@Component
public class RiotUtils {
    @Value("${riot.api.key}")
    private String apiKey;

    @Value("${riot.api.url}")
    private String riotApiUrl;

    private final Map<String, Integer> TIER_BASE_POINTS = new HashMap<>();

     {
        TIER_BASE_POINTS.put("IRON", 0);
        TIER_BASE_POINTS.put("BRONZE", 4);
        TIER_BASE_POINTS.put("SILVER", 8);
        TIER_BASE_POINTS.put("GOLD", 12);
        TIER_BASE_POINTS.put("PLATINUM", 16);
        TIER_BASE_POINTS.put("EMERALD", 20);
        TIER_BASE_POINTS.put("DIAMOND", 24);
    }

    //! 부하테스트할떄 진짜 api 요청하면 제한먹으니 조심.
    //TODO WebClient로 바꾸기???
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    public SummonerApiTotalDTO getSummoner(String summonerName, String tagLine) {

        //TODO 추가적인 예외처리 (e.g getPuuid() 없을 경우, 요청 제한 초과 에러, API 서버 안될떄)
        //TODO 에러 메세지 텔레그램 전송 추가하기

        //TODO Limit Rate 20 requests every 1 seconds, 100 requests every 2 minutes 인데, 1초에 20번 요청만 확인하기.
        //TODO League API 30 requests every 10 seconds 는 뭐지?

        RiotAccountApiResponseDTO accountApiInfo = null;
        RiotSummonerApiResponseDTO summonerApiInfo = null;
        RiotLeagueApiResponseDTO leagueApiInfo = null;

        try {
            accountApiInfo = getAccountInfo(summonerName, tagLine);
            summonerApiInfo = getSummonerInfo(accountApiInfo.getPuuid());
            leagueApiInfo = getLeagueInfo(summonerApiInfo.getId());
        }catch (HttpClientErrorException e) {

            Integer statusCode = e.getStatusCode().value();

            if(statusCode == 404) throw new NotFoundException("RIOT API에 등록되지 않은 소환사명입니다.", e);
            if(statusCode == 429) throw new RateExceededException("현재 서버에 너무 많은 요청이 있습니다. 잠시 후 다시 요청해주세요.", e);

            throw new HttpClientErrorException(e.getStatusCode());
        }

        Integer numberRank = RankStringToNumber(leagueApiInfo.getRank());
        Integer mmr = CalculatMmr(leagueApiInfo.getTier(), numberRank);

        return SummonerApiTotalDTO.from(accountApiInfo, summonerApiInfo, leagueApiInfo, mmr, numberRank);
    }

    private RiotAccountApiResponseDTO getAccountInfo(String summonerName, String tagLine) {

        URI uri = UriComponentsBuilder
                .fromUriString("https://asia.api.riotgames.com")
                .path("/riot/account/v1/accounts/by-riot-id/{summonerName}/{tagLine}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerName, tagLine)
                .toUri();

        return restTemplate.getForObject(uri, RiotAccountApiResponseDTO.class);
    }

    private RiotSummonerApiResponseDTO getSummonerInfo(String puuid) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/summoner/v4/summoners/by-puuid/{puuid}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(puuid)
                .toUri();

        return restTemplate.getForObject(uri, RiotSummonerApiResponseDTO.class);
    }

    private RiotLeagueApiResponseDTO getLeagueInfo(String summonerId) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/league/v4/entries/by-summoner/{summonerId}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerId)
                .toUri();


        RiotLeagueApiResponseDTO[] leagueInfoArr = restTemplate.getForObject(uri, RiotLeagueApiResponseDTO[].class);

        RiotLeagueApiResponseDTO leagueInfo = null;
        for (RiotLeagueApiResponseDTO riotLeagueApiResponseDTO : leagueInfoArr) {
            if(riotLeagueApiResponseDTO.getQueueType().equals("RANKED_SOLO_5x5")) {
                leagueInfo = riotLeagueApiResponseDTO;
            }
        }

        if(leagueInfo == null) return new RiotLeagueApiResponseDTO("RANKED_SOLO_5x5", "UNRANKED", null, 0, 0, 0);

        return leagueInfo;
    }

    private Integer RankStringToNumber(String rank) {
        if(StringUtils.isNotBlank(rank)) {
            if (rank.equals("I")) return 1;
            if(rank.equals("II")) return 2;
            if(rank.equals("III")) return 3;
            if(rank.equals("IV")) return 4;
        }
        return null;
    }

    public String mmrToString(int mmr) {
        if(mmr == 0) return "UNRANKED";
        if(mmr == 29) return "MASTER";
        if(mmr == 30) return "GRANDMASTER";
        if(mmr == 31) return "CHALLENGER";

        String[] tiers = {"IRON", "BRONZE", "SILVER", "GOLD", "PLATINUM", "EMERALD", "DIAMOND"};
        String[] divisions = {"IV", "III", "II", "I"};

        if (mmr >= 1 && mmr <= 28) {
            int tierIndex = (mmr - 1) / 4;
            int divisionIndex = (mmr - 1) % 4;
            return tiers[tierIndex] + " " + divisions[divisionIndex];
        }

        return "NEW TIER";
    }

    private Integer CalculatMmr(String tier, Integer rank) {
        if(tier.equals("UNRANKED")) return 0;
        if(tier.equals("MASTER")) return 29;
        if(tier.equals("GRANDMASTER")) return 30;
        if (tier.equals("CHALLENGER")) return 31;

        /**
         * 마스터,그마,챌린저 점수 세분화 추가할 경우 참고사항
         * 마스터, 그마, 챌린저는 모두 rank=I로 제공함. leaguePoints=557를 참고해야함.
         */

        // IRON 4(1) ~ DIAMOND 1(28) 사이인 경우
        int basePoints = TIER_BASE_POINTS.getOrDefault(tier, 0);
        int rankPoints = 5 - rank;
        return basePoints + rankPoints;
    }

}
