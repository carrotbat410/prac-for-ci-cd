package carrotbat410.lol.service;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SummonerRepository summonerRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private SummonerService summonerService;


    @DisplayName("이미 존재하는 username으로 회원 가입 시도 시 예외가 발생한다.")
    @Test
    void joinProcessWithDuplicateUser() {
        // given
        JoinDTO joinDTO1 = new JoinDTO("duplicateUser", "password123");
        JoinDTO joinDTO2 = new JoinDTO("duplicateUser", "password456");
        userService.joinProcess(joinDTO1);

        // when // then
        assertThatThrownBy(() -> userService.joinProcess(joinDTO2))
                .isInstanceOf(DataConflictException.class)
                .hasMessage("이미 존재하는 유저입니다.");
    }

    @DisplayName("회원 가입을 성공적으로 처리한다.")
    @Test
    void joinProcess() {
        // given
        String username = "test123";
        String password = "password123";
        JoinDTO joinDTO = new JoinDTO(username, password);

        // when
        userService.joinProcess(joinDTO);

        // then
        User findUser = userRepository.findByUsername(username);

        assertThat(username).isNotNull();
        assertThat(username).isEqualTo(findUser.getUsername());
        assertThat(bCryptPasswordEncoder.matches(password, findUser.getPassword())).isTrue();
    }

    @DisplayName("회원 탈퇴가 요청되면 user, summoner 테이블의 데이터가 hard delete 된다.")
    @Test
    void deleteUser() {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "password123");
        userService.joinProcess(joinDTO); // user 테이블에 데이터 저장

        User findUser = userRepository.findByUsername("test123");

        Summoner summoner = new Summoner(null, findUser.getId(), "hide on bush", "KR1", "GOLD", 1, 22, 100, 100, 70, 123);
        summonerRepository.save(summoner); //summoner 테이블에 데이터 저장

        // when
        userService.deleteUser(findUser.getId());

        // then
        assertThat(userRepository.findById(findUser.getId())).isEmpty();
        assertThat(summonerRepository.findMySummoners(findUser.getId(), PageRequest.of(0, 30))).isEmpty();
    }


}