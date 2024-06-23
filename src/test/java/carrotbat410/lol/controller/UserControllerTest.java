package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    //TODO 로그인 테스트 코드 작성하기

    @Test
    @DisplayName("username이 null이면 예외를 던진다.")
    void joinWithEmptyUsername() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO(null, "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("아이디를 입력해주세요."));
    }

    @Test
    @DisplayName("username은 특수 문자를 포함할 수 없다.")
    void joinWithUsernameIncludedSpecialLetter() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("asd!@#", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("아이디에 특수 문자는 사용할 수 없습니다."));
    }

    @Test
    @DisplayName("username의 길이가 6글자 미만이면 예외를 던진다.")
    void joinWithShortUsername() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test1", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 아이디 양식입니다."));
    }

    @Test
    @DisplayName("username의 길이가 20글자 초과하면 예외를 던진다.")
    void joinWithLongUsername() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("123456789012345678901", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 아이디 양식입니다."));
    }

    @Test
    @DisplayName("password가 null이면 예외를 던진다.")
    void joinWithEmptyPassword() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", null);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("password"))
                .andExpect(jsonPath("$.message").value("패스워드를 입력해주세요"));
    }

    @Test
    @DisplayName("password의 길이가 6글자 미만이면 예외를 던진다.")
    void joinWithShortPassword() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "asd12");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("password"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 패스워드 양식입니다."));
    }

    @Test
    @DisplayName("password의 길이가 20글자 초과이면 예외를 던진다.")
    void joinWithLongPassword() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "123456789012345678901");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("password"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 패스워드 양식입니다."));
    }


    @Test
    @DisplayName("유효성 검사를 통과하면 회원가입에 성공한다.")
    //TODO @WithAnonymousUser가 더 알맞는거 아닐까?
    void joinSuccess() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    //TODO ClassCastException 해결하기: extractUserIdFromAuthentication 메서드 내의 (CustomUserDetails) 캐스팅에서 에러남.
//    @Test
//    @DisplayName("회원 탈퇴할 수 있다.")
//    void deleteUserSuccess() throws Exception {
//        // given
//        Authentication authentication = Mockito.mock(Authentication.class);
//
//        UserTokenDTO userTokenDTO = new UserTokenDTO(1L, "test123", "asd123", "USER_ROLE");
//        CustomUserDetails userDetails = new CustomUserDetails(userTokenDTO);
//
//        // authentication.getPrincipal()이 userDetails를 반환하도록 설정
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//
//        // SecurityContext를 mock으로 설정하고 authentication을 반환하도록 설정
//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        // when // then
//        mockMvc.perform(MockMvcRequestBuilders.delete("/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf())
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("ok"));
//
//    }

}