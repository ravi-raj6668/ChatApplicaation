package com.test.ChatApplicaation.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserSecurityUtil userSecurityUtil;
    private final JwtHelper jwtHelper;

    @Autowired
    public JwtAuthenticationFilter(UserSecurityUtil userSecurityUtil, JwtHelper jwtHelper) {
        this.userSecurityUtil = userSecurityUtil;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        log.info("Header {} : ", requestHeader);
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
           // token = requestHeader.substring(7);
            token = requestHeader.split(" ")[1].trim();
            try {
                username = jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                log.error("Illegal Argument while fetching the username..!!", e);
            } catch (ExpiredJwtException e) {
                log.error("Given jwt token is expired..!!");
            } catch (MalformedJwtException e) {
                log.error("Some changes has done in token !! Invalid token", e);
            } catch (Exception e) {
                log.error("Error while getting token {} : ", e.getMessage());
            }
        } else {
            log.info("Invalid token value...!!!");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userSecurityUtil.loadUserByUsername(username);
            boolean validateToken = jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                log.info("Validation fails...!!❌");
            }
        }
        filterChain.doFilter(request, response);
    }
}