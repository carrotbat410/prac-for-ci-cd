package carrotbat410.lol.jwt;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.auth.UserTokenDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/login") || path.equals("/join") || path.equals("/error") || path.contains("/actuator") || path.equals("/boards"); //TODO SecurityConfig에도 명시해야함 => 하드코딩으로 관리되어 있어서, 두 곳 수정해야하는데 개선할 수 있으면 개선하기
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");


        //이 필터는 모든 요청에 적용됨. 그래서 아래 헤더 검증 코드는 로그인 안했으면 검증할 필요없으니 이 필터는 끝내는거
        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("no token");

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        String token = authorization.split(" ")[1];

        //token 검증
        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
            return ;
        } catch (SignatureException e) {
            //TODO 텔레그램 메시지 남기기, 누군가 토큰 변조함
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("token is invalid");
            return ;
        } catch (Exception e) {
            //TODO 텔레그램 메시지 남기기, 다른 예외
            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        Long id = jwtUtil.getId(token);

        UserTokenDTO userTokenDTO = UserTokenDTO.of(id, username, "tmppassword", role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userTokenDTO);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
