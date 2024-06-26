package carrotbat410.lol.controller.forStudy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;

@RestController
public class MainController {

    @GetMapping("/")
    public String forCiCdTest() {
        return "hi";
    }

    @GetMapping("/main/{id}")
    public String mainP(@PathVariable("id") String id) {

        System.out.println("id = " + id);

        if(id.equals("ex")) {
            throw new RuntimeException("런타임 에러뜸");
        }
        if(id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
//        if(id.equals("user")) {
//            throw new AlreadyExistException("이미 존재하는 유저입니다.");
//        }

        //INFO SecurityContextHolder에서 잠시 생긴 세션정보 가져올 수 있다.

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "main Controller  " + "name:" + name + " role:" + role;
    }


    @GetMapping("/main/test")
    public String defaultException(@RequestParam Integer data) {
        //* data qqq로 보내면 TypeMisMatchException뜸
        //원래는 500에러 전파하는데,
        //스프링 부트에 기본으로 등록되어있는 defaulthandlerexceptionresolver가 자동으로 400에러로 잡아줌.
        System.out.println("data = " + data);
        return "ok";
    }
}
