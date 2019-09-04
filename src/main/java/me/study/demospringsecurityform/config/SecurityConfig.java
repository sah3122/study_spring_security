package me.study.demospringsecurityform.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // securitycontextpersistentfiler
    // http session에서 기존에 저장되어있는 security context를 가져온다 async filter 다음 순서로 실행되는 filter
    // role 계층구조 설정.

    /**
     * XContentTypeOptionsHeaderWriter 마임타입 스니핑 방어.
     * XXssProtectionHeaderWriter 브라우저에 내장된 XSS 필터 적용.
     * CacheControlHeadersWriter 캐시 히스토리 취약점 방어.
     * HstsHeaderWriter HTTPS로만 소통하도록 강제
     * XFrameOptionsHeaderWriter clickjacking 방어
     *
     */

    /**
     * CsrfFilter
     * CSRF  어택 방지 필터
     * CSRF 토큰을 이용한 검증 진행
     * form 기반의 application 에서는 사용권장, rest api 기반은 번거러울수 있음.
     *
     */

    public AccessDecisionManager accessDecisionManager() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(handler);

        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);
        return new AffirmativeBased(voters);
    }

    // role 계층구조 설정.
    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // permitall 을 사용해서 무시하는건 추천하지 않고 아래 방법이 더 효율적인 방법이다. spring security를 적용하지 않을건데requestmatcher를 사용하면 쓸모없는 filter를 타게 된다.
        //web.ignoring().mvcMatchers("/favicon.ico"); //favicon만 ignore
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //정적 자원 모두 ignore
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "/signup").permitAll() // 모두 허용, 동적으로 처리하는 resource는 filter를 실행해야 한다.
                .mvcMatchers("/admin").hasRole("ADMIN") // 어드민롤 있어야
                .mvcMatchers("/user").hasRole("USER")
                .anyRequest().authenticated() // 나머지는 인증된 사용자만 인가
                //.accessDecisionManager(accessDecisionManager()) // custom accessdecision manager 사용, Role 계층 구조 적용
                //.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 해당방법은 추천하지 않음. 불필요한 filter를 실행시킴
                .expressionHandler(expressionHandler()); // express handler 만 custom, Role hierarchy 사용위해
        http.formLogin(); // 폼로그인 사용
        // 메서드 체이닝을 사용하지 않아도 됨
        http.httpBasic();    // http basic 사용

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); // 상위 쓰레드에서 하위 쓰레드까지의 securitycontext를 공유하기 위해 선언

    }

}
