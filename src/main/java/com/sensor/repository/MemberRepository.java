package com.sensor.repository;

import com.sensor.entity.Member;
import com.sensor.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Page<Member> findByAccount(Account account, Pageable pageable);
    long countByAccount(Account account);
}

