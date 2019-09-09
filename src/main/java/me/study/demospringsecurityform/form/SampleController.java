package me.study.demospringsecurityform.form;

import me.study.demospringsecurityform.account.Account;
import me.study.demospringsecurityform.account.AccountContext;
import me.study.demospringsecurityform.account.AccountRepository;
import me.study.demospringsecurityform.account.UserAccount;
import me.study.demospringsecurityform.common.CurrentUser;
import me.study.demospringsecurityform.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.concurrent.Callable;

@Controller
public class SampleController {

    @Autowired
    SampleService sampleService;

    @Autowired
    AccountRepository accountRepository;

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserAccount userAccount) { // AuthenticationPrincipal사용예
        if (userAccount == null) {
            model.addAttribute("message", "Hello Spring Security");
        } else {
            model.addAttribute("message", "Hello " + userAccount.getAccount().getUsername());
        }

        return "index";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("message", "info");
        return "info";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model, @CurrentUser Account account) { // custom anotation을 적용하여 account를 꺼낼수 있다.
        model.addAttribute("message", "hello " + account.getUsername());
        AccountContext.setAccount(accountRepository.findByUsername(account.getUsername()));
        sampleService.dashboard();
        sampleService.getAccount();
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("message", "hello admin " + principal.getName());
        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        model.addAttribute("message", "hello user " + principal.getName());
        return "user";
    }


    /**
     * webasyncmanagerintegrationfilter
     * 스프링 mvc에서 async기능을 사용할때 securitycontext를 공유하도록 도와주는 필터이다.
     */
    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler() {
        SecurityLogger.log("MVC");
        // spring 에서 제공하는 비동기 request 처리 방식중 하나
        return () -> {
            SecurityLogger.log("Callable"); // thread가 다르지만 동일한 principal이 참조된다. 해당 기능을 도와주는 필터가 webasyncmanagerintegrationfilter
            return "Async Handler";
        };
    }

    @GetMapping("/async-service")
    @ResponseBody
    public String asyncService() {
        SecurityLogger.log("MVC, before async service");
        sampleService.asyncService();
        SecurityLogger.log("MVC, before async service");
        return "Async Service";
    }
}
