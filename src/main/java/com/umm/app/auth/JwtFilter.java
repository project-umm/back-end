package com.umm.app.auth;

import com.umm.app.impl.CustomUserDetailsService;
import com.umm.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String path = request.getRequestURI();
        String auth = request.getHeader("Authorization");

        if (auth == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(auth);
        try {
            if (token.isBlank()) {
                SecurityContextHolder.clearContext();
                responseError(response, HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
                return;
            }

            if (jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUsernameFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        UsernamePasswordAuthenticationToken.authenticated(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                SecurityContextHolder.clearContext();
                responseError(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            }
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            responseError(response, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            responseError(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        } catch (BaseException e) {
            SecurityContextHolder.clearContext();
            responseError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

//    private boolean isPublicApi(String path) {
//        return "/api/test".equals(path) ||
//                path.startsWith("/api/prefix");
//    }

    private String resolveToken(String auth) {
        if (!StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            throw new BaseException(401, "액세스 토큰의 Grant Type은 Bearer입니다.");
        }
        return auth.substring(7);
    }

    private void responseError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
