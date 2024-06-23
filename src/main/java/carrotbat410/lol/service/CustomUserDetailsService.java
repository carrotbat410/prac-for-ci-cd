package carrotbat410.lol.service;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.auth.UserTokenDTO;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null) return null;

        UserTokenDTO userTokenDTO = UserTokenDTO.of(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
        return new CustomUserDetails(userTokenDTO);
    }

}
