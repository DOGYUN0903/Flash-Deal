package com.prj.flashdeal.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.prj.flashdeal.global.security.CustomUserDetails;

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

        return LoginResponse.of(savedMember.getId(), savedMember.getName(), savedMember.getRole());
    }

    public LoginResponse login(LoginRequest request) {
        // 1. 인증 처리
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. SecurityContext에 저장 (requireExplicitSave=false → 세션 자동 저장)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 인증된 사용자 정보 반환
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return LoginResponse.of(userDetails.getUserId(), userDetails.getName(), userDetails.getUserRole());
    }
}
