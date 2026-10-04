package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Matches accounts whose stored balance is not beyond the bound, which is inclusive; no bound matches every account.
 *
 * @param minBalance the lowest balance accepted, or {@code null}
 */
public record BankAccountMinBalanceSpec(@Nullable BigDecimal minBalance) implements Specification<BankAccountJpaEntity> {

    /** Builds the predicate, or {@code null} when there is no bound. */
    @Override
    public @Nullable Predicate toPredicate(Root<BankAccountJpaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (minBalance == null) {
            return null;
        }

        return cb.greaterThanOrEqualTo(root.<BigDecimal>get("balance"), minBalance);
    }
}
