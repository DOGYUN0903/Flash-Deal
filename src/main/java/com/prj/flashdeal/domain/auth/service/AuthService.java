package com.prj.flashdeal.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.auth.dto.request.LoginRequest;
import com.prj.flashdeal.domain.auth.dto.request.SignupRequest;
import com.prj.flashdeal.domain.auth.dto.response.TokenResponse;
import com.prj.flashdeal.domain.member.entity.Address;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.entity.MemberStatus;
import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.domain.member.repository.MemberRepository;
import com.prj.flashdeal.global.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public TokenResponse signup(SignupRequest request) {

        // 1. 이메일 중복 확인
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(MemberErrorCode.ALREADY_EXISTS_EMAIL);
        }

        // 2. 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // 3. 멤버 객체 생성
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

        // 4. 멤버 DB 저장
        Member savedMember = memberRepository.save(member);

        // 5. 토큰 생성
        String accessToken = jwtUtil.createToken(
            savedMember.getId(),
            savedMember.getEmail(),
            savedMember.getName(),
            savedMember.getRole()
        );

        // 6. 결과 반환
        return TokenResponse.of(accessToken);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {

        // 1. 이메일을 활용하여 멤버 찾기
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. 멤버 상태가 '활동'인지 확인
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.INACTIVE_MEMBER);
        }

        // 3. 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        // 4. 토큰 생성
        String accessToken = jwtUtil.createToken(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getRole()
        );

        // 5. 결과 반환
        return TokenResponse.of(accessToken);
    }
}
