//package com.NBE3_4_2_Team4.domain.member.member.repository;
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import jakarta.persistence.LockModeType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Lock;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Optional;
//
//public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<Member> findByUsername(String username);
//
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT m FROM Member m WHERE m.username = :username")
//    Optional<Member> findByUsernameWithLock(@Param("username") String username);
//
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT m FROM Member m WHERE m.id = :id")
//    Optional<Member> findByIdWithLock(@Param("id") Long id);
//
//    boolean existsByUsername(String username);
//}
