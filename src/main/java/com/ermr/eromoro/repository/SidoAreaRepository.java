package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.SidoArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SidoAreaRepository extends JpaRepository<SidoArea, Long> {
}
