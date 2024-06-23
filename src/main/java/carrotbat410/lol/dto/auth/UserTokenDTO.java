package carrotbat410.lol.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserTokenDTO {
    private final Long id;
    private final String username;
    private final String password;
    private final String role;

    public static UserTokenDTO of(Long id, String username, String password, String role) {
        return new UserTokenDTO(id, username, password, role);
    }
}