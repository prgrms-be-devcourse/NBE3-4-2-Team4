package com.NBE3_4_2_Team4.member.memberCategory.repository;

import com.NBE3_4_2_Team4.member.memberCategory.entity.MemberCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {
    Optional<MemberCategory> findByName(String name);
}
