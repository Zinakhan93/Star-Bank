package com.Star_Bank.recommendation;

import com.Star_Bank.DTO.Recommendation;
import com.Star_Bank.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
@Component
public class Invest500Rule implements RecommendationRule {
    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_DESCRIPTION = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    private final RecommendationRepository recommendationRepository;
    public Invest500Rule(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public Optional<Recommendation> check(UUID userId) {
        // Правило 1: Пользователь использует как минимум один продукт с типом DEBIT
        boolean usesDebit = recommendationRepository.usesProductType(userId, "DEBIT");

        // Правило 2: Пользователь не использует продукты с типом INVEST
        boolean notUsesInvest = !recommendationRepository.usesProductType(userId, "INVEST");

        // Правило 3: Сумма пополнений продуктов с типом SAVING больше 1000 ₽
        Long savingDeposits = recommendationRepository.getTotalDepositsByProductType(userId, "SAVING");
        boolean savingDepositsOver1000 = savingDeposits > 1000;

        if (usesDebit && notUsesInvest && savingDepositsOver1000) {
            return Optional.of(new Recommendation(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}
