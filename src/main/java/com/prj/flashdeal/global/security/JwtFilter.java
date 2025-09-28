package com.prj.flashdeal.global.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prj.flashdeal.domain.member.entity.Role;
import com.prj.flashdeal.global.security.dto.UserPrinciple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
        ServletException,
        IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // 2. 토큰이 존재하고, 'Bearer '로 시작하는 경우에만 인증 절차를 진행합니다.
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String jwt = jwtUtil.resolveToken(authorizationHeader);

            try {
                // 3. 토큰이 유효하면, Claims를 추출하고 인증 객체를 만들어 SecurityContext에 저장합니다.
                Claims claims = jwtUtil.extractClaims(jwt);

                Role userRole = Role.valueOf(claims.get("userRole", String.class));
                long userId = Long.parseLong(claims.getSubject());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    new UserPrinciple(userId, userRole), // UserPrincipal 사용을 강력히 권장합니다.
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (SecurityException | MalformedJwtException e) {
                log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
                return;
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
                return;
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
                return;
            } catch (Exception e) {
                log.error("Internal server error", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        // 4. 토큰이 없거나, 인증에 성공했거나, 예외가 발생하지 않은 모든 경우에 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }
}