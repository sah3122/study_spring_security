package me.study.demospringsecurityform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info").permitAll() // 모두 허용
                .mvcMatchers("/admin").hasRole("ADMIN") // 어드민롤 있어야
                .anyRequest().authenticated() // 나머지는 인증된 사용자만 인가
                .and()
            .formLogin(); // 폼로그인 사용
        // 메서드 체이닝을 사용하지 않아도 됨
        http.httpBasic();    // http basic 사용

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication() // 인메모리 유저 등록
                .withUser("dongchul").password("{noop}123").roles("USER").and() //{noop} 는 spring security에서 암호화 하지 않았다는걸 알려주기 위해 
                .withUser("admin").password("{noop}!@#");
    }
}
