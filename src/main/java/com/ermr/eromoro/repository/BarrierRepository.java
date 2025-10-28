package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Barrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarrierRepository extends JpaRepository<Barrier, Long> {
}
