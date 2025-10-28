package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Barrier;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarrierRepository extends JpaRepository<Barrier, Long> {
    @Query("SELECT b FROM Barrier b WHERE within(b.location, :expandedBoundingBox) = true")
    List<Barrier> findByLocationWithin(Polygon expandedBoundingBox);
}
