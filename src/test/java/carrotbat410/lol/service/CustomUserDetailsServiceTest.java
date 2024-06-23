package carrotbat410.lol.service;

import carrotbat410.lol.entity.User;
import carrotbat410.lol.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저가 존재하면 UserDetails를 반환한다")
    void loadUserByUsername() throws Exception {
        // given
        User newUser = new User(null, "test123", "asd123", "ROLE_USER");
        userRepository.save(newUser);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(newUser.getUsername());

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(newUser.getPassword());
    }


}