package carrotbat410.lol.repository;

import carrotbat410.lol.dto.summoner.QSummonerDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.entity.User;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static carrotbat410.lol.entity.QSummoner.*;

//! 클래스명은 spring data jpa interface + Impl 이여야함. =>  SummonerRepository+Imple
public class SummonerRepositoryImpl implements SummonerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SummonerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Page<SummonerDTO> findMySummoners(Long userId, Pageable pageable) {

        List<SummonerDTO> content = queryFactory
                .select(new QSummonerDTO(
                        summoner.id,
                        summoner.summonerName,
                        summoner.tagLine,
                        summoner.tier,
                        summoner.rank1,
                        summoner.mmr,
                        summoner.level,
                        summoner.wins,
                        summoner.losses,
                        summoner.iconId,
                        summoner.updated_at)
                )
                .from(summoner)
                .where(summoner.userId.eq(userId))
                .offset(0)  //.offset(pageable.getOffset())
                .limit(30)  //.limit(pageable.getPageSize())
                .orderBy(summoner.created_at.asc())
                .fetch();

        //! 여기는 페이징하지 않기 떄문에 count Query 날릴 필요 없다. (추후 게시판 기능에선 필요)
//        long count = queryFactory
//                .select(summoner.id)
//                .from(summoner)
//                .where(summoner.userId.eq(userId))
//                .fetch()
//                .size();

        return new PageImpl<>(content, pageable, content.size());
//        return content;
    }

    @Override
    public Summoner findExistingSummoner(Long userId, String summonerName, String tagLine) {
        summonerName = summonerName.toLowerCase();

        JPAQuery<Summoner> query = queryFactory
                .select(summoner)
                .from(summoner)
                .where(summoner.userId.eq(userId))
                .where(summoner.tagLine.eq(tagLine));

        if(isPureEnglish(summonerName)) {
            query.where(Expressions.stringTemplate("LOWER({0})", summoner.summonerName).eq(summonerName));
        }else {
            summonerName = summonerName.replaceAll("\\s", "");
            query.where(Expressions.stringTemplate("REPLACE(LOWER({0}), ' ', '')", summoner.summonerName).eq(summonerName));
        }

        Summoner content = query.fetchOne();

        return content;
    }

    private boolean isPureEnglish(String summonerName) {
        //공백은 있든 없든 판단에 상관 없다.
        return summonerName.replaceAll("\\s", "").matches("^[a-zA-Z]+$");
    }
}
