package me.study.demospringsecurityform.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LoggingFilter extends GenericFilterBean { // filter를 만들기 위해 편하게 제공해주는 클래스, 평범한 ServletFilter를 만들어도 된다.
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        StopWatch stopWatch = new StopWatch(); // 시간 표시
        stopWatch.start(((HttpServletRequest)servletRequest).getRequestURI()); // task name

        filterChain.doFilter(servletRequest, servletResponse);

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());
    }
}
