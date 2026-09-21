package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountBalanceRangeFilter;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public record BankAccountFilterRangeBalanceSpec(
        @Nullable BankAccountBalanceRangeFilter balanceRange
) implements Specification<BankAccountJpaEntity> {

    @Override
    public @Nullable Predicate toPredicate(
            Root<BankAccountJpaEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {

        if (balanceRange == null || balanceRange.isEmpty()) {
            return null;
        }

        var balance = root.<BigDecimal>get("balance");
        var from = balanceRange.from();
        var to = balanceRange.to();

        if (from == null) {
            return criteriaBuilder.lessThanOrEqualTo(balance, to);
        }

        if (to == null) {
            return criteriaBuilder.greaterThanOrEqualTo(balance, from);
        }

        return criteriaBuilder.between(balance, from, to);
    }
}
