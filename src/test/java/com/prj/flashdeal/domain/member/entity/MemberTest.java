package com.prj.flashdeal.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.member.exception.MemberException;

@DisplayName("Member 엔티티 단위 테스트")
class MemberTest {

    // ========== validateActive ==========

    @Test
    @DisplayName("validateActive 성공 - ACTIVE 상태")
    void validateActive_Success() {
        // given
        Member member = createMember();

        // when & then
        assertThatCode(() -> member.validateActive())
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateActive 실패 - WITHDRAWN 상태")
    void validateActive_Fail_Withdrawn() {
        // given
        Member member = createMember();
        member.delete(); // status = WITHDRAWN

        // when & then
        assertThatThrownBy(() -> member.validateActive())
            .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("validateActive 실패 - DORMANT 상태")
    void validateActive_Fail_Dormant() {
        // given
        Member member = createMember();
        setField(member, "status", MemberStatus.DORMANT);

        // when & then
        assertThatThrownBy(() -> member.validateActive())
            .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("validateActive 실패 - BANNED 상태")
    void validateActive_Fail_Banned() {
        // given
        Member member = createMember();
        setField(member, "status", MemberStatus.BANNED);

        // when & then
        assertThatThrownBy(() -> member.validateActive())
            .isInstanceOf(MemberException.class);
    }

    // ========== updateInfo ==========

    @Test
    @DisplayName("updateInfo - 이름과 전화번호가 변경됨")
    void updateInfo_Success() {
        // given
        Member member = createMember();
        String newName = "수정된이름";
        String newPhone = "010-9999-8888";

        // when
        member.updateInfo(newName, newPhone);

        // then
        assertThat(member.getName()).isEqualTo(newName);
        assertThat(member.getPhoneNumber()).isEqualTo(newPhone);
    }

    // ========== delete ==========

    @Test
    @DisplayName("delete - isDeleted = true, status = WITHDRAWN")
    void delete_Success() {
        // given
        Member member = createMember();

        // when
        member.delete();

        // then
        assertThat(member.getIsDeleted()).isTrue();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
    }

    // ========== changePassword ==========

    @Test
    @DisplayName("changePassword - 새 비밀번호로 교체됨")
    void changePassword_Success() {
        // given
        Member member = createMember();
        String newEncodedPassword = "newEncodedPassword";

        // when
        member.changePassword(newEncodedPassword);

        // then
        assertThat(member.getPassword()).isEqualTo(newEncodedPassword);
    }

    // ========== 빌더 기본값 ==========

    @Test
    @DisplayName("빌더로 생성한 회원의 기본값 - USER 역할, ACTIVE 상태, 삭제되지 않음")
    void builder_DefaultValues() {
        // when
        Member member = createMember();

        // then
        assertThat(member.getRole()).isEqualTo(Role.USER);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getIsDeleted()).isFalse();
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember() {
        return Member.builder()
            .email("test@test.com")
            .password("encodedPassword")
            .name("테스트유저")
            .phoneNumber("010-1234-5678")
            .build();
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
