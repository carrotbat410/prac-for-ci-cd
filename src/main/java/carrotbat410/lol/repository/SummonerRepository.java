package carrotbat410.lol.repository;

import carrotbat410.lol.entity.Summoner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummonerRepository extends JpaRepository<Summoner, Long>, SummonerRepositoryCustom {

//    Summoner findFirstByUserIdAndSummonerNameAndTagLine(Long userId, String summonerName, String tagLine);

    void deleteByUserId(Long userId);

    int countByUserId(Long userId);

}