package com.prj.flashdeal.global.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.entity.Role;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final Role userRole;
    private final String email;
    private final String password;

    public CustomUserDetails(Long userId, Role userRole, String email, String password) {
        this.userId = userId;
        this.userRole = userRole;
        this.email = email;
        this.password = password;
    }

    public static CustomUserDetails from(Member member) {
        return new CustomUserDetails(
            member.getId(),
            member.getRole(),
            member.getEmail(),
            member.getPassword()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
