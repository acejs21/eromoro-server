package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.FacilityInformation;
import com.ermr.eromoro.domain.enums.FacilityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FacilityInformationRepository extends JpaRepository<FacilityInformation, Long> {
    List<FacilityInformation> findAllByFacilityInfoIn(Collection<FacilityInfo> facilityInfoList);
}
