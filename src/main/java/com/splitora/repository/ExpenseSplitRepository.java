package com.splitora.repository;

import com.splitora.model.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findByUserId(Long userId);
    List<ExpenseSplit> findByExpenseId(Long expenseId);

    // Useful helper query for balance calculations between specific users
    @Query("SELECT es FROM ExpenseSplit es JOIN es.expense e WHERE e.paidBy = :userId AND es.user = :debtorId")
    List<ExpenseSplit> findExpensesPaidByUserAndOwedByDebtor(@Param("userId") Long userId, @Param("debtorId") Long debtorId);
}
