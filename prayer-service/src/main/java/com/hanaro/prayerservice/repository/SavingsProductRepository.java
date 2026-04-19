package com.hanaro.prayerservice.repository;

import com.hanaro.prayerservice.domain.SavingsProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsProductRepository extends JpaRepository<SavingsProduct, Long> {

    Optional<SavingsProduct> findByIsActiveTrue();
}
