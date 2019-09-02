package me.study.demospringsecurityform.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService { //userdetailsservice는 유저정보를 spring securityd에 제공하는 역할을 한다.

    @Autowired AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }

        return User.builder() // spring security 에서 userdetails 객체를 user 객체를 이용해 만들수 있도록 제공 해당 객체가 principal이다.
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account createNew(Account account) {
        account.encodePassword(passwordEncoder); // {noop} 인코딩 spring에선 반드시 encoding해야함 encoder bean 등록 필수.
        return this.accountRepository.save(account);
    }
    
}
