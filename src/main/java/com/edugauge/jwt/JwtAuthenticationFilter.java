package com.edugauge.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{

        System.out.println("==========핉처 실행=====");

        String authorization = request.getHeader("Authorization");
        System.out.println("Authorization = " + authorization);


        if (authorization ==null || !authorization.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7);
        if(!jwtProvider.validateToken(token)){
            System.out.println(" 토큰 검증 실패");
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println(" 토큰 검증 성공");

        Long userId = jwtProvider.getUserId(token);
        UsernamePasswordAuthenticationToken
        authentication = new UsernamePasswordAuthenticationToken(
                userId, null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println(
                "인증 저장됨 = " +
                        SecurityContextHolder.getContext().getAuthentication()
        );
        filterChain.doFilter(request, response);
    }
}
