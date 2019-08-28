package me.study.demospringsecurityform.form;

import me.study.demospringsecurityform.account.Account;
import me.study.demospringsecurityform.account.AccountContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by dongchul on 2019-08-27.
 */
@Service
public class SampleService {
    public void dashboard() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //securitycontextholder에서 authentication을 꺼낼수 있다.
        Object princial = authentication.getPrincipal(); // return 되는 principal은 userdetailsservice에서 리턴하는 객체 타입.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 유저가 가지고 있는 권한
        Object credentials = authentication.getCredentials(); // credential
        boolean authenticated = authentication.isAuthenticated();
    }


    public Account getAccount() {
        Account account = AccountContext.getAccount();
        System.out.println(account.getUsername());
        return account;
    }
}
