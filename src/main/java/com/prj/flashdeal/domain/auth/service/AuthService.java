package com.prj.flashdeal.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.auth.dto.request.LoginRequest;
import com.prj.flashdeal.domain.auth.dto.request.SignupRequest;
import com.prj.flashdeal.domain.auth.dto.response.LoginResponse;
import com.prj.flashdeal.domain.member.entity.Address;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.domain.member.repository.MemberRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse signup(SignupRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(MemberErrorCode.ALREADY_EXISTS_EMAIL);
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
            .email(request.getEmail())
            .password(encodePassword)
            .name(request.getName())
            .address(
                Address.of(
                    request.getZipcode(),
                    request.getStreet(),
                    request.getDetail()
                )
            )
            .phoneNumber(request.getPhoneNumber())
            .build();

        Member savedMember = memberRepository.save(member);

        return LoginResponse.of(savedMember.getId(), savedMember.getEmail(), savedMember.getName());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        try {
            // 1. AuthenticationManager → CustomUserDetailsService → BCrypt 비밀번호 검증
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. SecurityContext 설정
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 3. HttpSession에 SecurityContext 저장 → JSESSIONID 쿠키 자동 발급
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
            );

        } catch (BadCredentialsException e) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        // 4. 응답 반환
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return LoginResponse.of(member.getId(), member.getEmail(), member.getName());
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
