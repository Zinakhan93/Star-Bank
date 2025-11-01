package com.Star_Bank.recommendation;

import com.Star_Bank.DTO.RecommendationDto;
import com.Star_Bank.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
@Component
public class Invest500RuleSet implements RecommendationRuleSet {
    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_DESCRIPTION = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    private final UserDataRepository userDataRepository;
    public Invest500RuleSet(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        // Правило 1: Пользователь использует как минимум один продукт с типом DEBIT
        boolean usesDebit = userDataRepository.usesProductType(userId, "DEBIT");

        // Правило 2: Пользователь не использует продукты с типом INVEST
        boolean notUsesInvest = !userDataRepository.usesProductType(userId, "INVEST");

        // Правило 3: Сумма пополнений продуктов с типом SAVING больше 1000 ₽
        Long savingDeposits = userDataRepository.getTotalDepositsByProductType(userId, "SAVING");
        boolean savingDepositsOver1000 = savingDeposits > 1000;

        if (usesDebit && notUsesInvest && savingDepositsOver1000) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}
