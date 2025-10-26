package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Spot;
import com.ermr.eromoro.domain.enums.ContentType;
import com.ermr.eromoro.dto.SpotResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

//    @Query(
//            value = """
//        select s from Spot s
//        where (:category is null or s.contentType = :category)
//          and (:siGunGu is null or s.siGunGu = :siGunGu)
//          and (:keyword is null or lower(s.title) like lower(concat('%', :keyword, '%')))
//        """,
//            countQuery = """
//        select count(s) from Spot s
//        where (:category is null or s.contentType = :category)
//          and (:siGunGu is null or s.siGunGu = :siGunGu)
//          and (:keyword is null or lower(s.title) like lower(concat('%', :keyword, '%')))
//        """
//    )
//    Page<Spot> findSpotByContentTypeAndSiggAreaAndKeyword(@Param("category") ContentType category,
//                      @Param("siGunGu") String siGunGu,
//                      @Param("keyword") String keyword,
//                      Pageable pageable);

    @Query(
        value = """
        SELECT new com.ermr.eromoro.dto.SpotResponseDto$SpotDto(
            s.id,
            s.title,
            s.imageUrl,
            (SELECT CAST(COUNT(c.id) as int)
             FROM Course c
             WHERE c.spot = s),
            new com.ermr.eromoro.dto.SpotResponseDto$UserTypeDto(
                EXISTS(SELECT 1 FROM Course c JOIN c.user u WHERE c.spot = s AND u.userType = com.ermr.eromoro.domain.enums.UserType.DISABLED),
                EXISTS(SELECT 1 FROM Course c JOIN c.user u WHERE c.spot = s AND u.userType = com.ermr.eromoro.domain.enums.UserType.SENIOR),
                EXISTS(SELECT 1 FROM Course c JOIN c.user u WHERE c.spot = s AND u.userType = com.ermr.eromoro.domain.enums.UserType.PREGNANT),
                EXISTS(SELECT 1 FROM Course c JOIN c.user u WHERE c.spot = s AND u.userType = com.ermr.eromoro.domain.enums.UserType.INFANT_GUARDIAN),
                EXISTS(SELECT 1 FROM Course c JOIN c.user u WHERE c.spot = s AND u.userType = com.ermr.eromoro.domain.enums.UserType.CHILD)
            )
        )
        FROM Spot s
        LEFT JOIN s.siggArea sa
        WHERE (:category IS NULL OR s.contentType = :category)
          AND (:siGunGu IS NULL OR sa.name = :siGunGu)
          AND (:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
            countQuery = """
        SELECT COUNT(s)
        FROM Spot s
        LEFT JOIN s.siggArea sa
        WHERE (:category IS NULL OR s.contentType = :category)
          AND (:siGunGu IS NULL OR sa.name = :siGunGu)
          AND (:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """
    )
    Page<SpotResponseDto.SpotDto> findSpotsWithUserTypes(
            @Param("category") ContentType category,
            @Param("siGunGu") String siGunGu,
            @Param("keyword") String keyword,
            Pageable pageable);

    List<Spot> findTop800By();
}
