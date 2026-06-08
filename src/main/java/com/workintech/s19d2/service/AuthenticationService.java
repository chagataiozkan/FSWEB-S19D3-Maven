package com.workintech.s19d2.service;

import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Member register(String email, String password) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with given email already exist");
        }

        Optional<Role> adminRoleOpt = roleRepository.findByAuthority("ADMIN");
        Role finalRole;

        if (adminRoleOpt.isPresent()) {
            finalRole = adminRoleOpt.get();
        } else {
            finalRole = roleRepository.findByAuthority("USER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "USER")));
        }

        List<Role> roles = new ArrayList<>();
        roles.add(finalRole);

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setRoles(roles);

        return memberRepository.save(member);
    }
}