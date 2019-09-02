package me.study.demospringsecurityform.common;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by dongchul on 2019-09-02.
 */
public class SecurityLogger {
    public static void log(String message) {
        System.out.println(message);
        Thread thread = Thread.currentThread();
        System.out.println("Thread : " + thread.getName());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Principal : " + principal);
    }
}
