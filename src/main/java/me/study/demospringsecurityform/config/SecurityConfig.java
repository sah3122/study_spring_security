package me.study.demospringsecurityform.config;

import me.study.demospringsecurityform.account.AccountService;
import me.study.demospringsecurityform.common.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AccountService accountService;
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

    /**
     * RequestCacheAwareFilter
     * 캐시된 요청이 있으면 해당 작업 후 캐시된 요청 처리하는 필터
     * dashboard 접근시 login 요청을 먼저 처리 하고 dashboard 요청 처리하게 해주는 필터
     */

    /**
     * SessionManagementFilter
     * 세션 변조 방지 전략 설정 sessionFixation
     * changeSessionId (서블릿 3.1 이상 지원)
     * migrateSession (서블릿 3.0 이하)
     * 유효하지 않는 세션 리다이렉트
     *
     * 동시성 제어
     * maximumSessions
     * 추가 로그인을 막을지 여부 설정 (기본값 false)
     *
     * 세션 생성 전략 선언
     *
     * Spring Session -> session cluster project 참고
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
        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class); // custom filter 추가하기

        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "/signup").permitAll() // 모두 허용, 동적으로 처리하는 resource는 filter를 실행해야 한다.
                .mvcMatchers("/admin").hasRole("ADMIN") // 어드민롤 있어야
                .mvcMatchers("/user").hasRole("USER")
                .mvcMatchers("/test").fullyAuthenticated() // rememberme 로 인증한 사용자도 해당 페이지 접근은 다시 인증 받아야하는 설정
                .anyRequest().authenticated() // 나머지는 인증된 사용자만 인가
                //.accessDecisionManager(accessDecisionManager()) // custom accessdecision manager 사용, Role 계층 구조 적용
                //.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 해당방법은 추천하지 않음. 불필요한 filter를 실행시킴
                .expressionHandler(expressionHandler()); // express handler 만 custom, Role hierarchy 사용위해
        http.formLogin() // 폼로그인 사용
            .loginPage("/login")// custom login page 설정, defaultlogin / logout pagegeneratingfilter 사용하지 않음
            .permitAll();
            //.usernameParameter("my-parameter") // parameter custom
            //.passwordParameter("my-password");
        // 메서드 체이닝을 사용하지 않아도 됨
        http.httpBasic();    // http basic 사용
        /**
         * basic authentication
         * Authorization: Basic sdaklmflsamfklas (username:password base64 인코딩된 값) 를 담아 보내는 방식
         * curl localhost:8080 -u dong:123
         * 보안에 취약하기 때문에 https 사용 권장
         */

        http.logout() //logout 처리
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
                //.addLogoutHandler() logout handler 추가
                //.invalidateHttpSession(); 세션을 만료 시킬것인지
                // .deleteCookies("")쿠키 기반 인증시 설정

//        http.anonymous() anonymous 설정 관련 필터 Null Object Pattern 따름 실제 null이 아닌 null에 매칭되는 object를 사용하는 패턴
//                .principal()
//                .authorities()
//                .key();

        http.sessionManagement()
                .invalidSessionUrl("/login")
                .maximumSessions(1)
                    .expiredUrl("/")//만료되었을때
                    .maxSessionsPreventsLogin(false)
                .and()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // rest api에서 사용하는 전략


        // TODO ExceptionTranslatorFilter -> FilterSecurityInterceptor (AccessDecisionManager, AffirmativeBased)
        // TODO AuthenticationException 인증이 안된 경우 -> AuthenticationEntryPoint
        // TODO AccessDeniedException 권한이 부족한 경우 -> AccessDeniedHandler

        http.exceptionHandling()
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = principal.getUsername();
                    System.out.println(username + "is denied to access " + httpServletRequest.getRequestURI()); // logger를 써야함.
                    httpServletResponse.sendRedirect("/access-denied");
                });
                //.accessDeniedPage("/access-denied");


        http.rememberMe()
                .rememberMeParameter("remember")
                .userDetailsService(accountService)
                .key("remember-me-sample");
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); // 상위 쓰레드에서 하위 쓰레드까지의 securitycontext를 공유하기 위해 선언

    }

}
