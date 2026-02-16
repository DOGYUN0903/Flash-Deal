package com.prj.flashdeal.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.entity.MemberStatus;
import com.prj.flashdeal.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다: " + email));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new UsernameNotFoundException("비활성화된 계정입니다: " + email);
        }

        return CustomUserDetails.from(member);
    }
}
