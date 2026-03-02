package com.prj.flashdeal.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj.flashdeal.domain.member.dto.request.MemberUpdateRequest;
import com.prj.flashdeal.domain.member.dto.request.PasswordChangeRequest;
import com.prj.flashdeal.domain.member.dto.response.MemberProfileResponse;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.entity.MemberStatus;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    // ========== getMyProfile ==========

    @Test
    @DisplayName("내 프로필 조회 성공")
    void getMyProfile_Success() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        given(memberRepository.findById(userId)).willReturn(Optional.of(member));

        // when
        MemberProfileResponse response = memberService.getMyProfile(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("내 프로필 조회 실패 - 존재하지 않는 회원")
    void getMyProfile_Fail_MemberNotFound() {
        // given
        Long userId = 1L;
        given(memberRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMyProfile(userId))
            .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("내 프로필 조회 실패 - 비활성 회원 (BANNED)")
    void getMyProfile_Fail_InactiveMember() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        ReflectionTestUtils.setField(member, "status", MemberStatus.BANNED);

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.getMyProfile(userId))
            .isInstanceOf(MemberException.class);
    }

    // ========== getMember ==========

    @Test
    @DisplayName("회원 조회 성공")
    void getMember_Success() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        given(memberRepository.findById(userId)).willReturn(Optional.of(member));

        // when
        Member result = memberService.getMember(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    void getMember_Fail_NotFound() {
        // given
        Long userId = 999L;
        given(memberRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(userId))
            .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("회원 조회 실패 - 탈퇴한 회원 (소프트 딜리트)")
    void getMember_Fail_DeletedMember() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        member.delete(); // isDeleted = true, status = WITHDRAWN

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.getMember(userId))
            .isInstanceOf(MemberException.class);
    }

    // ========== updateMemberInfo ==========

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberInfo_Success() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        MemberUpdateRequest request = new MemberUpdateRequest();
        ReflectionTestUtils.setField(request, "name", "수정된이름");
        ReflectionTestUtils.setField(request, "phoneNumber", "010-9999-8888");

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));

        // when
        MemberProfileResponse response = memberService.updateMemberInfo(userId, request);

        // then
        assertThat(response.name()).isEqualTo("수정된이름");
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 회원")
    void updateMemberInfo_Fail_MemberNotFound() {
        // given
        Long userId = 1L;
        MemberUpdateRequest request = new MemberUpdateRequest();
        ReflectionTestUtils.setField(request, "name", "수정된이름");
        ReflectionTestUtils.setField(request, "phoneNumber", "010-9999-8888");

        given(memberRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.updateMemberInfo(userId, request))
            .isInstanceOf(MemberException.class);
    }

    // ========== verifyPassword ==========

    @Test
    @DisplayName("비밀번호 확인 성공")
    void verifyPassword_Success() {
        // given
        Long userId = 1L;
        String rawPassword = "Test1234!";
        Member member = createMember(userId);
        ReflectionTestUtils.setField(member, "password", "encodedPassword");

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(rawPassword, "encodedPassword")).willReturn(true);

        // when & then
        assertThatCode(() -> memberService.verifyPassword(userId, rawPassword))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비밀번호 확인 실패 - 비밀번호 불일치")
    void verifyPassword_Fail_InvalidPassword() {
        // given
        Long userId = 1L;
        String wrongPassword = "wrongPassword";
        Member member = createMember(userId);
        ReflectionTestUtils.setField(member, "password", "encodedPassword");

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(wrongPassword, "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.verifyPassword(userId, wrongPassword))
            .isInstanceOf(MemberException.class);
    }

    // ========== changePassword ==========

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_Success() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        ReflectionTestUtils.setField(member, "password", "encodedCurrentPassword");

        PasswordChangeRequest request = new PasswordChangeRequest();
        ReflectionTestUtils.setField(request, "currentPassword", "Current1234!");
        ReflectionTestUtils.setField(request, "newPassword", "New1234!!");

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("Current1234!", "encodedCurrentPassword")).willReturn(true);
        given(passwordEncoder.encode("New1234!!")).willReturn("encodedNewPassword");

        // when
        memberService.changePassword(userId, request);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void changePassword_Fail_InvalidCurrentPassword() {
        // given
        Long userId = 1L;
        Member member = createMember(userId);
        ReflectionTestUtils.setField(member, "password", "encodedCurrentPassword");

        PasswordChangeRequest request = new PasswordChangeRequest();
        ReflectionTestUtils.setField(request, "currentPassword", "WrongPassword!");
        ReflectionTestUtils.setField(request, "newPassword", "New1234!!");

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("WrongPassword!", "encodedCurrentPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.changePassword(userId, request))
            .isInstanceOf(MemberException.class);
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember(Long id) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("encodedPassword")
            .name("테스트유저")
            .phoneNumber("010-1234-5678")
            .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}
