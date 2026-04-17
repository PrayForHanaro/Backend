package com.hanaro.offeringservice.repository;

import com.hanaro.offeringservice.domain.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
}
