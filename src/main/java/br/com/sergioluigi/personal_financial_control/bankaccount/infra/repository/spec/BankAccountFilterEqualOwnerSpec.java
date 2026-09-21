package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public record BankAccountFilterEqualOwnerSpec(String owner) implements Specification<BankAccountJpaEntity> {

    public BankAccountFilterEqualOwnerSpec {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner must not be blank when querying bank accounts");
        }
    }

    @Override
    public @Nullable Predicate toPredicate(
            Root<BankAccountJpaEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        return criteriaBuilder.equal(root.get("audit").get("createdBy"), owner);
    }
}
