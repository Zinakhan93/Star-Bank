package com.Star_Bank.recommendation;

import com.Star_Bank.DTO.RecommendationDto;
import com.Star_Bank.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
@Component
public class SimpleCreditRuleSet  implements RecommendationRuleSet{
    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_DESCRIPTION = "Откройте мир выгодных кредитов с нами!\n\nИщете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n\nПочему выбирают нас:\n\nБыстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n\nУдобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n\nШирокий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n\nНе упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    private final UserDataRepository userDataRepository;

    public SimpleCreditRuleSet(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }
    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        // Правило 1: Пользователь не использует продукты с типом CREDIT
        boolean notUsesCredit = !userDataRepository.usesProductType(userId, "CREDIT");

        // Правило 2: Сумма пополнений по DEBIT больше суммы трат по DEBIT
        Long debitDeposits = userDataRepository.getTotalDepositsByProductType(userId, "DEBIT");
        Long debitSpends = userDataRepository.getTotalSpendsByProductType(userId, "DEBIT");
        boolean depositsGreaterThanSpends = debitDeposits > debitSpends;

        // Правило 3: Сумма трат по DEBIT больше 100,000 ₽
        boolean spendsOver100k = debitSpends > 100000;

        if (notUsesCredit && depositsGreaterThanSpends && spendsOver100k) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}
