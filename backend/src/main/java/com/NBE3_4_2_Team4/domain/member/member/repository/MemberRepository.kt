package com.NBE3_4_2_Team4.domain.member.member.repository

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String?): Member?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Member m WHERE m.username = :username")
    fun findByUsernameWithLock(@Param("username") username: String?): Member?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Member m WHERE m.id = :id")
    fun findByIdWithLock(@Param("id") id: Long?): Member?

    fun existsByUsername(username: String?): Boolean
}
