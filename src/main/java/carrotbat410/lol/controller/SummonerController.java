package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.summoner.AddSummonerReqeustDTO;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.service.SummonerService;
import carrotbat410.lol.utils.SecurityUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/summoners") //! 복수 주의
    public SuccessResult<Page<SummonerDTO>> getSummoners(Pageable pageable) {
        
        Long userId = SecurityUtils.getCurrentUserIdFromAuthentication();

        Page<SummonerDTO> summoners = summonerService.getSummoners(userId, pageable);

        return new SuccessResult<>("ok", summoners, summoners.getTotalElements());//TODO getTotalElements count query 최적화하기
    }

    @PostMapping("/summoner")
    public SuccessResult<SummonerDTO> addSummoner(@RequestBody @Validated AddSummonerReqeustDTO addSummonerReqeustDTO) {

        Long userId = SecurityUtils.getCurrentUserIdFromAuthentication();

        String summonerName = addSummonerReqeustDTO.getSummonerName();
        String tagLine = addSummonerReqeustDTO.getTagLine();
        if(StringUtils.isBlank(tagLine)) tagLine = "KR1";

        SummonerDTO addedSummoner = summonerService.addSummoner(userId, summonerName, tagLine);

        return new SuccessResult<>("ok", addedSummoner);
    }

    @PatchMapping("/summoner/{summonerId}")
    public SuccessResult<SummonerDTO> updateSummoner(@PathVariable("summonerId") Long summonerId) {
        SummonerDTO summonerDTO = summonerService.updateSummoner(summonerId);
        return new SuccessResult<>("ok", summonerDTO);
    }

    @DeleteMapping("/summoner/{summonerId}")
    public SuccessResult deleteSummoner(@PathVariable("summonerId") Long summonerId) {
        summonerService.deleteSummoner(summonerId);
        return new SuccessResult<>("ok");
    }


}
