package me.study.demospringsecurityform.form;

import me.study.demospringsecurityform.account.Account;
import me.study.demospringsecurityform.account.AccountContext;
import me.study.demospringsecurityform.common.SecurityLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

/**
 * Created by dongchul on 2019-08-27.
 */
@Service
public class SampleService {
    @Secured("ROLE_USER") // 메소드 호출 전에 권한 검사 --- method security
    @RolesAllowed("ROLE_USER") // 메소드 호출 전에 권한 검사 --- method security
    @PreAuthorize("hasRole('USER')") // 메소드 호출 전에 권한 검사 --- method security
    @PostAuthorize("hasRole('USER')") // 메소드 호출 후에 권한 검사 --- method security
    public void dashboard() {
        //인증은 authencationmanager에서 처리하고 인증된 정보를 securitycontextholder에서 가지고 있다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //securitycontextholder에서 authentication을 꺼낼수 있다.
        Object princial = authentication.getPrincipal(); // return 되는 principal은 userdetailsservice에서 리턴하는 객체 타입.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 유저가 가지고 있는 권한
        Object credentials = authentication.getCredentials(); // credential
        boolean authenticated = authentication.isAuthenticated();
    }

    /**
     * authenticationmanager가 인증을 마친뒤 리턴 받는 Authentication은 UsernamePasswordAuthenticationFilter 와 SecurityContextPersistenceFilter를 거치며 리턴된다.
     * UsernamePasswordAuthenticationFilter는 폼인증을 처리하는 시큐리티 필터이며 인증된 Authentication 객체를 SecurityContextHolder에 넣어준다.
     * SecurityContextPersistenceFilter는 SecurityContext를 HTTP session에서 캐시(기본)하여 여러 요청에서 Authentication을 공유할 수 있는 공유 필터 이다.
     * SecurityContextRepositoy를 교체하여 세션을 HTTP session이 아닌 다른곳에 저장하는것도 가능.
     *
     */
    public Account getAccount() {
        Account account = AccountContext.getAccount();
        System.out.println(account.getUsername());
        return account;
    }

    @Async //기본적으로 async를 사용한곳에선 securitycontext공유가 되지 않는다. 공유 하려면 strategy를 설정해야한다. MODE_INHERITABLETHREADLOCAL
    public void asyncService() {
        SecurityLogger.log("async service");
        System.out.println("Async service is called");
    }
}
