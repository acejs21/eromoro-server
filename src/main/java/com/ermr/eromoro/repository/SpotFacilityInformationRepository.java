package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.SpotFacilityInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotFacilityInformationRepository extends JpaRepository<SpotFacilityInformation, Long> {
}
