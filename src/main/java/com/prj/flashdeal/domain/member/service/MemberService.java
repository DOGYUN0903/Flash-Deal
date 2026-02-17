package com.prj.flashdeal.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.member.dto.request.MemberUpdateRequest;
import com.prj.flashdeal.domain.member.dto.request.PasswordChangeRequest;
import com.prj.flashdeal.domain.member.dto.response.MemberProfileResponse;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.entity.MemberStatus;
import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberProfileResponse getMyProfile(Long userId) {
        // 1. 멤버 조회
        Member member = getMember(userId);

        // 2. 멤버 상태가 '활동' 인지 확인
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.INACTIVE_MEMBER);
        }

        return MemberProfileResponse.from(member);
    }

    @Transactional(readOnly = true)
    public Member getMember(Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getIsDeleted()) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        return member;
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public MemberProfileResponse updateMemberInfo(Long userId, MemberUpdateRequest request) {
        Member member = getMember(userId);

        member.updateInfo(request.getName(), request.getPhoneNumber());

        return MemberProfileResponse.from(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        Member member = getMember(userId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.changePassword(encodedPassword);
    }
}
