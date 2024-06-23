package carrotbat410.lol.service;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final SummonerRepository summonerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       SummonerRepository summonerRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.summonerRepository = summonerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        validateDuplicatedUser(username);

        User user = new User(null, username, bCryptPasswordEncoder.encode(password), "ROLE_USER"); //TODO 게시판 기능 구현할거면,ENUM으로 사용하기

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        summonerRepository.deleteByUserId(id);
    }

    private void validateDuplicatedUser(String username) {
        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) throw new DataConflictException("이미 존재하는 유저입니다.");
    }

}