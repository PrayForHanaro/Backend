package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.RegisteredAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegisteredAccountRepository extends JpaRepository<RegisteredAccount, Long> {

    Optional<RegisteredAccount> findBySenderIdAndAccountNumber(Long senderId, String accountNumber);

    List<RegisteredAccount> findAllBySenderIdOrderByLastUsedAtDesc(Long senderId);
}
