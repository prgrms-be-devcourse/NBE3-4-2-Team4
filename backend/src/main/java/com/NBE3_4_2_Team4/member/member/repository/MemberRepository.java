package com.NBE3_4_2_Team4.member.member.repository;

import com.NBE3_4_2_Team4.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
