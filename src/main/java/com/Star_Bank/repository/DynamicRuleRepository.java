package com.Star_Bank.repository;

import com.Star_Bank.model.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface DynamicRuleRepository   extends JpaRepository<DynamicRule, UUID> {
    /**
     * Находит правило по productId рекомендуемого продукта
     * @param productId ID продукта из правила
     * @return Optional с найденным правилом
     */
    Optional<DynamicRule> findByProductId(String productId);

    /**
     * Возвращает все активные правила
     * @return список всех динамических правил
     */
    List<DynamicRule> findAll();
}

