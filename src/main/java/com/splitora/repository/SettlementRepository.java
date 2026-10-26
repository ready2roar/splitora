package com.splitora.repository;

import com.splitora.model.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("SELECT s FROM Settlement s WHERE s.payer.id = :userId OR s.payee.id = :userId")
    List<Settlement> findAllSettlementsForUser(@Param("userId") Long userId);
}
