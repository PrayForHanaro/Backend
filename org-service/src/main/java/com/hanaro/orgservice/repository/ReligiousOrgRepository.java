package com.hanaro.orgservice.repository;

import com.hanaro.orgservice.domain.ReligiousOrg;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReligiousOrgRepository extends JpaRepository<ReligiousOrg, Long> {

  Optional<ReligiousOrg> getReligiousOrgByReligiousOrgId(Long religiousOrgId);
}
