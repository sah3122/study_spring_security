package me.study.demospringsecurityform.form;

import me.study.demospringsecurityform.account.Account;
import me.study.demospringsecurityform.account.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    SampleService sampleService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Test
    // @WithMockUser로 테스트 가능하다
    public void dashboard() {
        Account account = new Account();
        account.setRole("USER");
        account.setUsername("dong");
        account.setPassword("!23");

        accountService.createNew(account);

        UserDetails userDetails = accountService.loadUserByUsername("dong");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, 123);
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        sampleService.dashboard();
    }

}