package com.fisa.wonq.member.repository;

import com.fisa.wonq.member.domain.Member;
import com.fisa.wonq.member.domain.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId(String accountId);

    Optional<Member> findByAccountIdAndStatusNot(String accountId, MemberStatus status);

}
