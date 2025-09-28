package com.prj.flashdeal.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.auth.dto.request.SignupRequest;
import com.prj.flashdeal.domain.auth.dto.response.SignupResponse;
import com.prj.flashdeal.domain.member.entity.Address;
import com.prj.flashdeal.domain.member.entity.Member;
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
    public SignupResponse signup(SignupRequest request) {

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
        return SignupResponse.of(accessToken);
    }
}
