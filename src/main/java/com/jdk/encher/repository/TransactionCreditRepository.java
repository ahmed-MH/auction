package com.jdk.encher.repository;

import com.jdk.encher.entity.TransactionCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionCreditRepository extends JpaRepository<TransactionCredit, Long> {
    Optional<TransactionCredit> findByPaymentIntentId(String paymentIntentId);
}
