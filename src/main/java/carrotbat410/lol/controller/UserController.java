package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.exhandler.exception.AccessDeniedException;
import carrotbat410.lol.service.UserService;
import carrotbat410.lol.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public SuccessResult joinProcess(@RequestBody @Validated JoinDTO joinDTO) {
        userService.joinProcess(joinDTO);

        return new SuccessResult("ok");
    }

    @DeleteMapping("/user")
    public SuccessResult deleteUser() {

        String username = SecurityUtils.getCurrentUsernameFromAuthentication();

        if(username.equals("test1")) throw new AccessDeniedException("test 계정은 회원 탈퇴할 수 없습니다.");

        Long id = SecurityUtils.getCurrentUserIdFromAuthentication();

        userService.deleteUser(id);

        return new SuccessResult("ok");
    }

}
