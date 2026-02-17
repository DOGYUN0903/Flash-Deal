package com.prj.flashdeal.domain.member.entity;

import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 사용자 이름

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private Long balance = 0L;

    @Override
    public void delete() {
        super.delete();
        this.status = MemberStatus.WITHDRAWN;
    }

    @Builder
    private Member(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = Role.USER;
        this.status = MemberStatus.ACTIVE;
    }

    /**
     * 회원 정보 수정
     */
    public void updateInfo(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 잔액 충전
     */
    public void charge(Long amount) {
        if (amount <= 0) {
            throw new MemberException(MemberErrorCode.INVALID_CHARGE_AMOUNT);
        }
        this.balance += amount;
    }

    /**
     * 잔액 사용
     */
    public void use(Long amount) {
        if (amount <= 0) {
            throw new MemberException(MemberErrorCode.INVALID_USE_AMOUNT);
        }
        if (this.balance < amount) {
            throw new MemberException(MemberErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance -= amount;
    }
}
