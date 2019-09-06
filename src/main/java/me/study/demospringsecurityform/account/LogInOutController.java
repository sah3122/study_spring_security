package me.study.demospringsecurityform.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by dongchul on 2019-09-05.
 */
@Controller
public class LogInOutController {

    @GetMapping("/login") // post 요청은 spring security 가 제공하는 filter 사용
    public String loginForm() {
        return "login";
    }

    @GetMapping("/logut")
    public String logoutForm() {
        return "logout";
    }

}
