package com.Star_Bank.recommendation;

import com.Star_Bank.DTO.RecommendationDto;
import com.Star_Bank.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
@Component
public class TopSavingRuleSet  implements RecommendationRuleSet{
    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_DESCRIPTION = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n\nПреимущества «Копилки»:\n\nНакопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n\nПрозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n\nБезопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n\nНачните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!";

    private final UserDataRepository userDataRepository;

    public TopSavingRuleSet(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        // Правило 1: Пользователь использует как минимум один продукт с типом DEBIT
        boolean usesDebit = userDataRepository.usesProductType(userId, "DEBIT");

        // Правило 2: Сумма пополнений по DEBIT >= 50,000 ИЛИ Сумма пополнений по SAVING >= 50,000
        Long debitDeposits = userDataRepository.getTotalDepositsByProductType(userId, "DEBIT");
        Long savingDeposits = userDataRepository.getTotalDepositsByProductType(userId, "SAVING");
        boolean depositsOver50k = (debitDeposits >= 50000) || (savingDeposits >= 50000);

        // Правило 3: Сумма пополнений по DEBIT больше суммы трат по DEBIT0
        Long debitSpends = userDataRepository.getTotalSpendsByProductType(userId, "DEBIT");
        boolean depositsGreaterThanSpends = debitDeposits > debitSpends;

        if (usesDebit && depositsOver50k && depositsGreaterThanSpends) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}

